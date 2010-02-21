using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Engine.Event;

namespace FireWorkflow.Net.Engine.Taskinstance
{
    public class DefaultTaskInstanceEventListener :
        ITaskInstanceEventListener
    {
        public void onTaskInstanceEventFired(TaskInstanceEvent e)// throws EngineException 
        {
            IWorkflowSession session = e.WorkflowSession;
            IProcessInstance proceInst = e.ProcessInstance;
            ITaskInstance taskInst = (ITaskInstance)e.Source;
            IWorkItem wi = e.WorkItem;
            if (e.EventType == TaskInstanceEventEnum.BEFORE_TASK_INSTANCE_START)
            {
                beforeTaskInstanceStart(session, proceInst, taskInst);
            }
            else if (e.EventType == TaskInstanceEventEnum.AFTER_TASK_INSTANCE_COMPLETE)
            {
                afterTaskInstanceCompleted(session, proceInst, taskInst);
            }
            else if (e.EventType == TaskInstanceEventEnum.AFTER_WORKITEM_CREATED)
            {
                afterWorkItemCreated(session, proceInst, taskInst, wi);
            }

        }

        protected void beforeTaskInstanceStart(IWorkflowSession currentSession,
                    IProcessInstance processInstance, ITaskInstance taskInstance)//throws EngineException
        {

        }
        protected void afterTaskInstanceCompleted(IWorkflowSession currentSession,
                IProcessInstance processInstance, ITaskInstance taskInstance)//throws EngineException
        {

        }
        protected void afterWorkItemCreated(IWorkflowSession currentSession,
                IProcessInstance processInstance, ITaskInstance taskInstance, IWorkItem workItem)//throws EngineException
        {

        }
    }
}
