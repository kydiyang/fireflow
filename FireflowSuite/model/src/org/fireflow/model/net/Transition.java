/**
 * Copyright 2004-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */

package org.fireflow.model.net;

import org.fireflow.model.AbstractWFElement;
import org.fireflow.model.WorkflowProcess;

public class Transition extends AbstractWFElement {
	Node fromNode = null;
	Node toNode = null;
//	String fromNodeId = null;
//	String toNodeId = null;
	String condition = null;
	
//        String fromSn = null;
//        String toSn = null;
        
	public Transition(WorkflowProcess workflowProcess, String name) {
		super(workflowProcess, name);
	}


	public Transition(WorkflowProcess workflowProcess, String name, Node fromNode,Node toNode) {
		super(workflowProcess, name);
		this.fromNode = fromNode;
		this.toNode = toNode;
	}


//	public String getFromNodeId() {
//		return fromNodeId;
//	}
//
//	public void setFromNodeId(String fromNodeId) {
//		this.fromNodeId = fromNodeId;
//	}
//
//	public String getToNodeId() {
//		return toNodeId;
//	}
//
//	public void setToNodeId(String toNodeId) {
//		this.toNodeId = toNodeId;
//	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

//    public String getFromSn() {
//        return fromSn;
//    }
//
//    public void setFromSn(String fromSn) {
//        this.fromSn = fromSn;
//    }
//
//    public String getToSn() {
//        return toSn;
//    }
//
//    public void setToSn(String toSn) {
//        this.toSn = toSn;
//    }
	
	public Node getFromNode() {
		return fromNode;
	}
	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}
	public Node getToNode() {
		return toNode;
	}
	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}
}