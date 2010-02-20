using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Plugin;

namespace FireWorkflow.Net.Kernel.Impl
{
    public abstract class AbstractNodeInstance : INodeInstance, IPlugable
    {
        public List<ITransitionInstance> LeavingTransitionInstances { get; set; }

        public List<ITransitionInstance> EnteringTransitionInstances { get; set; }

        public List<INodeInstanceEventListener> EventListeners { get; set; }

        public List<ILoopInstance> LeavingLoopInstances { get; set; }

        public List<ILoopInstance> EnteringLoopInstances { get; set; }

        public AbstractNodeInstance()
        {
            this.LeavingTransitionInstances = new List<ITransitionInstance>();
            this.EnteringTransitionInstances = new List<ITransitionInstance>();
            this.EventListeners = new List<INodeInstanceEventListener>();
            this.LeavingLoopInstances = new List<ILoopInstance>();
            this.EnteringLoopInstances = new List<ILoopInstance>();
        }


        public void AddLeavingTransitionInstance(ITransitionInstance transitionInstance)
        {
            LeavingTransitionInstances.Add(transitionInstance);
        }

        public void AddEnteringTransitionInstance(ITransitionInstance transitionInstance)
        {
            this.EnteringTransitionInstances.Add(transitionInstance);
        }

        public void AddLeavingLoopInstance(ILoopInstance loopInstance)
        {
            LeavingLoopInstances.Add(loopInstance);
        }

        public void AddEnteringLoopInstance(ILoopInstance loopInstance)
        {
            this.EnteringLoopInstances.Add(loopInstance);
        }

        public void fireNodeEnteredEvent(NodeInstanceEvent neevent)
        {
            for (int i = 0; i < this.EventListeners.Count; i++)
            {
                INodeInstanceEventListener listener = this.EventListeners[i];
                listener.onNodeInstanceEventFired(neevent);
            }
        }
        public void fireNodeLeavingEvent(NodeInstanceEvent neevent)
        {
            for (int i = 0; i < this.EventListeners.Count; i++)
            {
                INodeInstanceEventListener listener = this.EventListeners[i];
                listener.onNodeInstanceEventFired(neevent);
            }
        }

        #region INodeInstance 成员

        public virtual string getId()
        {
            throw new NotImplementedException();
        }

        public virtual void fire(IToken token)
        {
            throw new NotImplementedException();
        }

        #endregion

        #region IPlugable 成员

        public virtual string getExtensionTargetName()
        {
            throw new NotImplementedException();
        }

        public virtual List<string> getExtensionPointNames()
        {
            throw new NotImplementedException();
        }

        public virtual void registExtension(IKernelExtension extension)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}
