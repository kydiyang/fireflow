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
package org.fireflow.engine.persistence;

import java.util.List;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.impl.Token;

/**
 * @author chennieyun
 *
 */
public interface IPersistenceService {
	public void saveProcessInstance(IProcessInstance processInstance);
	public void saveTaskInstance(ITaskInstance taskInstance);
	public void saveWorkItem(IWorkItem workitem);
	public void saveJoinPoint(IJoinPoint joinPoint);
	public void saveToken(IToken token);
	
	public void updateWorkItem(IWorkItem workItem);
	public void updateTaskInstance(ITaskInstance taskInstance);
	
	public IJoinPoint findJoinPoint(IProcessInstance processInstance,String synchronizerId);
	public List<ITaskInstance> findTaskInstances(IProcessInstance processInstance,String activityID);
	public List<IToken> findTokens(IProcessInstance processInstance,String nodeId);
        public List<IToken> findTokens(IProcessInstance processInstance);
        
        public List<IWorkItem> findWorkItem4Task(String taskid) ;
	public List<IWorkItem> findWorkItemsForTaskInstance(TaskInstance taskInstance);
	public IWorkItem findWorkItemById(String id);
	public ITaskInstance findTaskInstanceById(String id);
        public List<IProcessInstance> findProcessInstanceByProcessId(String processId);
}
