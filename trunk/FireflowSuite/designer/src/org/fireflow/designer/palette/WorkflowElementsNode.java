/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.palette;

import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class WorkflowElementsNode extends AbstractNode{
    public static final String NAME = "Workflow_Elements_Palette";
    public WorkflowElementsNode(){
        super(new WorkflowElementsChidren()); 
        this.setName(NAME);
        this.setDisplayName("流程元素");
    }
}
