using System;
using System.Collections.Generic;
using System.Text;
using ISM.FireWorkflow.Engine.Event;

namespace ISM.FireWorkflow.Engine.Taskinstance
{
    public class DefaultTaskInstanceEventListener :
        ITaskInstanceEventListener
    {
        public void onTaskInstanceEventFired(TaskInstanceEvent e)// throws EngineException 
        {
            IWorkflowSession session = e.getWorkflowSession();
            IProcessInstance proceInst = e.getProcessInstance();
            ITaskInstance taskInst = (ITaskInstance)e.getSource();
            IWorkItem wi = e.getWorkItem();
            if (e.getEventType() == TaskInstanceEvent.BEFORE_TASK_INSTANCE_START)
            {
                beforeTaskInstanceStart(session, proceInst, taskInst);
            }
            else if (e.getEventType() == TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE)
            {
                afterTaskInstanceCompleted(session, proceInst, taskInst);
            }
            else if (e.getEventType() == TaskInstanceEvent.AFTER_WORKITEM_CREATED)
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
