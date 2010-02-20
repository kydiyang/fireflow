using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class TransitionInstance : EdgeInstance , ITransitionInstance, IPlugable {

    public const String Extension_Target_Name = "org.fireflow.kernel.TransitionInstance";
    public static List<String> Extension_Point_Names = new List<String>();
    public const String Extension_Point_TransitionInstanceEventListener = "TransitionInstanceEventListener";

    static TransitionInstance(){
        Extension_Point_Names.Add(Extension_Point_TransitionInstanceEventListener);
    }
        
    [NonSerialized]
    private  Transition transition = null;


    public TransitionInstance(Transition t) {
        transition = t;
    }

    public override String getId()
    {
        return this.transition.Id;
    }
//	private int weight = 0;
    public override int getWeight()
    {
        if (weight == 0)
        {
            if (enteringNodeInstance is StartNodeInstance)
            {
                weight = 1;
                return weight;
            }
            else if (leavingNodeInstance is EndNodeInstance)
            {
                weight = 1;
                return weight;
            }
            else if (leavingNodeInstance is ActivityInstance)
            {
                SynchronizerInstance synchronizerInstance = (SynchronizerInstance)enteringNodeInstance;
                weight = synchronizerInstance.Volume / enteringNodeInstance.LeavingTransitionInstances.Count;
                return weight;

            }
            else if (leavingNodeInstance is SynchronizerInstance)
            {
                SynchronizerInstance synchronizerInstance = (SynchronizerInstance)leavingNodeInstance;
                weight = synchronizerInstance.Volume / leavingNodeInstance.EnteringTransitionInstances.Count;
                return weight;
            }
        }

        return weight;
    }
//	public void setWeight(int i){
//		this.weight = i;
//	}


    public override Boolean take(IToken token) {
        EdgeInstanceEvent e = new EdgeInstanceEvent(this);
        e.setToken(token);
        e.setEventType(EdgeInstanceEvent.ON_TAKING_THE_TOKEN);

        for (int i = 0; this.eventListeners != null && i < this.eventListeners.Count; i++) {
            IEdgeInstanceEventListener listener =  this.eventListeners[i];
            listener.onEdgeInstanceEventFired(e);
        }

        INodeInstance nodeInst = this.getLeavingNodeInstance();
        token.Value=this.getWeight();
        Boolean alive = token.IsAlive;


        nodeInst.fire(token);

        return alive;
    }

    public Transition getTransition() {
        return this.transition;
    }

    public String getExtensionTargetName() {
        return TransitionInstance.Extension_Target_Name;
    }

    public List<String> getExtensionPointNames() {
        return TransitionInstance.Extension_Point_Names;
    }

    public void registExtension(IKernelExtension extension) {
        if (!Extension_Target_Name.Equals(extension.getExtentionTargetName())) {
            return;
//			throw new KenelException("Error:When construct the TansitionInstance,the Extension_Target_Name is mismatching");
        }
        if (Extension_Point_TransitionInstanceEventListener.Equals(extension.getExtentionPointName())) {
            if (extension is IEdgeInstanceEventListener) {
                this.eventListeners.Add((IEdgeInstanceEventListener) extension);
            } else {
                throw new Exception("Error:When construct the TransitionInstance,the extension MUST be a instance of ITransitionInstanceEventListener");
            }
        }
    }
}

}
