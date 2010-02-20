/**
 * Copyright 2003-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 * @author 非也,nychen2000@163.com
 * @Revision to .NET 无忧 lwz0721@gmail.com 2010-02
 */
using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Engine.Beanfactory;
using FireWorkflow.Net.Engine.Calendar;
using FireWorkflow.Net.Engine.Event;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Kernel.Impl;
using FireWorkflow.Net.Base;

namespace FireWorkflow.Net.Engine.Taskinstance
{
    /// <summary>
    /// 缺省的任务实例管理器实现
    /// </summary>
    public class BasicTaskInstanceManager : ITaskInstanceManager
    {
        public ITaskInstanceCreator DefaultTaskInstanceCreator { get; set; }
        public ITaskInstanceRunner DefaultFormTaskInstanceRunner { get; set; }
        public ITaskInstanceRunner DefaultSubflowTaskInstanceRunner { get; set; }
        public ITaskInstanceRunner DefaultToolTaskInstanceRunner { get; set; }
        public ITaskInstanceCompletionEvaluator DefaultFormTaskInstanceCompletionEvaluator { get; set; }
        public ITaskInstanceCompletionEvaluator DefaultToolTaskInstanceCompletionEvaluator { get; set; }
        public ITaskInstanceCompletionEvaluator DefaultSubflowTaskInstanceCompletionEvaluator { get; set; }

        public ITaskInstanceEventListener DefaultTaskInstanceEventListener { get; set; }

        public RuntimeContext RuntimeContext { get; set; }

        //fireflow.engine.taskinstance.ITaskInstanceManager#archiveTaskInstances(org.fireflow.kenel.IActivityInstance)
        public void archiveTaskInstances(IActivityInstance activityInstance)
        {
            // TODO Auto-generated method stub
        }

        //fireflow.engine.taskinstance.ITaskInstanceManager#createTaskInstances(org.fireflow.kenel.IActivityInstance)
        public void createTaskInstances(IToken token, IActivityInstance activityInstance) //throws EngineException, KernelException 
        {
            // TODO Auto-generated method stub
            Activity activity = activityInstance.getActivity();
            IPersistenceService persistenceService = RuntimeContext.PersistenceService;
            ICalendarService calService = RuntimeContext.getCalendarService();

            IProcessInstance processInstance = token.ProcessInstance;
            WorkflowSession workflowSession = (WorkflowSession)((IWorkflowSessionAware)processInstance).CurrentWorkflowSession;

            if (workflowSession == null)
            {
                throw new EngineException(token.ProcessInstance,
                        activityInstance.getActivity(),
                        "The workflow session in process instance can NOT be null");
            }

            int createdTaskInstanceCount = 0;
            for (int i = 0; i < activity.getTasks().Count; i++)
            {
                Task task = activity.getTasks()[i];
                // 1、创建Task实例，并设置工作流系统定义的属性
                ITaskInstance taskInstance = this.createTaskInstance(workflowSession, processInstance, task,
                        activity);

                if (taskInstance == null)
                {
                    continue;
                }
                createdTaskInstanceCount = createdTaskInstanceCount + 1;

                TaskTypeEnum taskType = task.TaskType;
                ((TaskInstance)taskInstance).TaskType=taskType;
                ((TaskInstance)taskInstance).StepNumber=token.StepNumber;
                //            ((TaskInstance) taskInstance).TokenId=token.Id;
                ((TaskInstance)taskInstance).ProcessInstanceId=processInstance.getId();
                ((TaskInstance)taskInstance).ProcessId=processInstance.getProcessId();
                ((TaskInstance)taskInstance).Version=processInstance.getVersion();
                ((TaskInstance)taskInstance).ActivityId=activity.Id;
                if (TaskTypeEnum.FORM==taskType)
                {
                    ((TaskInstance)taskInstance).AssignmentStrategy=((FormTask)task).AssignmentStrategy;
                    ((TaskInstance)taskInstance).CanBeWithdrawn=true;
                }
                else
                {
                    ((TaskInstance)taskInstance).CanBeWithdrawn=false;
                }
                ((TaskInstance)taskInstance).CreatedTime=calService.getSysDate();
                ((TaskInstance)taskInstance).DisplayName=task.DisplayName;
                ((TaskInstance)taskInstance).Name=task.Name;

                ((TaskInstance)taskInstance).State=TaskInstanceStateEnum.INITIALIZED;

                ((TaskInstance)taskInstance).TaskId=task.Id;

                ((TaskInstance)taskInstance).FromActivityId=token.FromActivityId;

                ((IRuntimeContextAware)taskInstance).RuntimeContext = this.RuntimeContext;
                ((IWorkflowSessionAware)taskInstance).CurrentWorkflowSession=workflowSession;
                //计算超时
                Duration duration = task.Duration;

                if (duration != null && calService != null)
                {
                    ((TaskInstance)taskInstance).ExpiredTime=calService.dateAfter(calService.getSysDate(), duration);
                }



                // 2、保存实例taskInstance
                persistenceService.saveOrUpdateTaskInstance(taskInstance);

                //3、启动实例
                this.startTaskInstance(workflowSession, processInstance, taskInstance);

            }
            if (createdTaskInstanceCount == 0)
            {
                //如果是空activity，哪么直接结束
                activityInstance.complete(token, null);
            }
        }

        public ITaskInstance createTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance, Task task,
                Activity activity)// throws EngineException 
        {
            //如果loopStrategy为SKIP且Task被执行过，则直接返回null;
            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;
            LoopStrategyEnum loopStrategy = task.LoopStrategy;
            if (LoopStrategyEnum.SKIP == loopStrategy && !currentSession.isInWithdrawOrRejectOperation())
            {
                //检查是否已经执行过的task instance
                Int32 count = persistenceService.getCompletedTaskInstanceCountForTask(processInstance.getId(), task.Id);
                if (count > 0)
                {
                    return null;
                }
            }

            String taskInstanceCreatorName = null;
            ITaskInstanceCreator taskInstanceCreator = null;

            //首先查找Task级别的TaskInstanceCreator
            taskInstanceCreatorName = task.TaskInstanceCreator;
            if (taskInstanceCreatorName != null && !taskInstanceCreatorName.Trim().Equals(""))
            {
                IBeanFactory beanFactory = this.RuntimeContext.getBeanFactory();
                taskInstanceCreator = (ITaskInstanceCreator)beanFactory.getBean(taskInstanceCreatorName);
            }
            //如果没有，则查询流程级别的TaskInstanceCreator
            if (taskInstanceCreator == null)
            {
                taskInstanceCreatorName = processInstance.getWorkflowProcess().TaskInstanceCreator;
                if (taskInstanceCreatorName != null && !taskInstanceCreatorName.Trim().Equals(""))
                {
                    IBeanFactory beanFactory = this.RuntimeContext.getBeanFactory();
                    taskInstanceCreator = (ITaskInstanceCreator)beanFactory.getBean(taskInstanceCreatorName);
                }
            }

            //如果流程定义中也没有指定TaskInstanceCreator,则用缺省的
            if (taskInstanceCreator == null)
            {
                taskInstanceCreator = DefaultTaskInstanceCreator;
            }

            return taskInstanceCreator.createTaskInstance(currentSession, RuntimeContext, processInstance, task, activity);
        }

        /// <summary>
        /// 启动TaskInstance。
        /// 该方法定义为final,不允许子类扩展。
        /// </summary>
        public void startTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance)
        {

            //触发事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(taskInstance);
            e.setWorkflowSession(currentSession);
            e.setProcessInstance(processInstance);
            e.setEventType(TaskInstanceEvent.BEFORE_TASK_INSTANCE_START);
            if (DefaultTaskInstanceEventListener != null)
            {
                DefaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }
            this.fireTaskInstanceEvent(taskInstance, e);

            ((TaskInstance)taskInstance).State=TaskInstanceStateEnum.RUNNING;
            ((TaskInstance)taskInstance).StartedTime=this.RuntimeContext.getCalendarService().getSysDate();
            this.RuntimeContext.PersistenceService.saveOrUpdateTaskInstance(taskInstance);

            Task task = taskInstance.getTask();
            String taskInstanceRunnerName = null;
            ITaskInstanceRunner taskInstanceRunner = null;

            TaskTypeEnum taskType = task.TaskType;

            taskInstanceRunnerName = task.TaskInstanceRunner;
            if (taskInstanceRunnerName != null && !taskInstanceRunnerName.Trim().Equals(""))
            {
                IBeanFactory beanFactory = this.RuntimeContext.getBeanFactory();
                taskInstanceRunner = (ITaskInstanceRunner)beanFactory.getBean(taskInstanceRunnerName);
            }

            if (taskInstanceRunner == null)
            {
                if (TaskTypeEnum.FORM == taskType)
                {
                    taskInstanceRunnerName = processInstance.getWorkflowProcess().FormTaskInstanceRunner;
                }
                else if (TaskTypeEnum.TOOL == taskType)
                {
                    taskInstanceRunnerName = processInstance.getWorkflowProcess().ToolTaskInstanceRunner;
                }
                else if (TaskTypeEnum.SUBFLOW == taskType)
                {
                    taskInstanceRunnerName = processInstance.getWorkflowProcess().SubflowTaskInstanceRunner;
                }
                if (taskInstanceRunnerName != null && !taskInstanceRunnerName.Trim().Equals(""))
                {
                    IBeanFactory beanFactory = this.RuntimeContext.getBeanFactory();
                    taskInstanceRunner = (ITaskInstanceRunner)beanFactory.getBean(taskInstanceRunnerName);
                }
            }

            if (taskInstanceRunner == null)
            {
                if (TaskTypeEnum.FORM == taskType)
                {
                    taskInstanceRunner = DefaultFormTaskInstanceRunner;
                }
                else if (TaskTypeEnum.TOOL == taskType)
                {
                    taskInstanceRunner = DefaultToolTaskInstanceRunner;
                }
                else if (TaskTypeEnum.SUBFLOW == taskType)
                {
                    taskInstanceRunner = DefaultSubflowTaskInstanceRunner;
                }
            }
            if (taskInstanceRunner != null)
            {
                taskInstanceRunner.run(currentSession, this.RuntimeContext, processInstance, taskInstance);
            }
            else
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId, process,
                        taskInstance.TaskId,
                        "无法获取TaskInstanceRunner,TaskId=" + task.Id + ", taskType=" + taskInstance.TaskType);
            }
        }

        /// <summary>
        /// 判断TaskInstance是否可以结束，缺省的判断规则是：没有活动的WorkItem即可结束。
        /// 业务代码可以重载该函数，对特定的Task采取特殊的判断规则。
        /// </summary>
        /// <param name="currentSession"></param>
        /// <param name="runtimeContext"></param>
        /// <param name="processInstance"></param>
        /// <param name="taskInstance"></param>
        /// <returns></returns>
        protected Boolean taskInstanceCanBeCompleted(IWorkflowSession currentSession, RuntimeContext runtimeContext,
                IProcessInstance processInstance, ITaskInstance taskInstance)
        {
            Task task = taskInstance.getTask();
            String taskInstanceCompletionEvaluatorName = null;
            ITaskInstanceCompletionEvaluator taskInstanceCompletionEvaluator = null;

            TaskTypeEnum taskType = task.TaskType;

            taskInstanceCompletionEvaluatorName = task.TaskInstanceCompletionEvaluator;
            if (taskInstanceCompletionEvaluatorName != null && !taskInstanceCompletionEvaluatorName.Trim().Equals(""))
            {
                IBeanFactory beanFactory = runtimeContext.getBeanFactory();
                taskInstanceCompletionEvaluator = (ITaskInstanceCompletionEvaluator)beanFactory.getBean(taskInstanceCompletionEvaluatorName);
            }

            if (taskInstanceCompletionEvaluator == null)
            {
                if (TaskTypeEnum.FORM == taskType)
                {
                    taskInstanceCompletionEvaluatorName = processInstance.getWorkflowProcess().FormTaskInstanceCompletionEvaluator;
                }
                else if (TaskTypeEnum.TOOL == taskType)
                {
                    taskInstanceCompletionEvaluatorName = processInstance.getWorkflowProcess().ToolTaskInstanceCompletionEvaluator;
                }
                else if (TaskTypeEnum.SUBFLOW == taskType)
                {
                    taskInstanceCompletionEvaluatorName = processInstance.getWorkflowProcess().SubflowTaskInstanceCompletionEvaluator;
                }
                if (taskInstanceCompletionEvaluatorName != null && !taskInstanceCompletionEvaluatorName.Trim().Equals(""))
                {
                    IBeanFactory beanFactory = runtimeContext.getBeanFactory();
                    taskInstanceCompletionEvaluator = (ITaskInstanceCompletionEvaluator)beanFactory.getBean(taskInstanceCompletionEvaluatorName);
                }
            }

            if (taskInstanceCompletionEvaluator == null)
            {
                if (TaskTypeEnum.FORM == taskType)
                {
                    taskInstanceCompletionEvaluator = this.DefaultFormTaskInstanceCompletionEvaluator;
                }
                else if (TaskTypeEnum.TOOL == taskType)
                {
                    taskInstanceCompletionEvaluator = this.DefaultToolTaskInstanceCompletionEvaluator;
                }
                else if (TaskTypeEnum.SUBFLOW == taskType)
                {
                    taskInstanceCompletionEvaluator = this.DefaultSubflowTaskInstanceCompletionEvaluator;
                }
            }
            if (taskInstanceCompletionEvaluator != null)
            {
                return taskInstanceCompletionEvaluator.taskInstanceCanBeCompleted(currentSession, runtimeContext, processInstance, taskInstance);
            }
            else
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId, process,
                        taskInstance.TaskId,
                        "无法获取TaskInstanceCompletionEvaluator,TaskId=" + task.Id + ", taskType=" + taskInstance.TaskType);
            }
        }

        protected Boolean activityInstanceCanBeCompleted(ITaskInstance taskInstance)
        {
            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;
            Activity thisActivity = (Activity)taskInstance.getActivity();
            //检查是否有尚未创建的TaskInstance
            if (thisActivity.getTasks().Count > 1)
            {
                List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstanceByStepNumber(taskInstance.ProcessInstanceId, taskInstance.StepNumber);
                if (taskInstanceList == null || taskInstanceList.Count < thisActivity.getTasks().Count)
                {
                    return false;
                }
            }
            if (thisActivity.CompletionStrategy == FormTaskEnum.ALL)
            {
                Int32 aliveTaskInstanceCount4ThisActivity = persistenceService.getAliveTaskInstanceCountForActivity(taskInstance.ProcessInstanceId, taskInstance.ActivityId);

                if (aliveTaskInstanceCount4ThisActivity > 0)
                {
                    return false;//尚有未结束的TaskInstance
                }
                else
                {
                    return true;
                }
            }
            else
            {
                return true;//此处应该不需要判断，因为对于已经结束的Activity已经没有对应的token。所以继续往下执行不会导致逻辑错误。
            }
        }

        /// <summary>
        /// 终止当前TaskInstance，检查是否可以中止当前ActivityInstance，如果可以，
        /// 则结束当前ActivityInstance，并触发targetActivityInstance或后继ActivityInstance
        /// </summary>
        /// <param name="currentSession"></param>
        /// <param name="processInstance"></param>
        /// <param name="taskInstance"></param>
        /// <param name="targetActivityInstance"></param>
        public void completeTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance,
                ITaskInstance taskInstance, IActivityInstance targetActivityInstance)
        {
            //如果TaskInstance处于结束状态，则直接返回
            if (taskInstance.State == TaskInstanceStateEnum.COMPLETED || taskInstance.State == TaskInstanceStateEnum.CANCELED)
            {
                return;
            }
            if (taskInstance.State == TaskInstanceStateEnum.INITIALIZED)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId, process,
                        taskInstance.TaskId,
                        "Complete task insatance failed.The state of the task insatnce[id=" + taskInstance.Id + "] is " + taskInstance.State);
            }
            if (taskInstance.IsSuspended())
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId, process,
                        taskInstance.TaskId,
                        "Complete task insatance failed. The task instance [id=" + taskInstance.Id + "] is suspended");
            }

            if (targetActivityInstance != null)
            {
                ((TaskInstance)taskInstance).TargetActivityId=targetActivityInstance.getActivity().Id;
            }

            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;

            //第一步，首先结束当前taskInstance
            if (!this.taskInstanceCanBeCompleted(currentSession, this.RuntimeContext, processInstance, taskInstance))
            {
                return;
            }
            ((TaskInstance)taskInstance).State=TaskInstanceStateEnum.COMPLETED;
            ((TaskInstance)taskInstance).CanBeWithdrawn=false;
            ((TaskInstance)taskInstance).EndTime=RuntimeContext.getCalendarService().getSysDate();
            persistenceService.saveOrUpdateTaskInstance(taskInstance);
            //触发相应的事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(taskInstance);
            e.setWorkflowSession(currentSession);
            e.setProcessInstance(processInstance);
            e.setEventType(TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE);
            if (this.DefaultTaskInstanceEventListener != null)
            {
                this.DefaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }

            this.fireTaskInstanceEvent(taskInstance, e);

            //第二步，检查ActivityInstance是否可以结束
            if (!activityInstanceCanBeCompleted(taskInstance))
            {
                return;
            }

            //第三步，尝试结束对应的activityInstance
            List<IToken> tokens = persistenceService.findTokensForProcessInstance(taskInstance.ProcessInstanceId, taskInstance.ActivityId);
            //        System.out.println("Inside TaskInstance.complete(targetActivityInstance):: tokens.size is "+tokens.Count);
            if (tokens == null || tokens.Count == 0)
            {
                return;//表明activityInstance已经结束了。
            }
            if (tokens.Count > 1)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId, process,
                        taskInstance.TaskId,
                        "与activityId=" + taskInstance.ActivityId + "对应的token数量(=" + tokens.Count + ")不正确，正确只能为1，因此无法完成complete操作");
            }
            IToken token = tokens[0];
            //stepNumber不相等，不允许执行结束操作。
            if (token.StepNumber != taskInstance.StepNumber)
            {
                return;
            }
            if (token.IsAlive == false)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId, process,
                        taskInstance.TaskId,
                        "与activityId=" + taskInstance.ActivityId + "对应的token.alive=false，因此无法完成complete操作");
            }

            INetInstance netInstance = RuntimeContext.getKernelManager().getNetInstance(taskInstance.ProcessId, taskInstance.Version);
            Object obj = netInstance.getWFElementInstance(taskInstance.ActivityId);
            if (obj == null)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId, process,
                        taskInstance.TaskId,
                        "系统没有找到与activityId=" + taskInstance.ActivityId + "对应activityInstance，无法执行complete操作。");
            }


            token.ProcessInstance = processInstance;

            ((IActivityInstance)obj).complete(token, targetActivityInstance);

        }

        /// <summary>
        /// 中止当前的TaskInstance,并使得流程流转到指定的环节。
        /// </summary>
        /// <param name="currentSession"></param>
        /// <param name="processInstance"></param>
        /// <param name="taskInstance"></param>
        /// <param name="targetActivityId"></param>
        public void abortTaskInstance(IWorkflowSession currentSession,
                IProcessInstance processInstance, ITaskInstance taskInstance,
                String targetActivityId)
        {
            // 如果TaskInstance处于结束状态，则直接返回
            if (taskInstance.State == TaskInstanceStateEnum.COMPLETED
                    || taskInstance.State == TaskInstanceStateEnum.CANCELED)
            {
                return;
            }
            // Initialized状态的TaskInstance也可以中止，20090830
            // if (taskInstance.State == ITaskInstance.INITIALIZED) {
            // WorkflowProcess process = taskInstance.getWorkflowProcess();
            // throw new EngineException(taskInstance.getProcessInstanceId(),
            // process,
            // taskInstance.getTaskId(),
            // "Complete task insatance failed.The state of the task insatnce[id=" +
            // taskInstance.getId() + "] is " + taskInstance.State);
            // }
            if (taskInstance.IsSuspended())
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId,
                        process, taskInstance.TaskId,
                        "Abort task insatance failed. The task instance [id="
                                + taskInstance.Id + "] is suspended");
            }

            // 1）检查是否在同一个“执行线”上
            WorkflowProcess workflowProcess = taskInstance.getWorkflowProcess();
            if (targetActivityId != null)
            {
                String thisActivityId = taskInstance.ActivityId;
                Boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId,
                        targetActivityId);
                if (!isInSameLine)
                {
                    throw new EngineException(
                            taskInstance.ProcessInstanceId,
                            taskInstance.getWorkflowProcess(),
                            taskInstance.TaskId,
                            "Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
                }
            }

            INetInstance netInstance = this.RuntimeContext.getKernelManager().getNetInstance(
                    workflowProcess.Id, taskInstance.Version);
            IActivityInstance targetActivityInstance = (IActivityInstance)netInstance.getWFElementInstance(targetActivityId);

            IActivityInstance thisActivityInstance = (IActivityInstance)netInstance.getWFElementInstance(taskInstance.ActivityId);
            if (thisActivityInstance == null)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId,
                        process, taskInstance.TaskId, "系统没有找到与activityId="
                                + taskInstance.ActivityId
                                + "对应activityInstance，无法执行abort操作。");
            }

            if (targetActivityInstance != null)
            {
                ((TaskInstance)taskInstance).TargetActivityId=targetActivityInstance.getActivity().Id;
            }

            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;

            // 第一步，首先Abort当前taskInstance
            persistenceService.abortTaskInstance((TaskInstance)taskInstance);

            // 触发相应的事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(taskInstance);
            e.setWorkflowSession(currentSession);
            e.setProcessInstance(processInstance);
            e.setEventType(TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE);
            if (this.DefaultTaskInstanceEventListener != null)
            {
                this.DefaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }

            this.fireTaskInstanceEvent(taskInstance, e);

            // 第二步，检查ActivityInstance是否可以结束
            if (!activityInstanceCanBeCompleted(taskInstance))
            {
                return;
            }

            // 第三步，尝试结束对应的activityInstance
            List<IToken> tokens = persistenceService.findTokensForProcessInstance(
                    taskInstance.ProcessInstanceId, taskInstance
                            .ActivityId);
            // System.out.println("Inside TaskInstance.complete(targetActivityInstance):: tokens.size is "+tokens.size());
            if (tokens == null || tokens.Count == 0)
            {
                return;// 表明activityInstance已经结束了。
            }
            if (tokens.Count > 1)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId,
                        process, taskInstance.TaskId, "与activityId="
                                + taskInstance.ActivityId + "对应的token数量(="
                                + tokens.Count + ")不正确，正确只能为1，因此无法完成complete操作");
            }
            IToken token = tokens[0];
            // stepNumber不相等，不允许执行结束操作。
            if (token.StepNumber != taskInstance.StepNumber)
            {
                return;
            }
            if (token.IsAlive == false)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.ProcessInstanceId,
                        process, taskInstance.TaskId, "与activityId="
                                + taskInstance.ActivityId
                                + "对应的token.alive=false，因此无法完成complete操作");
            }

            token.ProcessInstance = processInstance;

            thisActivityInstance.complete(token, targetActivityInstance);
        }

        public void abortTaskInstanceEx(IWorkflowSession currentSession,
                IProcessInstance processInstance, ITaskInstance thisTaskInst,
                String targetActivityId)
        {
            // 如果TaskInstance处于结束状态，则直接返回
            if (thisTaskInst.State == TaskInstanceStateEnum.COMPLETED
                    || thisTaskInst.State == TaskInstanceStateEnum.CANCELED)
            {
                return;
            }

            // Initialized状态的TaskInstance也可以中止，20090830
            // if (taskInstance.State == ITaskInstance.INITIALIZED) {
            // WorkflowProcess process = taskInstance.getWorkflowProcess();
            // throw new EngineException(taskInstance.getProcessInstanceId(),
            // process,
            // taskInstance.getTaskId(),
            // "Complete task insatance failed.The state of the task insatnce[id=" +
            // taskInstance.getId() + "] is " + taskInstance.State);
            // }
            if (thisTaskInst.IsSuspended())
            {
                WorkflowProcess process = thisTaskInst.getWorkflowProcess();
                throw new EngineException(thisTaskInst.ProcessInstanceId,
                        process, thisTaskInst.TaskId,
                        "Abort task insatance failed. The task instance [id="
                                + thisTaskInst.Id + "] is suspended");
            }

            // 
            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;
            WorkflowProcess workflowProcess = thisTaskInst.getWorkflowProcess();
            List<IToken> allTokens = null;
            List<String> aliveActivityIdsAfterJump = new List<String>();
            if (targetActivityId != null)
            {
                String thisActivityId = thisTaskInst.ActivityId;
                Boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId,
                        targetActivityId);

                if (isInSameLine)
                {
                    this.abortTaskInstance(currentSession, processInstance, thisTaskInst, targetActivityId);
                }

                //合法性检查
                allTokens = persistenceService.findTokensForProcessInstance(thisTaskInst.ProcessInstanceId, null);

                aliveActivityIdsAfterJump.Add(targetActivityId);

                for (int i = 0; allTokens != null && i < allTokens.Count; i++)
                {
                    IToken tokenTmp = allTokens[i];
                    IWFElement workflowElement = workflowProcess.findWFElementById(tokenTmp.NodeId);
                    if ((workflowElement is Activity) && !workflowElement.Id.Equals(thisActivityId))
                    {

                        aliveActivityIdsAfterJump.Add(workflowElement.Id);

                        if (workflowProcess.isReachable(targetActivityId, workflowElement.Id)
                            || workflowProcess.isReachable(workflowElement.Id, targetActivityId))
                        {
                            throw new EngineException(
                                    thisTaskInst.ProcessInstanceId,
                                    thisTaskInst.getWorkflowProcess(),
                                    thisTaskInst.TaskId,
                                    "Abort refused because of the business-logic conflict!");

                        }
                    }
                }

                //1）检查是否在同一个“执行线”上(不做该检查，20091008)	
                //			if (!isInSameLine) {
                //				throw new EngineException(
                //						taskInstance.getProcessInstanceId(),
                //						taskInstance.getWorkflowProcess(),
                //						taskInstance.getTaskId(),
                //						"Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
                //			}
            }

            INetInstance netInstance = this.RuntimeContext.getKernelManager().getNetInstance(
                    workflowProcess.Id, thisTaskInst.Version);
            IActivityInstance targetActivityInstance = null;
            if (targetActivityId != null)
            {
                targetActivityInstance = (IActivityInstance)netInstance
                    .getWFElementInstance(targetActivityId);
            }

            IActivityInstance thisActivityInstance = (IActivityInstance)netInstance
                    .getWFElementInstance(thisTaskInst.ActivityId);
            if (thisActivityInstance == null)
            {
                WorkflowProcess process = thisTaskInst.getWorkflowProcess();
                throw new EngineException(thisTaskInst.ProcessInstanceId,
                        process, thisTaskInst.TaskId, "系统没有找到与activityId="
                                + thisTaskInst.ActivityId
                                + "对应activityInstance，无法执行abort操作。");
            }

            if (targetActivityInstance != null)
            {
                ((TaskInstance)thisTaskInst).TargetActivityId=targetActivityInstance.getActivity().Id;
            }

            // 第一步，首先Abort当前taskInstance
            persistenceService.abortTaskInstance((TaskInstance)thisTaskInst);

            // 触发相应的事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(thisTaskInst);
            e.setWorkflowSession(currentSession);
            e.setProcessInstance(processInstance);
            e.setEventType(TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE);
            if (this.DefaultTaskInstanceEventListener != null)
            {
                this.DefaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }

            this.fireTaskInstanceEvent(thisTaskInst, e);

            // 第二步，检查ActivityInstance是否可以结束
            if (!activityInstanceCanBeCompleted(thisTaskInst))
            {
                return;
            }

            // 第三步，尝试结束对应的activityInstance
            List<IToken> tokens = persistenceService.findTokensForProcessInstance(
                    thisTaskInst.ProcessInstanceId, thisTaskInst
                            .ActivityId);
            // System.out.println("Inside TaskInstance.complete(targetActivityInstance):: tokens.size is "+tokens.size());
            if (tokens == null || tokens.Count == 0)
            {
                return;// 表明activityInstance已经结束了。
            }
            if (tokens.Count > 1)
            {
                WorkflowProcess process = thisTaskInst.getWorkflowProcess();
                throw new EngineException(thisTaskInst.ProcessInstanceId,
                        process, thisTaskInst.TaskId, "与activityId="
                                + thisTaskInst.ActivityId + "对应的token数量(="
                                + tokens.Count + ")不正确，正确只能为1，因此无法完成complete操作");
            }
            IToken token = tokens[0];
            // stepNumber不相等，不允许执行结束操作。
            if (token.StepNumber != thisTaskInst.StepNumber)
            {
                return;
            }
            if (token.IsAlive == false)
            {
                WorkflowProcess process = thisTaskInst.getWorkflowProcess();
                throw new EngineException(thisTaskInst.ProcessInstanceId,
                        process, thisTaskInst.TaskId, "与activityId="
                                + thisTaskInst.ActivityId
                                + "对应的token.alive=false，因此无法完成complete操作");
            }

            token.ProcessInstance = processInstance;

            //调整token布局
            if (targetActivityId != null)
            {
                List<Synchronizer> allSynchronizersAndEnds = new List<Synchronizer>();
                allSynchronizersAndEnds.AddRange(workflowProcess.Synchronizers);
                allSynchronizersAndEnds.AddRange((IEnumerable<Synchronizer>)workflowProcess.EndNodes);
                //allSynchronizersAndEnds.AddRange((List<Synchronizer>));
                for (int i = 0; i < allSynchronizersAndEnds.Count; i++)
                {
                    Synchronizer synchronizer = allSynchronizersAndEnds[i];
                    if (synchronizer.Name.Equals("Synchronizer4"))
                    {
                        //System.out.println(synchronizer.Name);
                    }
                    int volumn = 0;
                    if (synchronizer is EndNode)
                    {
                        volumn = synchronizer.EnteringTransitions.Count;
                    }
                    else
                    {
                        volumn = synchronizer.EnteringTransitions.Count * synchronizer.LeavingTransitions.Count;
                    }
                    IToken tokenTmp = new Token();
                    tokenTmp.NodeId = synchronizer.Id;
                    tokenTmp.IsAlive = false;
                    tokenTmp.ProcessInstanceId = thisTaskInst.ProcessInstanceId;
                    tokenTmp.StepNumber = -1;

                    List<String> incomingTransitionIds = new List<String>();
                    Boolean reachable = false;
                    List<Transition> enteringTrans = synchronizer.EnteringTransitions;
                    for (int m = 0; m < aliveActivityIdsAfterJump.Count; m++)
                    {
                        String aliveActivityId = aliveActivityIdsAfterJump[m];
                        if (workflowProcess.isReachable(aliveActivityId, synchronizer.Id))
                        {
                            Transition trans = null;
                            reachable = true;
                            for (int j = 0; j < enteringTrans.Count; j++)
                            {
                                trans = enteringTrans[j];
                                Node fromNode = (Node)trans.FromNode;
                                if (workflowProcess.isReachable(aliveActivityId, fromNode.Id))
                                {
                                    if (!incomingTransitionIds.Contains(trans.Id))
                                    {
                                        incomingTransitionIds.Add(trans.Id);
                                    }
                                }
                            }
                        }
                    }
                    if (reachable)
                    {
                        tokenTmp.Value = volumn - (incomingTransitionIds.Count * volumn / enteringTrans.Count);

                        IToken virtualToken = getJoinInfo(allTokens, synchronizer.Id);

                        if (virtualToken != null)
                        {
                            persistenceService.deleteTokensForNode(thisTaskInst.ProcessInstanceId, synchronizer.Id);
                        }

                        if (tokenTmp.Value != 0)
                        {
                            tokenTmp.ProcessInstance = processInstance;
                            persistenceService.saveOrUpdateToken(tokenTmp);
                        }
                    }
                }
            }
            thisActivityInstance.complete(token, targetActivityInstance);
        }

        /// <summary>
        /// 触发task instance相关的事件
        /// </summary>
        /// <param name="taskInstance"></param>
        /// <param name="e"></param>
        protected void fireTaskInstanceEvent(ITaskInstance taskInstance, TaskInstanceEvent e)
        {
            Task task = taskInstance.getTask();
            if (task == null)
            {
                return;
            }

            List<EventListener> listeners = task.EventListeners;
            for (int i = 0; i < listeners.Count; i++)
            {
                EventListener listener = (EventListener)listeners[i];
                Object obj = RuntimeContext.getBeanByName(listener.ClassName);
                if (obj != null && (obj is ITaskInstanceEventListener))
                {
                    ((ITaskInstanceEventListener)obj).onTaskInstanceEventFired(e);
                }
            }
        }

        public WorkItem createWorkItem(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance, String actorId)
        {
            IPersistenceService persistenceService = RuntimeContext.PersistenceService;

            WorkItem wi = new WorkItem();
            wi.setTaskInstance(taskInstance);
            wi.setActorId(actorId);
            wi.setState(IWorkItem.INITIALIZED);
            wi.setCreatedTime(RuntimeContext.getCalendarService().getSysDate());
            wi.setRuntimeContext(RuntimeContext);
            wi.CurrentWorkflowSession=currentSession;
            //保存到数据库
            persistenceService.saveOrUpdateWorkItem(wi);

            //触发事件
            //触发相应的事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(taskInstance);
            e.setWorkItem(wi);
            e.setWorkflowSession(currentSession);
            e.setProcessInstance(processInstance);

            e.setEventType(TaskInstanceEvent.AFTER_WORKITEM_CREATED);
            if (this.DefaultTaskInstanceEventListener != null)
            {
                this.DefaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }
            this.fireTaskInstanceEvent(taskInstance, e);

            return wi;
        }

        public IWorkItem claimWorkItem(String workItemId, String taskInstanceId)
        {
            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;

            persistenceService.lockTaskInstance(taskInstanceId);

            IWorkItem workItem = persistenceService.findWorkItemById(workItemId);

            if (workItem == null) return null;

            if (workItem.getState() != IWorkItem.INITIALIZED)
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId,
                        "Claim work item failed. The state of the work item is " + workItem.getState());
            }
            if (workItem.getTaskInstance().State != TaskInstanceStateEnum.INITIALIZED && workItem.getTaskInstance().State != TaskInstanceStateEnum.RUNNING)
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId,
                        "Claim work item failed .The state of the correspond task instance is " + workItem.getTaskInstance().State);
            }

            if (workItem.getTaskInstance().IsSuspended())
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId,
                        "Claim work item failed .The  correspond task instance is suspended");
            }



            //0、首先修改workitem的状态
            ((WorkItem)workItem).setState(IWorkItem.RUNNING);
            ((WorkItem)workItem).setClaimedTime(RuntimeContext.getCalendarService().getSysDate());
            persistenceService.saveOrUpdateWorkItem(workItem);

            //1、如果不是会签，则删除其他的workitem
            if (FormTaskEnum.ANY == workItem.getTaskInstance().AssignmentStrategy)
            {
                persistenceService.deleteWorkItemsInInitializedState(workItem.getTaskInstance().Id);
            }

            //2、将TaskInstance的canBeWithdrawn字段改称false。即不允许被撤销
            TaskInstance taskInstance = (TaskInstance)workItem.getTaskInstance();
            taskInstance.CanBeWithdrawn=false;
            persistenceService.saveOrUpdateTaskInstance(taskInstance);

            return workItem;
        }

        public void completeWorkItem(IWorkItem workItem, IActivityInstance targetActivityInstance, String comments)
        {
            if (workItem.getState() != IWorkItem.RUNNING)
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                //			System.out.println("WorkItem的当前状态为"+this.getState()+"，不可以执行complete操作。");
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId,
                        "Complete work item failed . The state of the work item [id=" + workItem.getId() + "] is " + workItem.getState());
            }

            if (workItem.getTaskInstance().IsSuspended())
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                WorkflowProcess process = thisTaskInst.getWorkflowProcess();
                throw new EngineException(thisTaskInst.ProcessInstanceId, process,
                        thisTaskInst.TaskId,
                        "Complete work item failed. The correspond task instance [id=" + thisTaskInst.Id + "] is suspended");
            }

            IPersistenceService persistenceService = RuntimeContext.PersistenceService;

            ((WorkItem)workItem).setComments(comments);
            ((WorkItem)workItem).setState(IWorkItem.COMPLETED);
            ((WorkItem)workItem).setEndTime(RuntimeContext.getCalendarService().getSysDate());
            persistenceService.saveOrUpdateWorkItem(workItem);

            // 触发AFTER_WORKITEM_COMPLETE事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(workItem.getTaskInstance());
            e.setWorkflowSession(((IWorkflowSessionAware)workItem).CurrentWorkflowSession);
            e.setProcessInstance(((TaskInstance)workItem.getTaskInstance()).getAliveProcessInstance());
            e.setEventType(TaskInstanceEvent.AFTER_WORKITEM_COMPLETE);
            if (this.DefaultTaskInstanceEventListener != null)
            {
                this.DefaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }

            this.fireTaskInstanceEvent(workItem.getTaskInstance(), e);

            ((TaskInstance)workItem.getTaskInstance()).complete(targetActivityInstance);
        }

        public void completeWorkItemAndJumpTo(IWorkItem workItem, String targetActivityId, String comments)
        {
            WorkflowSession workflowSession = (WorkflowSession)((IWorkflowSessionAware)workItem).CurrentWorkflowSession;
            //首先检查是否可以正确跳转
            //1）检查是否在同一个“执行线”上
            WorkflowProcess workflowProcess = workItem.getTaskInstance().getWorkflowProcess();
            String thisActivityId = workItem.getTaskInstance().ActivityId;
            Boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId, targetActivityId);
            TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
            if (!isInSameLine)
            {
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId, "Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
            }


            //2）检查目标Activity Form Task的数量(暂时关闭该检查项目)
            //        Activity targetActivity = (Activity)workflowProcess.findWFElementById(activityId);
            //        int count = getFormTaskCount(targetActivity);
            //        if (count!=1){
            //            if (!isInSameLine) throw new EngineException("Jumpto refused because of the  FORM-type-task count of the target activitgy  is NOT 1; the count is "+count);
            //        }

            //3)检查当前的 taskinstance是否可以结束
            IPersistenceService persistenceService = RuntimeContext.PersistenceService;

            Int32 aliveWorkItemCount = persistenceService.getAliveWorkItemCountForTaskInstance(thisTaskInst.Id);
            if (aliveWorkItemCount > 1)
            {
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId, "Jumpto refused because of current taskinstance can NOT be completed. some workitem of this taskinstance is in runing state or initialized state");

            }

            //4)检查当前的activity instance是否可以结束
            if (workItem.getTaskInstance().getActivity().CompletionStrategy == FormTaskEnum.ALL)
            {

                Int32 aliveTaskInstanceCount4ThisActivity = persistenceService.getAliveTaskInstanceCountForActivity(workItem.getTaskInstance().ProcessInstanceId, workItem.getTaskInstance().ActivityId);
                if (aliveTaskInstanceCount4ThisActivity > 1)
                {//大于2表明当前Activity不可以complete
                    throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                            thisTaskInst.TaskId, "Jumpto refused because of current activity instance can NOT be completed. some task instance of this activity instance is in runing state or initialized state");
                }
            }

            INetInstance netInstance = RuntimeContext.getKernelManager().getNetInstance(workflowProcess.Id, workItem.getTaskInstance().Version);
            if (netInstance == null)
            {
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId, "Not find the net instance for workflow process [id=" + workflowProcess.Id + ", version=" + workItem.getTaskInstance().Version + "]");
            }
            Object obj = netInstance.getWFElementInstance(targetActivityId);
            IActivityInstance targetActivityInstance = (IActivityInstance)obj;
            if (targetActivityInstance == null)
            {
                throw new EngineException(thisTaskInst.ProcessInstanceId, thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId, "Not find the activity instance  for activity[process id=" + workflowProcess.Id + ", version=" + workItem.getTaskInstance().Version + ",activity id=" + targetActivityId + "]");
            }


            if (RuntimeContext.isEnableTrace())
            {

                ProcessInstanceTrace trace = new ProcessInstanceTrace();
                trace.setProcessInstanceId(workItem.getTaskInstance().ProcessInstanceId);
                trace.setStepNumber(workItem.getTaskInstance().StepNumber + 1);
                trace.setType(ProcessInstanceTrace.JUMPTO_TYPE);
                trace.setFromNodeId(workItem.getTaskInstance().ActivityId);
                trace.setToNodeId(targetActivityId);
                trace.setEdgeId("");
                RuntimeContext.PersistenceService.saveOrUpdateProcessInstanceTrace(trace);
            }

            this.completeWorkItem(workItem, targetActivityInstance, comments);
        }

        /// <summary>
        /// 自由流方法扩展，去除“同一执行线”的限制
        /// </summary>
        /// <param name="workItem"></param>
        /// <param name="targetActivityId"></param>
        /// <param name="comments"></param>
        public void completeWorkItemAndJumpToEx(IWorkItem workItem, String targetActivityId, String comments)
        {
            // 首先检查是否可以正确跳转	
            WorkflowProcess workflowProcess = workItem.getTaskInstance().getWorkflowProcess();
            String thisActivityId = workItem.getTaskInstance().ActivityId;
            TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
            //如果是在同一条执行线上，那么可以直接跳过去，只是重复判断了是否在同一条执行线上
            Boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId, targetActivityId);
            if (isInSameLine)
            {
                this.completeWorkItemAndJumpTo(workItem, targetActivityId, comments);
                return;
            }

            // 1）检查是否在同一个“执行线”上(关闭该检查，20091002)		
            //		if (!isInSameLine) {
            //			throw new EngineException(
            //					thisTaskInst.getProcessInstanceId(),
            //					thisTaskInst.getWorkflowProcess(),
            //					thisTaskInst.getTaskId(),
            //					"Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
            //		}

            // 2）检查目标Activity Form Task的数量(暂时关闭该检查项目)
            // Activity targetActivity =
            // (Activity)workflowProcess.findWFElementById(activityId);
            // int count = getFormTaskCount(targetActivity);
            // if (count!=1){
            // if (!isInSameLine) throw new
            // EngineException("Jumpto refused because of the  FORM-type-task count of the target activitgy  is NOT 1; the count is "+count);
            // }

            // 3)检查当前的 taskinstance是否可以结束
            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;

            Int32 aliveWorkItemCount = persistenceService.getAliveWorkItemCountForTaskInstance(thisTaskInst.Id);
            if ( aliveWorkItemCount > 1)
            {
                throw new EngineException(
                        thisTaskInst.ProcessInstanceId,
                        thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId,
                        "Jumpto refused because of current taskinstance can NOT be completed. some workitem of this taskinstance is in runing state or initialized state");

            }

            // 4)检查当前的activity instance是否可以结束
            if (workItem.getTaskInstance().getActivity().CompletionStrategy == FormTaskEnum.ALL)
            {

                Int32 aliveTaskInstanceCount4ThisActivity = persistenceService
                        .getAliveTaskInstanceCountForActivity(workItem
                                .getTaskInstance().ProcessInstanceId, workItem
                                .getTaskInstance().ActivityId);
                if (aliveTaskInstanceCount4ThisActivity > 1)
                {// 大于1表明当前Activity不可以complete
                    throw new EngineException(
                            thisTaskInst.ProcessInstanceId,
                            thisTaskInst.getWorkflowProcess(),
                            thisTaskInst.TaskId,
                            "Jumpto refused because of current activity instance can NOT be completed. some task instance of this activity instance is in runing state or initialized state");
                }
            }

            //4)首先检查目标状态M是否存在冲突,如果存在冲突则不允许跳转；如果不存在冲突，则需要调整token
            List<IToken> allTokens = persistenceService.findTokensForProcessInstance(thisTaskInst.ProcessInstanceId, null);
            WorkflowProcess thisProcess = thisTaskInst.getWorkflowProcess();//找到当前的工作里模型
            List<String> aliveActivityIdsAfterJump = new List<String>();//计算跳转后，哪些activity节点复活
            aliveActivityIdsAfterJump.Add(targetActivityId);

            for (int i = 0; allTokens != null && i < allTokens.Count; i++)
            {
                IToken tokenTmp = allTokens[i];
                IWFElement workflowElement = thisProcess.findWFElementById(tokenTmp.NodeId); //找到拥有此token的工作流元素
                if ((workflowElement is Activity) && !workflowElement.Id.Equals(thisActivityId))
                {
                    //注意：不能自己跳转到自己，同时此工作流元素是activity类型
                    aliveActivityIdsAfterJump.Add(workflowElement.Id);

                    if (thisProcess.isReachable(targetActivityId, workflowElement.Id)
                        || thisProcess.isReachable(workflowElement.Id, targetActivityId))
                    {
                        throw new EngineException(
                                thisTaskInst.ProcessInstanceId,
                                thisTaskInst.getWorkflowProcess(),
                                thisTaskInst.TaskId,
                                "Jumpto refused because of the business-logic conflict!");

                    }
                }
            }

            //所有检查结束，开始执行跳转操作

            INetInstance netInstance = this.RuntimeContext.getKernelManager().getNetInstance(
                    workflowProcess.Id,
                    workItem.getTaskInstance().Version);
            if (netInstance == null)
            {
                throw new EngineException(thisTaskInst.ProcessInstanceId,
                        thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId,
                        "Not find the net instance for workflow process [id="
                                + workflowProcess.Id + ", version="
                                + workItem.getTaskInstance().Version + "]");
            }
            Object obj = netInstance.getWFElementInstance(targetActivityId);
            IActivityInstance targetActivityInstance = (IActivityInstance)obj;
            if (targetActivityInstance == null)
            {
                throw new EngineException(thisTaskInst.ProcessInstanceId,
                        thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.TaskId,
                        "Not find the activity instance  for activity[process id="
                                + workflowProcess.Id + ", version="
                                + workItem.getTaskInstance().Version
                                + ",activity id=" + targetActivityId + "]");
            }

            if (this.RuntimeContext.isEnableTrace())
            {

                ProcessInstanceTrace trace = new ProcessInstanceTrace();
                trace.setProcessInstanceId(workItem.getTaskInstance()
                        .ProcessInstanceId);
                trace.setStepNumber(workItem.getTaskInstance().StepNumber + 1);
                trace.setType(ProcessInstanceTrace.JUMPTO_TYPE);
                trace.setFromNodeId(workItem.getTaskInstance().ActivityId);
                trace.setToNodeId(targetActivityId);
                trace.setEdgeId("");
                this.RuntimeContext.PersistenceService.saveOrUpdateProcessInstanceTrace(
                        trace);
            }

            //调整token布局 
            List<Synchronizer> allSynchronizersAndEnds = new List<Synchronizer>();
            allSynchronizersAndEnds.AddRange(thisProcess.Synchronizers);
            allSynchronizersAndEnds.AddRange((IEnumerable<Synchronizer>)thisProcess.EndNodes);
            for (int i = 0; i < allSynchronizersAndEnds.Count; i++)
            {
                Synchronizer synchronizer = allSynchronizersAndEnds[i];

                int volumn = 0;
                if (synchronizer is EndNode)
                {
                    volumn = synchronizer.EnteringTransitions.Count;
                }
                else
                {
                    volumn = synchronizer.EnteringTransitions.Count * synchronizer.LeavingTransitions.Count;
                }
                IToken tokenTmp = new Token();
                tokenTmp.NodeId = synchronizer.Id;
                tokenTmp.IsAlive = false;
                tokenTmp.ProcessInstanceId = thisTaskInst.ProcessInstanceId;
                tokenTmp.StepNumber = -1;

                List<String> incomingTransitionIds = new List<String>();
                Boolean reachable = false;
                List<Transition> enteringTrans = synchronizer.EnteringTransitions;
                for (int m = 0; m < aliveActivityIdsAfterJump.Count; m++)
                {
                    String aliveActivityId = aliveActivityIdsAfterJump[m];
                    if (thisProcess.isReachable(aliveActivityId, synchronizer.Id))
                    {
                        Transition trans = null;
                        reachable = true;
                        for (int j = 0; j < enteringTrans.Count; j++)
                        {
                            trans = enteringTrans[j];
                            Node fromNode = trans.FromNode;
                            if (thisProcess.isReachable(aliveActivityId, fromNode.Id))
                            {
                                if (!incomingTransitionIds.Contains(trans.Id))
                                {
                                    incomingTransitionIds.Add(trans.Id);
                                }
                            }
                        }
                    }
                }
                if (reachable)
                {
                    tokenTmp.Value = volumn - (incomingTransitionIds.Count * volumn / enteringTrans.Count);

                    IToken virtualToken = getJoinInfo(allTokens, synchronizer.Id); //获取一个虚拟的综合性token

                    if (virtualToken != null)
                    {
                        persistenceService.deleteTokensForNode(thisTaskInst.ProcessInstanceId, synchronizer.Id);
                    }

                    if (tokenTmp.Value != 0)
                    {
                        tokenTmp.ProcessInstance = thisTaskInst.getAliveProcessInstance();
                        persistenceService.saveOrUpdateToken(tokenTmp);
                    }
                }
            }
            this.completeWorkItem(workItem, targetActivityInstance, comments);
        }


        /// <summary>
        /// 获取特定同步器的汇聚信息，如果该同步器已经存在token，则返回一个综合性的虚拟的token，否则返回null。
        /// </summary>
        /// <param name="allTokens"></param>
        /// <param name="synchronizerId"></param>
        /// <returns></returns>
        private IToken getJoinInfo(List<IToken> allTokens, String synchronizerId)
        {
            Boolean findTokens = false;
            Dictionary<String, IToken> tokensMap = new Dictionary<String, IToken>();
            for (int i = 0; i < allTokens.Count; i++)
            {
                IToken tmpToken = (IToken)allTokens[i];
                if (!tmpToken.NodeId.Equals(synchronizerId))
                {
                    continue;
                }
                findTokens = true;
                String tmpFromActivityId = tmpToken.FromActivityId;
                if (!tokensMap.ContainsKey(tmpFromActivityId))
                {
                    tokensMap.Add(tmpFromActivityId, tmpToken);
                }
                else
                {
                    IToken tmpToken2 = (IToken)tokensMap[tmpFromActivityId];
                    if (tmpToken2.StepNumber > tmpToken.StepNumber)
                    {
                        tokensMap.Add(tmpFromActivityId, tmpToken2);
                    }
                }
            }

            if (!findTokens) return null;
            IToken virtualToken = new Token();
            int stepNumber = 0;
            List<IToken> tokensList = new List<IToken>(tokensMap.Values);

            for (int i = 0; i < tokensList.Count; i++)
            {
                IToken _token = (IToken)tokensList[i];
                //Fixed by wmj2003  http://www.fireflow.org/viewthread.php?tid=1040&extra=page%3D1
                virtualToken.Value = virtualToken.Value == 0 ? 0 : virtualToken.Value + _token.Value;
                if (_token.IsAlive)
                {
                    virtualToken.IsAlive = true;
                    String oldFromActivityId = virtualToken.FromActivityId;
                    if (String.IsNullOrEmpty(oldFromActivityId.Trim()))
                    {
                        virtualToken.FromActivityId = _token.FromActivityId;
                    }
                    else
                    {
                        virtualToken.FromActivityId = oldFromActivityId + TokenFrom.FROM_ACTIVITY_ID_SEPARATOR + _token.FromActivityId;
                    }
                }
                if (_token.StepNumber > stepNumber)
                {
                    stepNumber = _token.StepNumber;
                }
            }

            virtualToken.StepNumber = stepNumber + 1;

            return virtualToken;
        }

        public void rejectWorkItem(IWorkItem workItem, String comments)
        {
            Activity thisActivity = workItem.getTaskInstance().getActivity();
            TaskInstance thisTaskInstance = (TaskInstance)workItem.getTaskInstance();
            if (workItem.getState() > 5 || workItem.getTaskInstance().IsSuspended())
            {//处于非活动状态,或者被挂起,则不允许reject
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId,
                        "Reject operation refused!Current work item is completed or the correspond task instance is suspended!!");
            }
            //当前Activity只允许一个Form类型的Task
            if (thisActivity.getTasks().Count > 1)
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId,
                        "Reject operation refused!The correspond activity has more than 1 tasks");
            }
            //汇签Task不允许Reject
            if (FormTaskEnum.ALL.Equals(thisTaskInstance.AssignmentStrategy))
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId,
                        "Reject operation refused!The assignment strategy is 'ALL'");
            }
            //----added by wmj2003 20090915 ---start---
            //处理拒收的边界问题
            if (thisTaskInstance.FromActivityId.Equals(TokenFrom.FROM_START_NODE))
            {
                throw new EngineException(
                                thisTaskInstance.ProcessInstanceId,
                                thisTaskInstance.getWorkflowProcess(),
                                thisTaskInstance.TaskId,
                                "Reject operation refused!Because the from activityId equals " + TokenFrom.FROM_START_NODE);
            }
            //----added by wmj2003 20090915 ---end---

            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;
            List<ITaskInstance> siblingTaskInstancesList = null;

            siblingTaskInstancesList = persistenceService.findTaskInstancesForProcessInstanceByStepNumber(workItem.getTaskInstance().ProcessInstanceId,
                    (int)thisTaskInstance.StepNumber);

            //如果执行了split操作，则不允许reject
            if (siblingTaskInstancesList.Count > 1)
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId, "Reject operation refused!Because the process instance has taken a split operation.");
            }

            //检查From Activity中是否有ToolTask和SubflowTask
            List<String> fromActivityIdList = new List<String>();
            String[] tokenizer = thisTaskInstance.FromActivityId.Split(new String[] { TokenFrom.FROM_ACTIVITY_ID_SEPARATOR }, StringSplitOptions.RemoveEmptyEntries);//)new string[]{});
            fromActivityIdList.AddRange(tokenizer);
            //StringTokenizer tokenizer = new StringTokenizer(thisTaskInstance.getFromActivityId(), IToken.FROM_ACTIVITY_ID_SEPARATOR);
            //while (tokenizer.hasMoreTokens()) {
            //    fromActivityIdList.add(tokenizer.nextToken());
            //}
            WorkflowProcess workflowProcess = workItem.getTaskInstance().getWorkflowProcess();
            for (int i = 0; i < fromActivityIdList.Count; i++)
            {
                String fromActivityId = (String)fromActivityIdList[i];
                Activity fromActivity = (Activity)workflowProcess.findWFElementById(fromActivityId);
                List<Task> fromTaskList = fromActivity.getTasks();
                for (int j = 0; j < fromTaskList.Count; j++)
                {
                    Task task = (Task)fromTaskList[j];
                    if (TaskTypeEnum.TOOL.Equals(task.TaskType) || TaskTypeEnum.SUBFLOW.Equals(task.TaskType))
                    {
                        throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                                thisTaskInstance.TaskId, "Reject operation refused!The previous activity contains tool-task or subflow-task");

                    }
                }
            }
            //恢复所有的FromTaskInstance
            INetInstance netInstance = RuntimeContext.getKernelManager().getNetInstance(workflowProcess.Id, workItem.getTaskInstance().Version);
            if (netInstance == null)
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId, "Not find the net instance for workflow process [id=" + workflowProcess.Id + ", version=" + workItem.getTaskInstance().Version + "]");
            }

            //执行reject操作。
            IWorkflowSession session = ((IWorkflowSessionAware)workItem).CurrentWorkflowSession;
            session.setWithdrawOrRejectOperationFlag(true);
            int newStepNumber = (int)thisTaskInstance.StepNumber + 1;
            try
            {
                //首先将本WorkItem和TaskInstance cancel掉。
                workItem.setComments(comments);
                ((WorkItem)workItem).setState(IWorkItem.CANCELED);
                ((WorkItem)workItem).setEndTime(this.RuntimeContext.getCalendarService().getSysDate());
                this.RuntimeContext.PersistenceService.saveOrUpdateWorkItem(workItem);

                persistenceService.abortTaskInstance(thisTaskInstance);

                //删除本环节的token
                persistenceService.deleteTokensForNode(thisTaskInstance.ProcessInstanceId, thisTaskInstance.ActivityId);

                IActivityInstance fromActivityInstance = null;
                for (int i = 0; i < fromActivityIdList.Count; i++)
                {
                    String fromActivityId = (String)fromActivityIdList[i];
                    Object obj = netInstance.getWFElementInstance(fromActivityId);
                    fromActivityInstance = (IActivityInstance)obj;
                    Token newToken = new Token();
                    ((Token)newToken).IsAlive = true;
                    ((Token)newToken).NodeId = fromActivityId;
                    newToken.ProcessInstanceId = thisTaskInstance.ProcessInstanceId;
                    newToken.ProcessInstance = ((TaskInstance)thisTaskInstance).getAliveProcessInstance();
                    newToken.FromActivityId = thisTaskInstance.ActivityId;
                    newToken.StepNumber = newStepNumber;
                    newToken.Value = 0;
                    persistenceService.saveOrUpdateToken(newToken);

                    this.createTaskInstances(newToken, fromActivityInstance);

                    if (this.RuntimeContext.isEnableTrace())
                    {
                        ProcessInstanceTrace trace = new ProcessInstanceTrace();
                        trace.setProcessInstanceId(thisTaskInstance.ProcessInstanceId);
                        trace.setStepNumber(newStepNumber);
                        trace.setType(ProcessInstanceTrace.REJECT_TYPE);
                        trace.setFromNodeId(thisActivity.Id);
                        trace.setToNodeId(fromActivityId);
                        trace.setEdgeId("");
                        this.RuntimeContext.PersistenceService.saveOrUpdateProcessInstanceTrace(trace);
                    }
                }

                ITransitionInstance theLeavingTransitionInstance = (ITransitionInstance)fromActivityInstance.LeavingTransitionInstances[0];
                ISynchronizerInstance synchronizerInstance = (ISynchronizerInstance)theLeavingTransitionInstance.getLeavingNodeInstance();
                if (synchronizerInstance.EnteringTransitionInstances.Count > fromActivityIdList.Count)
                {
                    Token supplementToken = new Token();
                    ((Token)supplementToken).IsAlive = false;
                    ((Token)supplementToken).NodeId = synchronizerInstance.getSynchronizer().Id;
                    supplementToken.ProcessInstanceId = thisTaskInstance.ProcessInstanceId;
                    supplementToken.ProcessInstance = ((TaskInstance)thisTaskInstance).getAliveProcessInstance();
                    supplementToken.FromActivityId = "EMPTY(created by reject)";
                    supplementToken.StepNumber = (int)thisTaskInstance.StepNumber + 1;
                    supplementToken.Value = synchronizerInstance.Volume - theLeavingTransitionInstance.getWeight() * fromActivityIdList.Count;
                    persistenceService.saveOrUpdateToken(supplementToken);
                }
            }
            finally
            {
                session.setWithdrawOrRejectOperationFlag(false);
            }
        }

        public IWorkItem withdrawWorkItem(IWorkItem workItem)
        {
            Activity thisActivity = workItem.getTaskInstance().getActivity();
            TaskInstance thisTaskInstance = (TaskInstance)workItem.getTaskInstance();
            if (workItem.getState() < 5)
            {//小于5的状态为活动状态
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId,
                        "Withdraw operation is refused! Current workitem is in running state!!");
            }
            //当前Activity只允许一个Form类型的Task
            if (thisActivity.getTasks().Count > 1)
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId,
                        "Withdraw operation is refused! The activity[id=" + thisActivity.Id + "] has more than 1 tasks");
            }

            //汇签Task不允许撤销
            if (FormTaskEnum.ALL.Equals(thisTaskInstance.AssignmentStrategy))
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId,
                        "Withdraw operation is refused! The assignment strategy for activity[id=" + thisActivity.Id + "] is 'ALL'");
            }
            // Activity targetActivity = null;
            // List targetActivityList = new ArrayList();
            IPersistenceService persistenceService = this.RuntimeContext.PersistenceService;
            List<ITaskInstance> targetTaskInstancesList = null;
            targetTaskInstancesList = persistenceService.findTaskInstancesForProcessInstanceByStepNumber(thisTaskInstance.ProcessInstanceId,
                    (int)thisTaskInstance.StepNumber + 1);

            // String targetActivityId = workItem.getTaskInstance().getTargetActivityId();


            //如果targetTaskInstancesList为空或size 等于0,则表示流程实例执行了汇聚操作。
            if (targetTaskInstancesList == null || targetTaskInstancesList.Count == 0)
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId, "Withdraw operation is refused!Because the process instance has taken a join operation after this activity[id=" + thisActivity.Id + "].");
            }
            else
            {
                TaskInstance taskInstance = (TaskInstance)targetTaskInstancesList[0];
                if (!taskInstance.FromActivityId.Equals(thisTaskInstance.ActivityId))
                {
                    throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.TaskId, "Withdraw operation is refused!Because the process instance has taken a join operation after this activity[id=" + thisActivity.Id + "].");
                }
            }

            for (int i = 0; targetTaskInstancesList != null && i < targetTaskInstancesList.Count; i++)
            {
                TaskInstance targetTaskInstanceTmp = (TaskInstance)targetTaskInstancesList[i];
                if (!targetTaskInstanceTmp.CanBeWithdrawn)
                {
                    //说明已经有某些WorkItem处于已签收状态，或者已经处于完毕状态，此时不允许退回
                    throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.TaskId,
                            "Withdraw operation is refused! Some task instances of the  next activity[id=" + targetTaskInstanceTmp.ActivityId + "] are not in 'Initialized' state");
                }
            }

            INetInstance netInstance = this.RuntimeContext.getKernelManager().getNetInstance(thisTaskInstance.ProcessId, workItem.getTaskInstance().Version);
            if (netInstance == null)
            {
                throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.TaskId, "Withdraw operation failed.Not find the net instance for workflow process [id=" + thisTaskInstance.ProcessId + ", version=" + workItem.getTaskInstance().Version + "]");
            }
            Object obj = netInstance.getWFElementInstance(thisTaskInstance.ActivityId);
            IActivityInstance thisActivityInstance = (IActivityInstance)obj;

            //一切检查通过之后进行“收回”处理

            IWorkflowSession session = ((IWorkflowSessionAware)workItem).CurrentWorkflowSession;
            session.setWithdrawOrRejectOperationFlag(true);
            int newStepNumber = (int)thisTaskInstance.StepNumber + 2;
            try
            {
                DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                List<String> actorIds = new List<String>();
                actorIds.Add(workItem.getActorId());
                dynamicAssignmentHandler.ActorIdsList=actorIds;
                ((WorkflowSession)session).setCurrentDynamicAssignmentHandler(dynamicAssignmentHandler);

                //1、首先将后续环节的TaskInstance极其workItem变成Canceled状态
                List<String> targetActivityIdList = new List<String>();

                StringBuilder theFromActivityIds = new StringBuilder();
                for (int i = 0; i < targetTaskInstancesList.Count; i++)
                {
                    TaskInstance taskInstTemp = (TaskInstance)targetTaskInstancesList[i];

                    persistenceService.abortTaskInstance(taskInstTemp);

                    if (!(targetActivityIdList.IndexOf(taskInstTemp.ActivityId) >= 0))
                    {
                        targetActivityIdList.Add(taskInstTemp.ActivityId);
                        if (theFromActivityIds.Length == 0)
                        {
                            theFromActivityIds.Append(taskInstTemp.ActivityId);
                        }
                        else
                        {
                            theFromActivityIds.Append(TokenFrom.FROM_ACTIVITY_ID_SEPARATOR).Append(taskInstTemp.ActivityId);
                        }
                    }
                }

                persistenceService.deleteTokensForNodes(thisTaskInstance.ProcessInstanceId, targetActivityIdList);

                if (this.RuntimeContext.isEnableTrace())
                {
                    for (int i = 0; targetActivityIdList != null && i < targetActivityIdList.Count; i++)
                    {
                        String tmpActId = (String)targetActivityIdList[i];
                        ProcessInstanceTrace trace = new ProcessInstanceTrace();
                        trace.setProcessInstanceId(thisTaskInstance.ProcessInstanceId);
                        trace.setStepNumber(newStepNumber);
                        trace.setType(ProcessInstanceTrace.WITHDRAW_TYPE);
                        trace.setFromNodeId(tmpActId);
                        trace.setToNodeId(thisActivity.Id);
                        trace.setEdgeId("");
                        this.RuntimeContext.PersistenceService.saveOrUpdateProcessInstanceTrace(trace);
                    }
                }

                ITransitionInstance thisLeavingTransitionInstance = (ITransitionInstance)thisActivityInstance.LeavingTransitionInstances[0];
                ISynchronizerInstance synchronizerInstance = (ISynchronizerInstance)thisLeavingTransitionInstance.getLeavingNodeInstance();
                if (synchronizerInstance.EnteringTransitionInstances.Count > 1)
                {
                    Token supplementToken = new Token();
                    ((Token)supplementToken).IsAlive = false;
                    ((Token)supplementToken).NodeId = synchronizerInstance.getSynchronizer().Id;
                    supplementToken.ProcessInstanceId = thisTaskInstance.ProcessInstanceId;
                    supplementToken.ProcessInstance = ((TaskInstance)thisTaskInstance).getAliveProcessInstance();
                    supplementToken.FromActivityId = "Empty(created by withdraw)";
                    supplementToken.StepNumber = newStepNumber;
                    supplementToken.Value = synchronizerInstance.Volume - thisLeavingTransitionInstance.getWeight();
                    persistenceService.saveOrUpdateToken(supplementToken);
                }

                Token newToken = new Token();
                ((Token)newToken).IsAlive = true;
                ((Token)newToken).NodeId = workItem.getTaskInstance().ActivityId;
                newToken.ProcessInstanceId = thisTaskInstance.ProcessInstanceId;
                newToken.ProcessInstance = ((TaskInstance)thisTaskInstance).getAliveProcessInstance();
                newToken.FromActivityId = theFromActivityIds.ToString();
                newToken.StepNumber = newStepNumber;
                newToken.Value = 0;
                persistenceService.saveOrUpdateToken(newToken);

                this.createTaskInstances(newToken, thisActivityInstance);

                List<IWorkItem> workItems = persistenceService.findTodoWorkItems(workItem.getActorId(), workItem.getTaskInstance().ProcessId, workItem.getTaskInstance().TaskId);
                if (workItems == null || workItems.Count == 0)
                {
                    throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.TaskId, "Withdraw operation failed.No work item has been created for Task[id=" + thisTaskInstance.TaskId + "]");
                }
                if (workItems.Count > 1)
                {
                    throw new EngineException(thisTaskInstance.ProcessInstanceId, thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.TaskId, "Withdraw operation failed.More than one work item have been created for Task[id=" + thisTaskInstance.TaskId + "]");

                }

                return (IWorkItem)workItems[0];
            }
            finally
            {
                session.setWithdrawOrRejectOperationFlag(false);
            }
        }

        public IWorkItem reasignWorkItemTo(IWorkItem workItem, String actorId, String comments)
        {
            WorkItem newWorkItem = new WorkItem();
            BeanUtils.CopyProperties(workItem, newWorkItem, OptionTyp.None);
            newWorkItem.setId(null);
            newWorkItem.setActorId(actorId);
            newWorkItem.setCreatedTime(this.RuntimeContext.getCalendarService().getSysDate());
            this.RuntimeContext.PersistenceService.saveOrUpdateWorkItem(newWorkItem);

            ((WorkItem)workItem).setState(IWorkItem.CANCELED);
            ((WorkItem)workItem).setEndTime(this.RuntimeContext.getCalendarService().getSysDate());
            ((WorkItem)workItem).setComments(comments);
            this.RuntimeContext.PersistenceService.saveOrUpdateWorkItem(workItem);
            return newWorkItem;
        }

        public ITaskInstanceCompletionEvaluator getDefaultFormTaskInstanceCompletionEvaluator()
        {
            return DefaultFormTaskInstanceCompletionEvaluator;
        }

        public void setDefaultFormTaskInstanceCompletionEvaluator(ITaskInstanceCompletionEvaluator defaultFormTaskInstanceCompletionEvaluator)
        {
            this.DefaultFormTaskInstanceCompletionEvaluator = defaultFormTaskInstanceCompletionEvaluator;
        }

        public ITaskInstanceRunner getDefaultFormTaskInstanceRunner()
        {
            return DefaultFormTaskInstanceRunner;
        }

        public void setDefaultFormTaskInstanceRunner(ITaskInstanceRunner defaultFormTaskInstanceRunner)
        {
            this.DefaultFormTaskInstanceRunner = defaultFormTaskInstanceRunner;
        }

        public ITaskInstanceCompletionEvaluator getDefaultSubflowTaskInstanceCompletionEvaluator()
        {
            return DefaultSubflowTaskInstanceCompletionEvaluator;
        }

        public void setDefaultSubflowTaskInstanceCompletionEvaluator(ITaskInstanceCompletionEvaluator defaultSubflowTaskInstanceCompletionEvaluator)
        {
            this.DefaultSubflowTaskInstanceCompletionEvaluator = defaultSubflowTaskInstanceCompletionEvaluator;
        }

        public ITaskInstanceRunner getDefaultSubflowTaskInstanceRunner()
        {
            return DefaultSubflowTaskInstanceRunner;
        }

        public void setDefaultSubflowTaskInstanceRunner(ITaskInstanceRunner defaultSubflowTaskInstanceRunner)
        {
            this.DefaultSubflowTaskInstanceRunner = defaultSubflowTaskInstanceRunner;
        }

        public ITaskInstanceCreator getDefaultTaskInstanceCreator()
        {
            return DefaultTaskInstanceCreator;
        }

        public void setDefaultTaskInstanceCreator(ITaskInstanceCreator defaultTaskInstanceCreator)
        {
            this.DefaultTaskInstanceCreator = defaultTaskInstanceCreator;
        }

        public ITaskInstanceCompletionEvaluator getDefaultToolTaskInstanceCompletionEvaluator()
        {
            return DefaultToolTaskInstanceCompletionEvaluator;
        }

        public void setDefaultToolTaskInstanceCompletionEvaluator(ITaskInstanceCompletionEvaluator defaultToolTaskInstanceCompletionEvaluator)
        {
            this.DefaultToolTaskInstanceCompletionEvaluator = defaultToolTaskInstanceCompletionEvaluator;
        }

        public ITaskInstanceRunner getDefaultToolTaskInstanceRunner()
        {
            return DefaultToolTaskInstanceRunner;
        }

        public void setDefaultToolTaskInstanceRunner(ITaskInstanceRunner defaultToolTaskInstanceRunner)
        {
            this.DefaultToolTaskInstanceRunner = defaultToolTaskInstanceRunner;
        }

        public ITaskInstanceEventListener getDefaultTaskInstanceEventListener()
        {
            return DefaultTaskInstanceEventListener;
        }

        public void setDefaultTaskInstanceEventListener(
                ITaskInstanceEventListener defaultTaskInstanceEventListener)
        {
            this.DefaultTaskInstanceEventListener = defaultTaskInstanceEventListener;
        }
    }
}
