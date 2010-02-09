using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Taskinstance
{
    /// <summary>
    /// 任务分配处理程序，工作流系统将真正的任务分配工作交给该处理程序完成。
    /// 所有的FORM类型的Task都需要设置其Performer属性，Performer属性实际上是一个Participant对象，
    /// 由该对象提供IAssignmentHandler实现类。
    /// </summary>
    public interface IAssignmentHandler
    {
        /// <summary>
        /// 实现任务分配工作，该方法一般的实现逻辑是：
        /// 首先根据performerName查询出所有的操作员，可以把performerName当作角色名称。
        /// 然后调用asignable.asignToActor(String actorId,Boolean needSign)或者
        /// asignable.asignToActor(String actorId)或者asignable.asignToActorS(List actorIds)
        /// 进行任务分配。
        /// </summary>
        /// <param name="asignable">IAssignable实现类，在FireWorkflow中实际上就是TaskInstance对象。</param>
        /// <param name="performerName">角色名称</param>
        void assign(IAssignable asignable, String performerName);// throws EngineException, KernelException;

        //后续版本实现。。。
        //    public void assign(IWorkflowSession workflowSession, IProcessInstance processInstance, 
        //            IAssignable asignable, String performerName)throws EngineException,KernelException;
    }
}
