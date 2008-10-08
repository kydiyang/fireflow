/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.kenel.event;

import org.fireflow.kenel.KenelException;

/**
 *
 * @author chennieyun
 */
public interface ITransitionInstanceEventListener {

    public void onTransitionInstanceEventFired(TransitionInstanceEvent e) throws KenelException;
}
