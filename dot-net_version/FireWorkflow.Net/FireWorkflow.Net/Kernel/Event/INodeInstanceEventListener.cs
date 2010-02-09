using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel.Event
{
    public interface INodeInstanceEventListener
    {
        void onNodeInstanceEventFired(NodeInstanceEvent e);// throws KernelException;
    }
}
