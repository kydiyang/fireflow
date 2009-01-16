/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.engine;

/**
 *
 * @author chennieyun
 */
public interface IWorkflowSessionAware {
    public IWorkflowSession getCurrentWorkflowSession();
    public void setCurrentWorkflowSession(IWorkflowSession session);
}
