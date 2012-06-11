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
package org.fireflow.pdl.fpdl20.enginemodules.instancemanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.InternalSessionAttributeKeys;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEvent;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventListener;
import org.fireflow.engine.modules.instancemanager.impl.AbsProcessInstanceManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.SubProcess;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceManagerFpdl20Impl extends AbsProcessInstanceManager {
	private Log log = LogFactory.getLog(ProcessInstanceManagerFpdl20Impl.class);
	
	/**
	 * 启动WorkflowProcess实际上是启动main subflow,该方法供WorkflowStatement调用
	 */
//	public ProcessInstance startProcess(WorkflowSession session,String workflowProcessId, int version,String processType,
//			String bizId, Map<String, Object> variables)
//			throws InvalidModelException,
//			WorkflowProcessNotFoundException, InvalidOperationException{
//		assert (session instanceof WorkflowSessionLocalImpl);
//		
//		session.setAttribute(InternalSessionAttributeKeys.BIZ_ID, bizId);
//		session.setAttribute(InternalSessionAttributeKeys.VARIABLES, variables);
//		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
//		KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);			
//		//启动WorkflowProcess实际上是启动该WorkflowProcess的main_flow，
//		kernelManager.startPObject(session, new PObjectKey(workflowProcessId,version,processType,workflowProcessId+"."+WorkflowProcess.MAIN_PROCESS_NAME));
//		
//		return session.getCurrentProcessInstance();
//	}
	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#createProcessInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, java.lang.String, java.util.Map, org.fireflow.engine.entity.repository.ProcessDescriptor, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public ProcessInstance createProcessInstance(WorkflowSession session,
			Object workflowElement, String bizId,ProcessDescriptor descriptor,
			ActivityInstance parentActivityInstance) {
		SubProcess subflow = (SubProcess)workflowElement;
		WorkflowProcess process = (WorkflowProcess)subflow.getParent();
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		CalendarService calendarService = context.getDefaultEngineModule(CalendarService.class);
		User u = sessionLocal.getCurrentUser();
		
		ProcessInstanceImpl processInstance = new ProcessInstanceImpl();
		processInstance.setProcessId(descriptor.getProcessId());
		processInstance.setVersion(descriptor.getVersion());
		processInstance.setProcessType(descriptor.getProcessType());
		processInstance.setSubflowId(subflow.getId());
		processInstance.setBizId(bizId);
		processInstance.setProcessName(process.getName());
		String displayName = process.getDisplayName();
		processInstance.setProcessDisplayName(StringUtils.isEmpty(displayName)?process.getName():displayName);
		if (subflow.getParent()!=null){
			processInstance.setBizCategory(process.getBizCategory());
		}
		
		processInstance.setSubflowName(subflow.getName());
		processInstance.setSubflowDisplayName(StringUtils.isEmpty(subflow.getDisplayName())?subflow.getName():subflow.getDisplayName());
		
		processInstance.setState(ProcessInstanceState.INITIALIZED);

		Date now = calendarService.getSysDate();
		processInstance.setCreatedTime(now);
		processInstance.setCreatorId(u.getId());
		processInstance.setCreatorName(u.getName());
		processInstance.setCreatorDeptId(u.getDeptId());
		processInstance.setCreatorDeptName(u.getDeptName());
		
		if (parentActivityInstance!=null){
			processInstance.setParentActivityInstanceId(parentActivityInstance.getId());
			processInstance.setParentProcessInstanceId(parentActivityInstance.getProcessInstanceId());
			processInstance.setParentScopeId(parentActivityInstance.getScopeId());
		}
		
		if (subflow.getDuration()!=null && subflow.getDuration().getValue()>0){
			Date expiredDate = calendarService.dateAfter(now, subflow.getDuration());
			processInstance.setExpiredTime(expiredDate);
		}else{
			processInstance.setExpiredTime(null);
		}
		
		return processInstance;
	}


	
}
