/**
 * Copyright 2004-2008 非也
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
package org.fireflow.kernel;

import java.util.List;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.model.IWFElement;
/**
 * (NodeInstance应该是无状态的，不会随着ProcessInstance的增加而增加。??)
 * @author 非也，nychen2000@163.com
 *
 */
public interface INodeInstance {
	public String getId();
	public void fire(IToken token)throws KernelException;
	
	public List<ITransitionInstance> getLeavingTransitionInstances();
	public void addLeavingTransitionInstance(ITransitionInstance transitionInstance);
	
	public List<ITransitionInstance> getEnteringTransitionInstances();
	public void addEnteringTransitionInstance(ITransitionInstance transitionInstance);

	public List<ILoopInstance> getLeavingLoopInstances();
	public void addLeavingLoopInstance(ILoopInstance loopInstance);

	public List<ILoopInstance> getEnteringLoopInstances();
	public void addEnteringLoopInstance(ILoopInstance loopInstance);
	
//	public void fireNodeEnteredEvent(NodeInstanceEvent event)throws KenelException;
//	public void fireNodeLeavingEvent(NodeInstanceEvent event) throws KenelException;
	
	public void setEventListeners(List<INodeInstanceEventListener> listeners);
	
	public List<INodeInstanceEventListener> getEventListeners();
	

}
