using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Engine;

namespace FireWorkflow.Net.Kernel
{

    /// <summary>
    /// token的生命周期开始与一个synchronizer（包括startnode 和 endnode)，结束于另一个synchronizer
    /// </summary>
    public abstract class IToken
    {

        public const String FROM_ACTIVITY_ID_SEPARATOR = "&";
        public const String FROM_START_NODE = "FROM_START_NODE";
        //token因该没有和外部运行环境(RuntimeContext)沟通的职责
        //	public void setRuntimeContext(IRuntimeContext rtCtx);
        //	public IRuntimeContext getRuntimeContext();

        public abstract IProcessInstance getProcessInstance();
        public abstract void setProcessInstance(IProcessInstance inst);

        public abstract void setProcessInstanceId(String id);
        public abstract String getProcessInstanceId();

        //	public INetInstance getNetInstance();
        //	public void setNetInstance(INetInstance netInst); 

        //	public INodeInstance getCurrentNodeInstance();
        //	public void setCurrentNodeInstance(INodeInstance currentNodeInstance);
        //	
        public abstract String getNodeId();
        public abstract void setNodeId(String nodeId);

        //	public void setTransitionInstance(ITransitionInstance transInst);
        //	public ITransitionInstance getTransitionInstance();

        public abstract void setValue(Int32 v);

        public abstract Int32 getValue();

        /// <summary>
        /// 通过alive标志来判断nodeinstance是否要fire
        /// </summary>
        /// <returns></returns>
        public abstract Boolean isAlive();

        public abstract void setAlive(Boolean b);

        //	public void setAppointedTransitionNames(Set<String> appointedTransitionNames);
        //	
        //	public Set<String> getAppointedTransitionNames();

        public abstract String getId();
        public abstract void setId(String id);

        public abstract Int32 getStepNumber();

        public abstract void setStepNumber(Int32 i);

        /// <summary>
        /// 获得前驱Activity的Id,如果有多个，则用"&"分割
        /// </summary>
        /// <returns></returns>
        public abstract String getFromActivityId();
        public abstract void setFromActivityId(String s);
    }

}
