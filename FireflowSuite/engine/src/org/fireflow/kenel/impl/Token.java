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

import java.util.List;
import java.util.Set;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.kenel.INodeInstance;
//import org.fireflow.kenel.IRuntimeContext;
import org.fireflow.kenel.INetInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.ITransitionInstance;

/**
 * @author chennieyun
 *
 */
public class Token implements IToken {
	private INodeInstance currentNodeInstance;
	private INetInstance currentProcessInstance;
//	private ITransitionInstance transitionInstance;
	private Boolean alive = null;
	private Integer value = null;
	private String nodeId = null;
	private String id = null;
	private IProcessInstance processInstance = null;
	/* (non-Javadoc)
	 * @see org.fireflow.kenel.IToken#getCurrentNodeInstance()
	 */
//	public INodeInstance getCurrentNodeInstance() {
//		return currentNodeInstance;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.kenel.IToken#getCurrentProcessInstance()
	 */
//	public INetInstance getNetInstance() {
//		return currentProcessInstance;
//	}
//	
//	public void setNetInstance(INetInstance procInst){
//		this.currentProcessInstance = procInst; 
//	}
			

	/* (non-Javadoc)
	 * @see org.fireflow.kenel.IToken#getRuntimeContext()
	 */
//	public IRuntimeContext getRuntimeContext() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.kenel.IToken#getValue()
	 */
	public Integer getValue() {
//		if (this.transitionInstance!=null){
//			return this.transitionInstance.getWeight();
//		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.kenel.IToken#setCurrentNodeInstance(org.fireflow.kenel.INodeInstance)
	 */
//	public void setCurrentNodeInstance(INodeInstance currentNodeInstance) {
//	
//		this.currentNodeInstance = currentNodeInstance;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.kenel.IToken#setRuntimeContext(org.fireflow.kenel.IRuntimeContext)
	 */
//	public void setRuntimeContext(IRuntimeContext rtCtx) {
//		
//	}

//	public void setTransitionInstance(ITransitionInstance transInst){
//		transitionInstance = transInst;
//	}
//	public ITransitionInstance getTransitionInstance(){
//		return transitionInstance;
//	}

	public Boolean isAlive() {
		return alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	
	public IProcessInstance getProcessInstance(){
		return processInstance;
	}
	
	public void setProcessInstance(IProcessInstance inst){
		processInstance = inst;
	}
	
	public void setAppointedTransitionNames(Set<String> appointedTransitionNames){
		
	}
	
	public Set<String> getAppointedTransitionNames(){
		return null;
	}
	
	public void setValue(Integer v){
		value = v;
	}
	
	public String getNodeId(){
		return nodeId;
	}
	public void setNodeId(String nodeId){
		this.nodeId = nodeId;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
}
