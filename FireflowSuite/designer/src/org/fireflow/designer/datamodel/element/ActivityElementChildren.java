/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.util.List;
import java.util.Vector;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.model.IWFElement;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author chennieyun
 */
public class ActivityElementChildren extends Children.Keys<String> implements IChildrenOperation {

    public void rebuildChildren() {
        Node parent = this.getNode();
        List tasks = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; tasks != null && i < tasks.size(); i++) {
            Object obj = tasks.get(i);
            snList.add(((Task) obj).getSn());
        }
        this.setKeys(snList);
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Node parent = this.getNode();
        List tasks = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; tasks != null && i < tasks.size(); i++) {
            Object obj = tasks.get(i);
            snList.add(((Task) obj).getSn());
        }
        setKeys(snList);
    }

    protected IFPDLElement createNode(IWFElement task) {
        Node parent = this.getNode();
        InstanceContent lookupContent = new InstanceContent();
        AbstractLookup lookup = new AbstractLookup(lookupContent);
        lookupContent.add(task);
        FPDLDataObject dataObj = parent.getLookup().lookup(FPDLDataObject.class);
        if (dataObj!=null)lookupContent.add(dataObj);
        lookupContent.add(parent.getLookup().lookup(WorkflowProcess.class));
        lookupContent.add(parent.getLookup().lookup(ExplorerManager.class));

        TaskElement taskElement = new TaskElement(lookup);
        return taskElement;
    }

    @Override
    protected Node[] createNodes(String arg0) {
        Node parent = this.getNode();
        List tasks = parent.getLookup().lookup(List.class);
        for (int i = 0; tasks != null && i < tasks.size(); i++) {
            Task task = (Task) tasks.get(i);
            if (!task.getSn().equals(arg0)) {
                continue;
            }
            IFPDLElement taskElement = this.createNode(task);
            return new Node[]{(TaskElement) taskElement};
        }
        return null;
    }
}
