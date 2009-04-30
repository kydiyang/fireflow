/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.model.resource;

/**
 *
 * @author chennieyun
 */
public class SubWorkflowProcess extends AbstractResource {

    private String workflowProcessId = null;

    public SubWorkflowProcess(String name) {
        this.setName(name);
    }

    public String getWorkflowProcessId() {
        return workflowProcessId;
    }

    public void setWorkflowProcessId(String workflowProcessId) {
        this.workflowProcessId = workflowProcessId;
    }
}
