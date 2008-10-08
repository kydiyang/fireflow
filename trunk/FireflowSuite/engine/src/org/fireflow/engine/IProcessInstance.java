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
package org.fireflow.engine;

import java.util.Map;

import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.ISynchronizerInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.KenelException;
import org.fireflow.model.WorkflowProcess;
/**
 * @author chennieyun
 *
 */
public interface IProcessInstance {
	public static final int INITIALIZED = 0;
	public static final int STARTED = 1;
	public static final int COMPLETED = 2;
	public static final int CANCELED = -1;
	
	public void run()throws EngineException,KenelException;
	
	public String getId();
	
	public String getParentProcessInstanceId() ;
        public String getParentTaskInstanceId();
	
//	public IJoinPoint createJoinPoint(ISynchronizerInstance synchInst,IToken token)throws EngineException;
	
	public Object getProcessInstanceVariable(String name);
	
	public void setProcessInstanceVariable(String name,Object var);
	
	public Map getProcessInstanceVariables();
	public void setProcessInstanceVariables(Map vars);
        
        public String getName();
        
        public WorkflowProcess getWorkflowProcess();
        
        public String getProcessId();
//	public ProcessInstance getParentProcessInstance() ;
//	public Set getTaskInstances();
	
}
