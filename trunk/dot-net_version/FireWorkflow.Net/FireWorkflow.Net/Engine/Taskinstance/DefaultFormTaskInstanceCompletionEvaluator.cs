using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Kernel;

namespace FireWorkflow.Net.Engine.Taskinstance
{
    public class DefaultFormTaskInstanceCompletionEvaluator : ITaskInstanceCompletionEvaluator
    {

        public Boolean taskInstanceCanBeCompleted(IWorkflowSession currentSession, RuntimeContext runtimeContext,
                IProcessInstance processInstance, ITaskInstance taskInstance)//throws EngineException ,KernelException 
        {
            IPersistenceService persistenceService = runtimeContext.getPersistenceService();
            Int32 aliveWorkItemCount = persistenceService.getAliveWorkItemCountForTaskInstance(taskInstance.getId());
            if (aliveWorkItemCount == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

    }
}
