using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Engine.Impl;

namespace FireWorkflow.Net.Engine.Taskinstance
{

    /// <summary>任务实例管理器</summary>
    public interface ITaskInstanceManager : IRuntimeContextAware
    {

        /// <summary>
        /// 创建taskinstance实例
        /// </summary>
        /// <param name="token"></param>
        /// <param name="activityInstance"></param>
        void createTaskInstances(IToken token, IActivityInstance activityInstance);// throws EngineException, KernelException;

        /// <summary>
        /// 将已经完成的taskinstance实例转移到已办表
        /// （该方法保留在1.0中未使用，暂时保留，20090317）
        /// </summary>
        /// <param name="activityInstance"></param>
        void archiveTaskInstances(IActivityInstance activityInstance);// throws EngineException, KernelException;

        /// <summary>
        /// 启动TaskInstance，其状态将从INITIALIZED变成STARTED状态。
        /// 对于Tool类型的TaskInstance,将直接调用外部应用程序。
        /// 对于Sbuflow类型的TaskInstance，将启动子流程。
        /// 对于Form类型的TaskInstance，仅改变其状态纪录启动时间。
        /// </summary>
        /// <param name="currentSession"></param>
        /// <param name="processInstance"></param>
        /// <param name="taskInstance"></param>
        void startTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance);// throws EngineException, KernelException   ;

        /// <summary>
        /// 结束TaskInstance以及当前的ActivityInstance，并执行targetActivityInstance环节实例。
        /// 如果targetActivityInstance为null表示由工作流引擎根据流程定义自动流转到下一个环节。
        /// </summary>
        void completeTaskInstance(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance, IActivityInstance targetActivityInstance);// throws EngineException, KernelException;

        /// <summary>
        /// 根据TaskInstance创建workItem。
        /// </summary>
        WorkItem createWorkItem(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance, String actorId);// throws EngineException;

        /// <summary>
        /// 签收WorkItem。
        /// </summary>
        IWorkItem claimWorkItem(String workItemId, String taskInstanceId);//throws EngineException, KernelException ;

        /// <summary>
        /// 结束WorkItem
        /// </summary>
        void completeWorkItem(IWorkItem workItem, IActivityInstance targetActivityInstance, String comments);//throws EngineException, KernelException ;


        void completeWorkItemAndJumpTo(IWorkItem workItem, String targetActivityId, String comments);// throws EngineException, KernelException ;

        /// <summary>
        /// 撤销刚才执行的Complete动作，系统将创建并返回一个新的Running状态的WorkItem
        /// </summary>
        /// <returns>新创建的工作项</returns>
        IWorkItem withdrawWorkItem(IWorkItem workItem);// throws EngineException, KernelException ;

        void rejectWorkItem(IWorkItem workItem, String comments);// throws  EngineException, KernelException ;

        /// <summary>
        /// 将工作项位派给其他人，自己的工作项变成CANCELED状态。返回新创建的WorkItem.
        /// </summary>
        /// <param name="workItem">我的WorkItem</param>
        /// <param name="actorId">被委派的Actor的Id</param>
        /// <param name="comments">备注信息</param>
        /// <returns>新创建的工作项</returns>
        IWorkItem reasignWorkItemTo(IWorkItem workItem, String actorId, String comments);
    }
}
