using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Kernel;

namespace FireWorkflow.Net.Engine
{
    public class EngineException : KernelException
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="processInstance">processInstance 发生异常的流程实例</param>
        /// <param name="workflowElement">workflowElement 发生异常的流程环节或者Task</param>
        /// <param name="errMsg">错误信息</param>
        public EngineException(IProcessInstance processInstance, IWFElement workflowElement, String errMsg)
            :base(processInstance, workflowElement, errMsg)
        {
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="processInstanceId">发生异常的流程实例Id</param>
        /// <param name="process">发生异常的流程</param>
        /// <param name="workflowElementId">发生异常的环节或者Task的Id</param>
        /// <param name="errMsg">错误信息</param>
        public EngineException(String processInstanceId, WorkflowProcess process,
                String workflowElementId, String errMsg)
            : base(null, null, errMsg)
        {
            this.setProcessInstanceId(processInstanceId);
            if (process != null)
            {
                this.setProcessId(process.getId());
                this.setProcessName(process.getName());
                this.setProcessDisplayName(process.getDisplayName());

                IWFElement workflowElement = process.findWFElementById(workflowElementId);
                if (workflowElement != null)
                {
                    this.setWorkflowElementId(workflowElement.getId());
                    this.setWorkflowElementName(workflowElement.getName());
                    this.setWorkflowElementDisplayName(workflowElement.getDisplayName());
                }
            }
        }
        /*
        public EngineException() {
        super();
        // TODO Auto-generated constructor stub
        }

        public EngineException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
        }

        public EngineException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
        }

        public EngineException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
        }
         */
    }
}
