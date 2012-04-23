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
package org.fireflow.model.binding.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceBindingImpl implements ResourceBinding {
	private static final String DEFAULT_ASSIGNMENT_HANDLER_CLASS_NAME= "org.fireflow.service.human.impl.DefaultAssignmentHandler";
	private String name = null;
	private WorkItemAssignmentStrategy assignmentStrategy = WorkItemAssignmentStrategy.ASSIGN_TO_ANY;
	private List<ResourceDef> administrators = new ArrayList<ResourceDef>();
	private List<ResourceDef> readers = new ArrayList<ResourceDef>();
	private List<ResourceDef> potentialOwners = new ArrayList<ResourceDef>();
	private String assignmentHandlerBeanName = null;
	private String assignmentHandlerClassName = DEFAULT_ASSIGNMENT_HANDLER_CLASS_NAME;
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getAdministrators()
	 */
	public List<ResourceDef> getAdministrators() {
		return administrators;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setAdministrators(java.util.List)
	 */
	public void setAdministrators(List<ResourceDef> administrators){
		this.administrators = administrators;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getName()
	 */
	public String getDisplayName() {
		return name;
	}
	
	public void setDisplayName(String nm){
		this.name = nm;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getPotentialOwners()
	 */
	public List<ResourceDef> getPotentialOwners() {
		return this.potentialOwners;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getReaders()
	 */
	public List<ResourceDef> getReaders() {
		return this.readers;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setPotentialOwners(java.util.List)
	 */
	public void setPotentialOwners(List<ResourceDef> potentialOwners) {
		this.potentialOwners = potentialOwners;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setReaders(java.util.List)
	 */
	public void setReaders(List<ResourceDef> readers) {
		this.readers = readers;

	}
	/**
	 * @return the assignmentStrategy
	 */
	public WorkItemAssignmentStrategy getAssignmentStrategy() {
		return assignmentStrategy;
	}
	/**
	 * @param assignmentStrategy the assignmentStrategy to set
	 */
	public void setAssignmentStrategy(WorkItemAssignmentStrategy assignmentStrategy) {
		this.assignmentStrategy = assignmentStrategy;
	}
	public String getAssignmentHandlerClassName() {
		return assignmentHandlerClassName;
	}
	public String getAssignmentHandlerBeanName() {
		return assignmentHandlerBeanName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAssignmentHandlerBeanName(String assignmentHandlerBeanName) {
		this.assignmentHandlerBeanName = assignmentHandlerBeanName;
	}
	public void setAssignmentHandlerClassName(String assignmentHandlerClassName) {
		this.assignmentHandlerClassName = assignmentHandlerClassName;
	}

}
