/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.simulation.engine.kenelextensions;

import org.fireflow.designer.simulation.FireflowSimulator;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.ITransitionInstanceEventListener;
import org.fireflow.kenel.event.TransitionInstanceEvent;
import org.fireflow.kenel.impl.TransitionInstance;
import org.fireflow.kenel.plugin.IKenelExtension;

/**
 *
 * @author chennieyun
 */
public class TransitionInstanceExtension implements IKenelExtension,
		ITransitionInstanceEventListener{
//    FireflowSimulator fireflowSimulator = null;
//        public void setFireflowSimulator(FireflowSimulator fireflowSimulator){
//            this.fireflowSimulator = fireflowSimulator;
//        }  
        
    public String getExtentionTargetName() {
       return TransitionInstance.Extension_Target_Name;
    }

    public String getExtentionPointName() {
        return TransitionInstance.Extension_Point_TransitionInstanceEventListener;
    }

    public void onTransitionInstanceEventFired(TransitionInstanceEvent e) throws KenelException {
//        System.out.println("========***TransitionInstanceExtension:: event fired!");

    }

}
