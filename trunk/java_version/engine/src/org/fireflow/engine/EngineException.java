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
package org.fireflow.engine;

import org.fireflow.kernel.KernelException;
import org.fireflow.model.IWFElement;
import org.fireflow.model.WorkflowProcess;

/**
 * @author chennieyun
 *
 */
public class EngineException extends KernelException {

    public EngineException(IProcessInstance processInstance,IWFElement workflowElement,String errMsg){
        super(processInstance,workflowElement,errMsg);
    }

    public EngineException(String processInstanceId, WorkflowProcess process,
            String workflowElementId, String errMsg) {
        super(null, null, errMsg);
        this.setProcessInstanceId(processInstanceId);
        if (process != null) {
            this.setProcessId(process.getId());
            this.setProcessName(process.getName());
            this.setProcessDisplayName(process.getDisplayName());

            IWFElement workflowElement = process.findWFElementById(workflowElementId);
            if (workflowElement != null) {
                this.setWorkflowElementId(workflowElement.getId());
                this.setWorkflowElementName(workflowElement.getName());
                this.setWorkflowElementDisplayName(workflowElement.getDisplayName());
            }
        }
    }
    /*
    public EngineException() {
    super();
    // TODO Auto-generated constructor stub
    }

    public EngineException(String arg0, Throwable arg1) {
    super(arg0, arg1);
    // TODO Auto-generated constructor stub
    }

    public EngineException(String arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
    }

    public EngineException(Throwable arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
    }
     */
}
