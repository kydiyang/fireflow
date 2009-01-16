/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.engine.definition;

import java.util.List;
import org.fireflow.engine.RuntimeContext;

/**
 *
 * @author chennieyun
 */
public class DefinitionService4DBMS implements IDefinitionService {
    protected RuntimeContext rtCtx = null;
    
    
    public List<WorkflowDefinition> getAllLatestVersionOfWorkflowDefinitions() {
        return rtCtx.getPersistenceService().findAllLatestVersionOfWorkflowDefinition();  
        
    }

    public WorkflowDefinition getWorkflowDefinitionByProcessIdAndVersion(String id, Integer version) {
        return rtCtx.getPersistenceService().findWorkflowDefinitionByProcessIdAndVersion(id, version);
    }

    public WorkflowDefinition getTheLatestVersionOfWorkflowDefinition(String processId) {
        return rtCtx.getPersistenceService().findLatestVersionOfWorkflowDefinitionByProcessId(processId);
    }

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }
    public RuntimeContext getRuntimeContext(){
        return this.rtCtx;
    } 
}
