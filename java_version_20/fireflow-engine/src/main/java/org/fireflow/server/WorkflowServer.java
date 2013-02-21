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
package org.fireflow.server;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.fireflow.client.impl.WorkflowQueryImpl;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.exception.EngineException;

/**
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@WebService(name=WorkflowServer.PORT_TYPE,
		targetNamespace=WorkflowServer.TARGET_NAMESPACE)
public interface WorkflowServer extends EngineModule {
	public static final String PORT_TYPE = "WorkflowServer";
	public static final String PORT_NAME = "WorkflowServerPort";
	public static final String SERVICE_LOCAL_NAME = "WorkflowServerService";
	public static final String TARGET_NAMESPACE = "http://www.fireflow.org/services/WorkflowServer";
	public static final QName SERVICE_QNAME = new QName(TARGET_NAMESPACE,SERVICE_LOCAL_NAME);
	public static final QName PORT_QNAME = new QName(TARGET_NAMESPACE,PORT_NAME);
	/**
	 * login成功后，返回session; 如果登录失败，则抛出
	 * org.fireflow.engine.exception.EngineException
	 * @param userName
	 * @param password
	 * @return
	 */
	@WebMethod
	public @WebResult(name="workflowSession") WorkflowSessionLocalImpl login(
			@WebParam(name="userName") String userName,
			@WebParam(name="password") String password)throws EngineException;
	
	
	/**
	 * 返回的Descriptor里面包含流程版本信息。
	 * @param processXml
	 * @param processDescriptor
	 * @return
	 */
	@WebMethod
	public @WebResult(name="processDescriptor") ProcessDescriptorImpl uploadProcessXml(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="processXml") String processXml,
			@WebParam(name="publishState") boolean publishState, 
			@WebParam(name="bizType") String bizType, 
			@WebParam(name="ownerDeptId") String ownerDeptId)throws EngineException;



	@WebMethod
	public  @WebResult(name="workflowEntity") List<AbsWorkflowEntity> executeQueryList(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowQuery") WorkflowQueryImpl q);

	@WebMethod
	public @WebResult(name="workflowEntity") AbsWorkflowEntity getEntity(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="entityId") String entityId,
			@WebParam(name="entityClassName") String entityClassName);
	
	@WebMethod
	public @WebResult(name="entityCount") int executeQueryCount(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowQuery") WorkflowQueryImpl q);

	/* 下面两个方法用于测试 */
	
//	@WebMethod
//	public @WebResult(name="customer") Customer test(@WebParam(name="name") String name);
	/*
	@WebMethod
	public ContactInfo test2(String s);
	*/
}
