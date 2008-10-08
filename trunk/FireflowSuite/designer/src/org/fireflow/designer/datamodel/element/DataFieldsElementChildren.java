/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.util.List;
import java.util.Vector;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.DataField;
import org.fireflow.model.IWFElement;
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
public class DataFieldsElementChildren extends Children.Keys<String> implements IChildrenOperation {

    public void rebuildChildren() {
        Node parent = this.getNode();
        List datafields = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; datafields != null && i < datafields.size(); i++) {
            String sn = ((IWFElement) datafields.get(i)).getSn();
            snList.add(sn);
        }
        this.setKeys(snList);
    }
//    private List datafields = null;
    public DataFieldsElementChildren() {
//        datafields = argDatafields;

    }

    @Override
    protected Node[] createNodes(String arg0) {
        Node parent = this.getNode();
        List datafields = parent.getLookup().lookup(List.class);
        for (int i = 0; datafields != null && i < datafields.size(); i++) {
            DataField df = (DataField) datafields.get(i);
            if (!df.getSn().equals(arg0)) {
                continue;
            }
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(df);
            FPDLDataObject dataObj = parent.getLookup().lookup(FPDLDataObject.class);
            if (dataObj!=null)lookupContent.add(dataObj);
            lookupContent.add(parent.getLookup().lookup(WorkflowProcess.class));
            lookupContent.add(parent.getLookup().lookup(ExplorerManager.class));
            
            DataFieldElement dataFieldElement = new DataFieldElement(lookup);
            return new Node[]{dataFieldElement};
        }
        return null;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Node parent = this.getNode();
        List datafields = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i = 0; datafields != null && i < datafields.size(); i++) {
            String sn = ((IWFElement) datafields.get(i)).getSn();
            snList.add(sn);
        }
        setKeys(snList);
    }
}
