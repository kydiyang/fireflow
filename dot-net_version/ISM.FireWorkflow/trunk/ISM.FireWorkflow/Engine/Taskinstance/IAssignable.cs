using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine.Taskinstance
{
    /// <summary>
    /// TaskInstance实现了该节口，用于任务分配
    /// </summary>
    public interface IAssignable
    {
        /**
         * 将TaskInstance分配给编号为actorId的操作员。即系统只创建一个WorkItem，并分配给编号为actorId的操作员
         * @param actorId 操作员Id
         * @param needClaim 是否需要签收，true表示需要操作员执行签收动作,系统将WorkItem设置为INITIALIZED状态；false不需要签收，Workitem直接被设置为STARTED状态。
         * @return  返回创建的WorkItem
         */
        //    public IWorkItem asignToActor(String actorId, Boolean needClaim) throws EngineException,KernelException;

        /// <summary>
        /// 将TaskInstance分配给编号为actorId的操作员。即系统只创建一个WorkItem，并分配给编号为actorId的操作员
        /// 该WorkItem需要签收
        /// </summary>
        /// <param name="actorId">操作员Id</param>
        /// <returns>返回创建的WorkItem</returns>
        IWorkItem asignToActor(String actorId);// throws EngineException,KernelException;

        /// <summary>
        /// 将TaskInstance分配给列表中的操作员。即创建N个WorkItem，每个操作员一个WorkItem，并且这些WorkItem都需要签收。
        /// 最终由那个操作员执行该任务实例，是由Task的分配策略决定的。
        /// 如果分配策略为ALL,即会签的情况，则所有的操作员都要完成相应的工单。
        /// 如果分配策略为ANY，则最先签收的那个操作员完成其工单和任务实例，其他操作员的工单被删除。
        /// </summary>
        /// <param name="actorIds">操作员Id</param>
        /// <returns>返回创建的WorkItem列表</returns>
        List<IWorkItem> asignToActors(List<String> actorIds);// throws EngineException, KernelException;
    }
}
