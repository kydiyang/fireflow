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
package org.fireflow.engine.entity.repository.impl;

import java.util.Date;

import org.fireflow.engine.entity.repository.RepositoryDescriptor;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public abstract class AbsRepositoryDescriptorImpl implements
		RepositoryDescriptor {
    protected String id; //主键
    protected String name; //流程英文名称
    protected String displayName;//流程显示名称
    protected String description;//流程业务说明
    protected String bizType = null;//业务类别
    protected String fileName = null;//流程文件在classpath中的全路径名

    protected Boolean publishState;//是否发布，1=已经发布,0未发布

    protected String ownerDeptName = null;//流程所属的部门名称
    protected String ownerDeptId = null;//流程所属的部门Id    
    protected String approver = null;//批准发布人
    protected Date approvedTime = null;//批准发布时间
    
    protected String latestEditor = null;//最后编辑流程的操作者姓名
    protected Date lastUpdateTime = null;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


	public Date getApprovedTime() {
		
		return this.approvedTime;
	}
	
	public void setApprovedTime(Date time){
		this.approvedTime = time;
	}


	public String getApprover() {
		return this.approver;
	}
	
	public void setApprover(String approver){
		this.approver = approver;
	}


	public String getLastEditor() {
		return this.latestEditor;
	}
	
	public void setLastEditor(String editorName){
		this.latestEditor = editorName;
	}

	
	public String getOwnerDeptId() {
		return this.ownerDeptId;
	}
	
	public void setOwnerDeptId(String orgId){
		this.ownerDeptId = orgId;
	}

	public String getOwnerDeptName() {
		return this.ownerDeptName;
	}
	
	public void setOwnerDeptName(String orgName){
		this.ownerDeptName = orgName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    
	public String getBizType(){
		return this.bizType;
	}
	
	public void setBizType(String bizCategory){
		this.bizType = bizCategory;
	}

	public Boolean getPublishState() {
		return publishState;
	}

	public void setPublishState(Boolean state){
		this.publishState = state;
	}
	public Date getLastUpdateTime(){
		return this.lastUpdateTime;
	}
	
	public void setLastUpdateTime(Date lastUpdateTime){
		this.lastUpdateTime = lastUpdateTime;
	}
}
