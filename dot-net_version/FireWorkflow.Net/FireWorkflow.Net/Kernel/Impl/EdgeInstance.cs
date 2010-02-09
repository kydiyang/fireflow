using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;

namespace FireWorkflow.Net.Kernel.Impl
{
    public abstract class EdgeInstance : IEdgeInstance
    {
        protected INodeInstance leavingNodeInstance = null;
        protected INodeInstance enteringNodeInstance = null;
        protected int weight = 0;
        protected List<IEdgeInstanceEventListener> eventListeners = new List<IEdgeInstanceEventListener>();

        public INodeInstance getLeavingNodeInstance()
        {
            return leavingNodeInstance;
        }

        public void setLeavingNodeInstance(INodeInstance nodeInst)
        {
            this.leavingNodeInstance = nodeInst;
        }

        public INodeInstance getEnteringNodeInstance()
        {
            return enteringNodeInstance;
        }

        public void setEnteringNodeInstance(INodeInstance nodeInst)
        {
            this.enteringNodeInstance = nodeInst;
        }


        #region IEdgeInstance 成员

        public virtual string getId()
        {
            throw new NotImplementedException();
        }

        public virtual int getWeight()
        {
            throw new NotImplementedException();
        }

        public virtual bool take(IToken token)
        {
            throw new NotImplementedException();
        }

        #endregion
    }

}
