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
    private int version = 1;

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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
        if (this.getStartNode().getId().equals(id)) {
            return this.getStartNode();
        }
        List activityList = this.getActivities();
        for (int i = 0; i < activityList.size(); i++) {
            IWFElement wfElement = (IWFElement) activityList.get(i);
            if (wfElement.getId().equals(id)) {
                return wfElement;
            }
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
        return null;
    }
    
    public String findSnById(String id){
        IWFElement elem = this.findWFElementById(id);
        if (elem!=null)return elem.getSn();
        return null;
    }
    
    /**
     * 验证workflow process是否完整正确
     * @return null表示流程正确；否则表示流程错误，返回值是错误原因
     */
    public String validate(){
        String errHead = "Workflow Process Validate Error：";
        if (this.getStartNode()==null ){
            return errHead+"must have one start node";
        }
        if (this.getStartNode().getLeavingTransitions().size()==0){
            return errHead+"start node must have leaving transitions.";
        }
        
        List<Activity> activities = this.getActivities();
        for (int i=0;i<activities.size();i++){
            Activity activity = activities.get(i);
            String theName = (activity.getDisplayName()==null||activity.getDisplayName().equals(""))?activity.getName():activity.getDisplayName();
            if (activity.getEnteringTransition()==null){
                return errHead+"activity["+theName+"] must have entering transition.";
            }
            if (activity.getLeavingTransition()==null){
                return errHead+"activity["+theName+"] must have leaving transition.";
            }
        }
        
        List<Synchronizer> synchronizers = this.getSynchronizers();
        for (int i=0;i<synchronizers.size();i++){
            Synchronizer synchronizer = synchronizers.get(i);
            String theName = (synchronizer.getDisplayName()==null || synchronizer.getDisplayName().equals(""))?synchronizer.getName():synchronizer.getDisplayName();
            if (synchronizer.getEnteringTransitions().size()==0){
                return errHead+"synchronizer["+theName+"] must have entering transition.";
            }
            if (synchronizer.getLeavingTransitions().size()==0){
                return errHead+"synchronizer["+theName+"] must have leaving transition.";
            }
        }
        
        List<EndNode> endnodes = this.getEndNodes();
        for(int i=0;i<endnodes.size();i++){
            EndNode endnode = endnodes.get(i);
            String theName = (endnode.getDisplayName()==null || endnode.getDisplayName().equals(""))?endnode.getName():endnode.getDisplayName();
            if (endnode.getEnteringTransitions().size()==0){
                return errHead+"end node["+theName+"] must have entering transition.";
            }
        }
        
        List<Transition> transitions = this.getTransitions();
        for (int i=0;i<transitions.size();i++){
            Transition transition = transitions.get(i);
            String theName = (transition.getDisplayName()==null||transition.getDisplayName().equals(""))?transition.getName():transition.getDisplayName();
            if (transition.getFromNode()==null){
                return errHead+"transition["+theName+"] must have from node.";
                
            }
            if (transition.getToNode()==null){
                return errHead+"transition["+theName+"] must have to node.";
            }
        }
        
        return null;
    }
}
