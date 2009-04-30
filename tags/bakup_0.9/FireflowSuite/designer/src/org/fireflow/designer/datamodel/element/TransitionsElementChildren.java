/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.util.List;
import java.util.Vector;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.IWFElement;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Transition;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author chennieyun
 */
public class TransitionsElementChildren extends Children.Keys<String> implements IChildrenOperation {

    public void rebuildChildren() {
        Node parent = this.getNode();
        List transitions = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; transitions != null && i < transitions.size(); i++) {
            String sn = ((IWFElement) transitions.get(i)).getSn();
            snList.add(sn);
        }
        this.setKeys(snList);
    }

    @Override
    protected Node[] createNodes(String arg0) {
        Node parent = this.getNode();
        List transitions = parent.getLookup().lookup(List.class);

        for (int i = 0; transitions != null && i < transitions.size(); i++) {
            Transition trans = (Transition) transitions.get(i);

            if (!trans.getSn().equals(arg0)) {
                continue;
            }
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(trans);
            lookupContent.add(parent.getLookup().lookup(WorkflowProcess.class));
            FPDLDataObject fpdlDataObject = parent.getLookup().lookup(FPDLDataObject.class);
            if (fpdlDataObject!=null) lookupContent.add(fpdlDataObject);
//            lookupContent.add(parent.getLookup().lookup(ExplorerManager.class));

            TransitionElement transElem = new TransitionElement(lookup);
            Node[] nodes = new Node[]{transElem};
            return nodes;
        }
        return null;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Node parent = this.getNode();
        List transitions = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; transitions != null && i < transitions.size(); i++) {
            String sn = ((IWFElement) transitions.get(i)).getSn();
            snList.add(sn);
        }
        this.setKeys(snList);
    }
}
