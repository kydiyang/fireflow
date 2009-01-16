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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.ISynchronizerInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.kenel.event.NodeInstanceEvent;
//import org.fireflow.kenel.event.NodeInstanceEventType;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Synchronizer;
/**
 * @author chennieyun
 * 
 */
public class EndNodeInstance extends AbstractNodeInstance implements
		ISynchronizerInstance {
	public transient static final Log log = LogFactory
			.getLog(EndNodeInstance.class);

	public static final String Extension_Target_Name = "org.fireflow.kenel.EndNodeInstance";
	public static List<String> Extension_Point_Names = new ArrayList<String>();
	public static final String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";
	static {
		Extension_Point_Names.add(Extension_Point_NodeInstanceEventListener);
	}

	private int volume = 0;// 即节点的容量
	private int tokenValue = 0;
	private EndNode endNode = null;
	private boolean alive = false;

	public EndNodeInstance() {

	}

	public String getId() {
		return this.endNode.getId();
	}

	public EndNodeInstance(EndNode endNd) {
		this.endNode = endNd;
		this.volume = this.endNode.getEnteringTransitions().size();
		
//		System.out.println("endnode's volume is "+volume);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.ISynchronizerInstance#getTokens()
	 */
	public int getValue() {
		return tokenValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.ISynchronizerInstance#getVolume()
	 */
	public int getVolume() {
		return volume;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.ISynchronizerInstance#setTokens(int)
	 */
	public void setValue(int tokenNum) {
		this.tokenValue = tokenNum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.ISynchronizerInstance#setVolume(int)
	 */
	public void setVolume(int k) {
		this.volume = k;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.IPTNetExecutor#fire(org.fireflow.kenel.RuntimeContext,
	 *      org.fireflow.kenel.ITransitionInstance)
	 */
	public void fire(IToken tk) throws KenelException {
		tk.setNodeId(this.getSynchronizer().getId());
		log.debug("The weight of the Entering TransitionInstance is "
				+ tk.getValue());

		IJoinPoint joinPoint = null;

		synchronized (this) {

                        joinPoint = ((ProcessInstance)tk.getProcessInstance()).createJoinPoint(this,tk);// JoinPoint由谁生成比较好？
			tk.setAlive(false);// tk的生命周期结束了！

			// 触发TokenEntered事件
			NodeInstanceEvent event1 = new NodeInstanceEvent(this);
			event1.setToken(tk);
			event1
					.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
			fireNodeLeavingEvent(event1);

			int value = joinPoint.getValue();

			log.debug("The volume of " + this.toString() + " is " + volume);
			log.debug("The value of " + this.toString() + " is " + value);
			if (value > volume) {
				throw new KenelException("FireFlow引擎内核执行发生异常，同步器实例["
						+ this.toString() + "]的token数量超过其容量");
			}
                        System.out.println("====Inside EndNodeInstance.fire(token):: value is "+value+";volume is "+volume);
			if (value < volume) {// 如果Value小于容量则继续等待其他弧的汇聚。
				return;
			}
		}

		NodeInstanceEvent event2 = new NodeInstanceEvent(this);
		event2.setToken(tk);
		event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
		fireNodeEnteredEvent(event2);

		NodeInstanceEvent event3 = new NodeInstanceEvent(this);
		event3.setToken(tk);
		event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
		fireNodeLeavingEvent(event3);

		NodeInstanceEvent event4 = new NodeInstanceEvent(this);
		event4.setToken(tk);
		event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
		fireNodeLeavingEvent(event4);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.plugin.IPlugable#getExtensionPointNames()
	 */
	public List<String> getExtensionPointNames() {
		return Extension_Point_Names;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.plugin.IPlugable#getExtensionTargetName()
	 */
	public String getExtensionTargetName() {
		return Extension_Target_Name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.kenel.plugin.IPlugable#registExtension(org.fireflow.kenel.plugin.IKenelExtension)
	 */
	public void registExtension(IKenelExtension extension)
			throws KenelException {
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
		return "EndNodeInstance_4_[" + endNode.getId() + "]";
	}

	public Synchronizer getSynchronizer() {
		return this.endNode;
	}
}
