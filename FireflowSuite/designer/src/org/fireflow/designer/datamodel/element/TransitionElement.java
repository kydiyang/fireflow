/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.awt.Component;
import java.util.Map;
import javax.swing.Action;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.properties.editor.TransitionEditorPane;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class TransitionElement extends AbstractNode implements IFPDLElement {

    Component editor = null;

    public IFPDLElement createChild(String childType, Map extendAtributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteSelf() {
        Transition trans = (Transition) this.getContent();
        WorkflowProcess workflowProcess = (WorkflowProcess) trans.getParent();
        workflowProcess.getTransitions().remove(trans);
        
        org.fireflow.model.net.Node from = trans.getFromNode();
        if (from instanceof Activity){
            ((Activity)from).setLeavingTransition(null);
        }else if ((from instanceof Synchronizer)|| (from instanceof StartNode)){
            ((Synchronizer)from).getLeavingTransitions().remove(trans);
        }
        
        org.fireflow.model.net.Node to = trans.getToNode();
        if (to instanceof Activity){
             ((Activity)to).setEnteringTransition(null);
        }else if ((to instanceof Synchronizer)|| (to instanceof EndNode)){
            ((Synchronizer)to).getEnteringTransitions().remove(trans);
        }

        Node parent = this.getParentNode();
        ((IChildrenOperation) parent.getChildren()).rebuildChildren();
    }

    public Object getContent() {
        return this.getLookup().lookup(Transition.class);
    }

    public TransitionElement(Lookup lookup) {
        super(Children.LEAF, lookup);
    }

    public String getElementType() {
        return IFPDLElement.TRANSITION;
    }

    @Override
    public String getName() {
        Transition transition = this.getLookup().lookup(Transition.class);
        return transition.getName();
    }

    @Override
    public String getDisplayName() {
        Transition transition = this.getLookup().lookup(Transition.class);
        return transition.toString();
    }

    @Override
    public String toString() {
        Transition transition = this.getLookup().lookup(Transition.class);
        return transition.getDisplayName();
    }

    public Component getEditor() {
        if (editor == null) {
            editor = new TransitionEditorPane(this,this.getLookup().lookup(Transition.class),
                    this.getLookup().lookup(FPDLDataObject.class));
        }
        return editor;
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        if (dataObj!=null)return dataObj.getActions();
        return new Action[]{};
    }        
}
