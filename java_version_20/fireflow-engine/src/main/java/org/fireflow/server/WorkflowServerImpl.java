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
public class WorkflowServerImpl extends WorkflowServerInternalImpl {

}
