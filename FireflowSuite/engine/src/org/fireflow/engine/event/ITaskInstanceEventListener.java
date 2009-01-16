/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.engine.event;

import org.fireflow.engine.EngineException;

/**
 *
 * @author chennieyun
 */
public interface ITaskInstanceEventListener {
    public void onTaskInstanceFired(TaskInstanceEvent e)throws EngineException;
}
