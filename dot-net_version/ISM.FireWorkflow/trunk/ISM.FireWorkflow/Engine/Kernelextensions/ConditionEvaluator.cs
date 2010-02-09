using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Kernel.Plugin;



namespace ISM.FireWorkflow.Engine.Kernelextensions
{
    public class ConditionEvaluator : IKernelExtension, IRuntimeContextAware
    {

        public String getExtentionTargetName()
        {
            throw new NotImplementedException("Not supported yet.");
        }

        public String getExtentionPointName()
        {
            throw new NotImplementedException("Not supported yet.");
        }

        public void setRuntimeContext(RuntimeContext ctx)
        {
            throw new NotImplementedException("Not supported yet.");
        }

        public RuntimeContext getRuntimeContext()
        {
            throw new NotImplementedException("Not supported yet.");
        }

    }
}
