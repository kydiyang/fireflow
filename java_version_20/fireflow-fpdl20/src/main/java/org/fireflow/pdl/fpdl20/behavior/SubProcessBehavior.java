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
package org.fireflow.pdl.fpdl20.behavior;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.impl.InternalSessionAttributeKeys;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.model.data.Property;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.SubProcess;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.JavaDataTypeConverter;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SubProcessBehavior implements WorkflowBehavior {
	private static final Log log = LogFactory.getLog(SubProcessBehavior.class);
	public CompensationHandler getCompensationHandler(String compensationCode){
		return null;
	}
	
	//（2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
//	public CancellationHandler getCancellationHandler(){
//		return null;
//	}
	
	public FaultHandler getFaultHandler(String errorCode){
		return null;
	}
	
	/**
	 * prepare方法创建流程实例
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		SubProcess subflow = (SubProcess)workflowElement;
		
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		ProcessInstanceManager processInstanceManager = context.getEngineModule(ProcessInstanceManager.class,FpdlConstants.PROCESS_TYPE);
		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, token.getProcessType());
		ProcessPersister processRepositoryPersister = persistenceService.getProcessPersister();
		ProcessKey pk = ProcessKey.valueOf(token);
		
		////不需要从数据库查找，
//		ProcessDescriptor processDescriptor = processRepositoryPersister.findProcessDescriptorByProcessKey(pk);
		ProcessDescriptorImpl processDescriptor = new ProcessDescriptorImpl();
		processDescriptor.setProcessId(token.getProcessId());
		processDescriptor.setVersion(token.getVersion());
		processDescriptor.setProcessType(token.getProcessType());

		
		VariablePersister variableService = persistenceService.getVariablePersister();
		ProcessInstancePersister procInstPersistSvc = persistenceService.getProcessInstancePersister();
		
		//1、创建流程实例，设置初始化参数
		String bizId = (String)session.getAttribute(InternalSessionAttributeKeys.BIZ_ID);
		Map<String,Object> variables = (Map<String,Object>)session.getAttribute(InternalSessionAttributeKeys.VARIABLES);
		ActivityInstance parentActivityInstance = session
				.getCurrentActivityInstance();
		ProcessInstance parentProcessInstance = session
				.getCurrentProcessInstance();

		ProcessInstance newProcessInstance = processInstanceManager
				.createProcessInstance(sessionLocal, workflowElement, bizId,
						processDescriptor,parentActivityInstance);
		((ProcessInstanceImpl)newProcessInstance).setTokenId(token.getId());
		procInstPersistSvc.saveOrUpdate(newProcessInstance);
		
		token.setProcessInstanceId(newProcessInstance.getId());
		token.setElementInstanceId(newProcessInstance.getId());
		TokenPersister tokenPersister = persistenceService.getTokenPersister();
		tokenPersister.saveOrUpdate(token);
		
		//2、初始化流程变量
		List<Property> processProperties = subflow.getProperties();
		this.initProcessInstanceVariables(variableService, newProcessInstance, processProperties,variables);
		
		//3、发布事件
		processInstanceManager.fireProcessInstanceEvent(session, newProcessInstance, workflowElement, ProcessInstanceEventTrigger.ON_PROCESS_INSTANCE_CREATED);
		
		//4、设置session和token
		token.setProcessInstanceId(newProcessInstance.getId());
		token.setElementInstanceId(newProcessInstance.getId());
		
		sessionLocal.setCurrentProcessInstance(newProcessInstance);

		return true;//true表示告诉虚拟机，“我”已经准备妥当了。
	}
	
	private void initProcessInstanceVariables(VariablePersister variablePersister,
			ProcessInstance processInstance,List<Property> processProperties,Map<String,Object> initVariables){
		if (processProperties!=null){
			for (Property property:processProperties){
				String valueAsStr = property.getInitialValueAsString();
				Object value = null;
				if (valueAsStr!=null && valueAsStr.trim()!=null){
					try {
						value = JavaDataTypeConverter.dataTypeConvert(property.getDataType(), property.getInitialValueAsString(), property.getDataPattern());
					} catch (ClassCastException e) {
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubflowId()+", variableName="+property.getName(), e);
					} catch (ClassNotFoundException e) {
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubflowId()+", variableName="+property.getName(), e);
					}
				}
				//从initVariables中获取value
				if (initVariables!=null){
					Object tmpValue = initVariables.remove(property.getName());
					if (tmpValue!=null){
						try {
							value = JavaDataTypeConverter.dataTypeConvert(property.getDataType(), tmpValue, property.getDataPattern());
						} catch (ClassCastException e) {
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubflowId()+", variableName="+property.getName(), e);
						} catch (ClassNotFoundException e) {
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubflowId()+", variableName="+property.getName(), e);
						}
					}
				}
				
				createVariable(variablePersister,processInstance,property.getName(),value,property.getDataType());
			}
		}
		
		if (initVariables!=null && !initVariables.isEmpty()){
			Iterator<String> keySet = initVariables.keySet().iterator();
			while (keySet.hasNext()){
				String key = keySet.next();
				Object value = initVariables.get(key);
				createVariable(variablePersister,processInstance,key,value,null);
			}
		}

	}
	
	private void createVariable(VariablePersister variablePersister,
			ProcessInstance processInstance,String name ,Object value,QName dataType){
		VariableImpl v = new VariableImpl();
		((AbsVariable)v).setScopeId(processInstance.getScopeId());
		((AbsVariable)v).setName(name);
		((AbsVariable)v).setProcessElementId(processInstance.getProcessElementId());
		((AbsVariable)v).setPayload(value);
		
		if (value!=null){
			if (value instanceof org.w3c.dom.Document){
				if (dataType != null ){
					((AbsVariable)v).setDataType(dataType);
				}
				v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.w3c.dom.Document");
			}else if (value instanceof org.dom4j.Document){
				if (dataType != null ){
					((AbsVariable)v).setDataType(dataType);
				}
				v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.dom4j.Document");
			}else{
				((AbsVariable)v).setDataType(new QName(NameSpaces.JAVA.getUri(),value.getClass().getName()));
			}
			
		}
		((AbsVariable)v).setProcessId(processInstance.getProcessId());
		((AbsVariable)v).setVersion(processInstance.getVersion());
		((AbsVariable)v).setProcessType(processInstance.getProcessType());
		

		variablePersister.saveOrUpdate(v);
	}

	


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token processToken,
			Object workflowElement) {
		SubProcess subflow = (SubProcess)workflowElement;
		Node entry = subflow.getEntry();
		
		PObjectKey pobjectKey = new PObjectKey(processToken.getProcessId(),processToken.getVersion(),processToken.getProcessType(),entry.getId());
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.fireChildPObject(session, pobjectKey, processToken);
		
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.RUNNING);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		ProcessInstancePersister procInstPersistenceService = persistenceStrategy.getProcessInstancePersister();
		
		ProcessInstance oldProcInst = session.getCurrentProcessInstance();
		ProcessInstance procInst = oldProcInst;
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getElementInstanceId())){
			procInst = procInstPersistenceService.find(ProcessInstance.class, token.getElementInstanceId());
			
		}
		
		ProcessInstanceManager processInstanceManager = ctx.getEngineModule(ProcessInstanceManager.class, FpdlConstants.PROCESS_TYPE);

		try{
			ProcessInstanceState state = ProcessInstanceState.valueOf(token.getState().name());
			processInstanceManager.changeProcessInstanceSate(session, procInst, state,workflowElement);
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
		}

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session,
			Token token, Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		List<Token> childTokenList = kernelManager.getChildren(token);
		if (childTokenList==null || childTokenList.size()==0){
			return ContinueDirection.closeMe();
		}else{
			for (Token tk : childTokenList){
				if (tk.getState().getValue()<TokenState.DELIMITER.getValue()){
					return ContinueDirection.waitingForClose();
				}
			}
		}
		return ContinueDirection.closeMe();
	}

}
