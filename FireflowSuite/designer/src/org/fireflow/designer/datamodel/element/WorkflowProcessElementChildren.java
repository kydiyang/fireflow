/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.model.WorkflowProcess;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author chennieyun
 */
public class WorkflowProcessElementChildren extends Children.Keys<String> implements IChildrenOperation {
//    public static final String DATA_FIELDS = "DATA_FIELDS";
//    public static final String START_NODE = "START_NODE";
//    public static final String ACTIVITIES = "ACTIVITIES";
//    public static final String SYNCHRONIZERS = "SYNCHRONIZERS";
//    public static final String TRANSITIONS = "TRANSITIONS";
//    public static final String END_NODES = "END_NODES";
    public static final String[] keys = new String[]{IFPDLElement.DATAFIELDS,
        IFPDLElement.START_NODE, IFPDLElement.ACTIVITIES, IFPDLElement.SYNCHRONIZERS, IFPDLElement.TRANSITIONS, IFPDLElement.END_NODES
    };

    public WorkflowProcessElementChildren() {

    }

    public void rebuildChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        setKeys(keys);
    }

    @Override
    protected Node[] createNodes(String arg0) {
        WorkflowProcess workflowProcess = null;
        FPDLDataObject dataObject = null;

        Node parent = this.getNode();
        workflowProcess = parent.getLookup().lookup(WorkflowProcess.class);
        dataObject = parent.getLookup().lookup(FPDLDataObject.class);
//        ExplorerManager explorerManager = parent.getLookup().lookup(ExplorerManager.class);

        if (arg0.equals(IFPDLElement.START_NODE)) {
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            if (workflowProcess.getStartNode()!=null){
                lookupContent.add(workflowProcess.getStartNode());
            }
            if (dataObject != null) {
                lookupContent.add(dataObject);
            }
            lookupContent.add(workflowProcess);
//            lookupContent.add(explorerManager);
            Node[] nodes = new Node[1];
            nodes[0] = new StartNodeElement(lookup);
            return nodes;
        }
        if (arg0.equals(IFPDLElement.DATAFIELDS)) {
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(workflowProcess.getDataFields());
            if (dataObject != null) {
                lookupContent.add(dataObject);
            }
            lookupContent.add(workflowProcess);
//            lookupContent.add(explorerManager);

            Node[] nodes = new Node[1];
            nodes[0] = new DataFieldsElement(lookup);
            return nodes;
        }

        if (arg0.equals(IFPDLElement.ACTIVITIES)) {
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(workflowProcess.getActivities());
            if (dataObject != null) {
                lookupContent.add(dataObject);
            }
            lookupContent.add(workflowProcess);
//            lookupContent.add(explorerManager);

            Node[] nodes = new Node[1];
            nodes[0] = new ActivitiesElement(lookup);
            return nodes;
        }

        if (arg0.equals(IFPDLElement.SYNCHRONIZERS)) {
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(workflowProcess.getSynchronizers());
            if (dataObject != null) {
                lookupContent.add(dataObject);
            }
            lookupContent.add(workflowProcess);
//            lookupContent.add(explorerManager);
            Node[] nodes = new Node[1];
            nodes[0] = new SynchronizersElement(lookup);
            return nodes;
        }

        if (arg0.equals(IFPDLElement.TRANSITIONS)) {
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(workflowProcess.getTransitions());
            if (dataObject != null) {
                lookupContent.add(dataObject);
            }
            lookupContent.add(workflowProcess);
//            lookupContent.add(explorerManager);
            Node[] nodes = new Node[1];
            nodes[0] = new TransitionsElement(lookup);
            return nodes;
        }

        if (arg0.equals(IFPDLElement.END_NODES)) {
            InstanceContent lookupContent = new InstanceContent();
            AbstractLookup lookup = new AbstractLookup(lookupContent);
            lookupContent.add(workflowProcess.getEndNodes());
            if (dataObject != null) {
                lookupContent.add(dataObject);
            }
            lookupContent.add(workflowProcess);
//            lookupContent.add(explorerManager);
            Node[] nodes = new Node[1];
            nodes[0] = new EndNodesElement(lookup);
            return nodes;
        }

        return null;
    }
}
