/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import javax.swing.JTabbedPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.net.Synchronizer;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class SynchronizerEditorPane extends JTabbedPane {
    Synchronizer synchronizer = null;
    FPDLDataObject fpdlDataObject = null;
    public SynchronizerEditorPane(AbstractNode node,Synchronizer synchronizer, FPDLDataObject fpdlDataObj) {
        this.synchronizer = synchronizer;
        fpdlDataObject  = fpdlDataObj;
        init(node);
    }

    private void init(AbstractNode node) {
        EntityEditor entityEdit = new SynchronizerEditor(node,synchronizer,
                fpdlDataObject);

        ExtendAttributeEditor xAttrEdt = new ExtendAttributeEditor(synchronizer, fpdlDataObject);

        this.add(entityEdit, "Synchronizer Attribute");
//      this.add("Activity Attribute", activityEdt);
//      this.add("Event",
//               eventEditor);

        this.add(xAttrEdt, "Extend Attribute");

        this.setFont(Utilities.windowsUIFont);
    }
}
