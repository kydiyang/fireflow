/**
 * Copyright 2007-2008陈乜云(Chen Nieyun)
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation��
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.designer.editpanel.graph;

import java.awt.Color;
import java.beans.PropertyVetoException;
import org.fireflow.designer.datamodel.element.ActivityElement;
import org.jgraph.*;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

public class ProcessDesignPanel extends JGraph implements GraphSelectionListener {
//	private ProcessDesignMediator mediator = null;
    public ProcessDesignPanel(ProcessGraphModel model,GraphLayoutCache layoutCache) {
        super(model,layoutCache);
        init();
    }

 

    // ******************˽�з���******************
    protected void init() {
        this.setHighlightColor(Color.WHITE);
        this.setAutoscrolls(true);
        this.setCloneable(false);
        this.setDisconnectable(false);
//         this.setBendable(true);
        this.setEditable(false);
        
        this.addGraphSelectionListener((GraphSelectionListener) this);
    }

    public void valueChanged(GraphSelectionEvent event) {
        try {
            DefaultGraphCell cell = (DefaultGraphCell) event.getCell();
            AbstractNode workflowElement = (AbstractNode) cell.getUserObject();
            ProcessGraphModel model = (ProcessGraphModel)this.getModel();
            
            model.getExplorerManager().setSelectedNodes(new Node[]{workflowElement});
            
            if (workflowElement instanceof ActivityElement){
                this.setEditable(true);
            }else{
                this.setEditable(false);
            }
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
