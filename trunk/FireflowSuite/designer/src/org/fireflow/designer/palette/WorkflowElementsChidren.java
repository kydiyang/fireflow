/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.palette;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author chennieyun
 */
public class WorkflowElementsChidren extends Children.Keys<String>{
    public static final String START_NODE = "START_NODE";
    public static final String END_NODE = "END_NODE";
    public static final String ACTIVITY = "ACTIVITY";
    public static final String SYNCHRONIZER = "SYNCHRONIZER";
    public static final String TRANSITION = "TRANSITION";
    public static final String TASK = "TASK";
    public static final String DATA_FIELD = "DATA_FIELD";    
    public static final String[] keys = new String[]{START_NODE,END_NODE,ACTIVITY,SYNCHRONIZER,TRANSITION,TASK,DATA_FIELD};
            
    @Override
    protected Node[] createNodes(String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void addNotify() {
        super.addNotify();

        setKeys(keys);
    }
}
