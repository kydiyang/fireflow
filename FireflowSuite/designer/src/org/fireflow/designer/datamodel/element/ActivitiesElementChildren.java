/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.util.List;
import java.util.Vector;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author chennieyun
 */
public class ActivitiesElementChildren extends Children.Keys<String> implements IChildrenOperation {

    public void rebuildChildren() {
        Node parent = this.getNode();
        List activities = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i=0;activities!=null && i<activities.size();i++){
            String sn = ((Activity)activities.get(i)).getSn();
            snList.add(sn);
        }        
        this.setKeys(snList);
//        List activityWrappers = new Vector();
//        for (int i=0;activities!=null && i<activities.size();i++){
//            ActivityWrapper wrapper = new ActivityWrapper((Activity)activities.get(i));
//            activityWrappers.add(wrapper);
//        }        
        
        
//        for (int i=0;activities!=null && i<activities.size();i++){
//            this.refreshKey((Activity)activities.get(i));
//        }
//        this.setKeys(activities);

//        this.refresh();
    }

    @Override
    protected Node[] createNodes(String sn) {
        Node parent = this.getNode();
        FPDLDataObject dataObject = parent.getLookup().lookup(FPDLDataObject.class);
        List activities = parent.getLookup().lookup(List.class);        
        for (int i=0;activities!=null && i<activities.size();i++){
            Activity activity = (Activity)activities.get(i);
            if (!activity.getSn().equals(sn))continue;
                    InstanceContent lookupContent = new InstanceContent();
        AbstractLookup lookup = new AbstractLookup(lookupContent);
        lookupContent.add(activity);
        if (dataObject!=null ) lookupContent.add(dataObject);
        lookupContent.add(parent.getLookup().lookup(WorkflowProcess.class));
        lookupContent.add(parent.getLookup().lookup(ExplorerManager.class));
        lookupContent.add(activity.getTasks());
        
        ActivityElement activityElement = new ActivityElement(lookup);
        return new Node[]{activityElement};
        }
        return null;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Node parent = this.getNode();
        List activities = parent.getLookup().lookup(List.class);
        List snList = new Vector();
        for (int i=0;activities!=null && i<activities.size();i++){
            String sn = ((Activity)activities.get(i)).getSn();
            snList.add(sn);
        }
        setKeys(snList);
    }
}


