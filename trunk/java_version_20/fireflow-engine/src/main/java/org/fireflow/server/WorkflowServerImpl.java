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

import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.impl.WorkflowStatementLocalImpl;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.impl.AbsActivityInstance;
import org.fireflow.engine.entity.runtime.impl.AbsProcessInstance;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.InvalidModelException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@WebService(name=WorkflowServer.PORT_TYPE,serviceName=WorkflowServer.SERVICE_LOCAL_NAME,
		portName=WorkflowServer.PORT_NAME,
		targetNamespace=WorkflowServer.TARGET_NAMESPACE,
		endpointInterface="org.fireflow.server.WorkflowServer")
@BindingType(value=SOAPBinding.SOAP11HTTP_BINDING)
public class WorkflowServerImpl implements WorkflowServer,RuntimeContextAware,EngineModule {
	private static final String SESSION_CACHE = "SESSION_CACHE";
	private RuntimeContext runtimeContext = null;
	private int sessionToIdleSeconds = 30*60;//最大空闲时间，30分钟，
	private int maxSessions = 50;//可并行存在的session数量
	private TransactionTemplate springTransactionTemplate = null;
	
	/**
	 * 登录成功后，WorkflowSession被缓存。
	 * 目前采用EhCache实现。
	 * 
	 */
	private CacheManager  cacheManager = null;

	
	public WorkflowServerImpl(){
	}
	
	public void init(RuntimeContext runtimeContext)throws EngineException{
		cacheManager = CacheManager.newInstance();
		Cache cache = new Cache(
				  new CacheConfiguration(SESSION_CACHE, maxSessions)
				    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
				    .overflowToDisk(false)
				    .eternal(false)
				    .timeToLiveSeconds(0)
				    .timeToIdleSeconds(sessionToIdleSeconds)
				    .diskPersistent(false));
		cacheManager.addCache(cache);
	}
	


	public TransactionTemplate getSpringTransactionTemplate() {
		return springTransactionTemplate;
	}

	public void setSpringTransactionTemplate(
			TransactionTemplate springTransactionTemplate) {
		this.springTransactionTemplate = springTransactionTemplate;
	}

	public int getMaxSessions() {
		return maxSessions;
	}

	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}

	public int getSessionToIdleSeconds() {
		return sessionToIdleSeconds;
	}

	public void setSessionToIdleSeconds(int sessionToIdleSeconds) {
		this.sessionToIdleSeconds = sessionToIdleSeconds;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	@Override
	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	@Override
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.server.WorkflowServer#login(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkflowSessionLocalImpl login(String userName, String password) throws EngineException{
		//OUSystemConnector是一个与流程语言无关的Module，
		OUSystemConnector connector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		if (connector==null){
			throw new EngineException("Fire Workflow 引擎没有配置正确的组织机构链接器模块。");
		}
		
		User u = connector.login(userName, password);
		if (u==null){
			throw new EngineException("用户名或者密码错误；登录Fire Workflow失败。");
		}
		
		WorkflowSessionLocalImpl session = (WorkflowSessionLocalImpl)WorkflowSessionFactory.createWorkflowSession(runtimeContext, u);
		
		Cache cache = cacheManager.getCache(SESSION_CACHE);
		cache.put(new Element(session.getSessionId(),session));
		
		return session;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.server.WorkflowServer#uploadProcessXml(java.lang.String, org.fireflow.engine.entity.repository.ProcessDescriptor)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ProcessDescriptorImpl uploadProcessXml(String sessionId,
			final String processXml,final boolean publishState,
			final String bizType,final String ownerDeptId)throws EngineException{
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessDescriptorImpl descriptor = (ProcessDescriptorImpl)springTransactionTemplate.execute(new TransactionCallback(){

			@Override
			public Object doInTransaction(TransactionStatus status) {
				try {
					return statement.uploadProcessXml(processXml, publishState, bizType, ownerDeptId);
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				}
			}
			
		});
		return descriptor;
	}

	public AbsWorkflowEntity getEntity(String sessionId,String entityId,String entityClassName){

		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		
		if (entityClassName==null || entityClassName.trim().equals("")){
			return null;
		}
		if (entityId==null || entityId.trim().equals("")) {
			return null;
		}
		
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		if (ProcessInstance.class.getName().equals(entityClassName.trim())){
			return (AbsProcessInstance)statement.getEntity(entityId, ProcessInstance.class);
		}
		else if (ActivityInstance.class.getName().equals(entityClassName.trim())){
			return (AbsActivityInstance)statement.getEntity(entityId, ActivityInstance.class);
		}
		else if (WorkItem.class.getName().equals(entityClassName.trim())){
			return (AbsWorkItem)statement.getEntity(entityId, WorkItem.class);
		}
		
		//TODO 待补充
		
		return null;
	}
	
	
	
	private WorkflowSessionLocalImpl validateSession(String sessionId){
		Cache cache = cacheManager.getCache(SESSION_CACHE);
		Element cacheElement = cache.get(sessionId);
		if (cacheElement==null){
			throw new EngineException("Workflow Session超时，sessionId是："+sessionId);
		}
		WorkflowSessionLocalImpl session = (WorkflowSessionLocalImpl)cacheElement.getValue();

		return session;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.server.WorkflowServer#test(java.lang.String)
	 */
//	@Override
//	public Customer test(String name) {
//		if (name.equals("zhangsan")){
//			Customer c = new Customer();
//			Address a = new Address();
//			a.setAddress("天河东路123号");
//			c.setContactInfo(a);
//			
//			c.addContactInfo(a);
//			
//			PhoneNumber phone = new PhoneNumber();
//			phone.setNumber("186203203012");
//			c.addContactInfo(phone);
//			
//			return c;
//		}else{
//			Customer c = new Customer();
//			PhoneNumber phone = new PhoneNumber();
//			phone.setNumber("186203203012");
//			c.setContactInfo(phone);
//			return c;
//		}
//	}
//
//	public ContactInfo test2(String s){
//		if (s.equals("a")){
//			Address a = new Address();
//			a.setAddress("天河东路123号");
//			return a;
//		}else{
//			PhoneNumber phone = new PhoneNumber();
//			phone.setNumber("186203203012");
//			return phone;
//		}
//	}
}
