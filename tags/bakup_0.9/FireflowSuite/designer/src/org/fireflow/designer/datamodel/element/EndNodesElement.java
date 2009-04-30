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
import org.fireflow.model.net.EndNode;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class EndNodesElement extends AbstractNode implements IFPDLElement {

    public IFPDLElement createChild(String childType, Map<String, String> extendAtributes) {
        WorkflowProcess process = this.getLookup().lookup(WorkflowProcess.class);
        int num = process.getEndNodes().size();
        List endNodesList = process.getEndNodes();
        String endNodeName = null;

        boolean haveSameName = false;
        do {
            haveSameName = false;
            num++;
            endNodeName = "EndNode" + num;
            for (int i = 0; i < endNodesList.size(); i++) {
                EndNode obj = (EndNode) endNodesList.get(i);
                if (obj.getName().equals(endNodeName)) {
                    haveSameName = true;
                    break;
                }
            }
        } while (haveSameName);

        EndNode endNode = new EndNode(process, endNodeName);
        endNode.setSn(UUID.randomUUID().toString());
        if (extendAtributes != null) {
            endNode.getExtendedAttributes().putAll(extendAtributes);
        }
        process.getEndNodes().add(endNode);

        ((IChildrenOperation) this.getChildren()).rebuildChildren();

        return (IFPDLElement) this.getChildren().findChild(endNodeName);
    }

    public void deleteSelf() {

    }

    public Object getContent() {
        return this.getLookup().lookup(List.class);
    }

    public EndNodesElement(Lookup lookup) {
        super(new EndNodesElementChildren(), lookup);
    }

    public String getElementType() {
        return IFPDLElement.END_NODES;
    }

    @Override
    public String getName() {
        return IFPDLElement.END_NODES;
    }

    @Override
    public String getDisplayName() {
        return "结束节点";
    }

    public Component getEditor() {
        return new JPanel();
    }

    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        return dataObj.getActions();
    }
}
