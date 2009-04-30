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
public class CategoryChildren extends Children.Keys<String>{
    public static final String WORKFLOW_ELEMENT = "WORKFLOW_ELEMENT";//工作流元素，如Activity，transition,startNode,等等
    public static final String GRAPH_ELEMENT = "GRAPH_ELEMENT";//纯粹的图形元素，如注释框等等。
    public static final String[] keys = new String[]{WORKFLOW_ELEMENT,GRAPH_ELEMENT};

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
