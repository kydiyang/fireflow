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
package org.fireflow.pdl.fpdl20.enginemodules.workitem;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.WorkItemImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.engine.modules.workitem.event.WorkItemEventTrigger;
import org.fireflow.engine.modules.workitem.impl.AbsWorkItemManager;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.service.human.HumanService;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class WorkItemManagerFpdl20Impl extends AbsWorkItemManager {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.service.form.WorkItemManager#createWorkItem(org.fireflow
	 * .engine.WorkflowSession,
	 * org.fireflow.engine.entity.runtime.ProcessInstance,
	 * org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.String)
	 */
	public WorkItem createWorkItem(WorkflowSession currentSession,
			ProcessInstance processInstance, ActivityInstance activityInstance,
			User user, Object theActivity,
			Map<WorkItemProperty, Object> workitemPropertyValues)
			throws EngineException {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) currentSession)
		.getRuntimeContext();
		
		Activity activity = (Activity) theActivity;
		ServiceBinding serviceBinding = activity.getServiceBinding();
		HumanService humanService = null;
		if (serviceBinding != null) {
			ProcessUtil processUtil = ctx.getEngineModule(ProcessUtil.class, activityInstance.getProcessType());
			humanService = (HumanService) processUtil.getServiceDef(activityInstance, activity, serviceBinding.getServiceId());
		}

		ResourceBinding resourceBinding = activity.getResourceBinding();


		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, activityInstance.getProcessType());
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, activityInstance.getProcessType());
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItemImpl wi = new WorkItemImpl();
		wi.setWorkItemName(activityInstance.getDisplayName());
		// 计算工作项摘要表达式
		if (humanService != null) {
			wi.setSubject(parseDescription(humanService.getWorkItemSubject(),
					currentSession, ctx, processInstance, activityInstance));

			wi.setFormUrl(humanService.getFormUrl());
		}

		wi.setActivityInstance(activityInstance);
		wi.setCreatedTime(calendarService.getSysDate());
		wi.setResponsiblePersonId(user.getId());
		wi.setResponsiblePersonName(user.getName());
		wi.setResponsiblePersonDeptId(user.getDeptId());
		wi.setResponsiblePersonDeptName(user.getDeptName());
		wi.setState(WorkItemState.INITIALIZED);
		wi.setOwnerId(user.getId());
		wi.setOwnerName(user.getName());
		wi.setOwnerDeptId(user.getDeptId());
		wi.setOwnerDeptName(user.getDeptName());
		if (resourceBinding != null
				&& resourceBinding.getAssignmentStrategy() != null) {
			wi.setAssignmentStrategy(resourceBinding.getAssignmentStrategy());
		}

		wi.setBizId(activityInstance.getBizId());
		wi.setExpiredTime(activityInstance.getExpiredTime());
		// wi.setWorkflowEngineLocation(workflowEngineLocation);//TODO 待补充
		wi.setProcInstCreatorName(processInstance.getCreatorName());
		// wi.setOriginalSystemName(originalSystemName);//TODO 待补充
		if (workitemPropertyValues != null) {
			if (workitemPropertyValues.get(WorkItemProperty.STATE) != null) {
				wi.setState((WorkItemState) workitemPropertyValues
						.get(WorkItemProperty.STATE));
			}
			if (workitemPropertyValues
					.get(WorkItemProperty.ASSIGNMENT_STRATEGY) != null) {
				wi.setAssignmentStrategy((WorkItemAssignmentStrategy) workitemPropertyValues
						.get(WorkItemProperty.ASSIGNMENT_STRATEGY));
			}

			wi.setReassignType((String) workitemPropertyValues
					.get(WorkItemProperty.REASSIGN_TYPE));
			wi.setParentWorkItemId((String) workitemPropertyValues
					.get(WorkItemProperty.PARENT_WORKITEM_ID));
		}

		workItemPersister.saveOrUpdate(wi);

		// 发布事件
		this.fireWorkItemEvent(currentSession, wi, theActivity,
				WorkItemEventTrigger.ON_WORKITEM_CREATED);
		return wi;
	}

	private String parseDescription(Expression descExpression,
			WorkflowSession session, RuntimeContext runtimeContext,
			ProcessInstance processInstance, ActivityInstance activityInstance) {
		if (descExpression == null
				|| StringUtils.isEmpty(descExpression.getLanguage())) {
			return "";
		}
		Map<String, Object> contextVars = ScriptEngineHelper
				.fulfillScriptContext(session, runtimeContext, processInstance,
						activityInstance);

		Object obj = null;

		obj = ScriptEngineHelper.evaluateExpression(runtimeContext,
				descExpression, contextVars);

		return obj == null ? null : obj.toString();

	}
}
