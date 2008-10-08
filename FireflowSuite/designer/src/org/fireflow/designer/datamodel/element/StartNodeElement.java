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
import org.fireflow.designer.properties.editor.StartNodeEditorPane;
import org.fireflow.model.net.StartNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;


/**
 *
 * @author chennieyun
 */
public class StartNodeElement extends AbstractNode  implements IFPDLElement{
    Component editor = null;
    public IFPDLElement createChild(String childType, Map extendAtributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteSelf() {

    }

    public Object getContent() {
        return this.getLookup().lookup(StartNode.class);
    }

    public StartNodeElement(Lookup lookup){
        super(Children.LEAF,lookup);
        
    }
    
    public String getElementType(){
        return IFPDLElement.START_NODE;
    }    
    
    @Override
    public String getName(){
        StartNode startNode = this.getLookup().lookup(StartNode.class);
        return startNode.getName();
    }
    
    @Override
    public String getDisplayName(){
        //TODO 需要区分name和displayname
        StartNode startNode = this.getLookup().lookup(StartNode.class);
        return startNode.toString();
    }

        @Override
    public String toString(){
        StartNode startNode = this.getLookup().lookup(StartNode.class);
        return startNode.getDisplayName();
    }
        
    public Component getEditor(){
        if (editor==null){
            editor = new StartNodeEditorPane(this,this.getLookup().lookup(StartNode.class),
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
