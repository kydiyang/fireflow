/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.properties.editor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import javax.swing.JTabbedPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.Task;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class TaskEditorPane extends JTabbedPane {

    Task task = null;
    FPDLDataObject fpdlDataObject = null;

    public TaskEditorPane(AbstractNode node,Task task, FPDLDataObject fpdlDataObj) {
        this.task = task;
        fpdlDataObject = fpdlDataObj;
        init(node);
    }

    private void init(AbstractNode node) {

        ExtendAttributeEditor xAttrEdt = new ExtendAttributeEditor(task, fpdlDataObject);

        TaskEditor taskEditor = new TaskEditor(node,task,fpdlDataObject);
        
        
        
        this.add( taskEditor,"Task Attribute");

        this.add( xAttrEdt,"Extend Attribute");

        this.setFont(Utilities.windowsUIFont);
    }
}
