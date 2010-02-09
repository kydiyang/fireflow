using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Taskinstance
{
    /// <summary>
    /// 任务实例终止评价器
    /// </summary>
    public interface ITaskInstanceCompletionEvaluator
    {
        Boolean taskInstanceCanBeCompleted(IWorkflowSession currentSession, RuntimeContext runtimeContext,
                IProcessInstance processInstance, ITaskInstance taskInstance);// throws EngineException, KernelException;
    }
}
