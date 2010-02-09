using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ISM.FireWorkflow.Model;
using ISM.FireWorkflow.Model.Net;
using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Engine.Impl;

namespace ISM.FireWorkflow.Engine.Taskinstance
{

    public class DefaultTaskInstanceCreator : ITaskInstanceCreator
    {

        public ITaskInstance createTaskInstance(IWorkflowSession currentSession,
                RuntimeContext runtimeContxt, IProcessInstance processInstance,
                Task task, Activity activity)// throws EngineException
        {

            TaskInstance taskInstance = new TaskInstance();

            return taskInstance;

        }
    }
}
