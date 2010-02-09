using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Resource
{
    /// <summary>参与者。</summary>
    public class Participant : AbstractResource
    {

        /// <summary>
        /// 任务分配句柄的类名。<br/>
        /// Fire workflow引擎调用该句柄获得真正的操作者ID。
        /// </summary>
        private String assignmentHandlerClassName = null;

        public Participant(String name)
        {
            this.setName(name);
        }

        public void setAssignmentHandler(String assignmentHandlerClassName)
        {
            this.assignmentHandlerClassName = assignmentHandlerClassName;
        }

        public String getAssignmentHandler()
        {
            return assignmentHandlerClassName;
        }
    }
}
