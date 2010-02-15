using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Condition;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class StartNodeInstance : AbstractNodeInstance, ISynchronizerInstance
    {

        public const String Extension_Target_Name = "org.fireflow.kernel.StartNodeInstance";
        public static List<String> Extension_Point_Names = new List<String>();
        public const String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";


        static StartNodeInstance()
        {
            Extension_Point_Names.Add(Extension_Point_NodeInstanceEventListener);
        }
        private int volume = 0;// 即节点的容量
        // private int tokenValue = 0;
        private StartNode startNode = null;

        // private Boolean alive = false;
        public StartNodeInstance(StartNode startNd)
        {
            this.startNode = startNd;
            volume = startNode.LeavingTransitions.Count;

            //		System.out.println(" startnode's volume is "+volume);
        }

        public override String getId()
        {
            return this.startNode.Id;
        }

        public override void fire(IToken tk)
        {
            if (!tk.isAlive())
            {
                return;//
            }
            if (tk.getValue() != volume)
            {
                KernelException exception = new KernelException(tk.getProcessInstance(),
                        this.startNode,
                        "Error:Illegal StartNodeInstance,the tokenValue MUST be equal to the volume ");
                throw exception;

            }

            tk.setNodeId(this.getSynchronizer().Id);

            IProcessInstance processInstance = tk.getProcessInstance();

            //触发token_entered事件
            NodeInstanceEvent event1 = new NodeInstanceEvent(this);
            event1.setToken(tk);
            event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
            fireNodeLeavingEvent(event1);

            //触发fired事件
            NodeInstanceEvent event2 = new NodeInstanceEvent(this);
            event2.setToken(tk);
            event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
            fireNodeEnteredEvent(event2);

            //触发leaving事件
            NodeInstanceEvent event4 = new NodeInstanceEvent(this);
            event4.setToken(tk);
            event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
            fireNodeLeavingEvent(event4);


            Boolean activiateDefaultCondition = true;
            ITransitionInstance defaultTransInst = null;
            for (int i = 0; leavingTransitionInstances != null && i < leavingTransitionInstances.Count; i++)
            {
                ITransitionInstance transInst = leavingTransitionInstances[i];
                String condition = transInst.getTransition().Condition;
                if (condition != null && condition.Equals(ConditionConstant.DEFAULT))
                {
                    defaultTransInst = transInst;
                    continue;
                }

                Token token = new Token(); // 产生新的token
                token.setAlive(true);
                token.setProcessInstance(processInstance);
                token.setFromActivityId(tk.getFromActivityId());
                token.setStepNumber(tk.getStepNumber() + 1);

                Boolean alive = transInst.take(token);
                if (alive)
                {
                    activiateDefaultCondition = false;
                }

            }
            if (defaultTransInst != null)
            {
                Token token = new Token();
                token.setAlive(activiateDefaultCondition);
                token.setProcessInstance(processInstance);
                token.setFromActivityId(token.getFromActivityId());
                token.setStepNumber(tk.getStepNumber() + 1);
                defaultTransInst.take(token);
            }


            //触发completed事件
            NodeInstanceEvent event3 = new NodeInstanceEvent(this);
            event3.setToken(tk);
            event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
            fireNodeLeavingEvent(event3);
        }

        public int getVolume()
        {
            return volume;
        }

        public void setVolume(int volume)
        {
            this.volume = volume;
        }

        public override String getExtensionTargetName()
        {
            return Extension_Target_Name;
        }

        public override List<String> getExtensionPointNames()
        {
            return Extension_Point_Names;
        }

        // TODO extesion是单态还是多实例？单态应该效率高一些。
        public override void registExtension(IKernelExtension extension)
        {
            // System.out.println("====extension class is
            // "+extension.getClass().Name);
            if (!Extension_Target_Name.Equals(extension.getExtentionTargetName()))
            {
                throw new Exception(
                        "Error:When construct the StartNodeInstance,the Extension_Target_Name is mismatching");
            }
            if (Extension_Point_NodeInstanceEventListener.Equals(extension.getExtentionPointName()))
            {
                if (extension is INodeInstanceEventListener)
                {
                    this.eventListeners.Add((INodeInstanceEventListener)extension);
                }
                else
                {
                    throw new Exception(
                            "Error:When construct the StartNodeInstance,the extension MUST be a instance of INodeInstanceEventListener");
                }
            }
        }

        public override String ToString()
        {
            return "StartNodeInstance_4_[" + startNode.Id + "]";
        }

        public Synchronizer getSynchronizer()
        {
            return this.startNode;
        }

        //    private Boolean determineTheAliveOfToken(ITransitionInstance transInst) {
        //        // TODO通过计算transition上的表达式来确定alive的值
        //        return true;
        //    }
    }

}
