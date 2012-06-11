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
package org.fireflow.service.subflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.impl.InternalSessionAttributeKeys;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.process.ProcessUtil;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.engine.query.Restrictions;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Output;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SubflowInvoker implements ServiceInvoker {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.ServiceInvoker#invoke(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding, java.lang.Object)
//	 */
	public boolean invoke(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivity)
			throws ServiceInvocationException {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		ProcessInstance oldProcessInstance = session.getCurrentProcessInstance();
		ActivityInstance oldActivityInstance = session.getCurrentActivityInstance();
		
		ProcessUtil processUtil = context.getEngineModule(ProcessUtil.class, activityInstance.getProcessType());
		try{
			//1、确定子流程的ProcessId,SubflowId,版本号等信息
			SubflowService subflowService = (SubflowService)processUtil.getServiceDef(activityInstance, theActivity, serviceBinding.getServiceId());
			String subflowId = subflowService.getSubflowId();
			String processId = subflowService.getProcessId();
			String processType = activityInstance.getProcessType();
			Integer version = subflowService.getProcessVersion();
			if (processId.equals(activityInstance.getProcessId())){
				version = activityInstance.getVersion();
			}
			
			if (version==SubflowService.THE_LATEST_VERSION){
				//查找流程的最新版本号。
				PersistenceService pesistenceService = context.getEngineModule(PersistenceService.class, oldActivityInstance.getProcessType());
				ProcessPersister processPersister = pesistenceService.getProcessPersister();
				version = processPersister.findTheLatestVersion(processId, oldActivityInstance.getProcessType());
			}
			
			//2、构建输入参数
			session.setAttribute(InternalSessionAttributeKeys.BIZ_ID, activityInstance.getBizId());
			Map<String, Object> variables = null;
			try {
				variables = AbsServiceInvoker.resolveInputAssignments(context, session, oldProcessInstance, oldActivityInstance, serviceBinding,subflowService);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.setAttribute(InternalSessionAttributeKeys.VARIABLES, variables);//待研究。

			
			//3、启动子流程
			KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);		
			Token parentToken = kernelManager.getToken(activityInstance.getTokenId(), activityInstance.getProcessType());
			kernelManager.fireChildPObject(session, new PObjectKey(processId,version,processType,subflowId),parentToken);
			

		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcessInstance);
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActivityInstance);
		}

		return false;//表示异步调用
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.ServiceInvoker#determineActivityCloseStrategy(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object)
	 */
	public int determineActivityCloseStrategy(WorkflowSession session,
			ActivityInstance activityInstance, Object theActivity, ServiceBinding serviceBinding) {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		int result = ServiceInvoker.CLOSE_ACTIVITY;
		//此处可以增加特殊逻辑，以判断子流程是否可以结束。
		


		if (result==ServiceInvoker.CLOSE_ACTIVITY){
			//校验currentActivityInstance和currentProcessInstance
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);
			ProcessInstance parentProcessInstance = session.getCurrentProcessInstance();
			if (parentProcessInstance==null || !parentProcessInstance.getId().equals(activityInstance.getProcessInstanceId())){
				parentProcessInstance = activityInstance.getProcessInstance(session);
				((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(parentProcessInstance);
			}
			
			//查询出子流程实例
			WorkflowQuery<ProcessInstance> query = session.createWorkflowQuery(ProcessInstance.class);
			query.add(Restrictions.eq(ProcessInstanceProperty.PARENT_ACTIVITY_INSTANCE_ID, activityInstance.getId()))
				.add(Restrictions.eq(ProcessInstanceProperty.PARENT_PROCESS_INSTANCE_ID, parentProcessInstance.getId()));
			
			ProcessInstance subProcInst = query.unique();//在不定制的情况下，只可能创建一个子流程
			Map<String,Object> subProcInstVars = subProcInst.getVariableValues(session);
			
			
			//处理输出，将子流程的流程变量反馈到父流程实例或者父活动实例
//			OperationDef operation = serviceBinding.getOperation();
			List<Assignment> outputAssignments = serviceBinding
					.getOutputAssignments();
			if (outputAssignments != null && outputAssignments.size() > 0) {
				Map<String, Object> scriptContext = new HashMap<String, Object>();

				scriptContext.put(ScriptContextVariableNames.OUTPUTS,
						subProcInstVars);

				try {
					ScriptEngineHelper.assignOutputToVariable(session, context,
							parentProcessInstance, activityInstance,
							outputAssignments, scriptContext);
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		return result;
	}

}
