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

package org.fireflow.model;

import org.fireflow.model.net.Activity;
import org.fireflow.model.reference.Application;
import org.fireflow.model.reference.Form;
import org.fireflow.model.reference.Participant;
import org.fireflow.model.reference.SubWorkflowProcess;


public class Task extends AbstractWFElement{
	public static final String TOOL = "TOOL";
	public static final String SUBFLOW = "SUBFLOW";
	public static final String FORM = "FORM";
	public static final String DUMMY = "DUMMY";
	
        /**
         * @deprecated 
         */
	public static final String MANUAL = "MANUAL";
         /**
         * @deprecated 
         */
	public static final String AUTOMATIC = "AUTOMATIC";
	
	public static final String ASYNCHR = "ASYNCHR";
	public static final String SYNCHR = "SYNCHR";
	
	public static final String EDITFORM = "EDITFORM";
	public static final String VIEWFORM = "VIEWFORM";
	public static final String LISTFORM = "LISTFORM";
	
	public static final String ALL = "ALL";
	public static final String ANY = "ANY";
	
	protected Form editForm = null;
	protected Form viewForm = null;
	protected Form listForm = null;
	
	protected Application application = null;
    
    protected Participant performer;//引用participant
    protected Duration duration;
    
    protected SubWorkflowProcess subWorkflowProcess = null;

    protected String type = FORM;//任务类型
//    protected String startMode = MANUAL ;//启动模式，启动模式没有意义，application和subflow自动启动，Form一般情况下签收时启动，如果需要自动启动则在assignable接口中实现。
    protected String assignmentStrategy=ANY;//workItem分配策略，即任何一个人完成，则taskinstance算完成。
    protected String defaultView = VIEWFORM ;//缺省视图是view form
    protected int priority = 1;
    protected String execution = SYNCHR;//缺省情况下是同步执行
    
    public Task(){
        
    }
   public Task(Activity activity,String name){
     super(activity, name);
   }


    public Duration getDuration(){
        return duration;
    }

    public void setDuration(Duration limit){
        this.duration = limit;
    }



    public Participant getPerformer(){
        return performer;
    }


    public void setPerformer(Participant performer){
        this.performer = performer;
    }





    public int getPriority(){
        return priority;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }



	public String toString() {
        return "Task[id='" + getId() + ", name='" + getName() + "']";
    }

	public String getType() {
		return type;
	}

	public void setType(String taskType) {
		this.type = taskType;
	}


	public Form getEditForm() {
		return editForm;
	}


	public void setEditForm(Form editForm) {
		this.editForm = editForm;
	}


	public Form getViewForm() {
		return viewForm;
	}


	public void setViewForm(Form viewForm) {
		this.viewForm = viewForm;
	}


	public Form getListForm() {
		return listForm;
	}


	public void setListForm(Form listForm) {
		this.listForm = listForm;
	}


	public Application getApplication() {
		return application;
	}


	public void setApplication(Application application) {
		this.application = application;
	}


//	public String getStartMode() {
//		return startMode;
//	}
//
//
//	public void setStartMode(String startMode) {
//		this.startMode = startMode;
//	}


	public String getAssignmentStrategy() {
		return assignmentStrategy;
	}


	public void setAssignmentStrategy(String argAssignmentStrategy) {
		this.assignmentStrategy = argAssignmentStrategy;
	}


	public String getDefaultView() {
		return defaultView;
	}


	public void setDefaultView(String defaultView) {
		this.defaultView = defaultView;
	}


	public String getExecution() {
		return execution;
	}


	public void setExecution(String execution) {
		this.execution = execution;
	}

    public SubWorkflowProcess getSubWorkflowProcess() {
        return subWorkflowProcess;
    }

    public void setSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess) {
        this.subWorkflowProcess = subWorkflowProcess;
    }



}