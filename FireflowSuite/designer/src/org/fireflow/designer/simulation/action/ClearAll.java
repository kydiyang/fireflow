/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.fireflow.designer.simulation.FireflowSimulator;
import org.fireflow.designer.simulation.engine.persistence.MemoryPersistenceService;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;

/**
 *
 * @author chennieyun
 */
public class ClearAll extends AbstractAction {

    FireflowSimulator fireflowSimulator = null;

    public ClearAll(FireflowSimulator simulator, String title, Icon image) {
        super(title, image);
        fireflowSimulator = simulator;
    }

    public void actionPerformed(ActionEvent e) {
        fireflowSimulator.initAll();
        IPersistenceService persistenceService = RuntimeContext.getInstance().getPersistenceService();
        ((MemoryPersistenceService) persistenceService).clearAll();

    }
}
