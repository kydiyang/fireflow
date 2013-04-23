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

import org.fireflow.pdl.fpdl20.diagram.CommentShape;
import org.fireflow.pdl.fpdl20.diagram.DiagramElement;
import org.fireflow.pdl.fpdl20.diagram.GroupShape;
import org.fireflow.pdl.fpdl20.diagram.ProcessNodeShape;
import org.fireflow.pdl.fpdl20.diagram.figure.Rectangle;
import org.fireflow.pdl.fpdl20.diagram.figure.impl.RectangleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class GroupShapeImpl extends AbsNodeShapeImpl implements GroupShape {
//	private List<TransitionShape> transitions = new ArrayList<TransitionShape>();
	private List<ProcessNodeShape> workflowNodes = new ArrayList<ProcessNodeShape>();
	private List<CommentShape> comments = new ArrayList<CommentShape>();
//	private List<AssociationShape> associations = new ArrayList<AssociationShape>();
	
	private boolean expand = true;
	
	public GroupShapeImpl(String id){
		this.id = id;
		Rectangle plane = new RectangleImpl();
		
		plane.getBounds().setWidth(200);
		plane.getBounds().setHeight(100);
		this.figure= plane;
	}
	
	public DiagramElement findChild(String diagramElementId){
		
		
		for (DiagramElement diagramElm : workflowNodes){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : comments){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
		}

		return null;
	}
	
	public DiagramElement findChildByWorkflowElementId(String workflowElementId){

		
		for (DiagramElement diagramElm : workflowNodes){
			if (diagramElm.getWorkflowElementRef()==null){
				continue;
			}
			if (workflowElementId.equals(diagramElm.getWorkflowElementRef().getId())){
				return diagramElm;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.GroupShape#isExpand()
	 */
	public boolean isExpand() {
		return expand;
	}
	
	public void setExpand(Boolean expand){
		this.expand = expand;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.GroupShape#getWorkflowNodeShapes()
	 */
	public List<ProcessNodeShape> getProcessNodeShapes() {
		return workflowNodes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.GroupShape#addWorkflowNodeShape(org.fireflow.pdl.fpdl20.diagram.ProcessNodeShape)
	 */
	public void addProcessNodeShape(ProcessNodeShape shape) {
		workflowNodes.add(shape);
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.pdl.fpdl20.diagram.GroupShape#getTransitions()
//	 */
//	public List<TransitionShape> getTransitions() {
//		return transitions;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.pdl.fpdl20.diagram.GroupShape#addTransition(org.fireflow.pdl.fpdl20.diagram.TransitionShape)
//	 */
//	public void addTransition(TransitionShape transitionShape) {
//		transitions.add(transitionShape);
//
//	}
	
	public List<CommentShape> getComments(){
		return comments;
	}
	public void addComment(CommentShape commentShape){
		this.comments.add(commentShape);
	}
//	
//	public List<AssociationShape> getAssociations(){
//		return this.associations;
//	}
//	public void addAssociation(AssociationShape association){
//		this.associations.add(association);
//	}

}
