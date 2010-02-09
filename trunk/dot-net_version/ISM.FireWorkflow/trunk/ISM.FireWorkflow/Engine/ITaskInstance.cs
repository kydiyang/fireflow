using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ISM.FireWorkflow.Model;
using ISM.FireWorkflow.Model.Net;

namespace ISM.FireWorkflow.Engine
{
    /// <summary>
    /// 任务实例
    /// 对任务实例的状态字段作如下规定：小于5的状态为“活动”状态，大于等于5的状态为“非活动”状态。
    /// 活动状态包括：INITIALIZED,RUNNING,SUSPENDED
    /// 非活动状态包括：COMPLETED,CANCELED
    /// </summary>
    public abstract class ITaskInstance
    {
        /// <summary>初始化状态</summary>
        public const int INITIALIZED = 0;

        /// <summary>运行状态</summary>
        public const int RUNNING = 1;

        //被挂起
        // public const int SUSPENDED = 3;

        /// <summary>已经结束</summary>
        public const int COMPLETED = 7;

        /// <summary>被撤销</summary>
        public const int CANCELED = 9;

        /// <summary>返回任务实例的Id</summary>
        public abstract String getId();

        /// <summary>返回对应的任务Id</summary>
        public abstract String getTaskId();

        /// <summary>返回任务Name</summary>
        public abstract String getName();

        /// <summary>返回任务显示名</summary>
        public abstract String getDisplayName();

        // public IProcessInstance getProcessInstance();

        /// <summary>返回对应的流程实例Id</summary>
        public abstract String getProcessInstanceId();

        /// <summary>返回对应的流程的Id</summary>
        public abstract String getProcessId();

        /// <summary>返回流程的版本</summary>
        public abstract Int32 getVersion();

        /// <summary>返回任务实例创建的时间</summary>
        public abstract DateTime? getCreatedTime();

        /// <summary>返回任务实例启动的时间</summary>
        public abstract DateTime? getStartedTime();

        /// <summary>返回任务实例结束的时间</summary>
        public abstract DateTime? getEndTime();

        /// <summary>返回任务实例到期日期</summary>
        public abstract DateTime? getExpiredTime();// 过期时间

        /// <summary>返回任务实例的状态，取值为：INITIALIZED(已初始化），STARTED(已启动),COMPLETED(已结束),CANCELD(被取消)</summary>
        public abstract Int32? getState();

        /// <summary>返回任务实例的分配策略，取值为 org.fireflow.model.Task.ALL或者org.fireflow.model.Task.ANY</summary>
        public abstract String getAssignmentStrategy();

        /// <summary>返回任务实例所属的环节的Id</summary>
        public abstract String getActivityId();

        /// <summary>
        /// 返回任务类型，取值为org.fireflow.model.Task.FORM,org.fireflow.model.Task.TOOL,
        /// fireflow.model.Task.SUBFLOW或者fireflow.model.Task.DUMMY
        /// </summary>
        public abstract String getTaskType();

        // 取消该任务（保留，未实现） 这个方法暂时取消，因为abort无清晰的无二义性的业务含义。（2009-04-12）
        // public void abort() throws EngineException,KernelException;

        /// <summary>
        /// 返回任务是里对应的环节
        /// fireflow.engine.EngineException
        /// </summary>
        public abstract Activity getActivity();// throws EngineException;

        /// <summary>
        /// 返回任务实例对应的流程
        /// fireflow.engine.EngineException
        /// </summary>
        public abstract WorkflowProcess getWorkflowProcess();// throws EngineException;

        /// <summary>
        /// 返回任务实例对应的Task对象
        /// fireflow.engine.EngineException
        /// </summary>
        public abstract Task getTask();// throws EngineException;

        /// <summary>当执行JumpTo和LoopTo操作时，返回目标Activity 的Id</summary>
        public abstract String getTargetActivityId();

        /// <summary>返回TaskInstance的"步数"。</summary>
        public abstract Int32 getStepNumber();

        /// <summary></summary>
        public abstract void suspend();// throws EngineException;

        public abstract Boolean? isSuspended();

        /// <summary>
        /// 从挂起状态恢复到挂起前的状态
        /// fireflow.engine.EngineException
        /// </summary>
        public abstract void restore();// throws EngineException;

        // public Set getWorkItems() ;
    }
}
