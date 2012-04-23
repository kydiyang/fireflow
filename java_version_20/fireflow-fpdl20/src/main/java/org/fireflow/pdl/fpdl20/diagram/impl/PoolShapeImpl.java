/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.pdl.fpdl20.diagram.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.pdl.fpdl20.diagram.DiagramElement;
import org.fireflow.pdl.fpdl20.diagram.GroupShape;
import org.fireflow.pdl.fpdl20.diagram.LaneShape;
import org.fireflow.pdl.fpdl20.diagram.PoolShape;
import org.fireflow.pdl.fpdl20.diagram.TransitionShape;
import org.fireflow.pdl.fpdl20.diagram.WorkflowNodeShape;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.PlaneImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class PoolShapeImpl extends AbsDiagramElement implements PoolShape {
	private boolean isAbstract = true;
	private List<LaneShape> lanes = new ArrayList<LaneShape>();
	private List<TransitionShape> transitions = new ArrayList<TransitionShape>();
	private List<WorkflowNodeShape> workflowNodes = new ArrayList<WorkflowNodeShape>();
	
	public PoolShapeImpl(String id ){
		this.id = id ;
		
		PlaneImpl plane = new PlaneImpl();
		
		plane.getBounds().setWidth(560);
		plane.getBounds().setHeight(400);
		
		this.shape = plane;
	}
	
	public DiagramElement findChild(String id){
		
		for (DiagramElement diagramElm : lanes){
			if (diagramElm.getId().equals(id)){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : transitions){
			if (diagramElm.getId().equals(id)){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : workflowNodes){
			if (diagramElm.getId().equals(id)){
				return diagramElm;
			}
			if (diagramElm instanceof GroupShape){
				DiagramElement tmp = diagramElm.findChild(id);
				if (tmp!=null){
					return tmp;
				}
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Pool#isAbstract()
	 */
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean b){
		this.isAbstract = b;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Pool#getLanes()
	 */
	public List<LaneShape> getLanes() {
		return lanes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Pool#addLane(org.fireflow.pdl.fpdl20.diagram.Lane)
	 */
	public void addLane(LaneShape ln) {
		this.lanes .add(ln);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Pool#getTransitions()
	 */
	public List<TransitionShape> getTransitions() {
		return transitions;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Pool#addTransition(org.fireflow.pdl.fpdl20.diagram.TransitionShape)
	 */
	public void addTransition(TransitionShape transitionShape) {
		transitions.add(transitionShape);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.PoolShape#getWorkflowNodeShapes()
	 */
	public List<WorkflowNodeShape> getWorkflowNodeShapes() {		
		return workflowNodes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.PoolShape#addWorkflowNodeShape(org.fireflow.pdl.fpdl20.diagram.WorkflowNodeShape)
	 */
	public void addWorkflowNodeShape(WorkflowNodeShape shape) {
		workflowNodes.add(shape);		
	}
}
