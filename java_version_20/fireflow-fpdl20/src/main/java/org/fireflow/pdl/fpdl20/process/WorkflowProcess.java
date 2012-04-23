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
package org.fireflow.pdl.fpdl20.process;

import java.util.List;

import org.fireflow.model.ModelElement;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl20.diagram.Diagram;


/**
 * 业务过程。<br/>
 * 这是Fire workflow 模型的最顶层元素，一个业务过程可以包含多个工作流程，也可以调用外部的业务过程。
 * <br/>
 * @author 非也,nychen2000@163.com
 * 
 */
public interface WorkflowProcess extends ModelElement{
	
	public static final String MAIN_FLOW_NAME="main_flow";
	
	/**
	 * 返回业务过程的运行时间，业务过程的duration等于main_flow的duration
	 * @return
	 */
	public Duration getDuration();
	
	
	/**
	 * 返回该流程所述的业务类别，业务类别字段用于流程管理目的，可将流程按照业务类别进行显示
	 * @return
	 */
	public String getBizCategory();
	
	public void setBizCategory(String bizCategory);
	
	/**
	 * 流程定义文件在classpath中的位置
	 * @return
	 */
	public String getClasspathUri();
	
	public void setClasspathUri(String classPathUri);
	

	
	/**
	 * 返回主流程的流程Id
	 * @return
	 */
	public Subflow getMainflow();
	
	/**
	 * 根据subflowId 返回subflow
	 * @param workflowId
	 * @return
	 */
	public Subflow getSubflow(String workflowId);
	
	/**
	 * 向WorkflowProcess中增加一个subflow
	 * @param flow
	 */
	public void addSubflow(Subflow flow);
	
	/**
	 * 获得流程所有的subflows，包括引入的外部流程的main_flow
	 * @return
	 */
	public List<Subflow> getSubflows();
	
	/**
	 * 获得本WorkflowProcess内部定义的所有subflow
	 * @return
	 */
	public List<Subflow> getLocalSubflows();
	
	/**
	 * 根据WorkflowElmentId查找对应的Workflow Element; 
	 * Workflow Element可以是Subflow,StartNode,Activity,EndNode,Router,Transition。
	 * @param workflowElementId
	 * @return
	 */
	public WorkflowElement findWorkflowElementById(String workflowElementId);
	
	/**
	 * 获得所有的Service，包括import进来的
	 * @return
	 */
	public List<ServiceDef> getServices();
	
	/**
	 * 获得在本WorkflowProcess定义的所有Service。
	 * @return
	 */
	public List<ServiceDef> getLocalServices();
	
	public ServiceDef getService(String serviceId);
	
	/**
	 * 定义一个局部Service到本业务流程
	 * @param svc
	 */
	public void addService(ServiceDef svc);
	
	/**
	 * 获得该流程所有的资源定义，包括import进来的。
	 * @return
	 */
	public List<ResourceDef> getResources();
	
	public List<ResourceDef> getLocalResources();
	
	public ResourceDef getResource(String resourceId);
	/**
	 * 定义一个局部的resource到本业务流程
	 * @param resource
	 */
	public void addResource(ResourceDef resource);

	
	
	/**
	 * 根据location值获得Import对象
	 * @param location
	 * @return
	 */
	public Import getImportByLocation(String location);
	
	/**
	 * 服务import列表
	 * @return
	 */
	public List<Import<ServiceDef>> getProcessImportForServices();
	
	/**
	 * 资源import列表
	 * @return
	 */
	public List<Import<ResourceDef>> getProcessImportForResources();
	
	/**
	 * 流程的namespace
	 * 2012-02-26，该属性放在service定义里面
	 * @return
	 */
//	public String getTargetNamespace();
	
	public List<Diagram> getDiagrams();
	public void addDiagram(Diagram diagram);
	public Diagram getDiagramBySubflowId(String subflowId);
}
