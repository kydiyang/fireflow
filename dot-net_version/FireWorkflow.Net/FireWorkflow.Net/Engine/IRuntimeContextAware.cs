using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine
{
    /// <summary>
    /// 类似IWorkflowSessionAware
    /// </summary>
    public interface IRuntimeContextAware
    {
        void setRuntimeContext(RuntimeContext ctx);

        RuntimeContext getRuntimeContext();
    }
}
