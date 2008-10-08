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
import org.fireflow.model.net.Synchronizer;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author chennieyun
 */
public class SynchronizersElementChildren extends Children.Keys<String> implements IChildrenOperation {

    public void rebuildChildren() {
        Node parent = this.getNode();
        List synchronizers = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; synchronizers != null && i < synchronizers.size(); i++) {
            String sn = ((IWFElement) synchronizers.get(i)).getSn();
            snList.add(sn);
        }
        this.setKeys(snList);
    }

    @Override
    protected Node[] createNodes(String arg0) {
        Node parent = this.getNode();
        List synchronizers = parent.getLookup().lookup(List.class);

        for (int i = 0; synchronizers != null && i < synchronizers.size(); i++) {
            Synchronizer synchronizer = (Synchronizer) synchronizers.get(i);
            if (!synchronizer.getSn().equals(arg0)) {
                continue;
            }
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(synchronizer);
            FPDLDataObject dataObject = parent.getLookup().lookup(FPDLDataObject.class);
            if (dataObject!=null)lookupContent.add(dataObject);
            lookupContent.add(parent.getLookup().lookup(WorkflowProcess.class));
            lookupContent.add(parent.getLookup().lookup(ExplorerManager.class));
            SynchronizerElement synchElement = new SynchronizerElement(lookup);
            Node[] nodes = new Node[]{synchElement};
            return nodes;
        }
        return null;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Node parent = this.getNode();
        List synchronizers = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; synchronizers != null && i < synchronizers.size(); i++) {
            String sn = ((IWFElement) synchronizers.get(i)).getSn();
            snList.add(sn);
        }
        this.setKeys(snList);

    }
}
