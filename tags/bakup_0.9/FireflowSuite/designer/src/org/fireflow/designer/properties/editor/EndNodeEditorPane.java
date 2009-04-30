/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import javax.swing.JTabbedPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.net.EndNode;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class EndNodeEditorPane extends JTabbedPane {

    EndNode endNode = null;
    FPDLDataObject fpdlDataObject = null;

    public EndNodeEditorPane(AbstractNode node,EndNode endNode, FPDLDataObject fpdlDataObj) {
        this.endNode = endNode;
        this.fpdlDataObject = fpdlDataObj;
        init(node);
    }

    private void init(AbstractNode node) {
        EntityEditor entityEdit = new EndNodeEditor(node,endNode,
                fpdlDataObject);

        ExtendAttributeEditor xAttrEdt = new ExtendAttributeEditor(endNode, fpdlDataObject);

        this.add(entityEdit, "EndNode Attribute");
//      this.add("Activity Attribute", activityEdt);
//      this.add("Event",
//               eventEditor);

        this.add(xAttrEdt, "Extend Attribute");

        this.setFont(Utilities.windowsUIFont);
    }
}
