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

import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.taskinstance.DynamicAssignmentHandler;
import org.fireflow.kernel.KernelException;

/**
 * WorkflowSession是所有工作流操作的入口，相当于Jdbc的connection对象。
 * 通过WorkflowSession可以创建IProcessInstance，查询ITaskInstance,IWorkItem等等。
 * 该类采用了Template设计模式
 * ，所有的方法最终都是调用IWorkflowSession.execute(IWorkflowSessionCallback callback)实现的。
 * 这样做的好处是，有很多公共的功能都可以放在execute中，不需要每个方法重写一遍。<br>
 * 缺省的WorkflowSession提供的方法不多，您可以利用IWorkflowSession.execute(
 * IWorkflowSessionCallback callback)实现更多的流程操作。
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public interface IWorkflowSession {
	public RuntimeContext getRuntimeContext();

	/**
	 * 模板方法
	 * 
	 * @param callbak
	 * @return 
	 *         返回的对象一般有三种情况：1、单个工作流引擎API对象（如IProcessInstance,ITaskInstance,IworkItem等
	 *         ）<br>
	 *         2、工作流引擎对象的列表、3、null
	 * @throws org.fireflow.engine.EngineException
	 * @throws org.fireflow.kenel.KenelException
	 */
	public Object execute(IWorkflowSessionCallback callbak)
			throws EngineException, KernelException;

	
	
    /******************************************************************************/
    /************                                                        **********/
    /************            从process instance 相关的API                **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/		
	
	/**
	 * 创建流程实例。如果流程定义了流程变量(DataField）,则自动把流程变量初始化成流程实例变量。 建议改用
	 * createProcessInstance(String workflowProcessName,String creatorId)。
	 * 
	 * @param workflowProcessName
	 *            流程的Name属性，在Fire workflow中，流程的Name和Id是相等的而且都是唯一的。
	 * @return 新创建的流程实例
	 * @throws org.fireflow.engine.EngineException
	 * @throws org.fireflow.kenel.KenelException
	 * @deprecated
	 */
	public IProcessInstance createProcessInstance(String workflowProcessName)
			throws EngineException, KernelException;

	/**
	 * 创建流程实例。如果流程定义了流程变量(DataField）,则自动把流程变量初始化成流程实例变量。
	 * 
	 * @param workflowProcessName
	 *            流程的Name属性，在Fire workflow中，流程的Name和Id是相等的而且都是唯一的。
	 * @param parentTaskInstance
	 *            父TaskInstance
	 * @return 新创建的流程实例
	 * @throws org.fireflow.engine.EngineException
	 * @throws org.fireflow.kenel.KenelException
	 */
	public IProcessInstance createProcessInstance(String workflowProcessName,
			ITaskInstance parentTaskInstance) throws EngineException,
			KernelException;

	/**
	 * 创建流程实例，并将creatorId填充到ProcessInstance的CreatorId字段。
	 * 如果流程定义了流程变量(DataField）,则自动把流程变量初始化成流程实例变量。
	 * 
	 * @param workflowProcessName
	 *            流程定义的名称
	 * @param creatorId
	 *            创建者Id
	 * @return 新创建的流程实例
	 */
	public IProcessInstance createProcessInstance(String workflowProcessName,
			String creatorId) throws EngineException, KernelException;

	
	
    /**
     * 终止流程实例。将流程实例、活动的TaskInstance、活动的WorkItem的状态设置为CANCELED；并删除所有的token
     * @param processInstanceId
     */
    public IProcessInstance abortProcessInstance(String processInstanceId)throws EngineException;

    /**
     * 挂起流程实例
     * @param processInstance
     */
    public IProcessInstance suspendProcessInstance(String processInstanceId)throws EngineException;

    /**
     * 恢复被挂起的流程实例
     * @param processInstance
     */
    public IProcessInstance restoreProcessInstance(String processInstanceId)throws EngineException;
    
    
	
    /******************************************************************************/
    /************                                                        **********/
    /************            从IPersistenceService引出的API              **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/	
	
    /**
     * 通过ID获得ProcessInstance对象。
     * @param id processInstance.id
     * @return process instance
     */
	public IProcessInstance findProcessInstanceById(String id);
	
    /**
     * 查找并返回同一个业务流程的所有实例
     * @param processId The id of the process definition.
     * @return A list of processInstance
     */	
	public List<IProcessInstance> findProcessInstancesByProcessId(String processId);
	
	
	
    /**
     * 查找并返回同一个指定版本业务流程的所有实例
     * @param processId The id of the process definition.
     * @return A list of processInstance
     */	
    public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(String processId,Integer version);
	
	/**
	 * 通过workitem的id查找到该workitem
	 * 
	 * @param id
	 *            workitem id
	 * @return
	 */
	public IWorkItem findWorkItemById(String id);

	/**
	 * 查找某个操作员的所有工单
	 * 
	 * @param actorId
	 *            操作员Id
	 * @return
	 */
	public List<IWorkItem> findMyTodoWorkItems(String actorId);

	/**
	 * 返回某个操作员在流程实例processInstanceId中的所有工单
	 * 
	 * @param actorId
	 *            操作员Id
	 * @param processInstanceId
	 *            流程实例Id
	 * @return
	 */
	public List<IWorkItem> findMyTodoWorkItems(String actorId,
			String processInstanceId);

	/**
	 * 返回某个操作员在某个流程某个任务上的所有工单
	 * 
	 * @param actorId
	 *            操作员Id
	 * @param processId
	 *            流程Id
	 * @param taskId
	 *            任务Id
	 * @return
	 */
	public List<IWorkItem> findMyTodoWorkItems(String actorId,
			String processId, String taskId);

	/**
	 * 根据任务实例的Id查找任务实例。
	 * 
	 * @param id
	 * @return
	 */
	public ITaskInstance findTaskInstanceById(String id);

    /**
     * 查询流程实例的所有的TaskInstance,如果activityId不为空，则返回该流程实例下指定环节的TaskInstance<br/>
     * (Engine没有引用到该方法，提供给业务系统使用，20090303)
     * @param processInstanceId  the id of the process instance
     * @param activityId  if the activityId is null, then return all the taskinstance of the processinstance;
     * @return
     */	
	public List<ITaskInstance> findTaskInstancesForProcessInstance(java.lang.String processInstanceId, String activityId);
	
	
    /******************************************************************************/
    /************                                                        **********/
    /************            workItem 相关的API                          **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/
	
    /**
     * 签收工单。如果任务实例的分配模式是ANY，则同一个任务实例的其他工单将被删除。
     * 如果任务是里的分配模式是ALL，则此操作不影响同一个任务实例的其他工单的状态。<br/>
     * 如果签收成功，则返回一个新的IWorkItem对象。<br/>
     * 如果签收失败，则返回null。<br/>
     * 例如：同一个TaskInstance被分配给Actor_1和Actor_2，且分配模式是ANY，即便Actor_1和Actor_2同时执行
     * 签收操作，也必然有一个人签收失败。系统对这种竞争性操作进行了同步。<br/>
     * 该方法和IWorkItem.claim()是等价的。
     * @param workItemId 被签收的工单的Id
     * @return 如果签收成功，则返回一个新的IWorkItem对象；否则返回null
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     * 
     */
	public IWorkItem claimWorkItem(String workItemId) throws EngineException, KernelException ;
	
	
	/**
	 * 对已经结束的工单执行取回操作。<br/>
	 * 只有满足如下约束才能正确执行取回操作：<br/>
	 * 1、下一个环节没有Tool类型或者Subflow类型的Task；<br/>
	 * 2、下一个环节Form类型的TaskInstance没有被签收。<br/>
	 * 如果在本WorkItem成功执行了jumpTo操作或者loopTo操作，只要满足上述条件，也可以
     * 成功执行withdraw。<br/>
	 * 该方法和IWorkItem.withdraw()等价
	 * @return 如果取回成功，则创建一个新的WorkItem 并返回该WorkItem
	 * @throws EngineException
	 * @throws KernelException
	 */
    public IWorkItem withdrawWorkItem(String workItemId)throws EngineException, KernelException;

    /**
     * 执行“拒收”操作，可以对已经签收的或者未签收的WorkItem拒收。<br/>
     * 该操作必须满足如下条件：<br/>
     * 1、前驱环节中没有没有Tool类型和Subflow类型的Task；<br/>
     * 2、没有合当前TaskInstance并行的其他TaskInstance；<br/>
     * @throws EngineException
     * @throws KernelException
     */
    public void rejectWorkItem(String workItemId)throws EngineException, KernelException;

    /**
     * 执行“拒收”操作，可以对已经签收的或者未签收的WorkItem拒收。<br/>
     * 该操作必须满足如下条件：<br/>
     * 1、前驱环节中没有没有Tool类型和Subflow类型的Task；<br/>
     * 2、没有合当前TaskInstance并行的其他TaskInstance；<br/>
     * @throws EngineException
     * @throws KernelException
     */    
    public void rejectWorkItem(String workItemId,String comments)throws EngineException, KernelException;
    
    
    
    /**
     * 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
     * 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
     * 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
     * 则结束当前TaskInstance<br/>
     * 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
     * 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
     * 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void completeWorkItem(String workItemId) throws EngineException, KernelException;

    /**
     * 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
     * 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
     * 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
     * 则结束当前TaskInstance<br/>
     * 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
     * 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
     * 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
     * @param workItemId
     * @param comments
     * @throws EngineException
     * @throws KernelException
     */
    public void completeWorkItem(String workItemId ,String comments)throws EngineException, KernelException;
    
    /**
     * 结束当前WorkItem；并由工作流引擎根据流程定义决定下一步操作。引擎的执行规则如下<br/>
     * 1、工作流引擎首先判断该WorkItem对应的TaskInstance是否可以结束。
     * 如果TaskInstance的assignment策略为ANY，或者，assignment策略为ALL且它所有的WorkItem都已经完成
     * 则结束当前TaskInstance<br/>
     * 2、判断TaskInstance对应的ActivityInstance是否可以结束。如果ActivityInstance的complete strategy
     * 为ANY，或者，complete strategy为ALL且他的所有的TaskInstance都已经结束，则结束当前ActivityInstance<br/>
     * 3、根据流程定义，启动下一个Activity，并创建相关的TaskInstance和WorkItem
     * @param workItemId
     * @param dynamicAssignmentHandler 用于指定下一个岗位的操作员。
     * @param comments
     * @throws EngineException
     * @throws KernelException
     */
    public void completeWorkItem(String workItemId, DynamicAssignmentHandler dynamicAssignmentHandler,String comments) throws EngineException, KernelException;

    
    /**
     * 结束当前WorkItem,启动指定的Activity，引擎调用流程设计时指定的AssignmentHandler分配任务。<br/>
     * 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
     * 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
     * 2)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 3)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * @param targetActivityId 将要被启动的ActivityId
     * @throws org.fireflow.engine.EngineException 
     * @throws org.fireflow.kenel.KenelException
     */
    public void completeWorkItemAndJumpTo(String workItemId ,String targetActivityId) throws EngineException, KernelException;

    public void completeWorkItemAndJumpTo(String workItemId,String targetActivityId,String comments) throws EngineException, KernelException;


    /**
     * 结束当前WorkItem，启动指定的Activity，引擎将新的TaskInstance分配给nextActorIds<br/>
     * 只有满足如下条件的情况下，该方法才能成功执行，否则抛出EngineException，流程状态恢复到调用该方法之前的状态。<br/>
     * 1)当前Activity和即将启动的Acitivty必须在同一个执行线上<br/>
     * 2)如果当前Task的assignment为Task.ALL且本WorkItem结束后仍然不能使得TaskInstance结束，引擎将抛出EngineException异常<br/>
     * 3)如果当前的Activity包含多个TaskInstance，且当前TaskInstance结束后ActivityInstance仍然不能结束，引擎将抛出EngineException异常
     * @param targetActivityId
     * @param nextActorIds
     * @param needClaim 是否需要签收
     * @throws org.fireflow.engine.EngineException
     * @throws org.fireflow.kenel.KenelException
     */
    public void completeWorkItemAndJumpTo(String workItemId,String targetActivityId, DynamicAssignmentHandler dynamicAssignmentHandler,String comments) throws EngineException, KernelException;
    

    /**
     * 将工单委派给其他人，自己的工单变成CANCELED状态
     * @param actorId 接受任务的操作员Id
     */    
    public IWorkItem reasignWorkItemTo(String workItemId,String actorId) throws EngineException;
    
    /**
     * 将工单委派给其他人，自己的工单变成CANCELED状态。返回新创建的工单
     * @param actorId 接受任务的操作员Id
     * @param comments 相关的备注信息
     * @return 新创建的工单
     */    
    public IWorkItem reasignWorkItemTo(String workItemId,String actorId,String comments) throws EngineException;
	
	
    /******************************************************************************/
    /************                                                        **********/
    /************            TaskInstance相关的 API                      **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/		
	/**
	 * 将TaskInstance挂起
	 * @param taskInstanceId
	 */
    public ITaskInstance suspendTaskInstance(String taskInstanceId)throws EngineException;
    
    /**
     * 恢复被挂起的TaskInstance
     * @param taskInstanceId
     * @return
     * @throws EngineException
     */
    public ITaskInstance restoreTaskInstance(String taskInstanceId)throws EngineException;
    
    /******************************************************************************/
    /************                                                        **********/
    /************            杂项 API                                    **********/    
    /************                                                        **********/
    /************                                                        **********/    
    /******************************************************************************/	
	public void setWithdrawOrRejectOperationFlag(boolean b);

	
	public boolean isInWithdrawOrRejectOperation();

	
	/**
	 * 设置一个动态任务分配处理器。
	 * @param dynamicAssignmentHandler
	 */
	public void setDynamicAssignmentHandler(
			DynamicAssignmentHandler dynamicAssignmentHandler);
	
	
	/**
	 * 类似HttpServletRequest的setAttribute,用于流程操作中传递参数。
	 * @param name
	 * @param attr
	 */
	public void setAttribute(String name ,Object attr);
	
	/**
	 * 类似HttpServletRequest的getAttribute,用于流程操作中传递参数。
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name);
	
	/**
	 * 清空workflowSession中的所有参数
	 */
	public void clearAttributes();
	
	/**
	 * 清空workflowSession中指定名称的参数
	 * @param name
	 */
	public void removeAttribute(String name);
}
