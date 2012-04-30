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
package org.fireflow.engine.entity.repository;

import java.util.Date;

import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface RepositoryDescriptor extends WorkflowEntity{
	public static final String OPERATION_UPLOAD = "UPLOAD";
	public static final String OPERATION_UPDATE = "UPDATE";
	public static final String OPERATION_PUBLISH = "PUBLISH";
	public static final String OPERATION_UNPUBLISH = "UNPUBLISH";
	public static final String OPERATION_UPLOAD_PUBLISH = "UPLOAD_PUBLISH";
	public static final String OPERATION_UPDATE_PUBLISH = "UPDATE_PUBLISH";
	public static final String OPERATION_UPDATE_UNPUBLISH = "UPDATE_UNPUBLISH";

    /**
     * 流程名称
     * @return
     */
    public String getName();
    
    /**
     * 流程中文名
     * @return
     */
    public String getDisplayName();

    /**
     * 流程描述信息
     * @return
     */
    public String getDescription() ;

    /**
     * 获得业务流程业务类别
     * @return
     */
    public String getBizCategory();

    /**
     * 流程发布状态
     * @return
     */
    public Boolean getPublishState();

    
	/**
	 * 最后一次修改操作的内容，可以是UPLOAD,UPDATE,PUBLISH
	 * @return
	 */
	public String getLastOperation();
	
    
    /**
     * 流程文件在classpath中的全路径名
     * @return
     */
    public String getFileName();
    
    /**
     * 流程所属部门的Id
     * @return
     */
    public String getOwnerDeptId();
    
    /**
     * 流程所属部门的名称
     * @return
     */
    public String getOwnerDeptName();
    

    /**
     * 批准人
     * @return
     */
    public String getApprover() ;
    
    
    /**
     * 批准时间
     * @return
     */
    public Date getApprovedTime() ;
    

    /**
     * 最后修改人姓名
     * @return
     */
	public String getLastEditor() ;

	/**
	 * 最后修改时间
	 * @return
	 */
	public Date getLastEditTime();
}
