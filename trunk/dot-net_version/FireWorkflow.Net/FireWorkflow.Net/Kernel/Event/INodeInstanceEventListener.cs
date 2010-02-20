using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Kernel.Event
{
    public interface INodeInstanceEventListener
    {
        /// <summary>节点实例监听器</summary>
        /// <param name="e"></param>
        void onNodeInstanceEventFired(NodeInstanceEvent e);// throws KernelException;
    }
}
