/**
 * Copyright 2004-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.kenel;

import java.util.List;
import org.fireflow.kenel.event.NodeInstanceEvent;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.model.IWFElement;
/**
 * (NodeInstance应该是无状态的，不会随着ProcessInstance的增加而增加。??)
 * @author chennieyun
 *
 */
public interface INodeInstance {
	public String getId();
	public void fire(IToken token)throws KenelException;	
	
	public List<ITransitionInstance> getLeavingTransitionInstances();
	public void addLeavingTransitionInstance(ITransitionInstance transitionInstance);
	
	public List<ITransitionInstance> getEnteringTransitionInstances();
	public void addEnteringTransitionInstance(ITransitionInstance transitionInstance);

	
//	public void fireNodeEnteredEvent(NodeInstanceEvent event)throws KenelException;
//	public void fireNodeLeavingEvent(NodeInstanceEvent event) throws KenelException;
	
	public void setEventListeners(List<INodeInstanceEventListener> listeners);
	
	public List<INodeInstanceEventListener> getEventListeners();
	

}
