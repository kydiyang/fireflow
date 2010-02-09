using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel.Plugin
{
    public interface IKernelExtension
    {
        String getExtentionTargetName();
        String getExtentionPointName();
    }
}
