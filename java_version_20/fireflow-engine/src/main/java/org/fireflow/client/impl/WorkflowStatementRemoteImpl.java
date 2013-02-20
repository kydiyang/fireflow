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
package org.fireflow.client.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.RepositoryDescriptor;
import org.fireflow.engine.entity.repository.ResourceDescriptor;
import org.fireflow.engine.entity.repository.ResourceDescriptorProperty;
import org.fireflow.engine.entity.repository.ServiceDescriptor;
import org.fireflow.engine.entity.repository.ServiceDescriptorProperty;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.misc.Utils;
import org.fireflow.model.InvalidModelException;
import org.fireflow.server.WorkflowServer;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowStatementRemoteImpl implements WorkflowStatement {
	private WorkflowServer workflowServer = null;
	private String processType = null;
	private WorkflowSessionRemoteImpl remoteSession = null;
	
	public WorkflowStatementRemoteImpl(WorkflowSessionRemoteImpl session,String processType){
		remoteSession = session;
		this.processType = processType;
	}
	public WorkflowServer getWorkflowServer() {
		return workflowServer;
	}

	public void setWorkflowServer(WorkflowServer workflowServer) {
		this.workflowServer = workflowServer;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getProcessType()
	 */
	@Override
	public String getProcessType() {
		return processType;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getCurrentProcessInstance()
	 */
	@Override
	public ProcessInstance getCurrentProcessInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getCurrentActivityInstance()
	 */
	@Override
	public ActivityInstance getCurrentActivityInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getLatestCreatedWorkItems()
	 */
	@Override
	public List<WorkItem> getLatestCreatedWorkItems() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#setDynamicAssignmentHandler(java.lang.String, org.fireflow.engine.invocation.AssignmentHandler)
	 */
	@Override
	public WorkflowStatement setDynamicAssignmentHandler(String activityId,
			AssignmentHandler dynamicAssignmentHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public WorkflowStatement setAttribute(String name, Object attr) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, int, java.lang.String, java.util.Map)
	 */
	@Override
	public ProcessInstance startProcess(String workflowProcessId, int version,
			String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, int, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public ProcessInstance startProcess(String workflowProcessId, int version,
			String subProcessId, String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public ProcessInstance startProcess(String workflowProcessId,
			String subProcessId, String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public ProcessInstance startProcess(String workflowProcessId, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.Object, java.lang.String, java.util.Map)
	 */
	@Override
	public ProcessInstance startProcess(Object process, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String)
	 */
	@Override
	public ProcessInstance createProcessInstance(String workflowProcessId)
			throws InvalidModelException, WorkflowProcessNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.Object)
	 */
	@Override
	public ProcessInstance createProcessInstance(Object process)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String, int)
	 */
	@Override
	public ProcessInstance createProcessInstance(String workflowProcessId,
			int version) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String, int, java.lang.String)
	 */
	@Override
	public ProcessInstance createProcessInstance(String workflowProcessId,
			int version, String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public ProcessInstance createProcessInstance(String workflowProcessId,
			String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#runProcessInstance(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public ProcessInstance runProcessInstance(String processInstanceId,
			String bizId, Map<String, Object> variables) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#abortProcessInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public ProcessInstance abortProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#suspendProcessInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public ProcessInstance suspendProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#restoreProcessInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public ProcessInstance restoreProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#suspendActivityInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public ActivityInstance suspendActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#restoreActivityInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public ActivityInstance restoreActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#abortActivityInstance(java.lang.String, java.lang.String)
	 */
	@Override
	public ActivityInstance abortActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#claimWorkItem(java.lang.String)
	 */
	@Override
	public WorkItem claimWorkItem(String workItemId)
			throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#disclaimWorkItem(java.lang.String, java.util.Map)
	 */
	@Override
	public List<WorkItem> disclaimWorkItem(String workItemId,
			Map<WorkItemProperty, Object> changedProperties)
			throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#withdrawWorkItem(java.lang.String)
	 */
	@Override
	public WorkItem withdrawWorkItem(String workItemId)
			throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItem(java.lang.String, java.util.Map)
	 */
	@Override
	public void completeWorkItem(String workItemId,
			Map<WorkItemProperty, Object> changedProperties)
			throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItem(java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public void completeWorkItem(String workItemId,
			Map<String, AssignmentHandler> assignmentStrategy,
			Map<WorkItemProperty, Object> changedProperties)
			throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItemAndJumpTo(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId,
			Map<WorkItemProperty, Object> changedProperties)
			throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItemAndJumpTo(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
	 */
	@Override
	public void completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId,
			Map<String, AssignmentHandler> assignmentStrategy,
			Map<WorkItemProperty, Object> changedProperties)
			throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#reassignWorkItemTo(java.lang.String, org.fireflow.engine.invocation.AssignmentHandler, java.util.Map)
	 */
	@Override
	public List<WorkItem> reassignWorkItemTo(String workItemId,
			AssignmentHandler reassignHandler,
			Map<WorkItemProperty, Object> changedProperties)
			throws InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadProcessObject(java.lang.Object, boolean, java.util.Map)
	 */
	@Override
	public ProcessDescriptor uploadProcessObject(Object processObject,
			boolean publishState,
			String bizCategory, String ownerDeptId)
			throws InvalidModelException {
		throw new UnsupportedOperationException("远程接口不支持该方法，请先将流程对象转换成Xml或者InputStream，然后调用uploadProcessStream(String processXml...)或者uploadProcessStream(InputStream inStream...)");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadProcessStream(java.io.InputStream, boolean, java.util.Map)
	 */
	@Override
	public ProcessDescriptor uploadProcessStream(InputStream inStream,
			boolean publishState,
			String bizCategory, String ownerDeptId)
			throws InvalidModelException {
		try {
			String processXml = Utils.inputStream2String(inStream, "UTF-8");
			return this.uploadProcessXml(processXml, publishState, bizCategory, ownerDeptId);
		} catch (IOException e) {
			throw new InvalidModelException(e);
		}
	}
	
	public ProcessDescriptor uploadProcessXml(String processXml,boolean publishState, String bizCategory, String ownerDeptId)throws InvalidModelException{
		return this.workflowServer.uploadProcessXml(this.remoteSession.getSessionId(), processXml, publishState, bizCategory, ownerDeptId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadModelDefsInZipFile(java.io.File, boolean)
	 */
	@Override
	public List<RepositoryDescriptor> uploadModelDefsInZipFile(File zipFile,
			boolean publishState) throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadServicesStream(java.io.InputStream, java.lang.Boolean, java.util.Map)
	 */
	@Override
	public List<ServiceDescriptor> uploadServicesStream(InputStream inStream,
			Boolean publishState,
			Map<ServiceDescriptorProperty, Object> metadata)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadResourcesStream(java.io.InputStream, java.lang.Boolean, java.util.Map)
	 */
	@Override
	public List<ResourceDescriptor> uploadResourcesStream(InputStream inStream,
			Boolean publishState,
			Map<ResourceDescriptorProperty, Object> metadata)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getVariableValue(org.fireflow.engine.entity.runtime.Scope, java.lang.String)
	 */
	@Override
	public Object getVariableValue(Scope scope, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#setVariableValue(org.fireflow.engine.entity.runtime.Scope, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setVariableValue(Scope scope, String name, Object value)
			throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#setVariableValue(org.fireflow.engine.entity.runtime.Scope, java.lang.String, java.lang.Object, java.util.Map)
	 */
	@Override
	public void setVariableValue(Scope scope, String name, Object value,
			Map<String, String> headers) throws InvalidOperationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getVariableValues(org.fireflow.engine.entity.runtime.Scope)
	 */
	@Override
	public Map<String, Object> getVariableValues(Scope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getWorkflowProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	@Override
	public Object getWorkflowProcess(ProcessKey key)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

}
