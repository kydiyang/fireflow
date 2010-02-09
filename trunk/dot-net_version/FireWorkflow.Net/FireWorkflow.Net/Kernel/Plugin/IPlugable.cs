using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel.Plugin
{
    public interface IPlugable
    {
        String getExtensionTargetName();
        List<String> getExtensionPointNames();

        //TODO extesion是单态还是多实例？单态应该效率高一些。
        void registExtension(IKernelExtension extension);// throws RuntimeException;
    }
}
