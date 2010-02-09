using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel
{
    public interface IActivityInstance : INodeInstance
    {
        void complete(IToken token, IActivityInstance targetActivityInstance);
        Activity getActivity();
    }
}
