using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine
{
    public interface IWorkflowSessionCallback
    {
        Object doInWorkflowSession(RuntimeContext ctx);// throws EngineException, KernelException;
    }
}
