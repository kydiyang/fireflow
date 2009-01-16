/**
 * Copyright 2003-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
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
package org.fireflow.model;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Node;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;

/**
 * @author chennieyun
 *
 */
public class WorkflowProcess extends AbstractWFElement {

    //子元素
//    private List formalParameters = new ArrayList();
    private List<DataField> dataFields = new ArrayList<DataField>();
    private List<Activity> activities = new ArrayList<Activity>();
    private List<Transition> transitions = new ArrayList<Transition>();
    private List<Synchronizer> synchronizers = new ArrayList<Synchronizer>();
    private StartNode startNode = null;
    private List<EndNode> endNodes = new ArrayList<EndNode>();
    //其他属性
    private String resourceFile = null;
    private String resourceManager = null;
//    private int version = 1;//version在流程定义中不需要，只有在流程存储中需要，每次updatge数据库，都需要增加Version值
    /**
     * 构造函数
     * @param id
     * @param name
     * @param pkg
     */
    public WorkflowProcess(String name) {
        super(null, name);
    }
//    public List getFormalParameters() {
//        return formalParameters;
//    }
    public List<DataField> getDataFields() {
        return dataFields;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

//    public int getVersion() {
//        return version;
//    }
//
//    public void setVersion(int version) {
//        this.version = version;
//    }
    public StartNode getStartNode() {
        return startNode;
    }

    public void setStartNode(StartNode startNode) {
        this.startNode = startNode;
    }

    public List<EndNode> getEndNodes() {
        return endNodes;
    }

    public List<Synchronizer> getSynchronizers() {
        return synchronizers;
    }

    public String getResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(String resourceFile) {
        this.resourceFile = resourceFile;
    }

    public String getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(String resourceMgr) {
        this.resourceManager = resourceMgr;
    }

    public IWFElement findWFElementById(String id) {
        if (this.getId().equals(id)) {
            return this;
        }

        List activityList = this.getActivities();
        for (int i = 0; i < activityList.size(); i++) {
            IWFElement wfElement = (IWFElement) activityList.get(i);
            if (wfElement.getId().equals(id)) {
                return wfElement;
            }
            List taskList = ((Activity) wfElement).getTasks();
            for (int j = 0; j < taskList.size(); j++) {
                IWFElement wfElement2 = (IWFElement) taskList.get(j);
                if (wfElement2.getId().equals(id)) {
                    return wfElement2;
                }
            }
        }
        if (this.getStartNode().getId().equals(id)) {
            return this.getStartNode();
        }
        List synchronizerList = this.getSynchronizers();
        for (int i = 0; i < synchronizerList.size(); i++) {
            IWFElement wfElement = (IWFElement) synchronizerList.get(i);
            if (wfElement.getId().equals(id)) {
                return wfElement;
            }
        }

        List endNodeList = this.getEndNodes();
        for (int i = 0; i < endNodeList.size(); i++) {
            IWFElement wfElement = (IWFElement) endNodeList.get(i);
            if (wfElement.getId().equals(id)) {
                return wfElement;
            }
        }

        List transitionList = this.getTransitions();
        for (int i = 0; i < transitionList.size(); i++) {
            IWFElement wfElement = (IWFElement) transitionList.get(i);
            if (wfElement.getId().equals(id)) {
                return wfElement;
            }
        }

        List dataFieldList = this.getDataFields();
        for (int i = 0; i < dataFieldList.size(); i++) {
            IWFElement wfElement = (IWFElement) dataFieldList.get(i);
            if (wfElement.getId().equals(id)) {
                return wfElement;
            }
        }
        return null;
    }

    public String findSnById(String id) {
        IWFElement elem = this.findWFElementById(id);
        if (elem != null) {
            return elem.getSn();
        }
        return null;
    }

    /**
     * 验证workflow process是否完整正确
     * @return null表示流程正确；否则表示流程错误，返回值是错误原因
     */
    public String validate() {
        String errHead = "Workflow Process Validate Error：";
        if (this.getStartNode() == null) {
            return errHead + "must have one start node";
        }
        if (this.getStartNode().getLeavingTransitions().size() == 0) {
            return errHead + "start node must have leaving transitions.";
        }

        List<Activity> activities = this.getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            String theName = (activity.getDisplayName() == null || activity.getDisplayName().equals("")) ? activity.getName() : activity.getDisplayName();
            if (activity.getEnteringTransition() == null) {
                return errHead + "activity[" + theName + "] must have entering transition.";
            }
            if (activity.getLeavingTransition() == null) {
                return errHead + "activity[" + theName + "] must have leaving transition.";
            }

            //check tasks
            List taskList = activity.getTasks();
            for (int j = 0; j < taskList.size(); j++) {
                Task task = (Task) taskList.get(j);
                if (task.getType() == null) {
                    return errHead + "task[" + task.getId() + "]'s taskType can Not be null.";
                } else if (task.getType().equals(Task.FORM)) {
                    if (task.getPerformer() == null) {
                        return errHead + "FORM task[" + task.getId() + "] must has a performer.";
                    }
                } else if (task.getType().equals(Task.TOOL)) {
                    if (task.getApplication() == null) {
                        return errHead + "TOOL task[" + task.getId() + "] must has a application.";
                    }
                } else if (task.getType().equals(Task.SUBFLOW)) {
                    if (task.getSubWorkflowProcess() == null) {
                        return errHead + "SUBFLOW task[" + task.getId() + "] must has a subflow.";
                    }
                } else {
                    return errHead + " unknown task type of task[" + task.getId() + "]";
                }
            }
        }

        List<Synchronizer> synchronizers = this.getSynchronizers();
        for (int i = 0; i < synchronizers.size(); i++) {
            Synchronizer synchronizer = synchronizers.get(i);
            String theName = (synchronizer.getDisplayName() == null || synchronizer.getDisplayName().equals("")) ? synchronizer.getName() : synchronizer.getDisplayName();
            if (synchronizer.getEnteringTransitions().size() == 0) {
                return errHead + "synchronizer[" + theName + "] must have entering transition.";
            }
            if (synchronizer.getLeavingTransitions().size() == 0) {
                return errHead + "synchronizer[" + theName + "] must have leaving transition.";
            }
        }

        List<EndNode> endnodes = this.getEndNodes();
        for (int i = 0; i < endnodes.size(); i++) {
            EndNode endnode = endnodes.get(i);
            String theName = (endnode.getDisplayName() == null || endnode.getDisplayName().equals("")) ? endnode.getName() : endnode.getDisplayName();
            if (endnode.getEnteringTransitions().size() == 0) {
                return errHead + "end node[" + theName + "] must have entering transition.";
            }
        }

        List<Transition> transitions = this.getTransitions();
        for (int i = 0; i < transitions.size(); i++) {
            Transition transition = transitions.get(i);
            String theName = (transition.getDisplayName() == null || transition.getDisplayName().equals("")) ? transition.getName() : transition.getDisplayName();
            if (transition.getFromNode() == null) {
                return errHead + "transition[" + theName + "] must have from node.";

            }
            if (transition.getToNode() == null) {
                return errHead + "transition[" + theName + "] must have to node.";
            }
        }

        //check datafield
        List dataFieldList = this.getDataFields();
        for (int i = 0; i < dataFieldList.size(); i++) {
            DataField df = (DataField) dataFieldList.get(i);
            if (df.getDataType() == null) {
                return errHead + "unknown data type of datafield[" + df.getId() + "]";
            }
        }

        return null;
    }

    /**
     * 判断两个Activity是否在同一个执行线上
     * @param activityId1
     * @param activityId2
     * @return
     */
    public boolean isInSameLine(String activityId1, String activityId2) {
        Node node1 = (Node)this.findWFElementById(activityId1);
        Node node2 = (Node)this.findWFElementById(activityId2);
        if (node1==null || node2==null) return false;
        List connectableNodes4Activity1 = new ArrayList();
        connectableNodes4Activity1.add(node1);
        connectableNodes4Activity1.addAll(getReachableNodes(activityId1));
        connectableNodes4Activity1.addAll(getEnterableNodes(activityId1));
        
        List connectableNodes4Activity2 = new ArrayList();
        connectableNodes4Activity1.add(node2);
        connectableNodes4Activity1.addAll(getReachableNodes(activityId2));
        connectableNodes4Activity1.addAll(getEnterableNodes(activityId2));
        
        if (connectableNodes4Activity1.size()!=connectableNodes4Activity2.size()){
            return false;
        }
        
        for (int i=0;i<connectableNodes4Activity1.size();i++){
            Node node = (Node)connectableNodes4Activity1.get(i);
            boolean find = false;
            for (int j=0;j<connectableNodes4Activity2.size();j++){
                Node tmpNode = (Node)connectableNodes4Activity2.get(j);
                if (node.getId().equals(tmpNode.getId())){
                    find = true;
                    break;
                }
            }
            if (!find) return false;
        }
        return true;
    }

    public List getReachableNodes(String nodeId) {
        List reachableNodesList = new ArrayList();
        Node node = (Node) this.findWFElementById(nodeId);
        if (node instanceof Activity) {
            Activity activity = (Activity) node;
            Transition leavingTransition = activity.getLeavingTransition();
            if (leavingTransition != null) {
                Node toNode = (Node) leavingTransition.getToNode();
                if (toNode != null) {
                    reachableNodesList.add(toNode);
                    reachableNodesList.addAll(getReachableNodes(toNode.getId()));
                }
            }
        } else if (node instanceof Synchronizer) {
            Synchronizer synchronizer = (Synchronizer) node;
            List leavingTransitions = synchronizer.getLeavingTransitions();
            for (int i = 0; leavingTransitions != null && i < leavingTransitions.size(); i++) {
                Transition leavingTransition = (Transition) leavingTransitions.get(i);
                if (leavingTransition != null) {
                    Node toNode = (Node) leavingTransition.getToNode();
                    if (toNode != null) {
                        reachableNodesList.add(toNode);
                        reachableNodesList.addAll(getReachableNodes(toNode.getId()));
                    }

                }
            }
        }
        return reachableNodesList;
    }
    
    public List getEnterableNodes(String nodeId){
        List enterableNodesList = new ArrayList();
        Node node = (Node) this.findWFElementById(nodeId);
        if (node instanceof Activity) {
            Activity activity = (Activity) node;
            Transition enteringTransition = activity.getEnteringTransition();
            if (enteringTransition != null) {
                Node fromNode = (Node) enteringTransition.getFromNode();
                if (fromNode != null) {
                    enterableNodesList.add(fromNode);
                    enterableNodesList.addAll(getEnterableNodes(fromNode.getId()));
                }
            }
        } else if (node instanceof Synchronizer) {
            Synchronizer synchronizer = (Synchronizer) node;
            List enteringTransitions = synchronizer.getEnteringTransitions();
            for (int i = 0; enteringTransitions != null && i < enteringTransitions.size(); i++) {
                Transition enteringTransition = (Transition) enteringTransitions.get(i);
                if (enteringTransition != null) {
                    Node fromNode = (Node) enteringTransition.getFromNode();
                    if (fromNode != null) {
                        enterableNodesList.add(fromNode);
                        enterableNodesList.addAll(getEnterableNodes(fromNode.getId()));
                    }

                }
            }
        }
        return enterableNodesList;
    }
}
