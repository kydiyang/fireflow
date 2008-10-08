/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.kenel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.kenel.impl.ActivityInstance;
import org.fireflow.kenel.impl.EndNodeInstance;
import org.fireflow.kenel.impl.NetInstance;
import org.fireflow.kenel.impl.StartNodeInstance;
import org.fireflow.kenel.impl.SynchronizerInstance;
import org.fireflow.kenel.impl.TransitionInstance;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;

/**
 * @author chennieyun
 *
 */
public class KenelManager {

    private HashMap<String,Object> wfElementInstanceMap = new HashMap<String,Object>();
    private Map<String, List<IKenelExtension>> kenelExtensions = new HashMap<String, List<IKenelExtension>>();

    public Object getWFElementInstance(String wfElementId) {
        return wfElementInstanceMap.get(wfElementId);
    }

    public void putWFElementInstance(String wfElementId, Object instance) {
        wfElementInstanceMap.put(wfElementId, instance);
    }
    
    public void clearAllWFElementInstance(){
        wfElementInstanceMap.clear();
    }

    public INetInstance createNetInstance(WorkflowProcess workflowProcess) throws KenelException {
//		Map nodeInstanceMap = new HashMap();
        if (workflowProcess.validate()!=null)return null;
        
        NetInstance netInstance = new NetInstance();
        netInstance.setWorkflowProcess(workflowProcess);
        wfElementInstanceMap.put(netInstance.getId(), netInstance);

        StartNode startNode = workflowProcess.getStartNode();
        StartNodeInstance startNodeInstance = new StartNodeInstance(startNode);
        List<IKenelExtension> extensionList = kenelExtensions.get(startNodeInstance.getExtensionTargetName());
        for (int i = 0; extensionList != null && i < extensionList.size(); i++) {
            IKenelExtension extension =  extensionList.get(i);
            startNodeInstance.registExtension(extension);
        }
        netInstance.setStartNodeInstance(startNodeInstance);
        wfElementInstanceMap.put(startNode.getId(), startNodeInstance);


        List activities = workflowProcess.getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = (Activity) activities.get(i);
            ActivityInstance activityInstance = new ActivityInstance(activity);
            extensionList = kenelExtensions.get(activityInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKenelExtension extension = extensionList.get(j);
                activityInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(activity.getId(), activityInstance);
        }

        List synchronizers = workflowProcess.getSynchronizers();
        for (int i = 0; i < synchronizers.size(); i++) {
            Synchronizer synchronizer = (Synchronizer) synchronizers.get(i);
            SynchronizerInstance synchronizerInstance = new SynchronizerInstance(synchronizer);
            extensionList = kenelExtensions.get(synchronizerInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKenelExtension extension =  extensionList.get(j);
                synchronizerInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(synchronizer.getId(), synchronizerInstance);
        }

        List<EndNode> endNodes = workflowProcess.getEndNodes();
        List<EndNodeInstance> endNodeInstances = netInstance.getEndNodeInstances();
        for (int i = 0; i < endNodes.size(); i++) {
            EndNode endNode = endNodes.get(i);
            EndNodeInstance endNodeInstance = new EndNodeInstance(endNode);
            endNodeInstances.add(endNodeInstance);
            extensionList = kenelExtensions.get(endNodeInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKenelExtension extension =  extensionList.get(j);
                endNodeInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(endNode.getId(), endNodeInstance);
        }

        List transitions = workflowProcess.getTransitions();
        for (int i = 0; i < transitions.size(); i++) {
            Transition transition = (Transition) transitions.get(i);
            TransitionInstance transitionInstance = new TransitionInstance(transition);

            String fromNodeId = transition.getFromNode().getId();
            if (fromNodeId != null) {
                INodeInstance enteringNodeInstance = (INodeInstance) wfElementInstanceMap.get(fromNodeId);
                if (enteringNodeInstance != null) {
                    enteringNodeInstance.addLeavingTransitionInstance(transitionInstance);
                    transitionInstance.setEnteringNodeInstance(enteringNodeInstance);
                }
            }

            String toNodeId = transition.getToNode().getId();
            if (toNodeId != null) {
                INodeInstance leavingNodeInstance = (INodeInstance) wfElementInstanceMap.get(toNodeId);
                if (leavingNodeInstance != null) {
                    leavingNodeInstance.addEnteringTransitionInstance(transitionInstance);
                    transitionInstance.setLeavingNodeInstance(leavingNodeInstance);
                }
            }
            extensionList = kenelExtensions.get(transitionInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKenelExtension extension =  extensionList.get(j);
                transitionInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(transitionInstance.getId(), transitionInstance);
        }
//		netInstance.setRtCxt(new RuntimeContext());
        return netInstance;
    }

    public Map<String, List<IKenelExtension>> getKenelExtensions() {
        return kenelExtensions;
    }

    public void setKenelExtensions(Map<String, List<IKenelExtension>> kenelExtensions) {
        this.kenelExtensions = kenelExtensions;
    }
}
