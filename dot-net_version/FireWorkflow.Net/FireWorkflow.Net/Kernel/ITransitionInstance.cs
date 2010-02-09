using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel
{
    public interface ITransitionInstance : IEdgeInstance
    {
        Transition getTransition();
    }
}
