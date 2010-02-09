using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.Serialization;
using System.Text;

namespace FireWorkflow.Net.Model
{
    /// <summary>工作流任务</summary>
    public abstract class Task : AbstractWFElement
    {
        /// <summary>任务类型之二 ：TOOL类型，即工具类型任务，该任务自动调用java代码完成特定的工作。</summary>
        public const String TOOL = "TOOL";


        /// <summary>任务类型之三：SUBFLOW类型，即子流程任务</summary>
        public const String SUBFLOW = "SUBFLOW";


        /// <summary>任务类型之一：FORM类型，最常见的一类任务，代表该任务需要操作员填写相关的表单。</summary>
        public const String FORM = "FORM";

        /// <summary>任务类型之四：DUMMY类型，该类型暂时没有用到，保留。</summary>
        public const String DUMMY = "DUMMY";

        /// <summary>
        /// 循环情况下，任务分配指示之一：重做<br />
        /// 对于Tool类型和Subflow类型的task会重新执行一遍
        /// 对于Form类型的Task，重新执行一遍，且将该任务实例分配给最近一次完成同一任务的操作员。
        /// </summary>
        public const String REDO = "REDO";

        /// <summary>
        /// 循环情况下，任务分配指示之二：忽略<br />
        /// 循环的情况下该任务将被忽略，即在流程实例的生命周期里，仅执行一遍。
        /// </summary>
        public const String SKIP = "SKIP";

        /// <summary>
        /// 循环的情况下，任务分配指示之三：无<br/>
        /// 对于Tool类型和Subflow类型的task会重新执行一遍，和REDO效果一样的。<br/>
        /// 对于Form类型的Task，重新执行一遍，且工作流引擎仍然调用Performer属性的AssignmentHandler分配任务
        /// </summary>
        public const String NONE = "NONE";

        /// <summary>任务类型,取值为FORM,TOOL,SUBFLOW,DUMMY(保留)，缺省值为FORM</summary>

        [XmlAttribute(AttributeName = "Type")]
        public String type = FORM;//

        /// <summary>任务执行的时限</summary>
        protected Duration duration;

        /// <summary>任务优先级别(1.0暂时没有用到)</summary>
        protected int priority = 1;

        /// <summary>循环情况下任务执行策略，取值为REDO、SKIP和NONE,</summary>
        protected String loopStrategy = REDO;//

        /// <summary>任务实例创建器。如果没有设置，则使用所在流程的全局任务实例创建器。</summary>
        protected String taskInstanceCreator = null;

        /// <summary>任务实例运行器，如果没有设置，则使用所在流程的全局的任务实例运行器</summary>
        protected String taskInstanceRunner = null;

        /// <summary>任务实例的终结评价器，用于告诉引擎，该实例是否可以结束。如果没有设置，则使用所在流程的全局的任务实例终结评价器。</summary>
        protected String taskInstanceCompletionEvaluator = null;

        public Task()
        {
        }

        public Task(IWFElement parent, String name)
            : base(parent, name)
        {
        }
        /// <summary>返回任务的优先级。（引擎暂时没有用到）</summary>
        public int getPriority()
        {
            return priority;
        }

        /// <summary>设置任务的优先级。（引擎暂时没有用到）</summary>
        public void setPriority(int priority)
        {
            this.priority = priority;
        }

        public override String ToString()
        {
            return "Task[id='" + getId() + ", name='" + getName() + "']";
        }

        /// <summary>返回任务的类型</summary>
        /// <returns>任务类型，FORM,TOOL,SUBFLOW或者DUMMY</returns>
        public String getType()
        {
            return type;
        }

        /// <summary>设置任务类型</summary>
        /// <param name="taskType">任务类型，FORM,TOOL,SUBFLOW或者DUMMY</param>
        public void setType(String taskType)
        {
            this.type = taskType;
        }
        /// <summary>返回任务的完成期限</summary>
        /// <returns>以Duration表示的任务的完成期限</returns>

        public Duration getDuration()
        {
            return duration;
        }

        /// <summary>设置任务的完成期限</summary>
        /// <param name="limit">时间间隔</param>
        public void setDuration(Duration limit)
        {
            this.duration = limit;
        }

        public String getTaskInstanceCreator()
        {
            return taskInstanceCreator;
        }

        public void setTaskInstanceCreator(String taskInstanceCreator)
        {
            this.taskInstanceCreator = taskInstanceCreator;
        }

        public String getTaskInstanceCompletionEvaluator()
        {
            return taskInstanceCompletionEvaluator;
        }

        public void setTaskInstanceCompletionEvaluator(String taskInstanceCompletionEvaluator)
        {
            this.taskInstanceCompletionEvaluator = taskInstanceCompletionEvaluator;
        }


        public String getTaskInstanceRunner()
        {
            return taskInstanceRunner;
        }

        public void setTaskInstanceRunner(String taskInstanceRunner)
        {
            this.taskInstanceRunner = taskInstanceRunner;
        }

        public String getLoopStrategy()
        {
            return loopStrategy;
        }

        public void setLoopStrategy(String loopStrategy)
        {
            this.loopStrategy = loopStrategy;
        }
    }
}
