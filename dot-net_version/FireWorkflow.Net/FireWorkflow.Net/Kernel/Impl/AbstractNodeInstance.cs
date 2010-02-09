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
        [NonSerialized]
        protected List<ITransitionInstance> leavingTransitionInstances = new List<ITransitionInstance>();
        [NonSerialized]
        protected List<ITransitionInstance> enteringTransitionInstances = new List<ITransitionInstance>();
        [NonSerialized]
        protected List<INodeInstanceEventListener> eventListeners = new List<INodeInstanceEventListener>();

        [NonSerialized]
        protected List<ILoopInstance> leavingLoopInstances = new List<ILoopInstance>();
        [NonSerialized]
        protected List<ILoopInstance> enteringLoopInstances = new List<ILoopInstance>();
        /* (non-Javadoc)
         * @see org.fireflow.kenel.INodeInstance#addLeavingTransitionInstance(org.fireflow.kenel.ITransitionInstance)
         */
        public void addLeavingTransitionInstance(
                ITransitionInstance transitionInstance)
        {
            leavingTransitionInstances.Add(transitionInstance);

        }

        public List<ITransitionInstance> getLeavingTransitionInstances()
        {
            return this.leavingTransitionInstances;
        }

        public List<ITransitionInstance> getEnteringTransitionInstances()
        {
            return this.enteringTransitionInstances;
        }
        public void addEnteringTransitionInstance(ITransitionInstance transitionInstance)
        {
            this.enteringTransitionInstances.Add(transitionInstance);
        }

        public void addLeavingLoopInstance(
                ILoopInstance loopInstance)
        {
            leavingLoopInstances.Add(loopInstance);

        }

        public List<ILoopInstance> getLeavingLoopInstances()
        {
            return this.leavingLoopInstances;
        }

        public List<ILoopInstance> getEnteringLoopInstances()
        {
            return this.enteringLoopInstances;
        }

        public void addEnteringLoopInstance(ILoopInstance loopInstance)
        {
            this.enteringLoopInstances.Add(loopInstance);
        }
        //TODO 此处是addAll还是直接替换？
        public void setEventListeners(List<INodeInstanceEventListener> listeners)
        {
            eventListeners.AddRange(listeners);
        }

        public List<INodeInstanceEventListener> getEventListeners()
        {
            return eventListeners;
        }

        public void fireNodeEnteredEvent(NodeInstanceEvent neevent)
        {
            for (int i = 0; i < this.eventListeners.Count; i++)
            {
                INodeInstanceEventListener listener = this.eventListeners[i];
                listener.onNodeInstanceEventFired(neevent);
            }
        }
        public void fireNodeLeavingEvent(NodeInstanceEvent neevent)
        {
            for (int i = 0; i < this.eventListeners.Count; i++)
            {
                INodeInstanceEventListener listener = this.eventListeners[i];
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
