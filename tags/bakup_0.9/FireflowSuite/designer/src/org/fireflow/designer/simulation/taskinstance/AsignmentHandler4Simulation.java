/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.simulation.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.ou.IAssignable;
import org.fireflow.engine.ou.IAssignmentHandler;

/**
 *
 * @author chennieyun
 */
public class AsignmentHandler4Simulation implements IAssignmentHandler{
    public static final String performer = "Fireflow_Simulator";

    public void assign(IAssignable asignable, String performerName) throws EngineException {
        asignable.asignToActor(performer);
    }

}
