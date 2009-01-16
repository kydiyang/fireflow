/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.simulation.engine.definition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.FPDLSerializerException;
import org.fireflow.model.io.JAXP_FPDL_Serializer;

/**
 *
 * @author chennieyun
 */
public class WorkflowDefinition4Simulation extends WorkflowDefinition{
    public void setWorkflowProcess(WorkflowProcess process) throws  EngineException {
        try {
            this.workflowProcess = process;
            this.processId = workflowProcess.getId();
            this.name = workflowProcess.getName();
            this.displayName = workflowProcess.getDisplayName();
            this.description = workflowProcess.getDescription();

            JAXP_FPDL_Serializer ser = new JAXP_FPDL_Serializer();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ser.serialize(workflowProcess, out);

            this.processContent = out.toString("utf-8");
        } catch (FPDLSerializerException ex) {
            Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
            throw new EngineException(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(WorkflowDefinition.class.getName()).log(Level.SEVERE, null, ex);
            throw new EngineException(ex.toString());
        }
    }
}
