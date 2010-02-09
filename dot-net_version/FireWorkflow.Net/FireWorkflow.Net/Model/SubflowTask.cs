using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model.Resource;

namespace FireWorkflow.Net.Model
{
    /// <summary>子流程类型的Task。</summary>
    public class SubflowTask : Task
    {
        /// <summary>任务所引用的子流程信息。</summary>
        protected SubWorkflowProcess subWorkflowProcess = null;

        //subflow Task如何会签？

        public SubflowTask()
        {
            this.setType(SUBFLOW);
        }

        public SubflowTask(IWFElement parent, String name)
            : base(parent, name)
        {
            this.setType(SUBFLOW);
        }

        /// <summary>返回SUBFLOW类型的任务的子流程信息。</summary>
        public SubWorkflowProcess getSubWorkflowProcess()
        {
            return subWorkflowProcess;
        }

        public void setSubWorkflowProcess(SubWorkflowProcess subWorkflowProcess)
        {
            this.subWorkflowProcess = subWorkflowProcess;
        }
    }
}
