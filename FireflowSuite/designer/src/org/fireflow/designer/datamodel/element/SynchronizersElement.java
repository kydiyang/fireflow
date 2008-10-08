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
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Synchronizer;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class SynchronizersElement extends AbstractNode implements IFPDLElement {

    public IFPDLElement createChild(String childType, Map<String, String> extendAtributes) {
        WorkflowProcess process = this.getLookup().lookup(WorkflowProcess.class);
        int num = process.getActivities().size();
        List synchronizerList = process.getSynchronizers();
        String synchronizerName = null;

        boolean haveSameName = false;
        do {
            haveSameName = false;
            num++;
            synchronizerName = "Synchronizer" + num;
            for (int i = 0; i < synchronizerList.size(); i++) {
                Synchronizer obj = (Synchronizer) synchronizerList.get(i);
                if (obj.getName().equals(synchronizerName)) {
                    haveSameName = true;

                    break;
                }
            }
        } while (haveSameName);
        Synchronizer synchronizer = new Synchronizer(process, synchronizerName);
        synchronizer.setSn(UUID.randomUUID().toString());
        if (extendAtributes != null) {
            synchronizer.getExtendedAttributes().putAll(extendAtributes);
        }

        process.getSynchronizers().add(synchronizer);

        ((IChildrenOperation) this.getChildren()).rebuildChildren();

        return (IFPDLElement) this.getChildren().findChild(synchronizerName);
    }

    public void deleteSelf() {

    }

    public Object getContent() {
        return this.getLookup().lookup(List.class);
    }

    public SynchronizersElement(Lookup lookup) {
        super(new SynchronizersElementChildren(), lookup);
    }

    public String getElementType() {
        return IFPDLElement.SYNCHRONIZERS;
    }

    @Override
    public String getName() {
        return IFPDLElement.SYNCHRONIZERS;
    }

    @Override
    public String getDisplayName() {
        return "同步器";
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
