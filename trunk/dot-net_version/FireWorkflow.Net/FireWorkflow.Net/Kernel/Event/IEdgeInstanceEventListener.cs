using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel.Event
{
    public interface IEdgeInstanceEventListener
    {
        void onEdgeInstanceEventFired(EdgeInstanceEvent e);// throws KernelException;
    }

}
