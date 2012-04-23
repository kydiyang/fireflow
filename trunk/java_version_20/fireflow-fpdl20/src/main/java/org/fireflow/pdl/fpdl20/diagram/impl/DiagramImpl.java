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

import org.fireflow.pdl.fpdl20.diagram.AssociationShape;
import org.fireflow.pdl.fpdl20.diagram.CommentShape;
import org.fireflow.pdl.fpdl20.diagram.Diagram;
import org.fireflow.pdl.fpdl20.diagram.DiagramElement;
import org.fireflow.pdl.fpdl20.diagram.MessageFlowShape;
import org.fireflow.pdl.fpdl20.diagram.PoolShape;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class DiagramImpl implements Diagram {
	private String id = null;
	private String subflowId = null;
	private String direction = Diagram.HORIZONAL;
	

	private List<PoolShape> pools = new ArrayList<PoolShape>();
	private List<MessageFlowShape> messageFlows = new ArrayList<MessageFlowShape>();
	private List<CommentShape> comments = new ArrayList<CommentShape>();
	private List<AssociationShape> associations = new ArrayList<AssociationShape>();

	public DiagramImpl(String id,String subflowId){
		this.id = id;
		this.subflowId = subflowId;
	}
	
	
	public DiagramElement findChild(String id){
		for (DiagramElement diagramElm : associations){
			if (diagramElm.getId().equals(id)){
				return diagramElm;
			}
		}
		for (DiagramElement diagramElm : comments){
			if (diagramElm.getId().equals(id)){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : messageFlows){
			if (diagramElm.getId().equals(id)){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : pools){
			if (diagramElm.getId().equals(id)){
				return diagramElm;
			}
			if (diagramElm instanceof PoolShape){
				DiagramElement tmp = diagramElm.findChild(id);
				if (tmp!=null){
					return tmp;
				}
			}
		}
		return null;
	}
	
	public PoolShape getDefaultPoolShape(){
		for (PoolShape poolShape : pools){
			if (subflowId.equals(poolShape.getWorkflowElementRef())){
				return poolShape;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#getId()
	 */
	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#getSubflowId()
	 */
	public String getSubflowId() {
		return subflowId;
	}
	
	public void setSubflowId(String subflowId){
		this.subflowId = subflowId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#getDirection()
	 */
	public String getDirection() {
		return this.direction;
	}
	
	public void setDirection(String d){
		this.direction = d;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#getPools()
	 */
	public List<PoolShape> getPools() {
		return pools;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#addPool(org.fireflow.pdl.fpdl20.diagram.Pool)
	 */
	public void addPool(PoolShape pool) {
		pools.add(pool);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#gegMessageFlows()
	 */
	public List<MessageFlowShape> getMessageFlows() {
		return messageFlows;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#addMessageFlow(org.fireflow.pdl.fpdl20.diagram.MessageFlow)
	 */
	public void addMessageFlow(MessageFlowShape msgFlow) {
		messageFlows.add(msgFlow);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#getComments()
	 */
	public List<CommentShape> getComments() {

		return comments;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#addComment(org.fireflow.pdl.fpdl20.diagram.Comment)
	 */
	public void addComment(CommentShape cmmt) {
		comments.add(cmmt);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#getAssociations()
	 */
	public List<AssociationShape> getAssociations() {
		return associations;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl20.diagram.Diagram#addAssociation(org.fireflow.pdl.fpdl20.diagram.Association)
	 */
	public void addAssociation(AssociationShape association) {
		associations.add(association);
	}

}
