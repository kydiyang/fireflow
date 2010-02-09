using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Taskinstance
{
    interface IApplicationHandler
    {
        void execute(ITaskInstance taskInstance);// throws EngineException;
    }
}
