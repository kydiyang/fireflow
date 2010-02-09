using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Model;

namespace FireWorkflow.Net.Kernel
{

    /// <summary>
    /// NodeInstance应该是无状态的，不会随着ProcessInstance的增加而增加。??)
    /// </summary>
    public interface INodeInstance
    {
        String getId();
        void fire(IToken token);// throws KernelException;

        List<ITransitionInstance> getLeavingTransitionInstances();
        void addLeavingTransitionInstance(ITransitionInstance transitionInstance);

        List<ITransitionInstance> getEnteringTransitionInstances();
        void addEnteringTransitionInstance(ITransitionInstance transitionInstance);

        List<ILoopInstance> getLeavingLoopInstances();
        void addLeavingLoopInstance(ILoopInstance loopInstance);

        List<ILoopInstance> getEnteringLoopInstances();
        void addEnteringLoopInstance(ILoopInstance loopInstance);

        //	public void fireNodeEnteredEvent(NodeInstanceEvent event)throws KenelException;
        //	public void fireNodeLeavingEvent(NodeInstanceEvent event) throws KenelException;

        void setEventListeners(List<INodeInstanceEventListener> listeners);

        List<INodeInstanceEventListener> getEventListeners();


    }
}
