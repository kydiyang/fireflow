/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.awt.Component;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.Action;
import javax.swing.JPanel;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.model.DataField;
import org.fireflow.model.WorkflowProcess;

import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class DataFieldsElement extends AbstractNode implements IFPDLElement {

    public IFPDLElement createChild(String childType, Map<String, String> extendAtributes) {
        WorkflowProcess process = this.getLookup().lookup(WorkflowProcess.class);
        int num = process.getDataFields().size();
        List datafieldsList = process.getDataFields();
        String datafieldName = null;
        boolean haveSameName = false;
        do {
            haveSameName = false;
            num++;
            datafieldName = "DataField" + num;
            for (int i = 0; i < datafieldsList.size(); i++) {
                DataField obj = (DataField) datafieldsList.get(i);
                if (obj.getName().equals(datafieldName)) {
                    haveSameName = true;

                    break;
                }
            }
        } while (haveSameName);
        DataField dataField = new DataField(process, datafieldName, DataField.STRING);
        dataField.setSn(UUID.randomUUID().toString());
        if (extendAtributes != null) {
            dataField.getExtendedAttributes().putAll(extendAtributes);
        }
        process.getDataFields().add(dataField);

        ((IChildrenOperation) this.getChildren()).rebuildChildren();

        return (IFPDLElement) this.getChildren().findChild(datafieldName);
    }

    public void deleteSelf() {
    }

    public Object getContent() {
        return this.getLookup().lookup(List.class);
    }

    public DataFieldsElement(Lookup lookup) {
        super(new DataFieldsElementChildren(), lookup);

    }

    public String getElementType() {
        return IFPDLElement.DATAFIELDS;
    }

    @Override
    public String getName() {
        return IFPDLElement.DATAFIELDS;
    }

    @Override
    public String getDisplayName() {
        return "流程变量";
    }

    public Component getEditor() {
        return new JPanel();
    }

    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        if (dataObj!=null)return dataObj.getActions();
        return new Action[]{};
    }
}
