/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.simulation.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.fireflow.designer.simulation.FireflowSimulator;

/**
 *
 * @author chennieyun
 */
public class StopAction extends AbstractAction {
    FireflowSimulator fireflowSimulator = null;

    public StopAction(FireflowSimulator simulator, String title, Icon image) {
        super(title, image);
        fireflowSimulator = simulator;
    }
    public void actionPerformed(ActionEvent e) {
        fireflowSimulator.initAll();
    }

}
