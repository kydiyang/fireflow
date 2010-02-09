using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model.Resource;

namespace FireWorkflow.Net.Model
{
    /// <summary>Tool类型的Task</summary>
    public class ToolTask : Task
    {
        /// <summary>TOOL类型的任务执行方式之一：异步执行</summary>
        public const String ASYNCHR = "ASYNCHR";

        /// <summary>TOOL类型的任务执行方式之二：同步执行</summary>
        public const String SYNCHR = "SYNCHR";

        /// <summary>任务所引用的应用程序对象</summary>
        protected Application application = null;

        protected String execution = SYNCHR;//缺省情况下是同步执行

        public ToolTask()
        {
            this.setType(TOOL);
        }

        public ToolTask(IWFElement parent, String name)
            : base(parent, name)
        {
            this.setType(TOOL);
        }

        /// <summary>返回任务自动执行的Application。只有TOOL类型的任务才有Application。</summary>
        public Application getApplication()
        {
            return application;
        }

        /// <summary>设置任务自动执行的Application</summary>
        public void setApplication(Application application)
        {
            this.application = application;
        }

        /// <summary>
        /// 返回TOOL类型的任务执行策略，取值为ASYNCHR或者SYNCHR
        /// 意义不大，已经被废除
        /// </summary>
        public String getExecution()
        {
            return execution;
        }

        /// <summary>
        /// 设置TOOL类型的任务执行策略，取值为ASYNCHR或者SYNCHR
        /// 意义不大，已经被废除
        /// </summary>
        public void setExecution(String execution)
        {
            this.execution = execution;
        }
    }
}
