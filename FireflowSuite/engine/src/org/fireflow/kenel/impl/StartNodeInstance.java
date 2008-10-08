/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package org.fireflow.kenel.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fireflow.kenel.impl.AbstractNodeInstance;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.INodeInstance;
import org.fireflow.kenel.ISynchronizerInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.ITransitionInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.kenel.event.NodeInstanceEvent;
//import org.fireflow.kenel.event.NodeInstanceEventType;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;

/**
 * @author chennieyun
 * 
 */
public class StartNodeInstance extends AbstractNodeInstance implements
		ISynchronizerInstance {
	public static final String Extension_Target_Name = "org.fireflow.kenel.StartNodeInstance";
	public static List<String> Extension_Point_Names = new ArrayList<String>();
	public static final String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";
	static {
		Extension_Point_Names.add(Extension_Point_NodeInstanceEventListener);
	}

	private int volume = 0;// 即节点的容量
	// private int tokenValue = 0;
	private StartNode startNode = null;

	// private boolean alive = false;

	public StartNodeInstance(StartNode startNd) {
		this.startNode = startNd;
		volume = startNode.getLeavingTransitions().size();
		
//		System.out.println(" startnode's volume is "+volume);
	}

	public String getId() {
		return this.startNode.getId();
	}

	public void fire(IToken tk) throws KenelException {
		if (!tk.isAlive())
			return;//

		if (tk.getValue() != volume) {
			throw new KenelException(
					"Error:Illegal StartNodeInstance,the tokenValue MUST be equal to the volume");
		}

		tk.setAlive(false);
		tk.setNodeId(this.getSynchronizer().getId());
		
		//触发token_entered事件
		NodeInstanceEvent event1 = new NodeInstanceEvent(this);
		event1.setToken(tk);
		event1
				.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
		fireNodeLeavingEvent(event1);
		
		//触发fired事件
		NodeInstanceEvent event2 = new NodeInstanceEvent(this);
		event2.setToken(tk);
		event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
		fireNodeEnteredEvent(event2);

		//触发completed事件
		NodeInstanceEvent event3 = new NodeInstanceEvent(this);
		event3.setToken(tk);
		event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
		fireNodeLeavingEvent(event3);
		
		//触发leaving事件
		NodeInstanceEvent event4 = new NodeInstanceEvent(this);
		event4.setToken(tk);
		event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
		fireNodeLeavingEvent(event4);

		for (int i = 0; leavingTransitionInstances != null
				&& i < leavingTransitionInstances.size(); i++) {
			ITransitionInstance transInst = leavingTransitionInstances
					.get(i);

			Token token = new Token();
			token.setAlive(determineTheAliveOfToken(transInst));

			token.setProcessInstance(tk.getProcessInstance());

			transInst.take(token);
		}

	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public String getExtensionTargetName() {
		return Extension_Target_Name;
	}

	public List<String> getExtensionPointNames() {
		return Extension_Point_Names;
	}

	// TODO extesion是单态还是多实例？单态应该效率高一些。
	public void registExtension(IKenelExtension extension)
			throws KenelException {
		// System.out.println("====extension class is
		// "+extension.getClass().getName());
		if (!Extension_Target_Name.equals(extension.getExtentionTargetName())) {
			throw new KenelException(
					"Error:When construct the ActivityInstance,the Extension_Target_Name is mismatching");
		}
		if (Extension_Point_NodeInstanceEventListener.equals(extension
				.getExtentionPointName())) {
			if (extension instanceof INodeInstanceEventListener) {
				this.eventListeners.add((INodeInstanceEventListener) extension);
			} else {
				throw new KenelException(
						"Error:When construct the ActivityInstance,the extension MUST be a instance of INodeInstanceEventListener");
			}
		}
	}

	public String toString() {
		return "StartNodeInstance_4_[" + startNode.getId() + "]";
	}

	public Synchronizer getSynchronizer() {
		return this.startNode;
	}

	private boolean determineTheAliveOfToken(ITransitionInstance transInst) {
		// TODO通过计算transition上的表达式来确定alive的值
		return true;
	}
}
