using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Kernel.Event;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class ActivityInstance : AbstractNodeInstance, IActivityInstance
    {

        //[NonSerialized]
        //public const Log log = LogFactory.getLog(ActivityInstance.class);
        [NonSerialized]
        public const String Extension_Target_Name = "org.fireflow.kernel.ActivityInstance";
        [NonSerialized]
        public static List<String> Extension_Point_Names = new List<String>();
        [NonSerialized]
        public const String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";


        static ActivityInstance()
        {
            Extension_Point_Names.Add(Extension_Point_NodeInstanceEventListener);
        }
        [NonSerialized]
        private Activity activity = null;

        public ActivityInstance(Activity a)
        {
            activity = a;
        }

        public override String getId()
        {
            return activity.getId();
        }

        public override void fire(IToken tk)
        {
            //log.debug("The weight of the Entering TransitionInstance is " + tk.getValue());
            IToken token = tk;
            token.setNodeId(this.getActivity().getId());

            //触发TokenEntered事件
            NodeInstanceEvent event1 = new NodeInstanceEvent(this);
            event1.setToken(tk);
            event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
            fireNodeEnteredEvent(event1);
            if (token.isAlive())
            {
                NodeInstanceEvent neevent = new NodeInstanceEvent(this);
                neevent.setToken(token);
                neevent.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
                fireNodeEnteredEvent(neevent);

                //如果没有task,即该activity是一个dummy activity，则直接complete
                //注释:2009-06-01,complete工作被移植到了BasicTaskInstanceManager.createTaskInstances(...)
                //            if (this.getActivity().getTasks().Count == 0) {
                //                this.complete(token, null);
                //            }
            }
            else
            {
                this.complete(token, null);
            }
        }

        public void complete(IToken token, IActivityInstance targetActivityInstance)
        {
            NodeInstanceEvent event2 = new NodeInstanceEvent(this);
            event2.setToken(token);
            event2.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
            fireNodeLeavingEvent(event2);


            token.setFromActivityId(this.getActivity().getId());

            if (targetActivityInstance != null)
            {
                /*为什么要新建一个Token?似乎没有必要。20090122
                Token newtoken = new Token();
                newtoken.setAlive(token.isAlive());
                newtoken.setProcessInstance(token.getProcessInstance());
                targetActivityInstance.fire(newtoken);
                 */
                token.setStepNumber(token.getStepNumber() + 1);
                targetActivityInstance.fire(token);
            }
            else
            {
                //按照定义，activity有且只有一个输出弧，所以此处只进行一次循环。
                for (int i = 0; leavingTransitionInstances != null && i < leavingTransitionInstances.Count; i++)
                {
                    ITransitionInstance transInst = leavingTransitionInstances[i];
                    transInst.take(token);
                }
            }

            if (token.isAlive())
            {
                NodeInstanceEvent neevent = new NodeInstanceEvent(this);
                neevent.setToken(token);
                neevent.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
                fireNodeLeavingEvent(neevent);
            }
        }

        public override String getExtensionTargetName()
        {
            return Extension_Target_Name;
        }

        public override List<String> getExtensionPointNames()
        {
            return Extension_Point_Names;
        }

        //TODO extesion是单态还是多实例？单态应该效率高一些。
        public override void registExtension(IKernelExtension extension)
        {
            if (!Extension_Target_Name.Equals(extension.getExtentionTargetName()))
            {
                throw new Exception("Error:When construct the ActivityInstance,the Extension_Target_Name is mismatching");
            }
            if (Extension_Point_NodeInstanceEventListener.Equals(extension.getExtentionPointName()))
            {
                if (extension is INodeInstanceEventListener)
                {
                    this.eventListeners.Add((INodeInstanceEventListener)extension);
                }
                else
                {
                    throw new Exception("Error:When construct the ActivityInstance,the extension MUST be a instance of INodeInstanceEventListener");
                }
            }
        }


        public override String ToString()
        {
            return "ActivityInstance_4_[" + activity.getName() + "]";
        }

        public Activity getActivity()
        {
            return activity;
        }
    }

}
