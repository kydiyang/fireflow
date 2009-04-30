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
import org.fireflow.model.net.EndNode;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author chennieyun
 */
public class EndNodesElementChildren extends Children.Keys<String> implements IChildrenOperation {

    public void rebuildChildren() {
        Node parent = this.getNode();
        List endNodes = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; endNodes != null && i < endNodes.size(); i++) {
            String sn = ((IWFElement) endNodes.get(i)).getSn();
            snList.add(sn);
        }
        this.setKeys(snList);
    }

    public EndNodesElementChildren() {

    }

    @Override
    protected Node[] createNodes(String arg0) {
        Node parent = this.getNode();
        List endNodes = parent.getLookup().lookup(List.class);
        for (int i = 0; endNodes != null && i < endNodes.size(); i++) {
            EndNode endNode = (EndNode) endNodes.get(i);
            if (!endNode.getSn().equals(arg0)) {
                continue;
            }
            InstanceContent instanceContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(instanceContent);
            instanceContent.add(endNode);
            FPDLDataObject dataObj = parent.getLookup().lookup(FPDLDataObject.class);
            if (dataObj!=null)instanceContent.add(dataObj);
            instanceContent.add(parent.getLookup().lookup(WorkflowProcess.class));
//            instanceContent.add(parent.getLookup().lookup(ExplorerManager.class));
            EndNodeElement endNodeElm = new EndNodeElement(lookup);

            Node[] nodes = new Node[]{endNodeElm};
            return nodes;
        }
        return null;

    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Node parent = this.getNode();
        List endNodes = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; endNodes != null && i < endNodes.size(); i++) {
            String sn = ((IWFElement) endNodes.get(i)).getSn();
            snList.add(sn);
        }
        this.setKeys(snList);
    }
}
