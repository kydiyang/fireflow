using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;

namespace FireWorkflow.Net.Engine.Taskinstance
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
