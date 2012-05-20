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
import org.fireflow.pdl.fpdl20.diagram.LaneShape;
import org.fireflow.pdl.fpdl20.diagram.WorkflowNodeShape;
import org.fireflow.pdl.fpdl20.diagram.basic.Rectangle;
import org.fireflow.pdl.fpdl20.diagram.basic.impl.RectangleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class LaneShapeImpl extends AbsDiagramElement implements LaneShape {
	private List<WorkflowNodeShape> workflowNodes = new ArrayList<WorkflowNodeShape>();
	private List<CommentShape> comments = new ArrayList<CommentShape>();
	private List<GroupShape> groups = new ArrayList<GroupShape>();
	
	public LaneShapeImpl(String id){
		this.id = id;
		
		Rectangle plane = new RectangleImpl();
		
		plane.getBounds().setWidth(560);
		plane.getBounds().setHeight(400);
		
		this.shape = plane;
	}
	

	public List<WorkflowNodeShape> getWorkflowNodeShapes() {
		return workflowNodes;
	}


	public void addWorkflowNodeShape(WorkflowNodeShape shape) {
		workflowNodes.add(shape);
	}

	
	public List<CommentShape> getComments(){
		return comments;
	}
	public void addComment(CommentShape commentShape){
		this.comments.add(commentShape);
	}
	

	public List<GroupShape> getGroups(){
		return this.groups;
	}
	public void addGroup(GroupShape groupShape){
		this.groups.add(groupShape);
	}
	public DiagramElement findChild(String diagramElementId){
		if (diagramElementId.equals(this.id)){
			return this;
		}

		
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
		

		for (DiagramElement diagramElm : groups){
			if (diagramElm.getId().equals(diagramElementId)){
				return diagramElm;
			}
			if (diagramElm instanceof GroupShape){
				DiagramElement tmp = diagramElm.findChild(diagramElementId);
				if (tmp!=null){
					return tmp;
				}
			}
		}
		return null;
	}
	public DiagramElement findChildByWorkflowElementId(String workflowElementId){
		
		for (DiagramElement diagramElm : workflowNodes){
			if (diagramElm.getWorkflowElementRef().equals(workflowElementId)){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : groups) {
			DiagramElement tmp = diagramElm.findChild(workflowElementId);
			if (tmp != null) {
				return tmp;
			}
		}
		
		return null;
	}
}
