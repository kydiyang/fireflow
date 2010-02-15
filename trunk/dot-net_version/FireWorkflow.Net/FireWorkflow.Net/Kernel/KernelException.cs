using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Engine;

namespace FireWorkflow.Net.Kernel
{
    public class KernelException : Exception
    {
        /// <summary>抛出异常的流程实例的Id</summary>
        String processInstanceId = null;

        /// <summary>抛出异常的流程定义的Id</summary>
        String processId = null;

        /// <summary>抛出异常的流程的名称</summary>
        String processName = null;

        /// <summary>抛出异常的流程的显示名称</summary>
        String processDisplayName = null;

        /// <summary>抛出异常的流程元素的Id</summary>
        String workflowElementId = null;

        /// <summary>抛出异常的流程元素的名称</summary>
        String workflowElementName = null;

        /// <summary>抛出异常的流程元素的显示名称</summary>
        String workflowElementDisplayName = null;

        public KernelException(IProcessInstance processInstance, IWFElement workflowElement, String errMsg)
            : base(errMsg)
        {
            if (processInstance != null)
            {
                this.setProcessInstanceId(processInstance.getId());
                this.setProcessId(processInstance.getProcessId());
                this.setProcessName(processInstance.getName());
                this.setProcessDisplayName(processInstance.getDisplayName());
            }
            if (workflowElement != null)
            {
                this.setWorkflowElementId(workflowElement.Id);
                this.setWorkflowElementName(workflowElement.Name);
                this.setWorkflowElementDisplayName(workflowElement.DisplayName);
            }
            // TODO Auto-generated constructor stub
        }

        //    public KenelException() {
        //        super();
        //        // TODO Auto-generated constructor stub
        //    }
        //
        //    public KenelException(String processInstanceId, String processId, String processName, String message) {
        //
        //    }
        public String getProcessId()
        {
            return processId;
        }

        public void setProcessId(String processId)
        {
            this.processId = processId;
        }

        public String getProcessInstanceId()
        {
            return processInstanceId;
        }

        public void setProcessInstanceId(String processInstanceId)
        {
            this.processInstanceId = processInstanceId;
        }

        public String getProcessName()
        {
            return processName;
        }

        public void setProcessName(String processName)
        {
            this.processName = processName;
        }

        public String getWorkflowElementId()
        {
            return workflowElementId;
        }

        public void setWorkflowElementId(String workflowElementId)
        {
            this.workflowElementId = workflowElementId;
        }

        public String getWorkflowElementName()
        {
            return workflowElementName;
        }

        public void setWorkflowElementName(String workflowElementName)
        {
            this.workflowElementName = workflowElementName;
        }

        public String getProcessDisplayName()
        {
            return processDisplayName;
        }

        public void setProcessDisplayName(String processDisplayName)
        {
            this.processDisplayName = processDisplayName;
        }

        public String getWorkflowElementDisplayName()
        {
            return workflowElementDisplayName;
        }

        public void setWorkflowElementDisplayName(String workflowElementDisplayName)
        {
            this.workflowElementDisplayName = workflowElementDisplayName;
        }
        /*
        public KenelException() {
        super();
        // TODO Auto-generated constructor stub
        }

        public KenelException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
        }

        public KenelException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
        }

        public KenelException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
        }
         */
    }
}
