/**
 * Copyright 2007-2008,Chen Nieyun��
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

import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexView;

/**
 * @author chennieyun
 *
 */
public class MyViewFactory extends DefaultCellViewFactory {
	public MyViewFactory(){
		super();
	}
	protected VertexView createVertexView(Object cell) {
		
		if (cell instanceof DefaultGraphCell) {
			DefaultGraphCell defaultGraphCell = (DefaultGraphCell)cell;
			IFPDLElement userObj = (IFPDLElement)defaultGraphCell.getUserObject();

//			System.out.println("Element Name is "+userObj.getElementName());
			VertexView view = null;
			if (IFPDLElement.START_NODE.equals(userObj.getElementType())){
				view = new StartNodeCellView(cell);
			}else if (IFPDLElement.END_NODE.equals(userObj.getElementType())){
				view = new EndNodeCellView(cell);
			}else if (IFPDLElement.SYNCHRONIZER.equals(userObj.getElementType())){
				view = new SynchronizerCellView(cell);
			}
			else{
				view = new ActivityCellView(cell);
			}
                        
			
			return view;
		}
		return super.createVertexView(cell);
		// if (v instanceof StartWorkFlowProcessCell) {
		// return new StartWorkFlowProcessCellView(v, this, cm);
		// }
		// else if (v instanceof RouteCell){
		// VertexView view = new RouteCellView(v,this,cm);
		// return view;
		// }else if (v instanceof CompleteWorkFlowProcessCell){
		// VertexView view = new CompleteWorkFlowProcessCellView(v,this,cm);
		// return view;
		// }
		// else {
		// VertexView view = new WorkFlowDesignGraphCellView(v, this, cm);
		// return view;
		// }

	}
}
