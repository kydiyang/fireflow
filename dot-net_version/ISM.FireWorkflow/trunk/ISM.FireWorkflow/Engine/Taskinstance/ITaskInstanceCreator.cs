using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ISM.FireWorkflow.Model;
using ISM.FireWorkflow.Model.Net;

namespace ISM.FireWorkflow.Engine.Taskinstance
{
    /// <summary>
    /// 任务实例创建器
    /// </summary>
    public interface ITaskInstanceCreator
    {
        /// <summary>
        /// 创建任务实例
        /// </summary>
        /// <param name="currentSession"></param>
        /// <param name="runtimeContxt"></param>
        /// <param name="processInstance"></param>
        /// <param name="task"></param>
        /// <param name="activity"></param>
        /// <returns></returns>
        ITaskInstance createTaskInstance(IWorkflowSession currentSession, RuntimeContext runtimeContxt, IProcessInstance processInstance, Task task, Activity activity);// throws EngineException;
    }
}
