/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import javax.swing.JTabbedPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.net.StartNode;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class StartNodeEditorPane extends JTabbedPane {

    StartNode startNode = null;
    FPDLDataObject fpdlDataObject = null;

    public StartNodeEditorPane(AbstractNode node,StartNode startNode, FPDLDataObject fpdlDataObj) {
        this.startNode = startNode;
        fpdlDataObject = fpdlDataObj;
        init(node);
    }

    private void init(AbstractNode node) {
        EntityEditor entityEdit = new StartNodeEditor(node,startNode,
                fpdlDataObject);

        ExtendAttributeEditor xAttrEdt = new ExtendAttributeEditor(startNode, fpdlDataObject);

        this.add(entityEdit, "StartNode Attribute");
//      this.add("Activity Attribute", activityEdt);
//      this.add("Event",
//               eventEditor);

        this.add(xAttrEdt, "Extend Attribute");

        this.setFont(Utilities.windowsUIFont);
    }
}
