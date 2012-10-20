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
package org.fireflow.engine.entity.config.impl;

import java.util.Date;

import org.fireflow.engine.entity.config.FireflowConfig;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FireflowConfigImpl implements FireflowConfig{
	private String id ;
	private String configId;
	private String configName;
	private String configValue;
	private String description;
	private String categoryId;
	private String parentConfigId;
	
	private String lastEditor;
	private Date lastUpdateTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getParentConfigId() {
		return parentConfigId;
	}
	public void setParentConfigId(String parentConfigId) {
		this.parentConfigId = parentConfigId;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.WorkflowEntity#getLastUpdateTime()
	 */
	@Override
	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}
	
	public void setLastUpdateTime(Date lastUpdateTime){
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getLastEditor() {
		return lastEditor;
	}
	public void setLastEditor(String lastEditor) {
		this.lastEditor = lastEditor;
	}
	
	
}
