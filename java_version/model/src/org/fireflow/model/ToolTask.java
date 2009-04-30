/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.model;

import org.fireflow.model.net.Activity;
import org.fireflow.model.resource.Application;

/**
 * Tool类型的Task
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
public class ToolTask extends Task{
    /**
     * TOOL类型的任务执行方式之一：异步执行
     * @deprecated
     */
    public static final String ASYNCHR = "ASYNCHR";

    /**
     * TOOL类型的任务执行方式之二：同步执行
     * @deprecated
     */
    public static final String SYNCHR = "SYNCHR";
    
    protected Application application = null;
    /**
     * @deprecated
     */
    protected String execution = SYNCHR;//缺省情况下是同步执行

    public ToolTask(){
        this.setType(TOOL);
    }

    public ToolTask(IWFElement parent, String name) {
        super(parent, name);
        this.setType(TOOL);
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
     * 返回TOOL类型的任务执行策略，取值为ASYNCHR或者SYNCHR<br/>
     * 意义不大，已经被废除
     * @deprecated
     * @return
     */
    public String getExecution() {
        return execution;
    }

    /**
     * 设置TOOL类型的任务执行策略，取值为ASYNCHR或者SYNCHR<br/>
     * 意义不大，已经被废除
     * @deprecated
     * @param execution
     */
    public void setExecution(String execution) {
        this.execution = execution;
    }
}
