/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.palette;

import javax.swing.Action;

import org.netbeans.spi.palette.PaletteActions;
//import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class DesignerPaletteActions extends PaletteActions {

    @Override
    public Action[] getImportActions() {
        return null;
    }

    @Override
    public Action[] getCustomPaletteActions() {
        return null;
    }

    @Override
    public Action[] getCustomCategoryActions(Lookup arg0) {
        return null;
    }

    @Override
    public Action[] getCustomItemActions(Lookup arg0) {
        return null;
    }

    @Override
    public Action getPreferredAction(Lookup arg0) {
        return null;//表示双击palette时，系统不需要反应。
    }

}
