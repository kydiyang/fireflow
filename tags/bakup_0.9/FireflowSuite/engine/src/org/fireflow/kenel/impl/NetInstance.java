/**
 * Copyright 2004-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.kenel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import org.fireflow.engine.IRuntimeContext;
import java.util.Map;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.kenel.INetInstance;

import org.fireflow.kenel.INodeInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.INodeInstanceEventListener;

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
public class NetInstance implements INetInstance {

    private WorkflowProcess workflowProcess = null;
    private Integer version = null;
    private StartNodeInstance startNodeInstance = null;
//	private List<EndNodeInstance> endNodeInstances = new ArrayList<EndNodeInstance>();
    private Map<String, Object> wfElementInstanceMap = new HashMap<String, Object>();
//	private IRuntimeContext runtimeContext = null;
    protected List<INodeInstanceEventListener> eventListeners = new ArrayList<INodeInstanceEventListener>();

    public NetInstance(WorkflowProcess process, final Map<String, List<IKenelExtension>> kenelExtensions) throws KenelException{
        this.workflowProcess = process;

        StartNode startNode = workflowProcess.getStartNode();
        startNodeInstance = new StartNodeInstance(startNode);
        List<IKenelExtension> extensionList = kenelExtensions.get(startNodeInstance.getExtensionTargetName());
        for (int i = 0; extensionList != null && i < extensionList.size(); i++) {
            IKenelExtension extension = extensionList.get(i);
            startNodeInstance.registExtension(extension);
        }
        this.setStartNodeInstance(startNodeInstance);
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
                IKenelExtension extension = extensionList.get(j);
                synchronizerInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(synchronizer.getId(), synchronizerInstance);
        }

        List<EndNode> endNodes = workflowProcess.getEndNodes();
//        List<EndNodeInstance> endNodeInstances = netInstance.getEndNodeInstances();
        for (int i = 0; i < endNodes.size(); i++) {
            EndNode endNode = endNodes.get(i);
            EndNodeInstance endNodeInstance = new EndNodeInstance(endNode);
//            endNodeInstances.add(endNodeInstance);
            extensionList = kenelExtensions.get(endNodeInstance.getExtensionTargetName());
            for (int j = 0; extensionList != null && j < extensionList.size(); j++) {
                IKenelExtension extension = extensionList.get(j);
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
                IKenelExtension extension = extensionList.get(j);
                transitionInstance.registExtension(extension);
            }
            wfElementInstanceMap.put(transitionInstance.getId(), transitionInstance);
        }
    }

    public String getId() {
        return this.workflowProcess.getId();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer v) {
        this.version = v;
    }

    public void run(IProcessInstance processInstance) throws KenelException {
        if (startNodeInstance == null) {
            throw new KenelException("Error:NetInstance is illegal ，the startNodeInstance can NOT be NULL ");
        }

        Token token = new Token();
        token.setAlive(true);
        token.setProcessInstance(processInstance);
        token.setValue(startNodeInstance.getVolume());

        //processevent应该放在processInstance中去触发
//        ProcessInstanceEvent event = new ProcessInstanceEvent();
//        event.setToken(token);
//        this.fireBeforeProcessInstanceRunEvent(event);//??

        startNodeInstance.fire(token);
    }

    /**
     * 结束流程实例，如果流程状态没有达到终态，则直接返回。
     * @throws RuntimeException
     */
    public void complete() throws RuntimeException {
        //1、判断是否所有的EndeNodeInstance都到达终态，如果没有到达，则直接返回。
        //2、执行compelete操作，
        //3、触发after complete事件
        //4、返回主流程
    }


//	public IRuntimeContext getRtCxt() {
//		return runtimeContext;
//	}
//
//	public void setRtCxt(IRuntimeContext rtCxt) {
//		this.runtimeContext = rtCxt;
//	}

    public WorkflowProcess getWorkflowProcess() {
        return workflowProcess;
    }

//	public void setWorkflowProcess(WorkflowProcess workflowProcess) {
//		this.workflowProcess = workflowProcess;
//
//	}
    public StartNodeInstance getStartNodeInstance() {
        return startNodeInstance;
    }

    public void setStartNodeInstance(StartNodeInstance startNodeInstance) {
        this.startNodeInstance = startNodeInstance;
    }

//    public List<EndNodeInstance> getEndNodeInstances() {
//        return endNodeInstances;
//    }

//	public void setEndNodeInstances(List endNodeInstances) {
//		EndNodeInstances = endNodeInstances;
//	}
//    protected void fireBeforeProcessInstanceRunEvent(ProcessInstanceEvent event) throws KenelException {
//        for (int i = 0; i < this.eventListeners.size(); i++) {
//            IProcessInstanceEventListener listener = (IProcessInstanceEventListener) this.eventListeners.get(i);
//            listener.onProcessInstanceFired(event);
//        }
//    }
//
//    protected void fireAfterProcessInstanceCompleteEvent(ProcessInstanceEvent event) throws KenelException {
//        for (int i = 0; i < this.eventListeners.size(); i++) {
//            IProcessInstanceEventListener listener = (IProcessInstanceEventListener) this.eventListeners.get(i);
//            listener.onProcessInstanceFired(event);
//        }
//    }
//	public void setRuntimeContext(IRuntimeContext rtCtx){
//		runtimeContext = rtCtx;
//	}
//	
//	public IRuntimeContext getRuntimeContext(){
//		return runtimeContext;
//	}

    public Object getWFElementInstance(String wfElementId) {
        return wfElementInstanceMap.get(wfElementId);
    }   
}
