using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Taskinstance;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Engine.Impl
{

    [Serializable]
    public class WorkItem : IWorkItem, IRuntimeContextAware, IWorkflowSessionAware
    {
        private String actorId;
        private String id;
        private Int32 state;
        private DateTime createdTime;

        /// <summary>签收时间</summary>
        private DateTime claimedTime;
        private DateTime endTime;
        private String comments;
        private ITaskInstance taskInstance;

        private String taskInstanceId; //added by wangmj 20090922 供springjdbc实现类使用
        public String getTaskInstanceId()
        {
            return taskInstanceId;
        }
        public void setTaskInstanceId(String taskInstanceId)
        {
            this.taskInstanceId = taskInstanceId;
        }

        //[NonSerialized]
        public RuntimeContext RuntimeContext { get; set; }
        [NonSerialized]
        protected IWorkflowSession _workflowSession = null;
        public IWorkflowSession CurrentWorkflowSession
        {
            get { return this._workflowSession; }
            set
            {
                this._workflowSession = value;
                if (this.taskInstance != null)
                {
                    ((IWorkflowSessionAware)taskInstance).CurrentWorkflowSession = this._workflowSession;
                }
            }
        }


        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.RuntimeContext = ctx;
            if (this.taskInstance != null)
            {
                ((IRuntimeContextAware)taskInstance).RuntimeContext = this.RuntimeContext;
            }
        }



        public WorkItem()
        {
        }

        public WorkItem(TaskInstance taskInstance)
        {
            this.taskInstance = taskInstance;
        }

        public WorkItem(Int32 state, DateTime createdTime, DateTime signedTm,
                DateTime endTime, String comments, TaskInstance taskInstance)
        {
            this.state = state;
            this.createdTime = createdTime;
            this.claimedTime = signedTm;
            this.endTime = endTime;
            this.comments = comments;
            this.taskInstance = taskInstance;
        }

        public override String getId()
        {
            return this.id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public override Int32 getState()
        {
            return this.state;
        }

        public void setState(Int32 state)
        {
            this.state = state;
        }

        public override DateTime getCreatedTime()
        {
            return this.createdTime;
        }

        public void setCreatedTime(DateTime createdTime)
        {
            this.createdTime = createdTime;
        }

        /**
         * @deprecated 
         * @return
         */
        public override DateTime getSignedTime()
        {
            return this.claimedTime;
        }

        public override DateTime getClaimedTime()
        {
            return this.claimedTime;
        }

        /**
         * @deprecated
         * @param acceptedTime
         */
        public void setSignedTime(DateTime claimedTime)
        {
            this.claimedTime = claimedTime;
        }

        public void setClaimedTime(DateTime claimedTime)
        {
            this.claimedTime = claimedTime;
        }

        public override DateTime getEndTime()
        {
            return this.endTime;
        }

        public void setEndTime(DateTime endTime)
        {
            this.endTime = endTime;
        }

        public override String getComments()
        {
            return this.comments;
        }

        public override void setComments(String comments)
        {
            this.comments = comments;
        }

        public override ITaskInstance getTaskInstance()
        {
            return this.taskInstance;
        }

        public void setTaskInstance(ITaskInstance taskInstance)
        {
            this.taskInstance = taskInstance;
        }

        public override String getActorId()
        {
            return actorId;
        }

        public void setActorId(String actorId)
        {
            this.actorId = actorId;
        }

        public override IWorkItem withdraw()
        {
            if (this._workflowSession == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current workflow session is null.");
            }
            if (this.RuntimeContext == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current runtime context is null.");
            }
            ITaskInstanceManager taskInstanceMgr = this.RuntimeContext.TaskInstanceManager;
            return taskInstanceMgr.withdrawWorkItem(this);
        }


        public override void reject()
        {
            reject(this.getComments());
        }

        public override void reject(String comments)
        {
            if (this._workflowSession == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current workflow session is null.");
            }
            if (this.RuntimeContext == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current runtime context is null.");
            }
            ITaskInstanceManager taskInstanceMgr = this.RuntimeContext.TaskInstanceManager;
            taskInstanceMgr.rejectWorkItem(this, comments);
        }

        public override void complete()
        {
            complete(null, this.getComments());
        }

        public override void complete(String comments)
        {
            complete(null, comments);
        }



        public override void complete(DynamicAssignmentHandler dynamicAssignmentHandler, String comments)
        {
            if (this._workflowSession == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current workflow session is null.");
            }
            if (this.RuntimeContext == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current runtime context is null.");
            }
            if (dynamicAssignmentHandler != null)
            {
                this._workflowSession.setDynamicAssignmentHandler(dynamicAssignmentHandler);
            }
            ITaskInstanceManager taskInstanceManager = this.RuntimeContext.TaskInstanceManager;
            taskInstanceManager.completeWorkItem(this, null, comments);
        }

        public override IWorkItem reasignTo(String actorId)
        {
            return reasignTo(actorId, this.getComments());
        }

        public override IWorkItem reasignTo(String actorId, String comments)
        {
            if (this._workflowSession == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current workflow session is null.");
            }
            if (this.RuntimeContext == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current runtime context is null.");
            }

            ITaskInstanceManager manager = this.RuntimeContext.TaskInstanceManager;
            return manager.reasignWorkItemTo(this, actorId, comments);
        }

        /**
         * @deprecated 
         * @throws org.fireflow.engine.EngineException
         * @throws org.fireflow.kenel.KenelException
         */
        public override void sign()
        {
            claim();
        }

        /**
         * 签收
         * @throws org.fireflow.engine.EngineException
         * @throws org.fireflow.kenel.KenelException
         */
        public override IWorkItem claim()
        {
            if (this._workflowSession == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current workflow session is null.");
            }
            if (this.RuntimeContext == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current runtime context is null.");
            }


            ITaskInstanceManager taskInstanceMgr = RuntimeContext.TaskInstanceManager;
            IWorkItem newWorkItem = taskInstanceMgr.claimWorkItem(this.getId(), this.getTaskInstance().Id);

            if (newWorkItem != null)
            {
                this.state = newWorkItem.getState();
                this.claimedTime = newWorkItem.getClaimedTime();

                ((IRuntimeContextAware)newWorkItem).RuntimeContext=this.RuntimeContext;
                ((IWorkflowSessionAware)newWorkItem).CurrentWorkflowSession=this._workflowSession;
            }
            else
            {
                this.state = IWorkItem.CANCELED;
            }

            return newWorkItem;
        }



        public override void jumpTo(String activityId)
        {
            jumpTo(activityId, null, this.getComments());
        }

        public override void jumpTo(String activityId, String comments)
        {
            jumpTo(activityId, null, comments);
        }



        public override void jumpTo(String targetActivityId, DynamicAssignmentHandler dynamicAssignmentHandler, String comments)
        {
            if (this._workflowSession == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current workflow session is null.");
            }
            if (this.RuntimeContext == null)
            {
                new EngineException(this.getTaskInstance().ProcessInstanceId,
                        this.getTaskInstance().WorkflowProcess, this.getTaskInstance().TaskId,
                        "The current runtime context is null.");
            }
            if (dynamicAssignmentHandler != null)
            {
                this._workflowSession.setDynamicAssignmentHandler(dynamicAssignmentHandler);
            }
            ITaskInstanceManager taskInstanceManager = this.RuntimeContext.TaskInstanceManager;
            taskInstanceManager.completeWorkItemAndJumpTo(this, targetActivityId, comments);
        }
    }
}
