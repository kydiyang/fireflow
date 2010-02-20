using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Condition;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class SynchronizerInstance : AbstractNodeInstance, ISynchronizerInstance
    {

        //[NonSerialized]
        //public  const Log log = LogFactory.getLog(SynchronizerInstance.class);
        [NonSerialized]
        public const String Extension_Target_Name = "org.fireflow.kernel.SynchronizerInstance";
        [NonSerialized]
        public static List<String> Extension_Point_Names = new List<String>();
        [NonSerialized]
        public const String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";

        /// <summary>
        /// volume是同步器的容量
        /// </summary>
        public int Volume { get; set; }

        static SynchronizerInstance()
        {
            Extension_Point_Names.Add(Extension_Point_NodeInstanceEventListener);
        }
        private int volume = 0;// 即节点的容量
        [NonSerialized]
        private Synchronizer synchronizer = null;


        public override String getId()
        {
            return this.synchronizer.Id;
        }

        public SynchronizerInstance(Synchronizer s)
        {
            synchronizer = s;
            int a = synchronizer.EnteringTransitions.Count;
            int b = synchronizer.LeavingTransitions.Count;
            volume = a * b;

            //		System.out.println("synchronizer "+synchronizer.Name+"'s volume is "+volume);
        }


        public IJoinPoint synchronized(IToken tk, SynchronizerInstance sthis)
        {
            IJoinPoint joinPoint = null;
            tk.NodeId=this.getSynchronizer().Id;
            //log.debug("The weight of the Entering TransitionInstance is " + tk.getValue());
            // 触发TokenEntered事件
            NodeInstanceEvent event1 = new NodeInstanceEvent(sthis);
            event1.setToken(tk);
            event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
            fireNodeLeavingEvent(event1);

            //汇聚检查

            joinPoint = ((ProcessInstance)tk.ProcessInstance).createJoinPoint(sthis, tk);// JoinPoint由谁生成比较好？
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
            //TODO 此处性能需要改善一下,20090312
            IJoinPoint joinPoint = synchronized(tk, this);
            if (joinPoint == null) return;

            IProcessInstance processInstance = tk.ProcessInstance;
            // Synchronize的fire条件应该只与joinPoint的value有关（value==volume），与alive无关
            NodeInstanceEvent event2 = new NodeInstanceEvent(this);
            event2.setToken(tk);
            event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
            fireNodeEnteredEvent(event2);

            //在此事件监听器中，删除原有的token
            NodeInstanceEvent event4 = new NodeInstanceEvent(this);
            event4.setToken(tk);
            event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
            fireNodeLeavingEvent(event4);

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
            {//如果没有循环，则执行transitionInstance
                //非顺序流转的需要生成新的token，
                Boolean activiateDefaultCondition = true;
                ITransitionInstance defaultTransInst = null;
                for (int i = 0; LeavingTransitionInstances != null && i < LeavingTransitionInstances.Count; i++)
                {
                    ITransitionInstance transInst = LeavingTransitionInstances[i];
                    String condition = transInst.getTransition().Condition;
                    if (condition != null && condition.Equals(ConditionConstant.DEFAULT))
                    {
                        defaultTransInst = transInst;
                        continue;
                    }

                    Token token = new Token(); // 产生新的token
                    token.IsAlive=joinPoint.getAlive();
                    token.ProcessInstance=processInstance;
                    token.StepNumber=joinPoint.getStepNumber();
                    token.FromActivityId=joinPoint.getFromActivityId();
                    Boolean alive = transInst.take(token);
                    if (alive)
                    {
                        activiateDefaultCondition = false;
                    }

                }
                if (defaultTransInst != null)
                {
                    Token token = new Token();
                    token.IsAlive=activiateDefaultCondition && joinPoint.getAlive();
                    token.ProcessInstance=processInstance;
                    token.StepNumber=joinPoint.getStepNumber();
                    token.FromActivityId=joinPoint.getFromActivityId();
                    defaultTransInst.take(token);
                }

            }

            NodeInstanceEvent event3 = new NodeInstanceEvent(this);
            event3.setToken(tk);
            event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
            fireNodeLeavingEvent(event3);
        }

        // public int getValue() {
        // return value;
        // }
        //
        // public void setValue(int tokens) {
        // this.value = tokens;
        // }
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
            if (!Extension_Target_Name.Equals(extension.getExtentionTargetName()))
            {
                throw new Exception(
                        "Error:When construct the SynchronizerInstance,the Extension_Target_Name is mismatching");
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
                            "Error:When construct the SynchronizerInstance,the extension MUST be a instance of INodeInstanceEventListener");
                }
            }
        }

        public override String ToString()
        {
            return "SynchronizerInstance_4_[" + synchronizer.Id + "]";
        }

        public Synchronizer getSynchronizer()
        {
            return this.synchronizer;
        }
    }

}
