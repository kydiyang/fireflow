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

import java.util.Date;
import java.util.Map;

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
	
        /**
         * return the process instance's Id.
         * @return
         */
	public String getId();
        
        /**
         * return the process instance's name,which equals to the workflow process's name
         * @return
         */
        public String getName();	
        
        /**
         * return the process instance's display-name ，
         * which equals to the workflow process's  display-name.
         * @return
         */
        public String getDisplayName();

        /**
         * return the workflow process's id
         * @return
         */
        public String getProcessId();
        
        public Integer getState();
        
        /**
         * return the workflow process's version.
         * 
         * @return
         */
        public Integer getVersion();		
        
        public Date getCreatedTime();
        
        public Date getStartedTime();
        
        public Date getEndTime();
        
        public Date getExpiredTime();
        /**
         * Get the process instance variable,return null if the variable is not existing .
         * @param name the name of the variable
         * @return the value of the variable. It may be Integer,String,Boolean,java.util.Date or Float
         */
	public Object getProcessInstanceVariable(String name);
	
        /**
         * Save the process instance variable.If there is a variable with the same name ,it will be updated.
         * @param name
         * @param var The value of the variable. It may be Integer,String,Boolean,java.util.Date or Float
         */
	public void setProcessInstanceVariable(String name,Object var);
	
        /**
         * Get all the process instance variables. the key of the returned map is the variable's name
         * @return 
         */
	public Map getProcessInstanceVariables();
        
        /**
         * update the process instance variables batched.
         * @param vars
         */
	public void setProcessInstanceVariables(Map vars);
        

        /**
         * return the corresponding workflow process.
         * @return
         */
        public WorkflowProcess getWorkflowProcess()throws EngineException;
        
        /**
         * get the parent process instance's id , null if no parent process instance.
         * @return
         */
	public String getParentProcessInstanceId() ;
        
        /**
         * get the parent taskinstance's id ,null if no parent taskinstance.
         * @return
         */
        public String getParentTaskInstanceId();    
        
        
	/**
	 * Abort the process instance 。
	 * @throws RuntimeException
	 */
	public void abort()throws EngineException;        
	
}
