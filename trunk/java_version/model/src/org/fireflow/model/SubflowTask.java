/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.model;

import org.fireflow.model.net.Activity;
import org.fireflow.model.resource.SubWorkflowProcess;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 15, 2009
 */
public class SubflowTask extends Task{

    protected SubWorkflowProcess subWorkflowProcess = null;

    //subflow Task如何会签？

    public SubflowTask(){
        this.setType(SUBFLOW);
    }

    public SubflowTask(IWFElement parent, String name) {
        super(parent, name);
        this.setType(SUBFLOW);
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
