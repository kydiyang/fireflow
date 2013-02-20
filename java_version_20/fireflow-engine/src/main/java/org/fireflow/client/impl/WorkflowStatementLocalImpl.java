/**
 * Copyright 2007-2010 非也
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
package org.fireflow.client.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.WorkflowQueryDelegate;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.config.FireflowConfig;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.repository.RepositoryDescriptor;
import org.fireflow.engine.entity.repository.ResourceDescriptor;
import org.fireflow.engine.entity.repository.ResourceDescriptorProperty;
import org.fireflow.engine.entity.repository.ServiceDescriptor;
import org.fireflow.engine.entity.repository.ServiceDescriptorProperty;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.loadstrategy.ProcessLoadStrategy;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.Persister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.ResourcePersister;
import org.fireflow.engine.modules.persistence.ServicePersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.misc.Utils;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.Token;
import org.firesoa.common.schema.NameSpaces;

/**
 * @author 非也
 * @version 2.0
 */
public class WorkflowStatementLocalImpl implements WorkflowStatement,WorkflowQueryDelegate {
	WorkflowSessionLocalImpl session = null;
	Map<String, Object> attributes = new HashMap<String, Object>();
	protected String processType = null;

	public String getProcessType() {
		return processType;
	}

	
	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public Object execute(StatementCallback callback) {
		session.clearAttributes();
		session.setAllAttributes(attributes);
		return callback.doInStatement(session);
	}

	public WorkflowStatementLocalImpl(WorkflowSessionLocalImpl s) {
		this.session = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#setDynamicAssignmentHandler(java
	 * .lang.String, org.fireflow.engine.service.human.AssignmentHandler)
	 */
	public WorkflowStatement setDynamicAssignmentHandler(String activityId,
			AssignmentHandler dynamicAssignmentHandler) {
		this.session.setDynamicAssignmentHandler(activityId,
				dynamicAssignmentHandler);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#getCurrentActivityInstance()
	 */
	public ActivityInstance getCurrentActivityInstance() {
		return this.session.getCurrentActivityInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#getCurrentProcessInstance()
	 */
	public ProcessInstance getCurrentProcessInstance() {
		return this.session.getCurrentProcessInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#getLatestCreatedWorkItems()
	 */
	public List<WorkItem> getLatestCreatedWorkItems() {
		return this.session.getLatestCreatedWorkItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	public WorkflowStatement setAttribute(String name, Object attr) {
		attributes.put(name, attr);
		return this;
	}

	/******************************************************************************/
	/************                                                        **********/
	/************ 与process instance 相关的API **********/
	/************                                                        **********/
	/************                                                        **********/
	/******************************************************************************/
	public ProcessInstance startProcess(String workflowProcessId,int version,String subProcessId,String bizId,Map<String,Object> variables) throws InvalidModelException,
	WorkflowProcessNotFoundException,InvalidOperationException{
		ProcessInstance processInstance = this.createProcessInstance(workflowProcessId, version,subProcessId);
		return this.runProcessInstance(processInstance.getId(), bizId, variables);
	}
	
	public ProcessInstance startProcess(String workflowProcessId,String subProcessId,String bizId,Map<String,Object> variables) throws InvalidModelException,
	WorkflowProcessNotFoundException,InvalidOperationException{
		ProcessInstance processInstance = this.createProcessInstance(workflowProcessId, subProcessId);
		return this.runProcessInstance(processInstance.getId(), bizId, variables);

	}
	
	public ProcessInstance startProcess(String workflowProcessId, int version,
			String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		ProcessInstance processInstance = this.createProcessInstance(workflowProcessId, version);
		return this.runProcessInstance(processInstance.getId(), bizId, variables);
//		RuntimeContext ctx = this.session.getRuntimeContext();
//
//
//		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, processType);
//
//		session.setAttribute(InternalSessionAttributeKeys.BIZ_ID, bizId);
//		session.setAttribute(InternalSessionAttributeKeys.VARIABLES, variables);
//		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
//		KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);			
//		//启动WorkflowProcess实际上是启动该WorkflowProcess的main_flow，
//		kernelManager.startPObject(session, new PObjectKey(workflowProcessId,version,processType,
//				processUtil.getProcessEntryId(workflowProcessId, version, workflowProcessId)));
//		
//		return session.getCurrentProcessInstance();

	}

	public ProcessInstance startProcess(String workflowProcessId, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		// 首先需要根据workflowProcessId找到待启动的流程，查找策略有多种，可能根据流程族来查找，也可能直接找到当前最新版本的流程。
//		RuntimeContext runtimeContext = this.session.getRuntimeContext();
//		ProcessLoadStrategy loadStrategy = runtimeContext.getEngineModule(
//				ProcessLoadStrategy.class, this.getProcessType());
//
//		ProcessKey pk = loadStrategy.findTheProcessKeyForRunning(session,
//				workflowProcessId, this.getProcessType());
//		return this.startProcess(workflowProcessId, pk.getVersion(), bizId,
//				variables);
		ProcessInstance processInstance = this.createProcessInstance(workflowProcessId);
		return this.runProcessInstance(processInstance.getId(), bizId, variables);
	}

	public ProcessInstance startProcess(Object process, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		ProcessInstance processInstance = this.createProcessInstance(process);
		return this.runProcessInstance(processInstance.getId(), bizId, variables);
//		ProcessDescriptor repository = this.uploadProcessObject(process, Boolean.TRUE, null);
//		return this.startProcess(repository.getProcessId(),
//				repository.getVersion(), bizId, variables);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#abortProcessInstance(java.lang.
	 * String, java.lang.String)
	 */
	public ProcessInstance abortProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {	
		RuntimeContext ctx = this.session.getRuntimeContext();
		ProcessInstance processInstance = this.getEntity(processInstanceId,
				ProcessInstance.class);
		if (processInstance == null) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is not found.");
		}
		if (processInstance.getState().getValue() > ProcessInstanceState.DELIMITER
				.getValue()
				|| processInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is dead.");
		}
		
		
		resetSession(this.session);//先清理session
		if (!StringUtils.isEmpty(note)){
			Map<EntityProperty,Object> fieldsValues = new HashMap<EntityProperty,Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES, fieldsValues);
		}
		session.setCurrentProcessInstance(processInstance);	
		
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);		
		Token token = kernelManager.getTokenById(processInstance.getTokenId(), processInstance.getProcessType());

		kernelManager.fireTerminationEvent(session, token, null);
		kernelManager.execute(session);
		
		return processInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#restoreProcessInstance(java.lang
	 * .String, java.lang.String)
	 */
	public ProcessInstance restoreProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {	
		RuntimeContext ctx = this.session.getRuntimeContext();
		ProcessInstance processInstance = this.getEntity(processInstanceId,
				ProcessInstance.class);
		if (processInstance == null) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is not found.");
		}
		if (processInstance.getState().getValue() > ProcessInstanceState.DELIMITER
				.getValue()
				|| processInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is dead.");
		}
		
		this.resetSession(session);
		if (!StringUtils.isEmpty(note)){
			Map<EntityProperty,Object> fieldsValues = new HashMap<EntityProperty,Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES, fieldsValues);
		}
		session.setCurrentProcessInstance(processInstance);

		ProcessInstanceManager procInstMgr = ctx.getEngineModule(
				ProcessInstanceManager.class, this.processType);
		procInstMgr.restoreProcessInstance(session, processInstance);
		return processInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#suspendProcessInstance(java.lang
	 * .String, java.lang.String)
	 */
	public ProcessInstance suspendProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {	
		RuntimeContext ctx = this.session.getRuntimeContext();
		ProcessInstance processInstance = this.getEntity(processInstanceId,
				ProcessInstance.class);
		if (processInstance == null) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is not found.");
		}
		if (processInstance.getState().getValue() > ProcessInstanceState.DELIMITER
				.getValue()
				|| processInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is dead.");
		}
		
		this.resetSession(session);
		if (!StringUtils.isEmpty(note)){
			Map<EntityProperty,Object> fieldsValues = new HashMap<EntityProperty,Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES, fieldsValues);
		}
		
		session.setCurrentProcessInstance(processInstance);

		ProcessInstanceManager procInstMgr = ctx.getEngineModule(
				ProcessInstanceManager.class, this.processType);
		procInstMgr.suspendProcessInstance(session, processInstance);
		return processInstance;
	}

	/******************************************************************************/
	/************                                                        **********/
	/************ ActivityInstance相关的 API **********/
	/************                                                        **********/
	/************                                                        **********/
	/******************************************************************************/

	public ActivityInstance suspendActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ActivityInstance activityInstance = this.getEntity(activityInstanceId,
				ActivityInstance.class);
		if (activityInstance == null) {
			throw new InvalidOperationException("Activity instance for id="
					+ activityInstanceId + " is not found.");
		}
		if (activityInstance.getState().getValue() > ActivityInstanceState.DELIMITER
				.getValue()
				|| activityInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Activiy instance for id="
					+ activityInstanceId + " is dead.");
		}
		this.resetSession(session);
		if (!StringUtils.isEmpty(note)){
			Map<EntityProperty,Object> fieldsValues = new HashMap<EntityProperty,Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES, fieldsValues);
		}
		
		session.setCurrentActivityInstance(activityInstance);

		ActivityInstanceManager actInstMgr = ctx.getEngineModule(
				ActivityInstanceManager.class, this.processType);
		actInstMgr.suspendActivityInstance(session, activityInstance);
		return activityInstance;
	}

	public ActivityInstance restoreActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ActivityInstance activityInstance = this.getEntity(activityInstanceId,
				ActivityInstance.class);
		if (activityInstance == null) {
			throw new InvalidOperationException("Activity instance for id="
					+ activityInstanceId + " is not found.");
		}
		if (activityInstance.getState().getValue() > ActivityInstanceState.DELIMITER
				.getValue()
				|| activityInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Activiy instance for id="
					+ activityInstanceId + " is dead.");
		}
		this.resetSession(session);
		if (!StringUtils.isEmpty(note)){
			Map<EntityProperty,Object> fieldsValues = new HashMap<EntityProperty,Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES, fieldsValues);
		}
		
		session.setCurrentActivityInstance(activityInstance);

		ActivityInstanceManager actInstMgr = ctx.getEngineModule(
				ActivityInstanceManager.class, this.processType);
		actInstMgr.restoreActivityInstance(session, activityInstance);
		return activityInstance;
	}

	public ActivityInstance abortActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ActivityInstance activityInstance = this.getEntity(activityInstanceId,
				ActivityInstance.class);
		if (activityInstance == null) {
			throw new InvalidOperationException("Activity instance for id="
					+ activityInstanceId + " is not found.");
		}
		if (activityInstance.getState().getValue() > ActivityInstanceState.DELIMITER
				.getValue()
				|| activityInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Activiy instance for id="
					+ activityInstanceId + " is dead.");
		}
		
		this.resetSession(session);
		if (!StringUtils.isEmpty(note)){
			Map<EntityProperty,Object> fieldsValues = new HashMap<EntityProperty,Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES, fieldsValues);
		}
		session.setCurrentActivityInstance(activityInstance);

		
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);		
		Token token = kernelManager.getTokenById(activityInstance.getTokenId(), activityInstance.getProcessType());

		kernelManager.fireTerminationEvent(session, token, null);
		kernelManager.execute(session);
		
		return activityInstance;
	}

	/******************************************************************************/
	/************                                                        **********/
	/************ workItem 相关的API **********/
	/************                                                        **********/
	/************                                                        **********/
	/******************************************************************************/

	//该方法不需要，2012-11-10
//	public void updateWorkItem(WorkItem workItem)
//			throws InvalidOperationException {
//		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {
//			throw new InvalidOperationException(
//					"Can not update the work item ,it is not in Running State.");
//		}
//		RuntimeContext rtCtx = this.session.getRuntimeContext();
//
//		PersistenceService persistenceService = rtCtx.getEngineModule(
//				PersistenceService.class, processType);
//		WorkItemPersister workItemPersister = persistenceService
//				.getWorkItemPersister();
//		workItemPersister.saveOrUpdate(workItem);
//	}

	public WorkItem claimWorkItem(String workItemId)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.find(WorkItem.class, workItemId);

		return claimWorkItem(workItem);
	}

	private WorkItem claimWorkItem(WorkItem workItem)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();
		if (workItem == null)
			throw new InvalidOperationException(
					"Claim work item failed. The work item is not found ,maybe ,it is claimed by others.");
		if (workItem.getState().getValue() != WorkItemState.INITIALIZED
				.getValue()) {

			throw new InvalidOperationException(
					"Claim work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}

		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();

		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Claim work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Claim work item failed .The  correspond activity instance is suspended");
		}

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.claimWorkItem(session, workItem);
	}

	public List<WorkItem> disclaimWorkItem(String workItemId,Map<WorkItemProperty,Object> changedProperties)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.find(WorkItem.class, workItemId);

		if (workItem == null)
			throw new InvalidOperationException(
					"Disclaim work item failed. The work item is not found .");

		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Disclaim work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Dislaim work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Disclaim work item failed .The  correspond activity instance is suspended");
		}
		//将审批意见等信息写入workItem
		updateWorkItemChangedProperties(workItem,changedProperties);
		
		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.disclaimWorkItem(session, workItem);
	}

	public WorkItem withdrawWorkItem(String workItemId)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.find(WorkItem.class, workItemId);

		if (workItem == null)
			throw new InvalidOperationException(
					"Withdraw work item failed. The work item is not found.");

		if (workItem.getState().getValue() < WorkItemState.DELIMITER.getValue()) {

			throw new InvalidOperationException(
					"Withdraw work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.withdrawWorkItem(session, workItem);
	}
	
    public void completeWorkItem(String workItemId,Map<String,AssignmentHandler> assignmentStrategy,Map<WorkItemProperty,Object> changedProperties)throws InvalidOperationException{
    	if (assignmentStrategy!=null){
    		this.session.dynamicAssignmentHandlers.putAll(assignmentStrategy);
    	}
    	completeWorkItem(workItemId,changedProperties);
    }

	public void completeWorkItem(String workItemId,Map<WorkItemProperty,Object> changedProperties)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.find(WorkItem.class, workItemId);

		if (workItem == null)
			throw new InvalidOperationException(
					"Complete work item failed. The work item is not found .");

		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Complete work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Complete work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Complete work item failed .The  correspond activity instance is suspended");
		}

		//将审批意见等信息写入workItem
		updateWorkItemChangedProperties(workItem,changedProperties);
		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		workItemMgr.completeWorkItem(session, workItem);
	}
    public void completeWorkItemAndJumpTo(String workItemId ,String targetActivityId,Map<String,AssignmentHandler> assignmentStrategy,Map<WorkItemProperty,Object>  changedProperties) throws InvalidOperationException{
    	if (assignmentStrategy!=null){
    		this.session.dynamicAssignmentHandlers.putAll(assignmentStrategy);
    	}
    	this.completeWorkItemAndJumpTo(workItemId, targetActivityId,changedProperties);
    }
	public void completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId,Map<WorkItemProperty,Object> changedProperties) throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.find(WorkItem.class, workItemId);

		if (workItem == null)
			throw new InvalidOperationException(
					"Complete work item failed. The work item is not found .");

		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Complete work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Complete work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Complete work item failed .The  correspond activity instance is suspended");
		}
		
		//将审批意见等信息写入workItem
		updateWorkItemChangedProperties(workItem,changedProperties);

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		workItemMgr.completeWorkItemAndJumpTo(session, workItem,
				targetActivityId);
	}

	public List<WorkItem> reassignWorkItemTo(String workItemId,AssignmentHandler dynamicAssignmentHandler,Map<WorkItemProperty,Object> changedProperties)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.find(WorkItem.class, workItemId);

		if (dynamicAssignmentHandler==null){
			throw new NullPointerException("DynamicAssignmentHandler can NOT be null");
		}

		if (workItem == null)
			throw new InvalidOperationException(
					"Complete work item failed. The work item is not found .");

		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Complete work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Complete work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Complete work item failed .The  correspond activity instance is suspended");
		}

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		ProcessUtil processUtil = rtCtx.getEngineModule(ProcessUtil.class, this.processType);
		ProcessKey pKey = new ProcessKey(activityInstance.getProcessId(),activityInstance.getVersion(),activityInstance.getProcessType());

		Object theActivity = null;
		ServiceBinding serviceBinding = null;
		ResourceBinding resourceBinding = null;
		try {
			theActivity = processUtil.getActivity(pKey, activityInstance.getSubProcessId(), activityInstance.getNodeId());
		
			serviceBinding = processUtil.getServiceBinding(pKey, activityInstance.getSubProcessId(), activityInstance.getNodeId());
			
			resourceBinding = processUtil.getResourceBinding(pKey, activityInstance.getSubProcessId(), activityInstance.getNodeId());
		} catch (InvalidModelException e) {
			throw new InvalidOperationException(e);
		}
		
		//将审批意见等信息写入workItem
		updateWorkItemChangedProperties(workItem,changedProperties);
		
		return workItemMgr.reassignWorkItemTo(session, workItem,
				dynamicAssignmentHandler, theActivity,
				serviceBinding,resourceBinding);
	}

	/*****************************************************************/
	/*************** 流程定义相关的api ******************************/
	/*****************************************************************/
	private ProcessDescriptor _uploadProcess(Object process,String processXml,
			boolean publishState,
			String bizCategory,
			String ownerDeptId){
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, this.processType);
		
		OUSystemConnector connector = ctx.getEngineModule(OUSystemConnector.class, processType);

		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, processType);
		//通过ProcessUtil已经生成了ProcessId,ProcessName等8个属性，Id,version属性需要自动生成
		ProcessDescriptorImpl descriptor = (ProcessDescriptorImpl)processUtil.generateProcessDescriptor(process);
		descriptor.setPublishState(publishState);//发布状态
		descriptor.setLastEditor(this.session    //最后修改人
				.getCurrentUser().getId()
				+ "["
				+ this.session.getCurrentUser().getName() + "]");
		descriptor.setOwnerDeptId(ownerDeptId);//所属业务部门
		if (connector!=null){
			Department dept = connector.findDepartmentById(ownerDeptId);
			descriptor.setOwnerDeptName(dept==null?"":dept.getName());
		}

		descriptor.setBizType(bizCategory);//业务类别
		
		
		//descriptor.setLastUpdateTime(calendarService.getSysDate());//最后修改时间。（数据库自动生成？）
		/* TODO 如下字段如何传入待研究
		descriptor.setFileName(fileName);//FileName应该存储在流程定义文件的某个字段里面
		descriptor.setApprovedTime(time);
		descriptor.setApprover(approver);
		*/
		
		return processPersister.persistProcessToRepository(processXml, descriptor);
	}
	public ProcessDescriptor uploadProcessXml(String processXml,boolean publishState, String bizCategory, String ownerDeptId)throws InvalidModelException{
		if (processXml==null)throw new InvalidModelException("流程定义文件不能为空。");
		RuntimeContext ctx = this.session.getRuntimeContext();
		ByteArrayInputStream byteIn = null;
		try {
			byteIn = new ByteArrayInputStream(processXml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new InvalidModelException(e);
		}
		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, processType);
		Object processObj = processUtil.deserializeXml2Process(byteIn);
		
		return this._uploadProcess(processObj, processXml, publishState, bizCategory, ownerDeptId);
	}
	public ProcessDescriptor uploadProcessObject(Object process,
			boolean publishState, String bizCategory,String ownerDeptId)
			throws InvalidModelException {
		RuntimeContext ctx = this.session.getRuntimeContext();

		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, processType);
		
		String processXml = processUtil.serializeProcess2Xml(process);

		return this._uploadProcess(process, processXml, publishState,bizCategory,ownerDeptId);
	}

	public ProcessDescriptor uploadProcessStream(InputStream inStream,
			boolean publishState,
			String bizCategory,String ownerDeptId)
			throws InvalidModelException {

		RuntimeContext ctx = this.session.getRuntimeContext();
		
		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, processType);
		//如果存在Process间的交叉引用，此处可能会死锁，报错
		//WorkflowProcess 之间的调用，不考虑使用Import机制，2012-04-30
		Object processObject = processUtil.deserializeXml2Process(inStream);
		
		//读取Xml字符串
		
		String processXml = null;
		try {
			processXml = Utils.inputStream2String(inStream, "UTF-8");
		} catch (IOException e) {
			throw new InvalidModelException(e);
		}
		
		return this._uploadProcess(processObject,processXml, publishState,bizCategory,ownerDeptId);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#uploadResources(java.io.InputStream
	 * , java.util.Map)
	 */
	public List<ResourceDescriptor> uploadResourcesStream(InputStream inStream,
			Boolean publishState, Map<ResourceDescriptorProperty, Object> resourceDescriptorKeyValue)
			throws InvalidModelException {
		Map<ResourceDescriptorProperty, Object> props = new HashMap<ResourceDescriptorProperty, Object>();

		if (resourceDescriptorKeyValue != null) {
			props.putAll(resourceDescriptorKeyValue);
		}
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		ResourcePersister resourcePersister = persistenceService
				.getResourcePersister();
		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, this.processType);
		props.put(ResourceDescriptorProperty.LAST_EDITOR, this.session
				.getCurrentUser().getId()
				+ "["
				+ this.session.getCurrentUser().getName() + "]");
		props.put(ResourceDescriptorProperty.LAST_EDIT_TIME,
				calendarService.getSysDate());
		props.put(ResourceDescriptorProperty.PUBLISH_STATE, publishState);

		try {
			List<ResourceDescriptor> descriptors = resourcePersister.persistResourceFileToRepository(inStream,
					props);
			return descriptors ;
		} catch (DeserializerException e) {
			throw new InvalidModelException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#uploadServices(java.io.InputStream,
	 * java.util.Map)
	 */
	public List<ServiceDescriptor> uploadServicesStream(InputStream inStream,
			Boolean publishState, Map<ServiceDescriptorProperty, Object> serviceDescriptorKeyValue)
			throws InvalidModelException {
		Map<ServiceDescriptorProperty, Object> props = new HashMap<ServiceDescriptorProperty, Object>();

		if (serviceDescriptorKeyValue != null) {
			props.putAll(serviceDescriptorKeyValue);
		}
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		ServicePersister servicePersister = persistenceService
				.getServicePersister();
		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, this.processType);
		props.put(ServiceDescriptorProperty.LAST_EDITOR, this.session
				.getCurrentUser().getId()
				+ "["
				+ this.session.getCurrentUser().getName() + "]");
		props.put(ServiceDescriptorProperty.LAST_EDIT_TIME,
				calendarService.getSysDate());
		props.put(ServiceDescriptorProperty.PUBLISH_STATE, publishState);
		try {
			List<ServiceDescriptor> descriptors = servicePersister.persistServiceFileToRepository(inStream, props);
			return descriptors;
		} catch (DeserializerException e) {
			throw new InvalidModelException(e);
		}
	}
	
	
	public List<RepositoryDescriptor> uploadModelDefsInZipFile(File zipFile,boolean publishState) throws InvalidModelException{
		Map<String,InputStream> modelDefsMap = parseModelDefsFromZipFile(zipFile);
		
		Iterator<Entry<String,InputStream>> entries = modelDefsMap.entrySet().iterator();
		
		List<RepositoryDescriptor> allDescriptors = new ArrayList<RepositoryDescriptor>();
	
		while(entries.hasNext()){
			Entry<String,InputStream> entry = entries.next();
			String fName = entry.getKey();
			InputStream inStream = entry.getValue();
			
			if (fName.toLowerCase().endsWith(".svc.xml")){
				Map<ServiceDescriptorProperty,Object> props = new HashMap<ServiceDescriptorProperty,Object>();
				props.put(ServiceDescriptorProperty.FILE_NAME, fName);

				List<ServiceDescriptor> svcDescs = this.uploadServicesStream(inStream, publishState, props);
				allDescriptors.addAll(svcDescs);
			}
			else if (fName.toLowerCase().endsWith(".rsc.xml")){
				Map<ResourceDescriptorProperty,Object> props = new HashMap<ResourceDescriptorProperty,Object>();
				props.put(ResourceDescriptorProperty.FILE_NAME, fName);

				List<ResourceDescriptor> svcDescs = this.uploadResourcesStream(inStream, publishState, props);
				allDescriptors.addAll(svcDescs);
			}else {
				Map<ProcessDescriptorProperty,Object> props = new HashMap<ProcessDescriptorProperty,Object>();
				props.put(ProcessDescriptorProperty.FILE_NAME, fName);
				
				this.uploadProcessStream(inStream, publishState, null, null);
			}
		}
		return allDescriptors;
	}

	private Map<String,InputStream> parseModelDefsFromZipFile(File processZipFile)
			throws InvalidModelException {
		Map<String,InputStream> modelDefsMap = new HashMap<String,InputStream>();
		ZipFile zf = null;
		try {
			zf = new ZipFile(processZipFile);
		} catch (ZipException e) {
			throw new InvalidModelException(e);
		} catch (IOException e) {
			throw new InvalidModelException(e);
		}

		Enumeration enu = zf.entries();
		while (enu.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) enu.nextElement();
			String fileName = entry.getName();
			try {
				if (!(entry.isDirectory())) {
					InputStream inputStream = zf.getInputStream(entry);
					modelDefsMap.put(fileName, inputStream);
//					ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//					byte[] buf = new byte[1024];
//					int read = 0;
//					do {
//						read = inputStream.read(buf, 0, buf.length);
//						if (read > 0)
//							out.write(buf, 0, read);
//					} while (read >= 0);
//					processDefinitionsContent.put(fileName,
//							out.toString("UTF-8"));
				}
			} catch (IOException e) {
				throw new InvalidModelException(e);
			}
		}

		return modelDefsMap;
	}
	/*****************************************************************/
	/*************** 流程变量相关的api ******************************/
	/*****************************************************************/

	public Object getVariableValue(Scope scope, String name) {
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		VariablePersister variablePersister = persistenceService
				.getVariablePersister();
		
		Variable var = variablePersister.findVariable(scope.getScopeId(), name);
		if (var != null) {
			return var.getPayload();
		} else {
			return null;
		}

	}
	
	public void setVariableValue(Scope scope, String name, Object value)
	throws InvalidOperationException {
		this.setVariableValue(scope, name, value,null);
	}

	public void setVariableValue(Scope scope, String name, Object value,Map<String,String> headers)
			throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();

		// 进行流程变量的类型校验，如果流程定义中有名称为参数“name”的变量定义，则value的类型必须和变量的类型相匹配。
		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class,
				scope.getProcessType());
		
		Property property = null;
		try{
			property = processUtil.getProperty(
					new ProcessKey(scope.getProcessId(), scope.getVersion(), scope
							.getProcessType()), scope.getProcessElementId(), name);
		}catch(InvalidModelException e){
			throw new InvalidOperationException(e);
		}


		if (property != null && property.getDataType() != null && value != null) {
			QName qName = property.getDataType();
			// java类型
			if (qName.getNamespaceURI().endsWith(NameSpaces.JAVA.getUri())) {
				String className = qName.getLocalPart();

				if (value instanceof org.w3c.dom.Document
						|| value instanceof org.dom4j.Document) {
					throw new ClassCastException(
							"Can NOT cast from xml content to " + className);
				}

				try {
					Class clz = Class.forName(className);
					if (!clz.isInstance(value)) {
						throw new ClassCastException("Can NOT cast from "
								+ value.getClass().getName() + " to "
								+ className);
					}
				} catch (ClassNotFoundException e) {
					throw new InvalidOperationException(e);
				}
			} 
			// xml类型
			else {
				if (!(value instanceof org.w3c.dom.Document)
						&& !(value instanceof org.dom4j.Document)) {
					throw new ClassCastException(
							"Can NOT cast from "+value.getClass().getName()+" to " +qName );
				}

			}
		}
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		VariablePersister variablePersister = persistenceService
				.getVariablePersister();
		Variable v = variablePersister.findVariable(scope.getScopeId(), name);
		if (v!=null){
			((AbsVariable)v).setPayload(value);
			if (headers!=null && headers.size()>0){
				v.getHeaders().putAll(headers);
			}
			variablePersister.saveOrUpdate(v);
		}else{
			v = new VariableImpl();
			((AbsVariable)v).setScopeId(scope.getScopeId());
			((AbsVariable)v).setName(name);
			((AbsVariable)v).setProcessElementId(scope.getProcessElementId());
			((AbsVariable)v).setPayload(value);
			if (value!=null){
				if (value instanceof org.w3c.dom.Document){
					if (property != null && property.getDataType() != null ){
						((AbsVariable)v).setDataType(property.getDataType());
					}
					v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.w3c.dom.Document");
				}else if (value instanceof org.dom4j.Document){
					if (property != null && property.getDataType() != null ){
						((AbsVariable)v).setDataType(property.getDataType());
					}
					v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.dom4j.Document");
				}else{
					((AbsVariable)v).setDataType(new QName(NameSpaces.JAVA.getUri(),value.getClass().getName()));
				}
				
			}
			((AbsVariable)v).setProcessId(scope.getProcessId());
			((AbsVariable)v).setVersion(scope.getVersion());
			((AbsVariable)v).setProcessType(scope.getProcessType());
			
			if (headers!=null && headers.size()>0){
				v.getHeaders().putAll(headers);
			}
			variablePersister.saveOrUpdate(v);
		}
		return;
	}

	public Map<String, Object> getVariableValues(Scope scope) {
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		VariablePersister variablePersister = persistenceService
				.getVariablePersister();
		
		List<Variable> vars = variablePersister.findVariables(scope.getScopeId());
		Map<String,Object> varValues = new HashMap<String,Object>();
		if (vars!=null && vars.size()>0){
			for (Variable var : vars){
				varValues.put(var.getName(), var.getPayload());
			}
		}
		return varValues;
	}

	public <T extends WorkflowEntity> List<T> executeQueryList(
			WorkflowQuery<T> q) {
		Class<T> entityClass = q.getEntityClass();
		Persister persister = this.getPersister(entityClass);
		return persister.list(q);
	}

	public <T extends WorkflowEntity> int executeQueryCount(WorkflowQuery<T> q) {
		Class<T> entityClass = q.getEntityClass();
		Persister persister = this.getPersister(entityClass);
		return persister.count(q);
	}

	public <T extends WorkflowEntity> T getEntity(String entityId,
			Class<T> entityClass) {
		Persister persister = this.getPersister(entityClass);
		return persister.find(entityClass, entityId);
	}

	public Object getWorkflowProcess(ProcessKey key)
			throws InvalidModelException {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, this.processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		ProcessRepository repository = processPersister
				.findProcessRepositoryByProcessKey(key);

		return repository.getProcessObject();
	}

	private <T> Persister getPersister(Class<T> entityClass) {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, this.processType);
		Persister persister = null;
		if (entityClass.isAssignableFrom(ActivityInstance.class)) {
			persister = persistenceService.getActivityInstancePersister();
		} else if (entityClass.isAssignableFrom(ProcessInstance.class)) {
			persister = persistenceService.getProcessInstancePersister();
		} else if (entityClass.isAssignableFrom(WorkItem.class)) {
			persister = persistenceService.getWorkItemPersister();
		} else if (entityClass.isAssignableFrom(Token.class)) {
			persister = persistenceService.getTokenPersister();
		} else if (entityClass.isAssignableFrom(Variable.class)) {
			persister = persistenceService.getVariablePersister();
		} else if (entityClass.isAssignableFrom(ProcessRepository.class)
				|| entityClass.isAssignableFrom(ProcessDescriptor.class)) {
			persister = persistenceService.getProcessPersister();
		} else if (entityClass.isAssignableFrom(ScheduleJob.class)) {
			persister = persistenceService.getScheduleJobPersister();
		} else if (entityClass.isAssignableFrom(FireflowConfig.class)){
			persister = persistenceService.getFireflowConfigPersister();
		}
		return persister;
	}



	/**
	 * WorkflowStatement都是客户端调用，在调用执行前，把上一次调用的一些遗留清除
	 * @param session
	 */
	private void resetSession(WorkflowSessionLocalImpl session){
		session.setCurrentActivityInstance(null);
		session.setCurrentProcessInstance(null);
		session.setLatestCreatedWorkItems(null);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String)
	 */
	@Override
	public ProcessInstance createProcessInstance(String workflowProcessId)
	 throws InvalidModelException ,WorkflowProcessNotFoundException{
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		ProcessLoadStrategy loadStrategy = runtimeContext.getEngineModule(
				ProcessLoadStrategy.class, this.getProcessType());

		ProcessKey pk = loadStrategy.findTheProcessKeyForRunning(session,
				workflowProcessId, this.getProcessType());
		if (pk==null){
			throw new WorkflowProcessNotFoundException("流程库中没有processId="+workflowProcessId+"的流程。");
		}
		return this.createProcessInstance(workflowProcessId, pk.getVersion());
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.Object)
	 */
	@Override
	public ProcessInstance createProcessInstance(Object process)throws InvalidModelException {
		ProcessDescriptor repository = this.uploadProcessObject(process, Boolean.TRUE, null, null);
		return _createProcessInstance(process,repository,null);
	}
	
	private ProcessInstance _createProcessInstance(Object workflowProcess,ProcessDescriptor processDescriptor,String subProcessId){
		RuntimeContext ctx = this.session.getRuntimeContext();
		
		ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, processType);
		String processEntryId = null;
		if (subProcessId==null || subProcessId.trim().equals("")){
			processEntryId = processUtil.getProcessEntryId(processDescriptor.getProcessId(), processDescriptor.getVersion(), processDescriptor.getProcessType());
		}else{
			processEntryId = subProcessId;
		}

		ProcessInstanceManager procInstMgr = ctx.getEngineModule(ProcessInstanceManager.class, this.processType);

		ProcessInstance processInstance = procInstMgr.createProcessInstance(session, workflowProcess,processEntryId, processDescriptor, null);
		
		((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(processInstance);
		return processInstance;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String, int)
	 */
	@Override
	public ProcessInstance createProcessInstance(String workflowProcessId,
			int version)throws InvalidModelException,WorkflowProcessNotFoundException  {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, this.getProcessType());
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		ProcessRepository repository = processPersister.findProcessRepositoryByProcessKey(new ProcessKey(workflowProcessId,version,this.getProcessType()));
		if (repository==null){
			throw new WorkflowProcessNotFoundException("流程库中没有ProcessId="+workflowProcessId+",version="+version+"的流程定义文件。");
		}
		Object workflowProcess = repository.getProcessObject();
		return _createProcessInstance(workflowProcess,repository,null);
	}
	
	public ProcessInstance createProcessInstance(String workflowProcessId,int version,String subProcessId) throws InvalidModelException ,WorkflowProcessNotFoundException{
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, this.getProcessType());
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		ProcessRepository repository = processPersister.findProcessRepositoryByProcessKey(new ProcessKey(workflowProcessId,version,this.getProcessType()));
		if (repository==null){
			throw new WorkflowProcessNotFoundException("流程库中没有ProcessId="+workflowProcessId+",version="+version+"的流程定义文件。");
		}
		Object workflowProcess = repository.getProcessObject();
		return _createProcessInstance(workflowProcess,repository,subProcessId);
	}
	public ProcessInstance createProcessInstance(String workflowProcessId,String subProcessId)throws InvalidModelException ,WorkflowProcessNotFoundException{
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		ProcessLoadStrategy loadStrategy = runtimeContext.getEngineModule(
				ProcessLoadStrategy.class, this.getProcessType());

		ProcessKey pk = loadStrategy.findTheProcessKeyForRunning(session,
				workflowProcessId, this.getProcessType());
		if (pk==null){
			throw new WorkflowProcessNotFoundException("流程库中没有processId="+workflowProcessId+"的流程。");
		}
		return this.createProcessInstance(workflowProcessId, pk.getVersion(),subProcessId);
	}
	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#runProcessInstance(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public ProcessInstance runProcessInstance(String processInstanceId,
			String bizId, Map<String, Object> variables) {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		ProcessInstanceManager procInstMgr = runtimeContext.getEngineModule(ProcessInstanceManager.class, this.getProcessType());
		return procInstMgr.runProcessInstance(session, processInstanceId, processType, bizId, variables);
	}
	
	private void updateWorkItemChangedProperties(WorkItem workItem,Map<WorkItemProperty,Object> changedProperties){
		//当前版本只能对审批详细意见，审批意见表Id进行修改，
		//其他字段不允许通过changedProperty进行修改。2012-11-10
		if (changedProperties!=null){			
			if (changedProperties.containsKey(WorkItemProperty.APPROVAL_ID)){
				String approvalId = (String)changedProperties.get(WorkItemProperty.APPROVAL_ID);
				workItem.setApprovalId(approvalId);
			}
			
			if (changedProperties.containsKey(WorkItemProperty.NOTE)){
				String approvalDetail = (String)changedProperties.get(WorkItemProperty.NOTE);
				workItem.setNote(approvalDetail);
			}
			
		}
	}
	

}
