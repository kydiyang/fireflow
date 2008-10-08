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
package org.fireflow.kenel.impl;

import java.util.ArrayList;
import java.util.List;

//import org.fireflow.engine.IRuntimeContext;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.kenel.INetInstance;

import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.kenel.event.IProcessInstanceEventListener;
import org.fireflow.kenel.event.ProcessInstanceEvent;

import org.fireflow.model.WorkflowProcess;


/**
 * @author chennieyun
 *
 */
public class NetInstance implements INetInstance {
	private WorkflowProcess workflowProcess = null;
	private StartNodeInstance startNodeInstance = null;
	private List<EndNodeInstance> endNodeInstances = new ArrayList<EndNodeInstance>();
	
//	private IRuntimeContext runtimeContext = null;
	
	protected List<INodeInstanceEventListener> eventListeners = new ArrayList<INodeInstanceEventListener>();
	
	
	public NetInstance(){

	}
	public String getId(){
		return this.workflowProcess.getId();
	}
	public void run(IProcessInstance processInstance) throws KenelException {
		if (startNodeInstance==null){
			throw new KenelException("Error:NetInstance is illegal ，the startNodeInstance can NOT be NULL ");
		}
				
		Token token = new Token();
		token.setAlive(true);
		token.setProcessInstance(processInstance);
		token.setValue(startNodeInstance.getVolume());
		
		ProcessInstanceEvent event = new ProcessInstanceEvent();
		event.setToken(token);
		this.fireAfterProcessInstanceCompleteEvent(event);//??
		startNodeInstance.fire(token);
	}
	
	/**
	 * 结束流程实例，如果流程状态没有达到终态，则直接返回。
	 * @throws RuntimeException
	 */
	public void complete()throws RuntimeException{
		//1、判断是否所有的EndeNodeInstance都到达终态，如果没有到达，则直接返回。
		
		//2、执行compelete操作，
		
		//3、触发after complete事件
		
		//4、返回主流程
	}
	
	/**
	 * 强行中止流程实例，不管是否达到终态。
	 * @throws RuntimeException
	 */
	public void abort()throws RuntimeException{
		
	}
//	public IRuntimeContext getRtCxt() {
//		return runtimeContext;
//	}
//
//	public void setRtCxt(IRuntimeContext rtCxt) {
//		this.runtimeContext = rtCxt;
//	}

	public WorkflowProcess getWorkflowProcess() {
		return workflowProcess;
	}

	public void setWorkflowProcess(WorkflowProcess workflowProcess) {
		this.workflowProcess = workflowProcess;
	}

	public StartNodeInstance getStartNodeInstance() {
		return startNodeInstance;
	}

	public void setStartNodeInstance(StartNodeInstance startNodeInstance) {
		this.startNodeInstance = startNodeInstance;
	}
	

	
	public List<EndNodeInstance> getEndNodeInstances() {
		return endNodeInstances;
	}

//	public void setEndNodeInstances(List endNodeInstances) {
//		EndNodeInstances = endNodeInstances;
//	}

	protected void fireBeforeProcessInstanceRunEvent(ProcessInstanceEvent event)throws KenelException{
		for (int i=0;i<this.eventListeners.size();i++){
			IProcessInstanceEventListener listener = (IProcessInstanceEventListener)this.eventListeners.get(i);
			listener.onProcessInstanceFired(event);
		}
	}
	protected void fireAfterProcessInstanceCompleteEvent(ProcessInstanceEvent event) throws KenelException{
		for (int i=0;i<this.eventListeners.size();i++){
			IProcessInstanceEventListener listener = (IProcessInstanceEventListener)this.eventListeners.get(i);
			listener.onProcessInstanceFired(event);
		}
	}
//	public void setRuntimeContext(IRuntimeContext rtCtx){
//		runtimeContext = rtCtx;
//	}
//	
//	public IRuntimeContext getRuntimeContext(){
//		return runtimeContext;
//	}
}
