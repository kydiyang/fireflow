using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Taskinstance
{
    public class DefaultToolTaskInstanceCompletionEvaluator : ITaskInstanceCompletionEvaluator
    {
        public Boolean taskInstanceCanBeCompleted(IWorkflowSession currentSession, RuntimeContext runtimeContext,
                IProcessInstance processInstance, ITaskInstance taskInstance)//throws EngineException ,KernelException
        {
            //Tool类型的TaskInstance在handler执行完后，都可以直接结束。
            return true;
        }

    }
}
