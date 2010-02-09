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

        [NonSerialized]
        protected RuntimeContext rtCtx = null;
        [NonSerialized]
        protected IWorkflowSession workflowSession = null;

        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.rtCtx = ctx;
            if (this.taskInstance != null)
            {
                ((IRuntimeContextAware)taskInstance).setRuntimeContext(this.rtCtx);
            }
        }

        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
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
            if (this.workflowSession == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current workflow session is null.");
            }
            if (this.rtCtx == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current runtime context is null.");
            }
            ITaskInstanceManager taskInstanceMgr = this.rtCtx.getTaskInstanceManager();
            return taskInstanceMgr.withdrawWorkItem(this);
        }


        public override void reject()
        {
            reject(this.getComments());
        }

        public override void reject(String comments)
        {
            if (this.workflowSession == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current workflow session is null.");
            }
            if (this.rtCtx == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current runtime context is null.");
            }
            ITaskInstanceManager taskInstanceMgr = this.rtCtx.getTaskInstanceManager();
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
            if (this.workflowSession == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current workflow session is null.");
            }
            if (this.rtCtx == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current runtime context is null.");
            }
            if (dynamicAssignmentHandler != null)
            {
                this.workflowSession.setDynamicAssignmentHandler(dynamicAssignmentHandler);
            }
            ITaskInstanceManager taskInstanceManager = this.rtCtx.getTaskInstanceManager();
            taskInstanceManager.completeWorkItem(this, null, comments);
        }

        public override IWorkItem reasignTo(String actorId)
        {
            return reasignTo(actorId, this.getComments());
        }

        public override IWorkItem reasignTo(String actorId, String comments)
        {
            if (this.workflowSession == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current workflow session is null.");
            }
            if (this.rtCtx == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current runtime context is null.");
            }

            ITaskInstanceManager manager = this.rtCtx.getTaskInstanceManager();
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
            if (this.workflowSession == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current workflow session is null.");
            }
            if (this.rtCtx == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current runtime context is null.");
            }


            ITaskInstanceManager taskInstanceMgr = rtCtx.getTaskInstanceManager();
            IWorkItem newWorkItem = taskInstanceMgr.claimWorkItem(this.getId(), this.getTaskInstance().getId());

            if (newWorkItem != null)
            {
                this.state = newWorkItem.getState();
                this.claimedTime = newWorkItem.getClaimedTime();

                ((IRuntimeContextAware)newWorkItem).setRuntimeContext(rtCtx);
                ((IWorkflowSessionAware)newWorkItem).setCurrentWorkflowSession(this.workflowSession);
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
            if (this.workflowSession == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current workflow session is null.");
            }
            if (this.rtCtx == null)
            {
                new EngineException(this.getTaskInstance().getProcessInstanceId(),
                        this.getTaskInstance().getWorkflowProcess(), this.getTaskInstance().getTaskId(),
                        "The current runtime context is null.");
            }
            if (dynamicAssignmentHandler != null)
            {
                this.workflowSession.setDynamicAssignmentHandler(dynamicAssignmentHandler);
            }
            ITaskInstanceManager taskInstanceManager = this.rtCtx.getTaskInstanceManager();
            taskInstanceManager.completeWorkItemAndJumpTo(this, targetActivityId, comments);
        }

        public IWorkflowSession getCurrentWorkflowSession()
        {
            return this.workflowSession;
        }

        public void setCurrentWorkflowSession(IWorkflowSession session)
        {
            this.workflowSession = session;
            if (this.taskInstance != null)
            {
                ((IWorkflowSessionAware)taskInstance).setCurrentWorkflowSession(this.workflowSession);
            }
        }

    }
}
