using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine
{
    /// <summary>
    /// org.fireflow.engine.impl.ProcessInstance,org.fireflow.engine.impl.TaskInstance,
    /// org.fireflow.engine.impl.WorkItem都实现了该接口。实现该接口的目的是使得对象可以保存和返回当前
    /// 的WorkflowSession。
    /// 方法IWorkflowSession.execute(IWorkflowSessionCallback callback)会自定判断待返回的对象是否
    /// 实现了IWorkflowSessionAware,如果实现该接口，则自动将本身设置该待返回的对象。 
    /// </summary>
    public interface IWorkflowSessionAware
    {
        /// <summary>设置或返回当前的IWorkflowSession</summary>
        /// <returns></returns>
        IWorkflowSession CurrentWorkflowSession { get; set; }
    }
}
