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
package org.fireflow.engine.entity.runtime.impl;

import java.util.Date;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.server.support.DateTimeXmlAdapter;
import org.fireflow.server.support.ObjectXmlAdapter;
import org.fireflow.server.support.PropertiesXmlAdapter;
import org.fireflow.server.support.QNameXmlAdapter;

/**
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absVariableType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({VariableImpl.class,VariableHistory.class})
public abstract class AbsVariable extends AbsWorkflowEntity implements Variable {
	String scopeId  = null;
	String name = null;
	
	@XmlJavaTypeAdapter(PropertiesXmlAdapter.class)
	Properties headers = new Properties();
	
	@XmlJavaTypeAdapter(QNameXmlAdapter.class)
	QName dataType = null;
//	String javaClassName = null;

//	String valueAsString = null;
	
//	String mainSchemaFileName = null;
	
//	Map<String,String> schemas = null;
	@XmlJavaTypeAdapter(ObjectXmlAdapter.class)
	Object value = null;
	
	String processElementId = null;
	String processId = null;
	Integer version = null;
	String processType = null;
	
	public String getScopeId() {
		return scopeId;
	}
	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Properties getHeaders() {
		return headers;
	}
	public void setHeaders(Properties headers) {
		this.headers = headers;
	}
	
	public QName getDataType() {
		return dataType;
	}
	public void setDataType(QName dataType) {
		this.dataType = dataType;
	}
//	public String getValueAsString() {
//		return valueAsString;
//	}
//	public void setValueAsString(String variableValue) {
//		this.valueAsString = variableValue;
//	}
	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}
	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * @return the processType
	 */
	public String getProcessType() {
		return processType;
	}
	/**
	 * @param processType the processType to set
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public Object getPayload(){
		return value;
	}
	
	public void setPayload(Object value){
		this.value = value;
	}
//	public String getMainSchemaFileName() {
//		return mainSchemaFileName;
//	}
//	public void setMainSchemaFileName(String mainSchemaFileName) {
//		this.mainSchemaFileName = mainSchemaFileName;
//	}
//	public Map<String, String> getSchemas() {
//		return schemas;
//	}
//	public void setSchemas(Map<String, String> schemas) {
//		this.schemas = schemas;
//	}
	public String getProcessElementId() {
		return processElementId;
	}
	public void setProcessElementId(String processElementId) {
		this.processElementId = processElementId;
	}
	
	
	
//	public String getJavaClassName() {
//		return javaClassName;
//	}
//	public void setJavaClassName(String javaClassName) {
//		this.javaClassName = javaClassName;
//	}

}
