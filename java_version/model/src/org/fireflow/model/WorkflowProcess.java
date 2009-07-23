/**
 * Copyright 2003-2008 非也
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
import org.fireflow.model.net.Loop;
import org.fireflow.model.net.Node;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;

/**
 * 业务流程。<br/>
 * 这是Fire workflow工作流模型的最顶层元素。
 * @author 非也,nychen2000@163.com
 *
 */
public class WorkflowProcess extends AbstractWFElement {

    //子元素
//    private List formalParameters = new ArrayList();
	/**
	 * 流程数据项，运行时转换为流程变量进行存储。
	 */
    private List<DataField> dataFields = new ArrayList<DataField>();
    
    /**
     * 全局Task
     */
    private List<Task> tasks = new ArrayList<Task>();
    
    /**
     * 流程环节
     */
    private List<Activity> activities = new ArrayList<Activity>();
    
    /**
     * 转移
     */
    private List<Transition> transitions = new ArrayList<Transition>();
    
    /**
     * 循环
     */
    private List<Loop> loops = new ArrayList<Loop>();
    
    /**
     * 同步器
     */
    private List<Synchronizer> synchronizers = new ArrayList<Synchronizer>();
    
    /**
     * 开始节点
     */
    private StartNode startNode = null;
    
    /**
     * 结束节点
     */
    private List<EndNode> endNodes = new ArrayList<EndNode>();
    
    
    //其他属性
    /**
     * 资源文件（在1.0中暂时未使用）
     */
    private String resourceFile = null;
    
    /**
     * 资源管理器（在1.0中暂时未使用）
     */
    private String resourceManager = null;

    /**
     * 本流程全局的任务实例创建器。
     * 如果没有设置，引擎将使用DefaultTaskInstanceCreator来创建TaskInstance。
     */
    protected String taskInstanceCreator = null;

    /**
     * 本流程全局的FormTask Instance运行器。如果没有设置，引擎将使用DefaultFormTaskInstanceRunner来运行TaskInstance。
     */
    protected String formTaskInstanceRunner = null;

    /**
     * 本流程全局的ToolTask Instance运行器。如果没有设置，引擎将使用DefaultToolTaskInstanceRunner来运行TaskInstance。
     */
    protected String toolTaskInstanceRunner = null;

    /**
     * 本流程全局的SubflowTask Instance运行器。如果没有设置，引擎将使用DefaultSubflowTaskInstanceRunner来运行TaskInstance。
     */
    protected String subflowTaskInstanceRunner = null;


    /**
     * 本流程全局的FormTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
     * 如果没有设置，引擎使用缺省实现DefaultFormTaskInstanceCompletionEvaluator。
     */
    protected String formTaskInstanceCompletionEvaluator = null;

    /**
     * 本流程全局的ToolTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
     * 如果没有设置，引擎使用缺省实现DefaultToolTaskInstanceCompletionEvaluator。
     */    
    protected String toolTaskInstanceCompletionEvaluator = null;

    /**
     * 本流程全局的SubflowTask Instance 终结评价器，用于告诉引擎该实例是否可以结束。<br/>
     * 如果没有设置，引擎使用缺省实现DefaultSubflowTaskInstanceCompletionEvaluator。
     */ 
    protected String subflowTaskInstanceCompletionEvaluator = null;
    
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
    /**
     * 返回所有的流程数据项
     * @return
     */
    public List<DataField> getDataFields() {
        return dataFields;
    }

    /**
     * 返回所有的环节
     * @return
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * 返回所有的循环
     * @return
     */
    public List<Loop> getLoops(){
        return loops;
    }

    /**
     * 返回所有的转移
     * @return
     */
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
    
    /**
     * 返回开始节点
     * @return
     */
    public StartNode getStartNode() {
        return startNode;
    }

    public void setStartNode(StartNode startNode) {
        this.startNode = startNode;
    }

    /**
     * 返回所有的结束节点
     * @return
     */
    public List<EndNode> getEndNodes() {
        return endNodes;
    }

    /**
     * 返回所有的同步器
     * @return
     */
    public List<Synchronizer> getSynchronizers() {
        return synchronizers;
    }

    /**
     * 返回所有的全局Task
     * @return
     */
    public List<Task> getTasks(){
        return this.tasks;
    }


    /**
     * 保留
     * @return
     */
    public String getResourceFile() {
        return resourceFile;
    }

    /**
     * 保留
     * @return
     */
    public void setResourceFile(String resourceFile) {
        this.resourceFile = resourceFile;
    }
    /**
     * 保留
     * @return
     */
    public String getResourceManager() {
        return resourceManager;
    }
    /**
     * 保留
     * @return
     */
    public void setResourceManager(String resourceMgr) {
        this.resourceManager = resourceMgr;
    }

    /**
     * 通过ID查找该流程中的任意元素
     * @param id 元素的Id
     * @return 流程元素，如：Activity,Task,Synchronizer等等
     */
    public IWFElement findWFElementById(String id) {
        if (this.getId().equals(id)) {
            return this;
        }

        List tasksList = this.getTasks();
        for (int i=0;i<tasksList.size();i++){
            Task task = (Task)tasksList.get(i);
            if (task.getId().equals(id)){
                return task;
            }
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

        List loopList = this.getLoops();
        for (int i=0;i<loopList.size();i++){
            IWFElement wfElement = (IWFElement)loopList.get(i);
            if (wfElement.getId().equals(id)){
                return wfElement;
            }
        }
        return null;
    }

    /**
     * 通过Id查找任意元素的序列号
     * @param id 流程元素的id
     * @return 流程元素的序列号
     */
    public String findSnById(String id) {
        IWFElement elem = this.findWFElementById(id);
        if (elem != null) {
            return elem.getSn();
        }
        return null;
    }

    /**
     * 验证workflow process是否完整正确。
     * @return null表示流程正确；否则表示流程错误，返回值是错误原因
     */
    public String validate() {
        String errHead = "Workflow process is invalid：";
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
                    FormTask formTask = (FormTask)task;
                    if (formTask.getPerformer() == null) {
                        return errHead + "FORM-task[id=" + task.getId() + "] must has a performer.";
                    }
                } else if (task.getType().equals(Task.TOOL)) {
                    ToolTask toolTask = (ToolTask)task;
                    if (toolTask.getApplication() == null) {
                        return errHead + "TOOL-task[id=" + task.getId() + "] must has a application.";
                    }
                } else if (task.getType().equals(Task.SUBFLOW)) {
                    SubflowTask subflowTask = (SubflowTask)task;
                    if (subflowTask.getSubWorkflowProcess() == null) {
                        return errHead + "SUBFLOW-task[id=" + task.getId() + "] must has a subflow.";
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
     * @return true表示在同一个执行线上，false表示不在同一个执行线上
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
        connectableNodes4Activity2.add(node2);
        connectableNodes4Activity2.addAll(getReachableNodes(activityId2));
        connectableNodes4Activity2.addAll(getEnterableNodes(activityId2));
        /*
        System.out.println("===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity1.size()="+connectableNodes4Activity1.size());
        System.out.println("===Inside WorkflowProcess.isInSameLine()::connectableNodes4Activity2.size()="+connectableNodes4Activity2.size());
        System.out.println("-----------------------activity1--------------");
        for (int i=0;i<connectableNodes4Activity1.size();i++){
            Node node = (Node)connectableNodes4Activity1.get(i);
            System.out.println("node.id of act1 is "+node.getId());
        }
        
        System.out.println("---------------------activity2--------------------");
        for (int i=0;i<connectableNodes4Activity2.size();i++){
            Node node = (Node)connectableNodes4Activity2.get(i);
            System.out.println("node.id of act2 is "+node.getId());
        }
         */ 
        
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
        
        List tmp = new ArrayList();
        boolean alreadyInTheList = false;
        for (int i=0;i<reachableNodesList.size();i++){
            Node nodeTmp = (Node)reachableNodesList.get(i);
            alreadyInTheList = false;
            for (int j=0;j<tmp.size();j++){
                Node nodeTmp2 = (Node)tmp.get(j);
                if (nodeTmp2.getId().equals(nodeTmp.getId())){
                    alreadyInTheList = true;
                    break;
                }
            }
            if (!alreadyInTheList){
                tmp.add(nodeTmp);
            }
        }
        reachableNodesList = tmp;
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
        
        List tmp = new ArrayList();
        boolean alreadyInTheList = false;
        for (int i=0;i<enterableNodesList.size();i++){
            Node nodeTmp = (Node)enterableNodesList.get(i);
            alreadyInTheList = false;
            for (int j=0;j<tmp.size();j++){
                Node nodeTmp2 = (Node)tmp.get(j);
                if (nodeTmp2.getId().equals(nodeTmp.getId())){
                    alreadyInTheList = true;
                    break;
                }
            }
            if (!alreadyInTheList){
                tmp.add(nodeTmp);
            }
        }
        enterableNodesList = tmp;        
        return enterableNodesList;
    }


    public String getTaskInstanceCreator() {
        return taskInstanceCreator;
    }

    public void setTaskInstanceCreator(String taskInstanceCreator) {
        this.taskInstanceCreator = taskInstanceCreator;
    }

    public String getFormTaskInstanceCompletionEvaluator() {
        return formTaskInstanceCompletionEvaluator;
    }

    public void setFormTaskInstanceCompletionEvaluator(String formTaskInstanceCompletionEvaluator) {
        this.formTaskInstanceCompletionEvaluator = formTaskInstanceCompletionEvaluator;
    }

    public String getFormTaskInstanceRunner() {
        return formTaskInstanceRunner;
    }

    public void setFormTaskInstanceRunner(String formTaskInstanceRunner) {
        this.formTaskInstanceRunner = formTaskInstanceRunner;
    }

    public String getSubflowTaskInstanceCompletionEvaluator() {
        return subflowTaskInstanceCompletionEvaluator;
    }

    public void setSubflowTaskInstanceCompletionEvaluator(String subflowTaskInstanceCompletionEvaluator) {
        this.subflowTaskInstanceCompletionEvaluator = subflowTaskInstanceCompletionEvaluator;
    }

    public String getSubflowTaskInstanceRunner() {
        return subflowTaskInstanceRunner;
    }

    public void setSubflowTaskInstanceRunner(String subflowTaskInstanceRunner) {
        this.subflowTaskInstanceRunner = subflowTaskInstanceRunner;
    }

    public String getToolTaskInstanceRunner() {
        return toolTaskInstanceRunner;
    }

    public void setToolTaskInstanceRunner(String toolTaskInstanceRunner) {
        this.toolTaskInstanceRunner = toolTaskInstanceRunner;
    }

    public String getToolTaskInstanceCompletionEvaluator() {
        return toolTaskInstanceCompletionEvaluator;
    }

    public void setToolTaskInstanceCompletionEvaluator(String toolTaskIntanceCompletionEvaluator) {
        this.toolTaskInstanceCompletionEvaluator = toolTaskIntanceCompletionEvaluator;
    }

    
}
