/**
 * Copyright 2004-2008 非也
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

/**
 * 流程转移
 * @author 非也,nychen2000@163.com
 */
public class Transition extends AbstractWFElement {

    Node fromNode = null;
    Node toNode = null;
//	String fromNodeId = null;
//	String toNodeId = null;
    String condition = null;
//        String fromSn = null;
//        String toSn = null;
    public Transition() {
    }

    public Transition(WorkflowProcess workflowProcess, String name) {
        super(workflowProcess, name);
    }

    public Transition(WorkflowProcess workflowProcess, String name, Node fromNode, Node toNode) {
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
    /**
     * 返回转移条件，转移条件是一个EL表达式
     * @return
     */
    public String getCondition() {
        return condition;
    }

    /**
     * 设置转移条件
     * @param condition
     */
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
    /**
     * 返回转移的源节点
     * @return
     */
    public Node getFromNode() {
        return fromNode;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    /**
     * 返回转移的目标节点
     * @return
     */
    public Node getToNode() {
        return toNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }
}