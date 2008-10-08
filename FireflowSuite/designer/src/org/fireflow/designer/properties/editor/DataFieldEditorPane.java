/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import javax.swing.JTabbedPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.DataField;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class DataFieldEditorPane extends JTabbedPane {

    DataField dataField = null;
    FPDLDataObject fpdlDataObject = null;
    AbstractNode theNode = null;

    public DataFieldEditorPane(AbstractNode node,DataField df, FPDLDataObject fpdlDataObj) {
        theNode = node;
        dataField = df;
        fpdlDataObject = fpdlDataObj;
        init();
    }

    private void init() {
        EntityEditor entityEdit = new DataFieldEditor(theNode,dataField,
                fpdlDataObject);

        ExtendAttributeEditor xAttrEdt = new ExtendAttributeEditor(dataField, fpdlDataObject);

        this.add(entityEdit, "DataField Attribute");
//      this.add("Activity Attribute", activityEdt);
//      this.add("Event",
//               eventEditor);

        this.add(xAttrEdt, "Extend Attribute");

        this.setFont(Utilities.windowsUIFont);
    }
}
