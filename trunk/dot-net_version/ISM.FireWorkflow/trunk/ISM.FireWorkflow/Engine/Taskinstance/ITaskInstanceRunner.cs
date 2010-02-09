using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Taskinstance
{
    /// <summary>
    /// 任务实例运行器
    /// </summary>
    public interface ITaskInstanceRunner
    {
        void run(IWorkflowSession currentSession, RuntimeContext runtimeContext,IProcessInstance processInstance, ITaskInstance taskInstance);// throws EngineException, KernelException;
    }
}
