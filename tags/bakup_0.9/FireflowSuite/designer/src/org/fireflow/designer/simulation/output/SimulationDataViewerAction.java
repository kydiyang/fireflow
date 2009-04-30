/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.output;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows SimulationDataViewer component.
 */
public class SimulationDataViewerAction extends AbstractAction {

    public SimulationDataViewerAction() {
        super(NbBundle.getMessage(SimulationDataViewerAction.class, "CTL_SimulationDataViewerAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(SimulationDataViewerTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = SimulationDataViewerTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
