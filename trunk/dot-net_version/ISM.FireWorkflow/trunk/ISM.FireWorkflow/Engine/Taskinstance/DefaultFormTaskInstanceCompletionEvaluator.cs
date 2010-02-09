using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Engine.Persistence;
using ISM.FireWorkflow.Kernel;

namespace ISM.FireWorkflow.Engine.Taskinstance
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
