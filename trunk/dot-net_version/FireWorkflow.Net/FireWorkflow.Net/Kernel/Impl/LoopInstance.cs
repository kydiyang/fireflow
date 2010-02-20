using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Kernel.Event;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class LoopInstance : EdgeInstance ,ILoopInstance ,IPlugable{
    [NonSerialized]
    public  const String Extension_Target_Name = "org.fireflow.kernel.LoopInstance";
    [NonSerialized]
    public  static List<String> Extension_Point_Names = new List<String>();
    [NonSerialized]
    public  const String Extension_Point_LoopInstanceEventListener = "LoopInstanceEventListener";

    static LoopInstance(){
        Extension_Point_Names.Add(Extension_Point_LoopInstanceEventListener);
//        Extension_Point_Names.add(Extension_Point_ConditionEvaluator);
    }

//private 
    
    [NonSerialized]
    private  Loop loop = null;

    public LoopInstance(Loop lp){
        this.loop = lp;
    }

    public override String getId()
    {
        return loop.Id;
    }

    public override int getWeight() {
        if (weight==0){
            if (leavingNodeInstance is SynchronizerInstance){
                weight=((SynchronizerInstance)this.leavingNodeInstance).getVolume();
            }else if (leavingNodeInstance is StartNodeInstance){
                weight = ((StartNodeInstance)this.leavingNodeInstance).getVolume();
            }else if (leavingNodeInstance is EndNodeInstance){
                weight = ((EndNodeInstance)this.leavingNodeInstance).getVolume();
            }
        }
        return weight;
    }

    public override Boolean take(IToken token)
    {
        Boolean oldAlive = token.IsAlive;

        EdgeInstanceEvent e = new EdgeInstanceEvent(this);
        e.setToken(token);
        e.setEventType(EdgeInstanceEvent.ON_TAKING_THE_TOKEN);

        for (int i = 0; this.eventListeners != null && i < this.eventListeners.Count; i++) {
            IEdgeInstanceEventListener listener =  this.eventListeners[i];
            listener.onEdgeInstanceEventFired(e);
        }


        Boolean newAlive = token.IsAlive;

        if (!newAlive){//循环条件不满足，则恢复token的alive标示
            token.IsAlive=oldAlive;
            return newAlive;
        }else{//否则流转到下一个节点

            INodeInstance nodeInst = this.getLeavingNodeInstance();
            token.Value=this.getWeight();
            nodeInst.fire(token);
            return newAlive;
        }
    }

    public Loop getLoop() {
        return loop;
    }

    public void setLoop(Loop arg0){
        this.loop = arg0;
    }

    public String getExtensionTargetName() {
        return LoopInstance.Extension_Target_Name;
    }

    public List<String> getExtensionPointNames() {
        return LoopInstance.Extension_Point_Names;
    }

    public void registExtension(IKernelExtension extension)  {
        if (!Extension_Target_Name.Equals(extension.getExtentionTargetName())) {
            return;
//			throw new KenelException("Error:When construct the TansitionInstance,the Extension_Target_Name is mismatching");
        }
        if (Extension_Point_LoopInstanceEventListener.Equals(extension.getExtentionPointName()))
        {
            if (extension is IEdgeInstanceEventListener) {
                this.eventListeners.Add((IEdgeInstanceEventListener) extension);
            } else {
                throw new Exception("Error:When construct the TransitionInstance,the extension MUST be a instance of ITransitionInstanceEventListener");
            }
        }
    }

}

}
