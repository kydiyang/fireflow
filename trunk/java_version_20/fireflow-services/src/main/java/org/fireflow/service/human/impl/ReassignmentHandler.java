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
package org.fireflow.service.human.impl;

import java.util.HashMap;
import java.util.Map;

import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;


/**
 * 加签处理器
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ReassignmentHandler extends DynamicAssignmentHandler {
	private String reassignType = WorkItem.REASSIGN_AFTER_ME;
	private String parentWorkItemId = null;
	public String getReassignType() {
		return reassignType;
	}
	public void setReassignType(String reassignType) {
		this.reassignType = reassignType;
	}
	public String getParentWorkItemId() {
		return parentWorkItemId;
	}
	public void setParentWorkItemId(String parentWorkItemId) {
		this.parentWorkItemId = parentWorkItemId;
	}
	
	public Map<WorkItemProperty,Object> resolveWorkItemPropertyValues(){
		Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
		values.put(WorkItemProperty.REASSIGN_TYPE, reassignType);
		values.put(WorkItemProperty.PARENT_WORKITEM_ID, parentWorkItemId);
		return values;
	}
}
