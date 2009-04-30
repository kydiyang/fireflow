/**
 * Copyright 2007-2008 非也
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

/**
 * 工作流任务
 * @author 非也,nychen2000@163.com
 */
public class Task extends AbstractWFElement {
    /**
     * 任务类型之一 ：TOOL类型，即工具类型任务，该任务自动调用java代码完成特定的工作。
     */
    public static final String TOOL = "TOOL";
    
    /**
     * 任务类型之三：SUBFLOW类型，即子流程任务
     */
    public static final String SUBFLOW = "SUBFLOW";
    
    /**
     * 任务类型之一：FORM类型，最常见的一类任务，代表该任务需要操作员填写相关的表单。
     */
    public static final String FORM = "FORM";
    
    /**
     * 任务类型之四：DUMMY类型，该类型暂时没有用到，保留。
     */
    public static final String DUMMY = "DUMMY";
    /**
     * @deprecated 
     */
    public static final String MANUAL = "MANUAL";
    /**
     * @deprecated 
     */
    public static final String AUTOMATIC = "AUTOMATIC";
    
    /**
     * TOOL类型的任务执行方式之一：异步执行
     */
    public static final String ASYNCHR = "ASYNCHR";
    
    /**
     * TOOL类型的任务执行方式之二：同步执行
     */
    public static final String SYNCHR = "SYNCHR";
    
    /**
     * 可编辑表单
     */
    public static final String EDITFORM = "EDITFORM";
    
    /**
     * 只读表单
     */
    public static final String VIEWFORM = "VIEWFORM";
    
    /**
     * 列表表单
     */
    public static final String LISTFORM = "LISTFORM";
    
    /**
     * 任务分配策略之一：ALL。任务分配给角色中的所有人，只有在所有工单结束结束的情况下，任务实例才结束。
     * 用于实现会签。
     */
    public static final String ALL = "ALL";
    
    /**
     * 任务分配策略之二：ANY。任何一个操作角签收该任务的工单后，其他人的工单被取消掉。
     */
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
    protected String assignmentStrategy = ANY;//workItem分配策略，即任何一个人完成，则taskinstance算完成。
    protected String defaultView = VIEWFORM;//缺省视图是view form
    protected int priority = 1;
    protected String execution = SYNCHR;//缺省情况下是同步执行

    public Task() {
    }

    public Task(Activity activity, String name) {
        super(activity, name);
    }

    /**
     * 返回任务的完成期限
     * @return 以Duration表示的任务的完成期限
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * 设置任务的完成期限
     * @param limit
     */
    public void setDuration(Duration limit) {
        this.duration = limit;
    }

    /**
     * 返回任务的操作员。只有FORM类型的任务才有操作员。
     * @return 操作员
     * @see org.fireflow.model.reference.Participant
     */
    public Participant getPerformer() {
        return performer;
    }

    /**
     * 设置任务的操作员
     * @param performer
     * @see org.fireflow.model.reference.Participant
     */
    public void setPerformer(Participant performer) {
        this.performer = performer;
    }

    /**
     * 返回任务的优先级。（引擎暂时没有用到）
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 设置任务的优先级。（引擎暂时没有用到）
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String toString() {
        return "Task[id='" + getId() + ", name='" + getName() + "']";
    }

    /**
     * 返回任务的类型
     * @return 任务类型，FORM,TOOL,SUBFLOW或者DUMMY
     */
    public String getType() {
        return type;
    }

    /**
     * 设置任务类型，
     * @param taskType 任务类型，FORM,TOOL,SUBFLOW或者DUMMY
     */
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

    /**
     * 返回任务自动执行的Application。只有TOOL类型的任务才有Application。
     * @return
     * @see org.fireflow.model.reference.Application
     */
    public Application getApplication() {
        return application;
    }

    /**
     * 设置任务自动执行的Application
     * @param application
     * @see org.fireflow.model.reference.Application
     */
    public void setApplication(Application application) {
        this.application = application;
    }

    /**
     * 返回任务的分配策略，只对FORM类型的任务有意义。取值为ALL或者ANY
     * @return 任务分配策略值
     */
    public String getAssignmentStrategy() {
        return assignmentStrategy;
    }

    /**
     * 设置任务分配策略，只对FORM类型的任务有意义。取值为ALL或者ANY
     * @param argAssignmentStrategy 任务分配策略值
     */
    public void setAssignmentStrategy(String argAssignmentStrategy) {
        this.assignmentStrategy = argAssignmentStrategy;
    }

    /**
     * 返回任务的缺省表单的类型，取值为EDITFORM、VIEWFORM或者LISTFORM。
     * 只有FORM类型的任务此方法才有意义。该方法的主要作用是方便系统开发，引擎不会用到该方法。
     * @return
     */
    public String getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(String defaultView) {
        this.defaultView = defaultView;
    }

    /**
     * 返回TOOL类型的任务执行策略，取值为ASYNCHR或者SYNCHR
     * @return
     */
    public String getExecution() {
        return execution;
    }

    /**
     * 设置TOOL类型的任务执行策略，取值为ASYNCHR或者SYNCHR
     * @param execution
     */
    public void setExecution(String execution) {
        this.execution = execution;
    }

    /**
     * 返回SUBFLOW类型的任务的子流程信息
     * @return
     * @see org.fireflow.model.reference.SubWorkflowProcess
     */
    public SubWorkflowProcess getSubWorkflowProcess() {
        return subWorkflowProcess;
    }

    public void setSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess) {
        this.subWorkflowProcess = subWorkflowProcess;
    }
}