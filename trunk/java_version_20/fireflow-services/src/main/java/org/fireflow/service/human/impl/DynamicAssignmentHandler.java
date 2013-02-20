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
package org.fireflow.service.human.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class DynamicAssignmentHandler extends AbsAssignmentHandler implements AssignmentHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 675889705207600732L;

	private List<User> potentialOwners = null;
	
	private List<User> readers = null;
	
	private WorkItemAssignmentStrategy assignmentStrategy = null;
	

	/**
	 * 获得潜在的工作参与者列表
	 * @return
	 */
	public List<User> resolvePotentialOwners(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance){
		return this.potentialOwners;
	}
	
	public void setPotentialOwners(List<User> owners){
		this.potentialOwners = owners;
	}
	
	/**
	 * 获得抄送人列表
	 * @return
	 */
	public List<User> resolveReaders(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance){
		return this.readers;
	}
	
	public void setReaders(List<User> users){
		this.readers = users;
	}

	/**
	 * @return the assignmentStrategy
	 */
	public WorkItemAssignmentStrategy resolveAssignmentStrategy(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity) {
		return assignmentStrategy;
	}

	/**
	 * @param assignmentStrategy the assignmentStrategy to set
	 */
	public void setAssignmentStrategy(WorkItemAssignmentStrategy assignmentStrategy) {
		this.assignmentStrategy = assignmentStrategy;
	}

	@Override
	public List<User> resolveAdministrators(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Map<WorkItemProperty,Object> resolveWorkItemPropertyValues(){
		return new HashMap<WorkItemProperty,Object>();
	}
	
}
