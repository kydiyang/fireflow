/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.model;

import org.fireflow.model.net.Activity;
import org.fireflow.model.resource.Form;
import org.fireflow.model.resource.Participant;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
public class FormTask extends Task{
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


    /**
     * @deprecated
     */
    public static final String MANUAL = "MANUAL";
    /**
     * @deprecated
     */
    public static final String AUTOMATIC = "AUTOMATIC";


    
    //----------Form Task 的属性
    protected Participant performer;//引用participant
    protected String assignmentStrategy = ANY;//workItem分配策略，即任何一个人完成，则taskinstance算完成。


    protected String defaultView = VIEWFORM;//缺省视图是view form

    protected Form editForm = null;
    protected Form viewForm = null;
    protected Form listForm = null;


//    protected String startMode = MANUAL ;//启动模式，启动模式没有意义，application和subflow自动启动，Form一般情况下签收时启动，如果需要自动启动则在assignable接口中实现。

    public FormTask(){
        this.setType(FORM);
    }

    public FormTask(IWFElement parent, String name) {
        super(parent, name);
        this.setType(FORM);
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



}
