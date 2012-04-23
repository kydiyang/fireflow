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
package org.fireflow.service.subflow;

import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.Util4Serializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class SubflowServiceParser extends ServiceParser {
	public static final String SERVICE_NAME = "service.subflow";
	public static final String PROCESS_ID = "process-id";
	public static final String SUBFLOW_ID = "subflow-id";
	// public static final String PROCESS_TYPE = "process-type";
	public static final String PROCESS_VERSION = "process-version";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.model.io.service.ServiceParser#deserializeService(org.w3c
	 * .dom.Element)
	 */
	@Override
	public ServiceDef deserializeService(Element element)
			throws DeserializerException {
		String localName_1 = element.getLocalName();
		String namespaceUri_1 = element.getNamespaceURI();

		if (!equalStrings(localName_1, SERVICE_NAME)
				|| !equalStrings(namespaceUri_1, SERVICE_NS_URI)) {
			throw new DeserializerException(
					"The element is not a subflow service, the element name is '"
							+ localName_1 + "'");
		}
		SubflowService subflowService = new SubflowService();
		this.loadCommonServiceAttribute(subflowService, element);

		InterfaceDef _interface = this.loadCommonInterface(subflowService,
				Util4Deserializer.child(element, INTERFACE_COMMON));
		subflowService.setInterface(_interface);

		subflowService.setProcessId(Util4Deserializer.elementAsString(element,
				PROCESS_ID));
		subflowService.setSubflowId(Util4Deserializer.elementAsString(element,
				SUBFLOW_ID));
		subflowService.setProcessVersion(Util4Deserializer.elementAsInteger(
				element, PROCESS_VERSION));

		this.loadExtendedAttributes(subflowService.getExtendedAttributes(),
				Util4Deserializer.child(element, EXTENDED_ATTRIBUTES));

		return subflowService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.model.io.service.ServiceParser#serializeService(org.fireflow
	 * .model.servicedef.ServiceDef, org.w3c.dom.Element)
	 */
	@Override
	public void serializeService(ServiceDef service, Element parentElement)
			throws SerializerException {
		if (!(service instanceof SubflowService)) {
			return;
		}
		SubflowService subflowService = (SubflowService) service;

		Document document = parentElement.getOwnerDocument();

		Element svcElem = document.createElementNS(SERVICE_NS_URI,
				SERVICE_NS_PREFIX + ":" + SERVICE_NAME);

		this.writeCommonServiceAttribute(subflowService, svcElem);

		this.writeCommonInterface(service.getInterface(), svcElem);

		Util4Serializer.addElement(svcElem, PROCESS_ID,
				subflowService.getProcessId());
		Util4Serializer.addElement(svcElem, SUBFLOW_ID,
				subflowService.getSubflowId());
		Util4Serializer.addElement(svcElem, PROCESS_VERSION, subflowService
				.getProcessVersion() == null ? Integer.toString(SubflowService.THE_LATEST_VERSION) : subflowService
				.getProcessVersion().toString());

		this.writeExtendedAttributes(subflowService.getExtendedAttributes(),
				svcElem);

		parentElement.appendChild(svcElem);

	}

}
