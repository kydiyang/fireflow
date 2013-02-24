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
package org.fireflow.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.impl.WorkflowSessionRemoteImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.server.WorkflowServer;

/**
 * @author 非也
 * @version 2.0
 */
public class WorkflowSessionFactory {
	private static Service workflowServerService = null;
	/**
	 * 获得本地Session
	 * @param currentUser
	 * @return
	 */
	public static WorkflowSession createWorkflowSession(RuntimeContext runtimeContext,User currentUser){
		WorkflowSessionLocalImpl localSession = new WorkflowSessionLocalImpl();
		String sessionId = UUID.randomUUID().toString();
		localSession.setSessionId(sessionId);
		localSession.setCurrentUser(currentUser);
		localSession.setRuntimeContext(runtimeContext);
		return localSession;
	}
	
	/**
	 * 获得远程Session
	 * @param url
	 * @param userId
	 * @param password
	 * @return
	 */
	public static WorkflowSession createWorkflowSession(String url,String userId,String password)throws MalformedURLException{
		Service svc = createWorkflowServerService(url);
		WorkflowServer serverStub = svc.getPort(WorkflowServer.PORT_QNAME,WorkflowServer.class);//获得WorkflowServer的远程代理
		//System.out.println("====serverStub is "+serverStub.hashCode());
		WorkflowSession sessionStub = serverStub.login(userId, password);
		WorkflowSessionRemoteImpl remoteSession = new WorkflowSessionRemoteImpl();
		remoteSession.setSessionId(sessionStub.getSessionId());
		remoteSession.setCurrentUser(sessionStub.getCurrentUser());
		remoteSession.setWorkflowServer(serverStub);
		return remoteSession;
	}
	
	protected static Service createWorkflowServerService(String url)throws MalformedURLException{
		if (workflowServerService==null){
			synchronized(WorkflowSessionFactory.class){
				if (workflowServerService==null){
					String wsdlAddress = url+"/"+WorkflowServer.SERVICE_LOCAL_NAME+"?wsdl";
					URL wsdlURL = new URL(wsdlAddress);

					workflowServerService = Service.create(wsdlURL,WorkflowServer.SERVICE_QNAME);
				}
			}
		}
		return workflowServerService;

	}
}
