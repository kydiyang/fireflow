using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Engine
{
    public enum ProcessInstanceEnum
    {
        /// <summary>初始化状态</summary>
        INITIALIZED = 0,
        /// <summary>运行状态</summary>
        RUNNING = 1,
        /// <summary>已经结束</summary>
        COMPLETED = 7,
        /// <summary>被撤销</summary>
        CANCELED = 9
    }
    /// <summary>
    /// <para>流程实例接口</para>
    /// <para>对流程实例的状态字段作如下规定：小于5的状态为“活动”状态，大于等于5的状态为“非活动”状态。</para>
    /// <para>活动状态包括：INITIALIZED,RUNNING,SUSPENDED</para>
    /// 非活动状态包括：COMPLETED,CANCELED
    /// </summary>
    public interface IProcessInstance
    {
        /// <summary>流程实例开始运行！</summary>
        void run();// throws EngineException, KernelException;

        /// <summary>主键.</summary>
        String Id { get; }

        /// <summary>流程实例的name（与流程定义的name相同）</summary>
        String Name { get; }

        /// <summary>流程实例的DisplayName（与流程定义的DisplayName相同）</summary>
        String DisplayName { get; }

        /// <summary>流程定义的id</summary>
        String ProcessId { get; }

        /// <summary>流程实例的状态</summary>
        ProcessInstanceEnum State { get; }

        /// <summary>流程定义的Version</summary>
        Int32 Version { get; }

        /// <summary>流程实例创建者ID</summary>
        String CreatorId { get; }

        /// <summary>返回流程实例的创建时间</summary>
        /// <returns>流程实例的创建时间</returns>
        DateTime? CreatedTime { get; }

        /// <summary>返回流程实例的启动时间，即执行IProcessInstance.run()的时间</summary>
        DateTime? StartedTime { get; }

        /// <summary>返回流程实例的结束时间</summary>
        DateTime? EndTime { get; }

        /// <summary>返回流程实例的到期时间</summary>
        DateTime? ExpiredTime { get; }

        Dictionary<String, Object> ProcessInstanceVariables { get; set; }

        /// <summary>
        /// <para>获取流程实例变量的值</para>
        /// Get the process instance variable,return null if the variable is not existing .
        /// </summary>
        /// <param name="name">the name of the variable</param>
        /// <returns>the value of the variable. It may be Int32,String,Boolean,java.util.DateTime or Float</returns>
        Object getProcessInstanceVariable(String name);

        /// <summary>
        /// Save the process instance variable.If there is a variable with the same name ,it will be updated.
        /// </summary>
        /// <param name="name"></param>
        /// <param name="var">The value of the variable. It may be Int32,String,Boolean,java.util.DateTime or Float</param>
        void setProcessInstanceVariable(String name, Object var);



        /// <summary>return the corresponding workflow process.</summary>
        WorkflowProcess WorkflowProcess { get; }// throws EngineException;

        /// <summary>get the parent process instance's id , null if no parent process instance.</summary>
        String ParentProcessInstanceId { get; }

        /// <summary>get the parent taskinstance's id ,null if no parent taskinstance.</summary>
        String ParentTaskInstanceId { get; }

        /// <summary>强行中止流程实例，不管是否达到终态。</summary>
        void abort();// throws EngineException;

        /// <summary>
        /// 挂起
        /// fireflow.engine.EngineException
        /// </summary>
        void suspend();// throws EngineException;

        /// <summary>是否挂起</summary>
        Boolean? IsSuspended();

        /// <summary>
        /// 从挂起状态恢复到挂起前的状态
        /// fireflow.engine.EngineException
        /// </summary>
        void restore();// throws EngineException;
    }
}
