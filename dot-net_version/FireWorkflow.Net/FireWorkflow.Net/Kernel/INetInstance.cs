using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Engine;

namespace FireWorkflow.Net.Kernel
{

    /// <summary>
    /// ProcessInstance负责和外部运行环境（RuntimeContext)沟通
    /// @author chennieyun
    /// 是否应该叫做WorkflowNetInstance?
    /// </summary>
    public interface INetInstance
    {
        //	public void setRuntimeContext(IRuntimeContext rtCtx);
        //	
        //	public IRuntimeContext getRuntimeContext();

        String getId();

        Int32 getVersion();

        //TODO 实参-形参如何体现？通过Context?
        void run(IProcessInstance processInstance);//throws KernelException;

        /// <summary>
        /// 结束流程实例，如果流程状态没有达到终态，则直接返回。
        /// @throws RuntimeException
        /// </summary>
        void complete();//throws KernelException;


        WorkflowProcess getWorkflowProcess();

        Object getWFElementInstance(String wfElementId);
    }
}
