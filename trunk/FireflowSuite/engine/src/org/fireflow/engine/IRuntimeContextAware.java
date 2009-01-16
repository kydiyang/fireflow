/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.engine;

/**
 *
 * @author chennieyun
 */
public interface IRuntimeContextAware {
    public void setRuntimeContext(RuntimeContext ctx);
    
    public RuntimeContext getRuntimeContext();
}
