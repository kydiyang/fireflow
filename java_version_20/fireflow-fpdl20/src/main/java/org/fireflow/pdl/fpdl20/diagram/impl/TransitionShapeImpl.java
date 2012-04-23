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

import org.fireflow.pdl.fpdl20.diagram.TransitionShape;
import org.fireflow.pdl.fpdl20.diagram.WorkflowNodeShape;
import org.fireflow.pdl.fpdl20.diagram.basic.Label;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.LabelImpl;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.LineImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class TransitionShapeImpl extends AbsDiagramElement implements
		TransitionShape {
	WorkflowNodeShape fromWorkflowNode = null;
	WorkflowNodeShape toWorkflowNode = null;
	
	public TransitionShapeImpl(String id){
		this.id = id;
		
		LineImpl line = new LineImpl();
		Label lb = new LabelImpl();
		line.setLabel(lb);
		
		this.shape = line;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.TransitionShape#getFromWorkflowNodeShape()
	 */
	public WorkflowNodeShape getFromWorkflowNodeShape() {
		return fromWorkflowNode;
	}
	
	public void setFromWorkflowNodeShape(WorkflowNodeShape fromNode){
		this.fromWorkflowNode = fromNode;
	}
	
	public void setToWorkflowNodeShape(WorkflowNodeShape toNode){
		this.toWorkflowNode = toNode;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.TransitionShape#getToWorkflowNodeShape()
	 */
	public WorkflowNodeShape getToWorkflowNodeShape() {
		return toWorkflowNode;
	}

}
