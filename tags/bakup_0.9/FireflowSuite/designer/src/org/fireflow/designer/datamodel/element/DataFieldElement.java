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
import org.fireflow.designer.properties.editor.DataFieldEditorPane;
import org.fireflow.model.DataField;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class DataFieldElement extends AbstractNode implements IFPDLElement{
    Component editor = null;
    public IFPDLElement createChild(String childType, Map extendAtributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteSelf() {
        Node parent = this.getParentNode();
        parent.getChildren().remove(new Node[]{this});
    }

    public Object getContent() {
        return this.getLookup().lookup(DataField.class);
    }
    public DataFieldElement(Lookup lookup){
        super(Children.LEAF,lookup);
    }
    
    public String getElementType(){
        return IFPDLElement.DATAFIELD;
    }    
    @Override
    public String getName(){
        DataField datafield = this.getLookup().lookup(DataField.class);
        return datafield.getName();
    }
    
    @Override
    public String getDisplayName(){
        DataField datafield = this.getLookup().lookup(DataField.class);
        return datafield.getDisplayName();
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        if (dataObj!=null)return dataObj.getActions();
        return new Action[]{};
    }
    
    @Override
    public String toString(){
        if (this.getDisplayName()!=null && 
                !this.getDisplayName().trim().equals("")){
            return this.getDisplayName();
        }else {
            return this.getName();
        }
    } 
    public Component getEditor(){
        if (editor==null){
            editor = new DataFieldEditorPane(this,this.getLookup().lookup(DataField.class),
                    this.getLookup().lookup(FPDLDataObject.class));
        }
        return editor;
    }    
}
