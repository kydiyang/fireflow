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
package org.fireflow.engine.impl;

import java.util.List;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IFireflowSession;
import org.fireflow.engine.IFireflowSessionCallback;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kenel.KenelException;
import org.fireflow.model.DataField;
import org.fireflow.model.WorkflowProcess;


/**
 * @author chennieyun
 * 
 */
public class FireflowSession implements IFireflowSession {
	RuntimeContext runtimeContext = null;
	public void begin(Object dbsession){
		runtimeContext.setCurrentDBSession(dbsession);
//		runtimeContext.setCurrentFireflowSession(this);
	}
	public void end(){
		runtimeContext.setCurrentDBSession(null);
//		runtimeContext.setCurrentFireflowSession(null);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.engine.IFireflowSession#createProcessInstance(java.lang.String)
	 */
	public IProcessInstance createProcessInstance(String workflowProcessName)
			throws EngineException, KenelException {
		// TODO Auto-generated method stub
		final String wfprocessname = workflowProcessName;
		return (IProcessInstance) this.execute(new IFireflowSessionCallback() {
			public Object doInFireflowSession(RuntimeContext ctx)
					throws EngineException, KenelException {
				WorkflowProcess wfProcess = ctx.getDefinitionService()
						.getWorkflowProcessByName(wfprocessname);
				if (wfProcess == null) {
					throw new EngineException("系统中没有名称为" + wfprocessname
							+ "的流程定义");
				}

				ProcessInstance processInstance = new ProcessInstance();
				processInstance.setProcessId(wfProcess.getId());
				processInstance.setDisplayName(wfProcess.getDisplayName());
				processInstance.setName(wfProcess.getName());
				processInstance.setState(IProcessInstance.INITIALIZED);

                                //初始化流程变量
                                List datafields = wfProcess.getDataFields();
                                for (int i=0;datafields!=null && i<datafields.size();i++){
                                    DataField df = (DataField)datafields.get(i);
                                    if (df.getDataType().equals(DataField.STRING)){
                                        if (df.getInitialValue()!=null){
                                            processInstance.setProcessInstanceVariable(df.getName(), df.getInitialValue());
                                        }else{
                                            processInstance.setProcessInstanceVariable(df.getName(), "");
                                        }
                                    }
                                    else if (df.getDataType().equals(DataField.INTEGER)){
                                        if (df.getInitialValue()!=null){
                                            try{
                                                Integer intValue = new Integer(df.getInitialValue());
                                                processInstance.setProcessInstanceVariable(df.getName(), intValue);
                                            }catch(Exception e){
                                                
                                            }
                                        }else{
                                            processInstance.setProcessInstanceVariable(df.getName(), new Integer(0));
                                        }
                                    }
                                    else if (df.getDataType().equals(DataField.FLOAT)){
                                        if (df.getInitialValue()!=null){
                                            Float floatValue = new Float(df.getInitialValue());
                                            processInstance.setProcessInstanceVariable(df.getName(), floatValue);
                                        }else{
                                            processInstance.setProcessInstanceVariable(df.getName(), new Float(0));
                                        }
                                    }
                                    else if (df.getDataType().equals(DataField.BOOLEAN)){
                                        if (df.getInitialValue()!=null){
                                            Boolean booleanValue = new Boolean(df.getInitialValue());
                                            processInstance.setProcessInstanceVariable(df.getName(), booleanValue);
                                        }else{
                                            processInstance.setProcessInstanceVariable(df.getName(), Boolean.FALSE);
                                        }
                                    }
                                    else if (df.getDataType().equals(DataField.DATETIME)){
                                        //TODO 需要完善一下
                                    }
                                }
                                
                                
				ctx.getPersistenceService()
						.saveProcessInstance(processInstance);


				return processInstance;
			}
		});
	}

	public IWorkItem findWorkItemById(String id){
		final String workItemId = id;
		try{
		return (IWorkItem) this.execute(new IFireflowSessionCallback() {
			public Object doInFireflowSession(RuntimeContext ctx)
					throws EngineException, KenelException {
				IPersistenceService persistenceService = ctx.getPersistenceService();
				
				return persistenceService.findWorkItemById(workItemId);
			}
		});
		}catch(EngineException ex){
			ex.printStackTrace();
			return null;
		}catch(KenelException ex){
			ex.printStackTrace();
			return null;
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.engine.IFireflowSession#execute(org.fireflow.engine.IFireflowSessionCallback)
	 */
	public Object execute(IFireflowSessionCallback callback)
			throws EngineException, KenelException

	{
            //不需要检查,2008-06-08
//		if (runtimeContext.getCurrentDBSession()==null){
//			throw new EngineException("当前的数据库联接对象为null！请确定在调用fireflow API之前调用了IFireflowSesssion.begin(Object dbsession)方法");
//		}
		Object result =  callback.doInFireflowSession(runtimeContext);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.engine.IFireflowSession#setRuntimeContext(org.fireflow.engine.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;
	}
        
        public RuntimeContext getRuntimeContext(){
            return runtimeContext;
        }
        
	
	public ITaskInstance findTaskInstanceById(String id){
		final String taskInstanceId = id;
		try{
		return (ITaskInstance) this.execute(new IFireflowSessionCallback() {
			public Object doInFireflowSession(RuntimeContext ctx)
					throws EngineException, KenelException {
				IPersistenceService persistenceService = ctx.getPersistenceService();
				
				return persistenceService.findTaskInstanceById(taskInstanceId);
			}
		});
		}catch(EngineException ex){
			ex.printStackTrace();
			return null;
		}catch(KenelException ex){
			ex.printStackTrace();
			return null;
		}
	}

}
