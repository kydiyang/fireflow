/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.modules.process;

import java.io.InputStream;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.servicedef.ServiceDef;

/**
 * 流程定义服务。
 * @author 非也，nychen2000@163.com
 *
 */
public interface ProcessUtil extends RuntimeContextAware,EngineModule {
	/**
	 * 返回流程的入口元素的Id，例如：FPDL20流程返回的是main_subflow的Id
	 * @param workflowProcessId
	 * @param version
	 * @param processType
	 * @return
	 */
	public String getProcessEntryId(String workflowProcessId, int version,String processType);
	
	public String serializeProcess2Xml(Object process) throws InvalidModelException;
	
	public Object deserializeXml2Process(InputStream inStream)throws InvalidModelException;
	
	public ProcessRepository serializeProcess2ProcessRepository(Object process)throws InvalidModelException;

	/**
	 * 获得ServiceBinding对象
	 * @param processKey processId,processType,version组成的processKey对象
	 * @param subflowId 对于没有subflowId的模型，该值等于processId
	 * @param activityId 需要获取servicebinding的activity的Id
	 * @return
	 * @throws InvalidModelException
	 */
    public ServiceBinding getServiceBinding(ProcessKey processKey,String subflowId, String activityId)throws InvalidModelException;
    
    /**
     * 根据serviceBinding.getServiceId()和activity，找到ServiceDef对象
     * @param activity
     * @param serviceBinding
     * @return
     */
    public ServiceDef getServiceDef(ActivityInstance activityInstance,Object activity,String serviceId);
    /**
     * 获得resource binding对象
     * @param processKey processId,processType,version组成的processKey对象
     * @param subflowId 对于没有subflowId的模型，该值等于processId
     * @param activityId 需要获取resourcebinding的activity的Id
     * @return
     * @throws InvalidModelException
     */
    public ResourceBinding getResourceBinding(ProcessKey processKey,String subflowId, String activityId)throws InvalidModelException;
    
    /**
     * 根据条件查找Activity
     * @param processKey
     * @param subflowId
     * @param activityId
     * @return
     * @throws InvalidModelException
     */
    public Object getActivity(ProcessKey processKey,String subflowId, String activityId)throws InvalidModelException;
    
    /**
     * 提取子流程或者Activity的流程变量（Property对象）。
     * @param processKey 
     * @param workflowElementId 在FPDL2.0中可以是subflow或者activity的id
     * @param propertyName property的名字
     * @return
     */
    public Property getProperty(ProcessKey processKey,String workflowElementId, String propertyName)throws InvalidModelException;
    
//    public Schema getSchema
}
