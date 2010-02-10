using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Engine
{
    /// <summary>
    /// 流程实例接口
    /// 对流程实例的状态字段作如下规定：小于5的状态为“活动”状态，大于等于5的状态为“非活动”状态。
    /// 活动状态包括：INITIALIZED,RUNNING,SUSPENDED
    /// 非活动状态包括：COMPLETED,CANCELED
    /// </summary>
    public abstract class IProcessInstance
    {
        /// <summary>初始化状态</summary>
        public const int INITIALIZED = 0;

        /// <summary>运行状态</summary>
        public const int RUNNING = 1;

        //被挂起
        //public const int SUSPENDED = 3;

        /// <summary>已经结束</summary>
        public const int COMPLETED = 7;

        /// <summary>被撤销</summary>
        public const int CANCELED = 9;

        public abstract void run();// throws EngineException, KernelException;

        /// <summary>return the process instance's Id.</summary>
        public abstract String getId();

        /// <summary>return the process instance's name,which Equals to the workflow process's name</summary>
        public abstract String getName();

        /// <summary>
        /// return the process instance's display-name ，
        /// which Equals to the workflow process's  display-name.
        /// </summary>
        public abstract String getDisplayName();

        /// <summary>return the workflow process's id</summary>
        public abstract String getProcessId();

        public abstract Int32 getState();

        /// <summary>return the workflow process's version.</summary>
        public abstract Int32 getVersion();

        /// <summary>流程实例创建者ID</summary>
        public abstract String getCreatorId();

        /// <summary>返回流程实例的创建时间</summary>
        /// <returns>流程实例的创建时间</returns>
        public abstract DateTime? getCreatedTime();

        /// <summary>返回流程实例的启动时间，即执行IProcessInstance.run()的时间</summary>
        public abstract DateTime? getStartedTime();

        /// <summary>返回流程实例的结束时间</summary>
        public abstract DateTime? getEndTime();

        /// <summary>返回流程实例的到期时间</summary>
        public abstract DateTime? getExpiredTime();

        /// <summary>
        /// Get the process instance variable,return null if the variable is not existing .
        /// </summary>
        /// <param name="name">the name of the variable</param>
        /// <returns>the value of the variable. It may be Int32,String,Boolean,java.util.DateTime or Float</returns>
        public abstract Object getProcessInstanceVariable(String name);

        /// <summary>
        /// Save the process instance variable.If there is a variable with the same name ,it will be updated.
        /// </summary>
        /// <param name="name"></param>
        /// <param name="var">The value of the variable. It may be Int32,String,Boolean,java.util.DateTime or Float</param>
        public abstract void setProcessInstanceVariable(String name, Object var);

        /// <summary>Get all the process instance variables. the key of the returned map is the variable's name</summary>
        public abstract Dictionary<String, Object> getProcessInstanceVariables();

        /// <summary>update the process instance variables batched.</summary>
        public abstract void setProcessInstanceVariables(Dictionary<String, Object> vars);

        /// <summary>return the corresponding workflow process.</summary>
        public abstract WorkflowProcess getWorkflowProcess();// throws EngineException;

        /// <summary>get the parent process instance's id , null if no parent process instance.</summary>
        public abstract String getParentProcessInstanceId();

        /// <summary>get the parent taskinstance's id ,null if no parent taskinstance.</summary>
        public abstract String getParentTaskInstanceId();

        /// <summary>强行中止流程实例，不管是否达到终态。</summary>
        public abstract void abort();// throws EngineException;

        /// <summary>
        /// 挂起
        /// fireflow.engine.EngineException
        /// </summary>
        public abstract void suspend();// throws EngineException;

        public abstract Boolean? IsSuspended();

        /// <summary>
        /// 从挂起状态恢复到挂起前的状态
        /// fireflow.engine.EngineException
        /// </summary>
        public abstract void restore();// throws EngineException;
    }
}
