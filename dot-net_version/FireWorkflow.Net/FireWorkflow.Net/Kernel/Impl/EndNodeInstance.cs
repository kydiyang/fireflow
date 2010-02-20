using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Kernel.Event;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class EndNodeInstance : AbstractNodeInstance, ISynchronizerInstance
    {

        //[NonSerialized]
        //public const Log log = LogFactory.getLog(EndNodeInstance.class);
        public const String Extension_Target_Name = "org.fireflow.kernel.EndNodeInstance";
        public static List<String> Extension_Point_Names = new List<String>();
        public const String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";

        /// <summary>
        /// volume是同步器的容量
        /// </summary>
        public int Volume { get; set; }

        static EndNodeInstance()
        {
            Extension_Point_Names.Add(Extension_Point_NodeInstanceEventListener);
        }
        private int volume = 0;// 即节点的容量
        private int tokenValue = 0;
        private EndNode endNode = null;
        //private Boolean alive = false;

        public EndNodeInstance()
        {
        }

        public override String getId()
        {
            return this.endNode.Id;
        }

        public EndNodeInstance(EndNode endNd)
        {
            this.endNode = endNd;
            this.volume = this.endNode.EnteringTransitions.Count;

            //		System.out.println("endnode's volume is "+volume);
        }

        /*
         * (non-Javadoc)
         *
         * @see org.fireflow.kenel.ISynchronizerInstance#getTokens()
         */
        public int getValue()
        {
            return tokenValue;
        }


        /*
         * (non-Javadoc)
         *
         * @see org.fireflow.kenel.ISynchronizerInstance#setTokens(int)
         */
        public void setValue(int tokenNum)
        {
            this.tokenValue = tokenNum;
        }



        /*
         * (non-Javadoc)
         *
         * @see org.fireflow.kenel.IPTNetExecutor#fire(org.fireflow.kenel.RuntimeContext,
         *      org.fireflow.kenel.ITransitionInstance)
         */

        public IJoinPoint synchronized(IToken tk, EndNodeInstance teni)
        {
            IJoinPoint joinPoint = null;
            tk.NodeId=this.getSynchronizer().Id;
            //log.debug("The weight of the Entering TransitionInstance is " + tk.getValue());
            // 触发TokenEntered事件
            NodeInstanceEvent event1 = new NodeInstanceEvent(teni);
            event1.setToken(tk);
            event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
            fireNodeLeavingEvent(event1);

            //汇聚检查
            joinPoint = ((ProcessInstance)tk.ProcessInstance).createJoinPoint(teni, tk);// JoinPoint由谁生成比较好？
            int value = (int)joinPoint.getValue();

            //log.debug("The volume of " + this.toString() + " is " + volume);
            //log.debug("The value of " + this.toString() + " is " + value);
            if (value > volume)
            {
                KernelException exception = new KernelException(tk.ProcessInstance,
                        this.getSynchronizer(),
                        "Error:The token count of the synchronizer-instance can NOT be  greater than  it's volumn  ");
                throw exception;
            }
            if (value < volume)
            {// 如果Value小于容量则继续等待其他弧的汇聚。
                return null;
            }
            return joinPoint;
        }

        public override void fire(IToken tk)
        {
            IJoinPoint joinPoint = synchronized(tk, this);
            if (joinPoint == null) return;
            IProcessInstance processInstance = tk.ProcessInstance;
            NodeInstanceEvent event2 = new NodeInstanceEvent(this);
            event2.setToken(tk);
            event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
            fireNodeEnteredEvent(event2);
            //首先必须检查是否有满足条件的循环
            Boolean doLoop = false;//表示是否有满足条件的循环，false表示没有，true表示有。
            if (joinPoint.getAlive())
            {
                IToken tokenForLoop = null;

                tokenForLoop = new Token(); // 产生新的token
                tokenForLoop.IsAlive=joinPoint.getAlive();
                tokenForLoop.ProcessInstance=processInstance;
                tokenForLoop.StepNumber=joinPoint.getStepNumber() - 1;
                tokenForLoop.FromActivityId=joinPoint.getFromActivityId();

                for (int i = 0; i < this.LeavingLoopInstances.Count; i++)
                {
                    ILoopInstance loopInstance = this.LeavingLoopInstances[i];
                    doLoop = loopInstance.take(tokenForLoop);
                    if (doLoop)
                    {
                        break;
                    }
                }
            }

            if (!doLoop)
            {
                NodeInstanceEvent event3 = new NodeInstanceEvent(this);
                event3.setToken(tk);
                event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
                fireNodeLeavingEvent(event3);
            }

            //        NodeInstanceEvent event4 = new NodeInstanceEvent(this);
            //        event4.setToken(tk);
            //        event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
            //        fireNodeLeavingEvent(event4);
        }

        /*
         * (non-Javadoc)
         *
         * @see org.fireflow.kenel.plugin.IPlugable#getExtensionPointNames()
         */
        public override List<String> getExtensionPointNames()
        {
            return Extension_Point_Names;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.fireflow.kenel.plugin.IPlugable#getExtensionTargetName()
         */
        public override String getExtensionTargetName()
        {
            return Extension_Target_Name;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.fireflow.kenel.plugin.IPlugable#registExtension(org.fireflow.kenel.plugin.IKenelExtension)
         */
        public override void registExtension(IKernelExtension extension)
        {
            if (!Extension_Target_Name.Equals(extension.getExtentionTargetName()))
            {
                throw new Exception(
                        "Error:When construct the EndNodeInstance,the Extension_Target_Name is mismatching");
            }
            if (Extension_Point_NodeInstanceEventListener.Equals(extension.getExtentionPointName()))
            {
                if (extension is INodeInstanceEventListener)
                {
                    this.EventListeners.Add((INodeInstanceEventListener)extension);
                }
                else
                {
                    throw new Exception(
                            "Error:When construct the EndNodeInstance,the extension MUST be a instance of INodeInstanceEventListener");
                }
            }

        }

        public override String ToString()
        {
            return "EndNodeInstance_4_[" + endNode.Id + "]";
        }


        public Synchronizer getSynchronizer()
        {
            return this.endNode;
        }
    }

}
