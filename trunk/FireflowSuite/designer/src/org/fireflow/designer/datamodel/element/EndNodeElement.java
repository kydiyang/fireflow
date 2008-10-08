/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.datamodel.element;

import java.awt.Component;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.Action;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.properties.editor.EndNodeEditorPane;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Transition;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class EndNodeElement extends AbstractNode implements IFPDLElement{
    Component editor = null;
    public IFPDLElement createChild(String childType, Map extendAtributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteSelf() {
        EndNode endNode = (EndNode) this.getContent();
        WorkflowProcess workflowProcess = (WorkflowProcess) endNode.getParent();
        workflowProcess.getEndNodes().remove(endNode);


        ExplorerManager explorerManager = this.getLookup().lookup(ExplorerManager.class);
        WorkflowProcessElement workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];        
        Node transitionsElement = workflowProcessElement.getChildren().findChild(IFPDLElement.TRANSITIONS);
        List transitionList = workflowProcess.getTransitions();
        List<TransitionElement> trans4Delete = new Vector<TransitionElement>();
        for (int i = 0; transitionList != null && i < transitionList.size(); i++) {
            Transition trans = (Transition) transitionList.get(i);
            if (trans.getFromNode().getId().equals(endNode.getId()) || trans.getToNode().getId().equals(endNode.getId())) {
                TransitionElement transitionElement = (TransitionElement) transitionsElement.getChildren().findChild(trans.getName());
                if (transitionElement != null) {
                    trans4Delete.add(transitionElement);
                }
            }
        }
        for (int i = 0; i < trans4Delete.size(); i++) {
            TransitionElement transitionElement = trans4Delete.get(i);
            transitionElement.deleteSelf();
        }        
        
        Node parent = this.getParentNode();
        ((IChildrenOperation) parent.getChildren()).rebuildChildren();        
    }

    public Object getContent() {
        return this.getLookup().lookup(EndNode.class);
    }

    public EndNodeElement(Lookup lookup) {
        super(Children.LEAF,lookup);
    }
    
    public String getElementType(){
        return IFPDLElement.END_NODE;
    }
    
    @Override
    public String getName(){
        EndNode endNode = this.getLookup().lookup(EndNode.class);
        return endNode.getName();
    }
    
    @Override
    public String getDisplayName(){
        EndNode endNode = this.getLookup().lookup(EndNode.class);
        return endNode.toString();
    }
    
    @Override
    public String toString(){
        EndNode endNode = this.getLookup().lookup(EndNode.class);
        return endNode.getDisplayName();
    }   
    public Component getEditor(){
        if (editor==null){
            editor = new EndNodeEditorPane(this, this.getLookup().lookup(EndNode.class),
                     this.getLookup().lookup(FPDLDataObject.class));
        }
        return editor;
    }    
    
    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        return dataObj.getActions();
    }        
}
