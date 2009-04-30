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
import org.fireflow.model.net.Node;
import org.fireflow.model.net.Transition;

import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class TransitionsElement extends AbstractNode implements IFPDLElement {

    public static final String FROM_NODE_ID = "FROM_NODE_ID";
    public static final String TO_NODE_ID = "TO_NODE_ID";
    public static final String FROM_SN = "FROM_SN";
    public static final String TO_SN = "TO_SN";

    public IFPDLElement createChild(String childType, Map extendAtributes) {
        if (extendAtributes == null) {
            return null;
        }
        String fromNodeID = (String) extendAtributes.get(FROM_NODE_ID);
        extendAtributes.remove(FROM_NODE_ID);
        String toNodeID = (String) extendAtributes.get(TO_NODE_ID);
        extendAtributes.remove(TO_NODE_ID);
        String fromSn = (String)extendAtributes.get(FROM_SN);
        extendAtributes.remove(FROM_SN);
        String toSn = (String)extendAtributes.get(TO_SN);
        extendAtributes.remove(TO_SN);
        if (fromNodeID == null || toNodeID == null) {
            return null;
        }
        return createTranstion(fromNodeID,fromSn, toNodeID,toSn, extendAtributes);
    }

    public IFPDLElement createTranstion(String fromNodeID,String fromSn, String toNodeID,String toSn, Map<String, String> extendAttributes) {
        WorkflowProcess process = this.getLookup().lookup(WorkflowProcess.class);
        int num = process.getTransitions().size();
        List transitionsList = process.getTransitions();
        String transitionName = null;

        boolean haveSameName = false;
        do {
            haveSameName = false;
            num++;
            transitionName = "transition" + num;
            for (int i = 0; i < transitionsList.size(); i++) {
                Transition obj = (Transition) transitionsList.get(i);
                if (obj.getName().equals(transitionName)) {
                    haveSameName = true;

                    break;
                }
            }
        } while (haveSameName);

        Transition transition = new Transition(process, transitionName);
        transition.setSn(UUID.randomUUID().toString());
        transition.setFromNode((Node)process.findWFElementById(fromNodeID));
        transition.setToNode((Node)process.findWFElementById(toNodeID));
//        transition.setFromNodeId(fromNodeID);
//        transition.setToNodeId(toNodeID);
//        transition.setFromSn(fromSn);
//        transition.setToSn(toSn);
        
        if (extendAttributes != null) {
            transition.getExtendedAttributes().putAll(extendAttributes);
        }

        process.getTransitions().add(transition);

        ((IChildrenOperation) this.getChildren()).rebuildChildren();

        return (IFPDLElement) this.getChildren().findChild(transitionName);
    }


    
    public void deleteSelf() {

    }

    public Object getContent() {
        return this.getLookup().lookup(List.class);
    }

    public TransitionsElement(Lookup lookup) {
        super(new TransitionsElementChildren(), lookup);
    }

    public String getElementType() {
        return IFPDLElement.TRANSITIONS;
    }

    @Override
    public String getName() {
        return IFPDLElement.TRANSITIONS;
    }

    @Override
    public String getDisplayName() {
        return "转移";
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
