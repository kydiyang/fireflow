/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.simulation.engine.persistence;

import java.util.EventObject;
import org.fireflow.engine.IProcessInstance;

/**
 *
 * @author chennieyun
 */
public class StorageChangedEvent extends EventObject{
    private String processId = null;
    private String processInstanceId = null;
    private IProcessInstance processInstance = null;
    
    public StorageChangedEvent(Object source){
        super(source);
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public IProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(IProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
    
    
}
