﻿using System;
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
    public class BasicTaskInstanceManager :
            ITaskInstanceManager
    {

        ITaskInstanceCreator defaultTaskInstanceCreator = null;
        ITaskInstanceRunner defaultFormTaskInstanceRunner = null;
        ITaskInstanceRunner defaultSubflowTaskInstanceRunner = null;
        ITaskInstanceRunner defaultToolTaskInstanceRunner = null;
        ITaskInstanceCompletionEvaluator defaultFormTaskInstanceCompletionEvaluator = null;
        ITaskInstanceCompletionEvaluator defaultToolTaskInstanceCompletionEvaluator = null;
        ITaskInstanceCompletionEvaluator defaultSubflowTaskInstanceCompletionEvaluator = null;

        ITaskInstanceEventListener defaultTaskInstanceEventListener = null;

        protected RuntimeContext rtCtx = null;

        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.rtCtx = ctx;
        }

        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
        }

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
            IPersistenceService persistenceService = rtCtx.getPersistenceService();
            ICalendarService calService = rtCtx.getCalendarService();

            IProcessInstance processInstance = token.getProcessInstance();
            WorkflowSession workflowSession = (WorkflowSession)((IWorkflowSessionAware)processInstance).getCurrentWorkflowSession();

            if (workflowSession == null)
            {
                throw new EngineException(token.getProcessInstance(),
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

                String taskType = task.getType();
                ((TaskInstance)taskInstance).setTaskType(taskType);
                ((TaskInstance)taskInstance).setStepNumber(token.getStepNumber());
                //            ((TaskInstance) taskInstance).setTokenId(token.getId());
                ((TaskInstance)taskInstance).setProcessInstanceId(processInstance.getId());
                ((TaskInstance)taskInstance).setProcessId(processInstance.getProcessId());
                ((TaskInstance)taskInstance).setVersion(processInstance.getVersion());
                ((TaskInstance)taskInstance).setActivityId(activity.getId());
                if (Task.FORM.Equals(taskType))
                {
                    ((TaskInstance)taskInstance).setAssignmentStrategy(((FormTask)task).getAssignmentStrategy());
                    ((TaskInstance)taskInstance).setCanBeWithdrawn(true);
                }
                else
                {
                    ((TaskInstance)taskInstance).setCanBeWithdrawn(false);
                }
                ((TaskInstance)taskInstance).setCreatedTime(calService.getSysDate());
                ((TaskInstance)taskInstance).setDisplayName(task.getDisplayName());
                ((TaskInstance)taskInstance).setName(task.getName());

                ((TaskInstance)taskInstance).setState(TaskInstance.INITIALIZED);

                ((TaskInstance)taskInstance).setTaskId(task.getId());

                ((TaskInstance)taskInstance).setFromActivityId(token.getFromActivityId());

                ((IRuntimeContextAware)taskInstance).setRuntimeContext(rtCtx);
                ((IWorkflowSessionAware)taskInstance).setCurrentWorkflowSession(workflowSession);
                //计算超时
                Duration duration = task.getDuration();

                if (duration != null && calService != null)
                {
                    ((TaskInstance)taskInstance).setExpiredTime(calService.dateAfter(calService.getSysDate(), duration));
                }



                // 2、保存实例taskInstance
                persistenceService.saveOrUpdateTaskInstance(taskInstance);

                //3、启动实例
                this.startTaskInstance(workflowSession, processInstance, taskInstance);

            }
            if (createdTaskInstanceCount == 0)
            {
                activityInstance.complete(token, null);
            }
        }

        public ITaskInstance createTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance, Task task,
                Activity activity)// throws EngineException 
        {
            //如果loopStrategy为SKIP且Task被执行过，则直接返回null;
            IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
            String loopStrategy = task.getLoopStrategy();
            if (loopStrategy != null && Task.SKIP.Equals(loopStrategy) && !currentSession.isInWithdrawOrRejectOperation())
            {
                //检查是否已经执行过的task instance
                Int32 count = persistenceService.getCompletedTaskInstanceCountForTask(processInstance.getId(), task.getId());
                if (count > 0)
                {
                    return null;
                }
            }

            String taskInstanceCreatorName = null;
            ITaskInstanceCreator taskInstanceCreator = null;

            //首先查找Task级别的TaskInstanceCreator
            taskInstanceCreatorName = task.getTaskInstanceCreator();
            if (taskInstanceCreatorName != null && !taskInstanceCreatorName.Trim().Equals(""))
            {
                IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
                taskInstanceCreator = (ITaskInstanceCreator)beanFactory.getBean(taskInstanceCreatorName);
            }
            //如果没有，则查询流程级别的TaskInstanceCreator
            if (taskInstanceCreator == null)
            {
                taskInstanceCreatorName = processInstance.getWorkflowProcess().getTaskInstanceCreator();
                if (taskInstanceCreatorName != null && !taskInstanceCreatorName.Trim().Equals(""))
                {
                    IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
                    taskInstanceCreator = (ITaskInstanceCreator)beanFactory.getBean(taskInstanceCreatorName);
                }
            }

            //如果流程定义中也没有指定TaskInstanceCreator,则用缺省的
            if (taskInstanceCreator == null)
            {
                taskInstanceCreator = defaultTaskInstanceCreator;
            }

            return taskInstanceCreator.createTaskInstance(currentSession, rtCtx, processInstance, task, activity);
        }

        /// <summary>
        /// 启动TaskInstance。
        /// 该方法定义为final,不允许子类扩展。
        /// </summary>
        /// <param name="currentSession"></param>
        /// <param name="processInstance"></param>
        /// <param name="taskInstance"></param>
        public void startTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance,
                ITaskInstance taskInstance)// throws EngineException, KernelException 
        {

            //触发事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(taskInstance);
            e.setWorkflowSession(currentSession);
            e.setProcessInstance(processInstance);
            e.setEventType(TaskInstanceEvent.BEFORE_TASK_INSTANCE_START);
            if (defaultTaskInstanceEventListener != null)
            {
                defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }
            this.fireTaskInstanceEvent(taskInstance, e);

            ((TaskInstance)taskInstance).setState(ITaskInstance.RUNNING);
            ((TaskInstance)taskInstance).setStartedTime(this.rtCtx.getCalendarService().getSysDate());
            this.rtCtx.getPersistenceService().saveOrUpdateTaskInstance(taskInstance);

            Task task = taskInstance.getTask();
            String taskInstanceRunnerName = null;
            ITaskInstanceRunner taskInstanceRunner = null;

            String taskType = task.getType();

            taskInstanceRunnerName = task.getTaskInstanceRunner();
            if (taskInstanceRunnerName != null && !taskInstanceRunnerName.Trim().Equals(""))
            {
                IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
                taskInstanceRunner = (ITaskInstanceRunner)beanFactory.getBean(taskInstanceRunnerName);
            }

            if (taskInstanceRunner == null)
            {
                if (Task.FORM.Equals(taskType))
                {
                    taskInstanceRunnerName = processInstance.getWorkflowProcess().getFormTaskInstanceRunner();
                }
                else if (Task.TOOL.Equals(taskType))
                {
                    taskInstanceRunnerName = processInstance.getWorkflowProcess().getToolTaskInstanceRunner();
                }
                else if (Task.SUBFLOW.Equals(taskType))
                {
                    taskInstanceRunnerName = processInstance.getWorkflowProcess().getSubflowTaskInstanceRunner();
                }
                if (taskInstanceRunnerName != null && !taskInstanceRunnerName.Trim().Equals(""))
                {
                    IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
                    taskInstanceRunner = (ITaskInstanceRunner)beanFactory.getBean(taskInstanceRunnerName);
                }
            }

            if (taskInstanceRunner == null)
            {
                if (Task.FORM.Equals(taskType))
                {
                    taskInstanceRunner = defaultFormTaskInstanceRunner;
                }
                else if (Task.TOOL.Equals(taskType))
                {
                    taskInstanceRunner = defaultToolTaskInstanceRunner;
                }
                else if (Task.SUBFLOW.Equals(taskType))
                {
                    taskInstanceRunner = defaultSubflowTaskInstanceRunner;
                }
            }
            if (taskInstanceRunner != null)
            {
                taskInstanceRunner.run(currentSession, this.rtCtx, processInstance, taskInstance);
            }
            else
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "无法获取TaskInstanceRunner,TaskId=" + task.getId() + ", taskType=" + taskInstance.getTaskType());
            }
        }

        /**
         * 判断TaskInstance是否可以结束，缺省的判断规则是：没有活动的WorkItem即可结束。
         * 业务代码可以重载该函数，对特定的Task采取特殊的判断规则。
         * @param taskInstance
         * @return
         */
        protected Boolean taskInstanceCanBeCompleted(IWorkflowSession currentSession, RuntimeContext runtimeContext,
                IProcessInstance processInstance, ITaskInstance taskInstance)
        {
            Task task = taskInstance.getTask();
            String taskInstanceCompletionEvaluatorName = null;
            ITaskInstanceCompletionEvaluator taskInstanceCompletionEvaluator = null;

            String taskType = task.getType();

            taskInstanceCompletionEvaluatorName = task.getTaskInstanceCompletionEvaluator();
            if (taskInstanceCompletionEvaluatorName != null && !taskInstanceCompletionEvaluatorName.Trim().Equals(""))
            {
                IBeanFactory beanFactory = runtimeContext.getBeanFactory();
                taskInstanceCompletionEvaluator = (ITaskInstanceCompletionEvaluator)beanFactory.getBean(taskInstanceCompletionEvaluatorName);
            }

            if (taskInstanceCompletionEvaluator == null)
            {
                if (Task.FORM.Equals(taskType))
                {
                    taskInstanceCompletionEvaluatorName = processInstance.getWorkflowProcess().getFormTaskInstanceCompletionEvaluator();
                }
                else if (Task.TOOL.Equals(taskType))
                {
                    taskInstanceCompletionEvaluatorName = processInstance.getWorkflowProcess().getToolTaskInstanceCompletionEvaluator();
                }
                else if (Task.SUBFLOW.Equals(taskType))
                {
                    taskInstanceCompletionEvaluatorName = processInstance.getWorkflowProcess().getSubflowTaskInstanceCompletionEvaluator();
                }
                if (taskInstanceCompletionEvaluatorName != null && !taskInstanceCompletionEvaluatorName.Trim().Equals(""))
                {
                    IBeanFactory beanFactory = runtimeContext.getBeanFactory();
                    taskInstanceCompletionEvaluator = (ITaskInstanceCompletionEvaluator)beanFactory.getBean(taskInstanceCompletionEvaluatorName);
                }
            }

            if (taskInstanceCompletionEvaluator == null)
            {
                if (Task.FORM.Equals(taskType))
                {
                    taskInstanceCompletionEvaluator = this.defaultFormTaskInstanceCompletionEvaluator;
                }
                else if (Task.TOOL.Equals(taskType))
                {
                    taskInstanceCompletionEvaluator = this.defaultToolTaskInstanceCompletionEvaluator;
                }
                else if (Task.SUBFLOW.Equals(taskType))
                {
                    taskInstanceCompletionEvaluator = this.defaultSubflowTaskInstanceCompletionEvaluator;
                }
            }
            if (taskInstanceCompletionEvaluator != null)
            {
                return taskInstanceCompletionEvaluator.taskInstanceCanBeCompleted(currentSession, runtimeContext, processInstance, taskInstance);
            }
            else
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "无法获取TaskInstanceCompletionEvaluator,TaskId=" + task.getId() + ", taskType=" + taskInstance.getTaskType());
            }
        }

        protected Boolean activityInstanceCanBeCompleted(ITaskInstance taskInstance)
        {
            IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
            Activity thisActivity = (Activity)taskInstance.getActivity();
            //检查是否有尚未创建的TaskInstance
            if (thisActivity.getTasks().Count > 1)
            {
                List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstanceByStepNumber(taskInstance.getProcessInstanceId(), taskInstance.getStepNumber());
                if (taskInstanceList == null || taskInstanceList.Count < thisActivity.getTasks().Count)
                {
                    return false;
                }
            }
            if (thisActivity.getCompletionStrategy().Equals(Activity.ALL))
            {
                Int32 aliveTaskInstanceCount4ThisActivity = persistenceService.getAliveTaskInstanceCountForActivity(taskInstance.getProcessInstanceId(), taskInstance.getActivityId());

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


        /**
         * 终止当前TaskInstance，检查是否可以中止当前ActivityInstance，如果可以，
         * 则结束当前ActivityInstance，并触发targetActivityInstance或后继ActivityInstance
         * @param taskInstance
         * @param targetActivityInstance
         * @throws org.fireflow.engine.EngineException
         * @throws org.fireflow.kernel.KernelException
         */
        public void completeTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance,
                ITaskInstance taskInstance, IActivityInstance targetActivityInstance)
        {
            //如果TaskInstance处于结束状态，则直接返回
            if (taskInstance.getState() == ITaskInstance.COMPLETED || taskInstance.getState() == ITaskInstance.CANCELED)
            {
                return;
            }
            if (taskInstance.getState() == ITaskInstance.INITIALIZED)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "Complete task insatance failed.The state of the task insatnce[id=" + taskInstance.getId() + "] is " + taskInstance.getState());
            }
            if (taskInstance.isSuspended()!=null && (bool)taskInstance.isSuspended())
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "Complete task insatance failed. The task instance [id=" + taskInstance.getId() + "] is suspended");
            }

            if (targetActivityInstance != null)
            {
                ((TaskInstance)taskInstance).setTargetActivityId(targetActivityInstance.getActivity().getId());
            }

            IPersistenceService persistenceService = this.rtCtx.getPersistenceService();

            //第一步，首先结束当前taskInstance
            if (!this.taskInstanceCanBeCompleted(currentSession, this.rtCtx, processInstance, taskInstance))
            {
                return;
            }
            ((TaskInstance)taskInstance).setState(ITaskInstance.COMPLETED);
            ((TaskInstance)taskInstance).setCanBeWithdrawn(false);
            ((TaskInstance)taskInstance).setEndTime(rtCtx.getCalendarService().getSysDate());
            persistenceService.saveOrUpdateTaskInstance(taskInstance);
            //触发相应的事件
            TaskInstanceEvent e = new TaskInstanceEvent();
            e.setSource(taskInstance);
            e.setWorkflowSession(currentSession);
            e.setProcessInstance(processInstance);
            e.setEventType(TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE);
            if (this.defaultTaskInstanceEventListener != null)
            {
                this.defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }

            this.fireTaskInstanceEvent(taskInstance, e);

            //第二步，检查ActivityInstance是否可以结束
            if (!activityInstanceCanBeCompleted(taskInstance))
            {
                return;
            }

            //第三步，尝试结束对应的activityInstance
            List<IToken> tokens = persistenceService.findTokensForProcessInstance(taskInstance.getProcessInstanceId(), taskInstance.getActivityId());
            //        System.out.println("Inside TaskInstance.complete(targetActivityInstance):: tokens.size is "+tokens.Count);
            if (tokens == null || tokens.Count == 0)
            {
                return;//表明activityInstance已经结束了。
            }
            if (tokens.Count > 1)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "与activityId=" + taskInstance.getActivityId() + "对应的token数量(=" + tokens.Count + ")不正确，正确只能为1，因此无法完成complete操作");
            }
            IToken token = tokens[0];
            //stepNumber不相等，不允许执行结束操作。
            if (token.getStepNumber() != taskInstance.getStepNumber())
            {
                return;
            }
            if (token.isAlive() == false)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "与activityId=" + taskInstance.getActivityId() + "对应的token.alive=false，因此无法完成complete操作");
            }

            INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(taskInstance.getProcessId(), taskInstance.getVersion());
            Object obj = netInstance.getWFElementInstance(taskInstance.getActivityId());
            if (obj == null)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "系统没有找到与activityId=" + taskInstance.getActivityId() + "对应activityInstance，无法执行complete操作。");
            }


            token.setProcessInstance(processInstance);

            ((IActivityInstance)obj).complete(token, targetActivityInstance);

        }

        /**
         * 触发task instance相关的事件
         * @param e
         * @throws org.fireflow.engine.EngineException
         */
        protected  void fireTaskInstanceEvent(ITaskInstance taskInstance, TaskInstanceEvent e)
        {
            Task task = taskInstance.getTask();
            if (task == null)
            {
                return;
            }

            List<EventListener> listeners = task.getEventListeners();
            for (int i = 0; i < listeners.Count; i++)
            {
                EventListener listener = (EventListener)listeners[i];
                Object obj = rtCtx.getBeanByName(listener.getClassName());
                if (obj != null && (obj is ITaskInstanceEventListener))
                {
                    ((ITaskInstanceEventListener)obj).onTaskInstanceEventFired(e);
                }
            }
        }

        public WorkItem createWorkItem(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance, String actorId)
        {
            IPersistenceService persistenceService = rtCtx.getPersistenceService();

            WorkItem wi = new WorkItem();
            wi.setTaskInstance(taskInstance);
            wi.setActorId(actorId);
            wi.setState(IWorkItem.INITIALIZED);
            wi.setCreatedTime(rtCtx.getCalendarService().getSysDate());
            wi.setRuntimeContext(rtCtx);
            wi.setCurrentWorkflowSession(currentSession);
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
            if (this.defaultTaskInstanceEventListener != null)
            {
                this.defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
            }
            this.fireTaskInstanceEvent(taskInstance, e);

            return wi;
        }



        public IWorkItem claimWorkItem(String workItemId, String taskInstanceId)
        {
            IPersistenceService persistenceService = rtCtx.getPersistenceService();

            persistenceService.lockTaskInstance(taskInstanceId);

            IWorkItem workItem = persistenceService.findWorkItemById(workItemId);

            if (workItem == null) return null;

            if (workItem.getState() != IWorkItem.INITIALIZED)
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(),
                        "Claim work item failed. The state of the work item is " + workItem.getState());
            }
            if (workItem.getTaskInstance().getState() != ITaskInstance.INITIALIZED && workItem.getTaskInstance().getState() != ITaskInstance.RUNNING)
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(),
                        "Claim work item failed .The state of the correspond task instance is " + workItem.getTaskInstance().getState());
            }

            if (workItem.getTaskInstance().isSuspended() != null && (bool)workItem.getTaskInstance().isSuspended())
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(),
                        "Claim work item failed .The  correspond task instance is suspended");
            }



            //0、首先修改workitem的状态
            ((WorkItem)workItem).setState(IWorkItem.RUNNING);
            ((WorkItem)workItem).setClaimedTime(rtCtx.getCalendarService().getSysDate());
            persistenceService.saveOrUpdateWorkItem(workItem);

            //1、如果不是会签，则删除其他的workitem
            if (FormTask.ANY.Equals(workItem.getTaskInstance().getAssignmentStrategy()))
            {
                persistenceService.deleteWorkItemsInInitializedState(workItem.getTaskInstance().getId());
            }

            //2、将TaskInstance的canBeWithdrawn字段改称false。即不允许被撤销
            TaskInstance taskInstance = (TaskInstance)workItem.getTaskInstance();
            taskInstance.setCanBeWithdrawn(false);
            persistenceService.saveOrUpdateTaskInstance(taskInstance);

            return workItem;
            //不需要做这个工作,2009-03-21
            //        ((TaskInstance) workItem.getTaskInstance()).start();

        }

        public void completeWorkItem(IWorkItem workItem, IActivityInstance targetActivityInstance, String comments)
        {
            if (workItem.getState() != IWorkItem.RUNNING)
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                //			System.out.println("WorkItem的当前状态为"+this.getState()+"，不可以执行complete操作。");
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(),
                        "Complete work item failed . The state of the work item [id=" + workItem.getId() + "] is " + workItem.getState());
            }

            if (workItem.getTaskInstance().isSuspended() != null && (bool)workItem.getTaskInstance().isSuspended())
            {
                TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
                WorkflowProcess process = thisTaskInst.getWorkflowProcess();
                throw new EngineException(thisTaskInst.getProcessInstanceId(), process,
                        thisTaskInst.getTaskId(),
                        "Complete work item failed. The correspond task instance [id=" + thisTaskInst.getId() + "] is suspended");
            }

            IPersistenceService persistenceService = rtCtx.getPersistenceService();

            ((WorkItem)workItem).setComments(comments);
            ((WorkItem)workItem).setState(IWorkItem.COMPLETED);
            ((WorkItem)workItem).setEndTime(rtCtx.getCalendarService().getSysDate());
            persistenceService.saveOrUpdateWorkItem(workItem);
            ((TaskInstance)workItem.getTaskInstance()).complete(targetActivityInstance);
        }

        public void completeWorkItemAndJumpTo(IWorkItem workItem, String targetActivityId, String comments)
        {
            WorkflowSession workflowSession = (WorkflowSession)((IWorkflowSessionAware)workItem).getCurrentWorkflowSession();
            //首先检查是否可以正确跳转
            //1）检查是否在同一个“执行线”上
            WorkflowProcess workflowProcess = workItem.getTaskInstance().getWorkflowProcess();
            String thisActivityId = workItem.getTaskInstance().getActivityId();
            Boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId, targetActivityId);
            TaskInstance thisTaskInst = (TaskInstance)workItem.getTaskInstance();
            if (!isInSameLine)
            {
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(), "Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
            }


            //2）检查目标Activity Form Task的数量(暂时关闭该检查项目)
            //        Activity targetActivity = (Activity)workflowProcess.findWFElementById(activityId);
            //        int count = getFormTaskCount(targetActivity);
            //        if (count!=1){
            //            if (!isInSameLine) throw new EngineException("Jumpto refused because of the  FORM-type-task count of the target activitgy  is NOT 1; the count is "+count);
            //        }

            //3)检查当前的 taskinstance是否可以结束
            IPersistenceService persistenceService = rtCtx.getPersistenceService();

            Int32 aliveWorkItemCount = persistenceService.getAliveWorkItemCountForTaskInstance(thisTaskInst.getId());
            if (aliveWorkItemCount > 1)
            {
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(), "Jumpto refused because of current taskinstance can NOT be completed. some workitem of this taskinstance is in runing state or initialized state");

            }

            //4)检查当前的activity instance是否可以结束
            if (Activity.ALL.Equals(workItem.getTaskInstance().getActivity().getCompletionStrategy()))
            {

                Int32 aliveTaskInstanceCount4ThisActivity = persistenceService.getAliveTaskInstanceCountForActivity(workItem.getTaskInstance().getProcessInstanceId(), workItem.getTaskInstance().getActivityId());
                if (aliveTaskInstanceCount4ThisActivity > 1)
                {//大于2表明当前Activity不可以complete
                    throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                            thisTaskInst.getTaskId(), "Jumpto refused because of current activity instance can NOT be completed. some task instance of this activity instance is in runing state or initialized state");
                }
            }

            INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(workflowProcess.getId(), workItem.getTaskInstance().getVersion());
            if (netInstance == null)
            {
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(), "Not find the net instance for workflow process [id=" + workflowProcess.getId() + ", version=" + workItem.getTaskInstance().getVersion() + "]");
            }
            Object obj = netInstance.getWFElementInstance(targetActivityId);
            IActivityInstance targetActivityInstance = (IActivityInstance)obj;
            if (targetActivityInstance == null)
            {
                throw new EngineException(thisTaskInst.getProcessInstanceId(), thisTaskInst.getWorkflowProcess(),
                        thisTaskInst.getTaskId(), "Not find the activity instance  for activity[process id=" + workflowProcess.getId() + ", version=" + workItem.getTaskInstance().getVersion() + ",activity id=" + targetActivityId + "]");
            }


            if (rtCtx.isEnableTrace())
            {

                ProcessInstanceTrace trace = new ProcessInstanceTrace();
                trace.setProcessInstanceId(workItem.getTaskInstance().getProcessInstanceId());
                trace.setStepNumber(workItem.getTaskInstance().getStepNumber() + 1);
                trace.setType(ProcessInstanceTrace.JUMPTO_TYPE);
                trace.setFromNodeId(workItem.getTaskInstance().getActivityId());
                trace.setToNodeId(targetActivityId);
                trace.setEdgeId("");
                rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(trace);
            }

            this.completeWorkItem(workItem, targetActivityInstance, comments);
        }

        public void rejectWorkItem(IWorkItem workItem, String comments)
        {
            Activity thisActivity = workItem.getTaskInstance().getActivity();
            TaskInstance thisTaskInstance = (TaskInstance)workItem.getTaskInstance();
            if (workItem.getState() > 5 || (workItem.getTaskInstance().isSuspended()!=null && (bool)workItem.getTaskInstance().isSuspended()))
            {//处于非活动状态,或者被挂起,则不允许reject
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(),
                        "Reject operation refused!Current work item is completed or the correspond task instance is suspended!!");
            }
            //当前Activity只允许一个Form类型的Task
            if (thisActivity.getTasks().Count > 1)
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(),
                        "Reject operation refused!The correspond activity has more than 1 tasks");
            }
            //汇签Task不允许Reject
            if (FormTask.ALL.Equals(thisTaskInstance.getAssignmentStrategy()))
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(),
                        "Reject operation refused!The assignment strategy is 'ALL'");
            }

            IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
            List<ITaskInstance> siblingTaskInstancesList = null;

            siblingTaskInstancesList = persistenceService.findTaskInstancesForProcessInstanceByStepNumber(workItem.getTaskInstance().getProcessInstanceId(),
                    (int)thisTaskInstance.getStepNumber());

            //如果执行了split操作，则不允许reject
            if (siblingTaskInstancesList.Count > 1)
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(), "Reject operation refused!Because the process instance has taken a split operation.");
            }

            //检查From Activity中是否有ToolTask和SubflowTask
            List<String> fromActivityIdList = new List<String>();
            String[] tokenizer = thisTaskInstance.getFromActivityId().Split(new String[] { IToken.FROM_ACTIVITY_ID_SEPARATOR }, StringSplitOptions.RemoveEmptyEntries);//)new string[]{});
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
                    if (Task.TOOL.Equals(task.getType()) || Task.SUBFLOW.Equals(task.getType()))
                    {
                        throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                                thisTaskInstance.getTaskId(), "Reject operation refused!The previous activity contains tool-task or subflow-task");

                    }
                }
            }
            //恢复所有的FromTaskInstance
            INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(workflowProcess.getId(), workItem.getTaskInstance().getVersion());
            if (netInstance == null)
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(), "Not find the net instance for workflow process [id=" + workflowProcess.getId() + ", version=" + workItem.getTaskInstance().getVersion() + "]");
            }

            //执行reject操作。

            IWorkflowSession session = ((IWorkflowSessionAware)workItem).getCurrentWorkflowSession();
            session.setWithdrawOrRejectOperationFlag(true);
            int newStepNumber = (int)thisTaskInstance.getStepNumber() + 1;
            try
            {
                //首先将本WorkItem和TaskInstance cancel掉。
                workItem.setComments(comments);
                ((WorkItem)workItem).setState(IWorkItem.CANCELED);
                ((WorkItem)workItem).setEndTime(rtCtx.getCalendarService().getSysDate());
                rtCtx.getPersistenceService().saveOrUpdateWorkItem(workItem);

                persistenceService.abortTaskInstance(thisTaskInstance);


                //删除本环节的token
                persistenceService.deleteTokensForNode(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getActivityId());


                IActivityInstance fromActivityInstance = null;
                for (int i = 0; i < fromActivityIdList.Count; i++)
                {
                    String fromActivityId = (String)fromActivityIdList[i];
                    Object obj = netInstance.getWFElementInstance(fromActivityId);
                    fromActivityInstance = (IActivityInstance)obj;
                    Token newToken = new Token();
                    ((Token)newToken).setAlive(true);
                    ((Token)newToken).setNodeId(fromActivityId);
                    newToken.setProcessInstanceId(thisTaskInstance.getProcessInstanceId());
                    newToken.setProcessInstance(((TaskInstance)thisTaskInstance).getAliveProcessInstance());
                    newToken.setFromActivityId(thisTaskInstance.getActivityId());
                    newToken.setStepNumber(newStepNumber);
                    newToken.setValue(0);
                    persistenceService.saveOrUpdateToken(newToken);

                    this.createTaskInstances(newToken, fromActivityInstance);

                    if (rtCtx.isEnableTrace())
                    {
                        ProcessInstanceTrace trace = new ProcessInstanceTrace();
                        trace.setProcessInstanceId(thisTaskInstance.getProcessInstanceId());
                        trace.setStepNumber(newStepNumber);
                        trace.setType(ProcessInstanceTrace.REJECT_TYPE);
                        trace.setFromNodeId(thisActivity.getId());
                        trace.setToNodeId(fromActivityId);
                        trace.setEdgeId("");
                        rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(trace);
                    }
                }

                ITransitionInstance theLeavingTransitionInstance = (ITransitionInstance)fromActivityInstance.getLeavingTransitionInstances()[0];
                ISynchronizerInstance synchronizerInstance = (ISynchronizerInstance)theLeavingTransitionInstance.getLeavingNodeInstance();
                if (synchronizerInstance.getEnteringTransitionInstances().Count > fromActivityIdList.Count)
                {
                    Token supplementToken = new Token();
                    ((Token)supplementToken).setAlive(false);
                    ((Token)supplementToken).setNodeId(synchronizerInstance.getSynchronizer().getId());
                    supplementToken.setProcessInstanceId(thisTaskInstance.getProcessInstanceId());
                    supplementToken.setProcessInstance(((TaskInstance)thisTaskInstance).getAliveProcessInstance());
                    supplementToken.setFromActivityId("EMPTY(created by reject)");
                    supplementToken.setStepNumber((int)thisTaskInstance.getStepNumber() + 1);
                    supplementToken.setValue(synchronizerInstance.getVolume() - theLeavingTransitionInstance.getWeight() * fromActivityIdList.Count);
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
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(),
                        "Withdraw operation is refused! Current workitem is in running state!!");
            }
            //当前Activity只允许一个Form类型的Task
            if (thisActivity.getTasks().Count > 1)
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(),
                        "Withdraw operation is refused! The activity[id=" + thisActivity.getId() + "] has more than 1 tasks");
            }

            //汇签Task不允许撤销
            if (FormTask.ALL.Equals(thisTaskInstance.getAssignmentStrategy()))
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(),
                        "Withdraw operation is refused! The assignment strategy for activity[id=" + thisActivity.getId() + "] is 'ALL'");
            }
            // Activity targetActivity = null;
            // List targetActivityList = new ArrayList();
            IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
            List<ITaskInstance> targetTaskInstancesList = null;
            targetTaskInstancesList = persistenceService.findTaskInstancesForProcessInstanceByStepNumber(thisTaskInstance.getProcessInstanceId(),
                    (int)thisTaskInstance.getStepNumber() + 1);

            // String targetActivityId = workItem.getTaskInstance().getTargetActivityId();


            //如果targetTaskInstancesList为空或size 等于0,则表示流程实例执行了汇聚操作。
            if (targetTaskInstancesList == null || targetTaskInstancesList.Count == 0)
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(), "Withdraw operation is refused!Because the process instance has taken a join operation after this activity[id=" + thisActivity.getId() + "].");
            }
            else
            {
                TaskInstance taskInstance = (TaskInstance)targetTaskInstancesList[0];
                if (!taskInstance.getFromActivityId().Equals(thisTaskInstance.getActivityId()))
                {
                    throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.getTaskId(), "Withdraw operation is refused!Because the process instance has taken a join operation after this activity[id=" + thisActivity.getId() + "].");
                }
            }

            for (int i = 0; targetTaskInstancesList != null && i < targetTaskInstancesList.Count; i++)
            {
                TaskInstance targetTaskInstanceTmp = (TaskInstance)targetTaskInstancesList[i];
                if (!targetTaskInstanceTmp.getCanBeWithdrawn())
                {
                    //说明已经有某些WorkItem处于已签收状态，或者已经处于完毕状态，此时不允许退回
                    throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.getTaskId(),
                            "Withdraw operation is refused! Some task instances of the  next activity[id=" + targetTaskInstanceTmp.getActivityId() + "] are not in 'Initialized' state");
                }
            }

            INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(thisTaskInstance.getProcessId(), workItem.getTaskInstance().getVersion());
            if (netInstance == null)
            {
                throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                        thisTaskInstance.getTaskId(), "Withdraw operation failed.Not find the net instance for workflow process [id=" + thisTaskInstance.getProcessId() + ", version=" + workItem.getTaskInstance().getVersion() + "]");
            }
            Object obj = netInstance.getWFElementInstance(thisTaskInstance.getActivityId());
            IActivityInstance thisActivityInstance = (IActivityInstance)obj;

            //一切检查通过之后进行“收回”处理

            IWorkflowSession session = ((IWorkflowSessionAware)workItem).getCurrentWorkflowSession();
            session.setWithdrawOrRejectOperationFlag(true);
            int newStepNumber = (int)thisTaskInstance.getStepNumber() + 2;
            try
            {
                DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
                List<String> actorIds = new List<String>();
                actorIds.Add(workItem.getActorId());
                dynamicAssignmentHandler.setActorIdsList(actorIds);
                ((WorkflowSession)session).setCurrentDynamicAssignmentHandler(dynamicAssignmentHandler);

                //1、首先将后续环节的TaskInstance极其workItem变成Canceled状态
                List<String> targetActivityIdList = new List<String>();

                StringBuilder theFromActivityIds = new StringBuilder();
                for (int i = 0; i < targetTaskInstancesList.Count; i++)
                {
                    TaskInstance taskInstTemp = (TaskInstance)targetTaskInstancesList[i];

                    persistenceService.abortTaskInstance(taskInstTemp);

                    if (!(targetActivityIdList.IndexOf(taskInstTemp.getActivityId()) >= 0))
                    {
                        targetActivityIdList.Add(taskInstTemp.getActivityId());
                        if (theFromActivityIds.Length == 0)
                        {
                            theFromActivityIds.Append(taskInstTemp.getActivityId());
                        }
                        else
                        {
                            theFromActivityIds.Append(IToken.FROM_ACTIVITY_ID_SEPARATOR).Append(taskInstTemp.getActivityId());
                        }
                    }
                }

                persistenceService.deleteTokensForNodes(thisTaskInstance.getProcessInstanceId(), targetActivityIdList);

                if (rtCtx.isEnableTrace())
                {
                    for (int i = 0; targetActivityIdList != null && i < targetActivityIdList.Count; i++)
                    {
                        String tmpActId = (String)targetActivityIdList[i];
                        ProcessInstanceTrace trace = new ProcessInstanceTrace();
                        trace.setProcessInstanceId(thisTaskInstance.getProcessInstanceId());
                        trace.setStepNumber(newStepNumber);
                        trace.setType(ProcessInstanceTrace.WITHDRAW_TYPE);
                        trace.setFromNodeId(tmpActId);
                        trace.setToNodeId(thisActivity.getId());
                        trace.setEdgeId("");
                        rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(trace);
                    }
                }

                ITransitionInstance thisLeavingTransitionInstance = (ITransitionInstance)thisActivityInstance.getLeavingTransitionInstances()[0];
                ISynchronizerInstance synchronizerInstance = (ISynchronizerInstance)thisLeavingTransitionInstance.getLeavingNodeInstance();
                if (synchronizerInstance.getEnteringTransitionInstances().Count > 1)
                {
                    Token supplementToken = new Token();
                    ((Token)supplementToken).setAlive(false);
                    ((Token)supplementToken).setNodeId(synchronizerInstance.getSynchronizer().getId());
                    supplementToken.setProcessInstanceId(thisTaskInstance.getProcessInstanceId());
                    supplementToken.setProcessInstance(((TaskInstance)thisTaskInstance).getAliveProcessInstance());
                    supplementToken.setFromActivityId("Empty(created by withdraw)");
                    supplementToken.setStepNumber(newStepNumber);
                    supplementToken.setValue(synchronizerInstance.getVolume() - thisLeavingTransitionInstance.getWeight());
                    persistenceService.saveOrUpdateToken(supplementToken);
                }



                Token newToken = new Token();
                ((Token)newToken).setAlive(true);
                ((Token)newToken).setNodeId(workItem.getTaskInstance().getActivityId());
                newToken.setProcessInstanceId(thisTaskInstance.getProcessInstanceId());
                newToken.setProcessInstance(((TaskInstance)thisTaskInstance).getAliveProcessInstance());
                newToken.setFromActivityId(theFromActivityIds.ToString());
                newToken.setStepNumber(newStepNumber);
                newToken.setValue(0);
                persistenceService.saveOrUpdateToken(newToken);

                this.createTaskInstances(newToken, thisActivityInstance);

                List<IWorkItem> workItems = persistenceService.findTodoWorkItems(workItem.getActorId(), workItem.getTaskInstance().getProcessId(), workItem.getTaskInstance().getTaskId());
                if (workItems == null || workItems.Count == 0)
                {
                    throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.getTaskId(), "Withdraw operation failed.No work item has been created for Task[id=" + thisTaskInstance.getTaskId() + "]");
                }
                if (workItems.Count > 1)
                {
                    throw new EngineException(thisTaskInstance.getProcessInstanceId(), thisTaskInstance.getWorkflowProcess(),
                            thisTaskInstance.getTaskId(), "Withdraw operation failed.More than one work item have been created for Task[id=" + thisTaskInstance.getTaskId() + "]");

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
            newWorkItem.setCreatedTime(rtCtx.getCalendarService().getSysDate());
            rtCtx.getPersistenceService().saveOrUpdateWorkItem(newWorkItem);

            ((WorkItem)workItem).setState(IWorkItem.CANCELED);
            ((WorkItem)workItem).setEndTime(rtCtx.getCalendarService().getSysDate());
            ((WorkItem)workItem).setComments(comments);
            rtCtx.getPersistenceService().saveOrUpdateWorkItem(workItem);
            return newWorkItem;
        }

        public ITaskInstanceCompletionEvaluator getDefaultFormTaskInstanceCompletionEvaluator()
        {
            return defaultFormTaskInstanceCompletionEvaluator;
        }

        public void setDefaultFormTaskInstanceCompletionEvaluator(ITaskInstanceCompletionEvaluator defaultFormTaskInstanceCompletionEvaluator)
        {
            this.defaultFormTaskInstanceCompletionEvaluator = defaultFormTaskInstanceCompletionEvaluator;
        }

        public ITaskInstanceRunner getDefaultFormTaskInstanceRunner()
        {
            return defaultFormTaskInstanceRunner;
        }

        public void setDefaultFormTaskInstanceRunner(ITaskInstanceRunner defaultFormTaskInstanceRunner)
        {
            this.defaultFormTaskInstanceRunner = defaultFormTaskInstanceRunner;
        }

        public ITaskInstanceCompletionEvaluator getDefaultSubflowTaskInstanceCompletionEvaluator()
        {
            return defaultSubflowTaskInstanceCompletionEvaluator;
        }

        public void setDefaultSubflowTaskInstanceCompletionEvaluator(ITaskInstanceCompletionEvaluator defaultSubflowTaskInstanceCompletionEvaluator)
        {
            this.defaultSubflowTaskInstanceCompletionEvaluator = defaultSubflowTaskInstanceCompletionEvaluator;
        }

        public ITaskInstanceRunner getDefaultSubflowTaskInstanceRunner()
        {
            return defaultSubflowTaskInstanceRunner;
        }

        public void setDefaultSubflowTaskInstanceRunner(ITaskInstanceRunner defaultSubflowTaskInstanceRunner)
        {
            this.defaultSubflowTaskInstanceRunner = defaultSubflowTaskInstanceRunner;
        }

        public ITaskInstanceCreator getDefaultTaskInstanceCreator()
        {
            return defaultTaskInstanceCreator;
        }

        public void setDefaultTaskInstanceCreator(ITaskInstanceCreator defaultTaskInstanceCreator)
        {
            this.defaultTaskInstanceCreator = defaultTaskInstanceCreator;
        }

        public ITaskInstanceCompletionEvaluator getDefaultToolTaskInstanceCompletionEvaluator()
        {
            return defaultToolTaskInstanceCompletionEvaluator;
        }

        public void setDefaultToolTaskInstanceCompletionEvaluator(ITaskInstanceCompletionEvaluator defaultToolTaskInstanceCompletionEvaluator)
        {
            this.defaultToolTaskInstanceCompletionEvaluator = defaultToolTaskInstanceCompletionEvaluator;
        }

        public ITaskInstanceRunner getDefaultToolTaskInstanceRunner()
        {
            return defaultToolTaskInstanceRunner;
        }

        public void setDefaultToolTaskInstanceRunner(ITaskInstanceRunner defaultToolTaskInstanceRunner)
        {
            this.defaultToolTaskInstanceRunner = defaultToolTaskInstanceRunner;
        }

        public ITaskInstanceEventListener getDefaultTaskInstanceEventListener()
        {
            return defaultTaskInstanceEventListener;
        }

        public void setDefaultTaskInstanceEventListener(
                ITaskInstanceEventListener defaultTaskInstanceEventListener)
        {
            this.defaultTaskInstanceEventListener = defaultTaskInstanceEventListener;
        }


    }
}