/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.fireflow.designer.simulation.FireflowSimulator;
import org.fireflow.designer.simulation.engine.persistence.IStorageChangeListener;
import org.fireflow.designer.simulation.engine.persistence.MemoryPersistenceService;
import org.fireflow.designer.simulation.output.SimulationDataViewerTopComponent;
import org.openide.windows.TopComponent;

//import org.fireflow.designer.simulation.output.

/**
 *
 * @author chennieyun
 */
public class StartWorkflowProcessAction extends AbstractAction {
    FireflowSimulator fireflowSimulator = null;

    public StartWorkflowProcessAction(FireflowSimulator simulator, String title, Icon image) {
        super(title, image);
        fireflowSimulator = simulator;
    }

    public void actionPerformed(ActionEvent e) {
        TopComponent win = SimulationDataViewerTopComponent.findInstance();
        win.open();
        win.requestActive();
        
        fireflowSimulator.run();
    }
}
