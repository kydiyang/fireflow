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
import java.util.Set;

import org.fireflow.engine.ou.IAssignable;
import org.fireflow.kenel.KenelException;
import org.fireflow.model.Task;

/**
 * @author chennieyun
 *
 */
public interface ITaskInstance extends IAssignable{
	public static final int INITIALIZED = 0;
	public static final int STARTED = 1;
	public static final int COMPLETED = 2;
	public static final int CANCELED = -1;
	public Task getTask();
	public String getId();
	public String getTaskId();
	public String getName();
	public String getDisplayName();
//	public String getDescription();
//	public String getWorkflowProcessInstanceId();
	public IProcessInstance getProcessInstance();
	public Date getCreatedTime();
	public Date getStartedTime();
	public Date getEndTime();
	public Date getExpiredTime();//过期时间
	public Integer getState();
	public String getCompletionStrategy();
	public void setCompletionStrategy(String s);
	
	public void complete()throws EngineException,KenelException;	
	public void start()throws EngineException,KenelException;
	
//	public Set getWorkItems() ;	
}
