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
package org.fireflow.engine;

import java.util.List;
import org.fireflow.kenel.KenelException;

/**
 * WorkflowSession是所有工作流操作的入口，相当于Jdbc的connection对象。
 * 通过WorkflowSession可以创建IProcessInstance，查询ITaskInstance,IWorkItem等等。
 * 该类采用了Template设计模式，所有的方法最终都是调用IWorkflowSession.execute(IWorkflowSessionCallback callback)实现的。
 * 这样做的好处是，有很多公共的功能都可以放在execute中，不需要每个方法重写一遍。<br>
 * 缺省的WorkflowSession提供的方法不多，您可以利用IWorkflowSession.execute(IWorkflowSessionCallback callback)实现更多的流程操作。
 * @author 非也,nychen2000@163.com
 * 
 */
public interface IWorkflowSession {
        public RuntimeContext getRuntimeContext();

        /**
         * 模板方法
         * @param callbak
         * @return 返回的对象一般有三种情况：1、单个工作流引擎API对象（如IProcessInstance,IworkItem等）<br>
         * 2、工作流引擎对象的列表、3、null
         * @throws org.fireflow.engine.EngineException
         * @throws org.fireflow.kenel.KenelException
         */
	public Object execute(IWorkflowSessionCallback callbak)
			throws EngineException, KenelException;

        /**
         * 创建流程实例。如果流程定义了流程变量(DataField）,则自动把流程变量初始化成流程实例变量。
         * @param workflowProcessName 流程的Name属性，在Fire workflow中，流程的Name和Id是相等的而且都是唯一的。
         * @return 新创建的流程实例
         * @throws org.fireflow.engine.EngineException
         * @throws org.fireflow.kenel.KenelException
         */
	public IProcessInstance createProcessInstance(String workflowProcessName)
			throws EngineException,KenelException;
	
        /**
         * 通过workitem的id查找到该workitem
         * @param id workitem id 
         * @return
         */
	public IWorkItem findWorkItemById(String id);
        
        /**
         * 查找某个操作员的所有工单
         * @param actorId 操作员Id
         * @return
         */
        public List<IWorkItem> findMyTodoWorkItems(String actorId);
        
        /**
         * 返回某个操作员在流程实例processInstanceId中的所有工单
         * @param actorId 操作员Id
         * @param processInstanceId 流程实例Id
         * @return
         */
        public List<IWorkItem> findMyTodoWorkItems(String actorId,String processInstanceId);
        
        /**
         * 返回某个操作员在某个流程某个任务上的所有工单
         * @param actorId 操作员Id
         * @param processId 流程Id
         * @param taskId 任务Id
         * @return
         */
        public List<IWorkItem> findMyTodoWorkItems(String actorId,String processId,String taskId);
	
        /**
         * 根据任务实例的Id查找任务实例。
         * @param id
         * @return
         */
	public ITaskInstance findTaskInstanceById(String id);
}
