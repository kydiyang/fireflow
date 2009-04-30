/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows PropertiesEditPane component.
 */
public class PropertiesEditPaneAction extends AbstractAction {

    public PropertiesEditPaneAction() {
        super(NbBundle.getMessage(PropertiesEditPaneAction.class, "CTL_PropertiesEditPaneAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(PropertiesEditPaneTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = PropertiesEditPaneTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
