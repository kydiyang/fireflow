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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEvent;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEventListener;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.instancemanager.impl.AbsActivityInstanceManager;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Activity;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.event.EventListenerDef;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityInstanceManagerFpdl20Impl extends
		AbsActivityInstanceManager {
	private Log log = LogFactory.getLog(ActivityInstanceManagerFpdl20Impl.class);
	
	public ActivityInstanceManagerFpdl20Impl(){
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#createActivityInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance, java.lang.Object)
	 */
	public ActivityInstance createActivityInstance(WorkflowSession session,
			ProcessInstance processInstance, Object workflowElement) {
		CalendarService calendarService = this.runtimeContext.getDefaultEngineModule(CalendarService.class);
		
		
		Node node = (Node)workflowElement;
		ActivityInstanceImpl actInst = new ActivityInstanceImpl();
		actInst.setName(node.getName());
		String displayName = node.getName();		
		actInst.setDisplayName((displayName==null || displayName.trim().equals(""))?node.getName():displayName);
		actInst.setState(ActivityInstanceState.INITIALIZED);
		
		actInst.setProcessName(processInstance.getProcessName());
		actInst.setProcessDisplayName(processInstance.getProcessDisplayName());
		actInst.setSubflowName(processInstance.getSubflowName());
		actInst.setSubflowDisplayName(processInstance.getSubflowDisplayName());
		actInst.setBizCategory(processInstance.getBizCategory());
		
		actInst.setProcessId(processInstance.getProcessId());
		actInst.setVersion(processInstance.getVersion());
		actInst.setProcessType(processInstance.getProcessType());
		actInst.setProcessInstanceId(processInstance.getId());
		actInst.setNodeId(node.getId());		
		actInst.setBizId(processInstance.getBizId());
		actInst.setSubflowId(processInstance.getSubflowId());
		
		if (node instanceof Activity){
			Activity activity = (Activity) node;
			ServiceBinding serviceBinding = activity.getServiceBinding();
			if (serviceBinding!=null){
				actInst.setServiceId(serviceBinding.getServiceId());
				ServiceDef svcDef = serviceBinding.getService();
				if (svcDef!=null){
					actInst.setServiceVersion(svcDef.getVersion());
				}
			}
		}
		
		
		actInst.setParentScopeId(processInstance.getScopeId());
		
		Date now = calendarService.getSysDate();
		actInst.setCreatedTime(now);

		if (node instanceof Activity){
			Activity fpdl20Activity = (Activity)node;
			
			if (fpdl20Activity.getDuration()!=null && fpdl20Activity.getDuration().getValue()>0){
				Date expiredDate = calendarService.dateAfter(now, fpdl20Activity.getDuration());
				actInst.setExpiredTime(expiredDate);
			}
			
		}
		
		return actInst;
	}





	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#runActivityInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public boolean runActivityInstance(WorkflowSession session,
			Object workflowElement, ActivityInstance activityInstance)
			throws ServiceInvocationException {
		Activity activity = (Activity)workflowElement;	
		
		//调用ServiceExecutor
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
		RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();

		ServiceBinding serviceBinding = activity.getServiceBinding();
		if (serviceBinding!=null){
			ServiceDef serviceDef = serviceBinding.getService();
			if (serviceDef==null){
				return true;//没有绑定service，直接结束activity instance
			}
			ServiceInvoker serviceExecutor = this.getServiceInvoker(runtimeContext, serviceDef, activityInstance.getProcessType());

			if (serviceExecutor==null){
				throw new EngineException(session.getCurrentActivityInstance(),
						"Can NOT find the service invoker for the "+serviceDef.toString());
			}

			boolean b = serviceExecutor.invoke(sessionLocalImpl, session.getCurrentActivityInstance(), serviceBinding, activity.getResourceBinding(), activity);
				
			return b;
		}else{
			return true;
		}

	}
	
	public int tryCloseActivityInstance(WorkflowSession session,ActivityInstance activityInstance,Object workflowElement){
		Activity activity = (Activity)workflowElement;		
		//调用ServiceExecutor
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
		RuntimeContext runtimeContext = sessionLocalImpl.getRuntimeContext();

		ServiceBinding serviceBinding = activity.getServiceBinding();
		if (serviceBinding!=null){
			ServiceDef serviceDef = serviceBinding.getService();
			if (serviceDef==null){
				return ServiceInvoker.CLOSE_ACTIVITY;
			}
			ServiceInvoker serviceExecutor = this.getServiceInvoker(runtimeContext, serviceDef, activityInstance.getProcessType());
			
			if (serviceExecutor==null){
				throw new EngineException(session.getCurrentActivityInstance(),
						"Can NOT find the service invoker for the  "+serviceDef.toString());
			}
			int b = serviceExecutor.determineActivityCloseStrategy(sessionLocalImpl, activityInstance, activity, serviceBinding);
			
			return b;
		}else{
			return ServiceInvoker.CLOSE_ACTIVITY;
		}

	}	
	
	
}
