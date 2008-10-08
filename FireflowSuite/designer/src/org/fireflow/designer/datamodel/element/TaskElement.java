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
import org.fireflow.designer.properties.editor.TaskEditorPane;
import org.fireflow.model.Task;
import org.fireflow.model.net.Activity;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class TaskElement extends AbstractNode implements IFPDLElement{
    Component editor = null;
    
    public TaskElement(Lookup lookup){
        super(Children.LEAF,lookup);
    }
    public String getElementType() {
                Task task = this.getLookup().lookup(Task.class);
        if (task.getType().equals(Task.FORM)){
            return IFPDLElement.FORM_TASK; 
        }else if (task.getType().equals(Task.SUBFLOW)){
            return IFPDLElement.SUBFLOW_TASK;
        }else if (task.getType().equals(Task.TOOL)){
            return IFPDLElement.TOOL_TASK;
        }else{
            return IFPDLElement.FORM_TASK; 
        }
        
    }

    public IFPDLElement createChild(String childType, Map extendAtributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteSelf() {
        Task task = (Task)this.getContent();
        Activity activity = (Activity)task.getParent();
        activity.getTasks().remove(task);
        
        Node parent = this.getParentNode();
        ((IChildrenOperation)parent.getChildren()).rebuildChildren();
    }

    public Object getContent() {
        return this.getLookup().lookup(Task.class);
    }
    @Override
    public String getName(){
        Task task = this.getLookup().lookup(Task.class);
        return task.getName();
    }
    
    @Override
    public String getDisplayName(){
        Task task = this.getLookup().lookup(Task.class);
        if (task.getDisplayName()!=null && 
                !task.getDisplayName().trim().equals("")){
            return task.getDisplayName();
        }else {
            return task.getName();
        }     
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
            editor = new TaskEditorPane(this,this.getLookup().lookup(Task.class),
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
