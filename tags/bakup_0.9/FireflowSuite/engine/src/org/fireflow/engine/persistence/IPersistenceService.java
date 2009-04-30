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
package org.fireflow.engine.persistence;

import java.util.List;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.IToken;

/**
 * 数据存储接口，<br>
 * (目前该接口的方法还不够，下一步增加方法，把hibernate的QBC和QBE直接集成进来。)
 * @author 非也,nychen2000@163.com
 *
 */
public interface IPersistenceService extends IRuntimeContextAware{
    /********************Persistence methodes for ProcessInstance ***********************/
    /**
     * Save or update processinstance. 
     * If the processInstance.id is null then insert a new process instance record
     * and genarate a new id for it (save operation);
     * otherwise update the existent one.
     * @param processInstance
     */
    public void saveOrUpdateProcessInstance(IProcessInstance processInstance);
    
    /**
     * Find the process instance by id
     * @param id processInstance.id
     * @return process instance 
     */
    public IProcessInstance findProcessInstanceById(String id);
    
    /**
     * Find all process instances for the same process definition.
     * @param processId The id of the process definition.
     * @return A list of processInstance
     */
    public List<IProcessInstance> findProcessInstanceByProcessId(String processId);
    
    
    
    /*************************Persistence methods for TaskInstance ***********************/
    /**
     * Save or update task instance. If the taskInstance.id is null then insert a new task instance record
     * and generate a new id for it ;
     * otherwise update the existent one. 
     * @param taskInstance
     */
    public void saveOrUpdateTaskInstance(ITaskInstance taskInstance);

    /**
     * Find the task instance by id 
     * @param id
     * @return
     */
    public ITaskInstance findTaskInstanceById(String id);
    
    /**
     * Find all the task instances for process instance, and the activityId of the taskinstance must equals to the second arguments. 
     * 
     * @param processInstanceId  the id of the process instance
     * @param activityId  if the activityId is null, then return all the taskinstance of the processinstance;
     * @return
     */
    public List<ITaskInstance> findTaskInstancesForProcessInstance(java.lang.String processInstanceId, String activityId);
    
   
    
    /*********************************Persistence methods for workitem*************************/
    /**
     * save or update workitem
     * @param workitem
     */
    public void saveOrUpdateWorkItem(IWorkItem workitem);

    /**
     * Find workItem by id
     * @param id
     * @return
     */
    public IWorkItem findWorkItemById(String id);
    
    
    /**
     * Find all workitems for task
     * @param taskid
     * @return
     */
    public List<IWorkItem> findWorkItemForTask(String taskid);    
    
    /**
     * Find all workitem for taskinstance
     * @param taskInstance
     * @return
     */
    public List<IWorkItem> findWorkItemsForTaskInstance(String taskInstanceId);
    
    /**
     * 根据操作员的Id返回其待办工单。如果actorId==null，则返回系统所有的待办任务
     * 待办工单是指状态等于INITIALIZED或STARTED工单
     * @param actorId
     * @return
     */
    public List<IWorkItem> findTodoWorkItems(String actorId);
    
    /**
     * 查找操作员在某个流程实例中的待办工单。
     * 如果processInstanceId为空，则等价于调用findTodoWorkItems(String actorId)
     * 待办工单是指状态等于INITIALIZED或STARTED工单
     * @param actorId
     * @param processInstanceId
     * @return
     */
    public List<IWorkItem> findTodoWorkItems(String actorId,String processInstanceId);
    
    /**
     * 查找操作员在某个流程某个任务上的待办工单。
     * actorId，processId，taskId都可以为空（null或者""）,为空的条件将被忽略
     * 待办工单是指状态等于INITIALIZED或STARTED工单
     * @param actorId
     * @param processId
     * @param taskId
     * @return
     */
    public List<IWorkItem> findTodoWorkItems(String actorId,String processId,String taskId);
    
    /**
     * 根据操作员的Id返回其已办工单。如果actorId==null，则返回系统所有的已办任务
     * 已办工单是指状态等于COMPLETED或CANCELED的工单
     * @param actorId
     * @return
     */
    public List<IWorkItem> findHaveDoneWorkItems(String actorId);
    
    /**
     * 查找操作员在某个流程实例中的已办工单。
     * 如果processInstanceId为空，则等价于调用findHaveDoneWorkItems(String actorId)
     * 已办工单是指状态等于COMPLETED或CANCELED的工单
     * @param actorId
     * @param processInstanceId
     * @return
     */
    public List<IWorkItem> findHaveDoneWorkItems(String actorId,String processInstanceId);
    
    /**
     * 查找操作员在某个流程某个任务上的已办工单。
     * actorId，processId，taskId都可以为空（null或者""）,为空的条件将被忽略
     * 已办工单是指状态等于COMPLETED或CANCELED的工单
     * @param actorId
     * @param processId
     * @param taskId
     * @return
     */
    public List<IWorkItem> findHaveDoneWorkItems(String actorId,String processId,String taskId);
        

    /**
     * 删除处于初始化状态的workitem,
     * 此方法用于签收Workitem时，删除其他Actor的WorkItem
     * @param taskInstanceId
     */
    public void deleteWorkItemsInInitializedState(String taskInstanceId);
    /*************************Persistence methods for joinpoint*********************/
    /**
     * Save joinpoint
     * @param joinPoint
     */
    public void saveOrUpdateJoinPoint(IJoinPoint joinPoint);


    /**
     * Find the joinpoint id
     * @param id
     * @return
     */
    public IJoinPoint findJoinPointById(String id);
    
    /**
     * Find all the joinpoint of the process instance, and the synchronizerId of the joinpoint must equals to the seconds argument.
     * @param processInstanceId
     * @param synchronizerId if the synchronizerId is null ,then all the joinpoint of the process instance will be returned.
     * @return
     */
    public List<IJoinPoint> findJoinPointsForProcessInstance(String processInstanceId, String synchronizerId);

    
    /************************Persistence methods for Token**************************;
    /**
     * Save token
     * @param token
     */
    public void saveOrUpdateToken(IToken token);


    /**
     * Find all the tokens for process instance ,and the nodeId of the token must equals to the second argument.
     * @param processInstanceId the id of the process instance
     * @param nodeId if the nodeId is null ,then return all the tokens of the process instance.
     * @return
     */
    public List<IToken> findTokensForProcessInstance(String processInstanceId, String nodeId);


    /**
     * Save or update the workflow definition. The version will be increased automatically when insert a new record.
     * @param workflowDef
     */
    public void saveOrUpdateWorkflowDefinition(WorkflowDefinition workflowDef);
     
    /**
     * Find the workflow definition by id .
     * @param id
     * @return
     */
    public WorkflowDefinition findWorkflowDefinitionById(String id);
    
    /**
     * Find workflow definition by workflow process id and version
     * @param processId
     * @param version
     * @return
     */
    public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersion(String processId,int version);
    
    /**
     * Find the latest version of the workflow definition.
     * @param processId the workflow process id 
     * @return
     */
    public WorkflowDefinition findLatestVersionOfWorkflowDefinitionByProcessId(String processId);
            
    /**
     * Find all the workflow definitions for the workflow process id.
     * @param processId
     * @return
     */
    public List<WorkflowDefinition> findWorkflowDefinitionByProcessId(String processId);

    /**
     * Find all of the latest version of workflow definitions.
     * @return
     */
    public List<WorkflowDefinition> findAllLatestVersionOfWorkflowDefinition();
    
    /**
     * Find the latest version of the workflowdefinition
     * @param processId
     * @return the version number ,null if there is no workflow definition stored in the DB.
     */
    public Integer findTheLatestVersionOfWorkflowDefinition(String processId);
}
