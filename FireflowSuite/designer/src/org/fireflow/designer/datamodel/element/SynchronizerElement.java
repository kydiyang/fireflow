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
import org.fireflow.designer.properties.editor.SynchronizerEditorPane;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Synchronizer;
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
public class SynchronizerElement extends AbstractNode implements IFPDLElement{
    Component editor = null;
    public IFPDLElement createChild(String childType, Map extendAtributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteSelf() {
        Synchronizer synch = (Synchronizer) this.getContent();
        WorkflowProcess workflowProcess = (WorkflowProcess) synch.getParent();
        workflowProcess.getSynchronizers().remove(synch);
        
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        ExplorerManager explorerManager = dataObj.getExplorerManager();
//        ExplorerManager explorerManager = this.getLookup().lookup(ExplorerManager.class);
        WorkflowProcessElement workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];        
        Node transitionsElement = workflowProcessElement.getChildren().findChild(IFPDLElement.TRANSITIONS);
        List transitionList = workflowProcess.getTransitions();
        List<TransitionElement> trans4Delete = new Vector<TransitionElement>();
        for (int i = 0; transitionList != null && i < transitionList.size(); i++) {
            Transition trans = (Transition) transitionList.get(i);
            if (trans.getFromNode().getId().equals(synch.getId()) || trans.getToNode().getId().equals(synch.getId())) {
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
        return this.getLookup().lookup(Synchronizer.class);
    }

    public SynchronizerElement(Lookup lookup){
        super(Children.LEAF,lookup);
    }
    
    @Override 
    public String getName(){
        return this.getLookup().lookup(Synchronizer.class).getName();
    }
    
    @Override
    public String getDisplayName(){
        return this.getLookup().lookup(Synchronizer.class).toString();
    }
    public String getElementType(){
        return IFPDLElement.SYNCHRONIZER;
    }    
    
    @Override
    public String toString(){
        return this.getLookup().lookup(Synchronizer.class).getDisplayName();
    }    
    public Component getEditor(){
        if (editor==null){
            editor = new SynchronizerEditorPane(this,this.getLookup().lookup(Synchronizer.class),
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
