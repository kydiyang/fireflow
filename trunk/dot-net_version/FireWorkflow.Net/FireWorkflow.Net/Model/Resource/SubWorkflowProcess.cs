using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Resource
{
    /// <summary>子流程。保存对另一个流程引用信息</summary>
    public class SubWorkflowProcess : AbstractResource
    {
        /// <summary>所引用的流程的id</summary>
        private String workflowProcessId = null;

        public SubWorkflowProcess(String name)
        {
            this.setName(name);
        }

        public String getWorkflowProcessId()
        {
            return workflowProcessId;
        }

        public void setWorkflowProcessId(String workflowProcessId)
        {
            this.workflowProcessId = workflowProcessId;
        }
    }
}
