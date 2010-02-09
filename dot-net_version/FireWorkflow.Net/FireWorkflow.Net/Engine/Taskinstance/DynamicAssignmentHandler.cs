using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Engine.Impl;

namespace FireWorkflow.Net.Engine.Taskinstance
{
    /// <summary>
    /// 动态任务分配句柄，用于指定后续环节的操作员。
    /// </summary>
    public class DynamicAssignmentHandler : IAssignmentHandler
    {
        /// <summary>工作项是否需要签收</summary>
        Boolean needClaim = false;

        /// <summary>操作员Id列表</summary>
        List<String> actorIdsList = null;

        public void assign(IAssignable asignable, String performerName)// throws EngineException, KernelException 
        {
            if (actorIdsList == null || actorIdsList.Count == 0)
            {
                TaskInstance taskInstance = (TaskInstance)asignable;
                throw new EngineException(taskInstance.getProcessInstanceId(), taskInstance.getWorkflowProcess(),
                        taskInstance.getTaskId(), "actorIdsList can not be empty");
            }

            List<IWorkItem> workItems = asignable.asignToActors(actorIdsList);

            ITaskInstance taskInst = (ITaskInstance)asignable;
            if (!needClaim)
            {
                if (FormTask.ALL.Equals(taskInst.getAssignmentStrategy()) ||
                        (FormTask.ANY.Equals(taskInst.getAssignmentStrategy()) && actorIdsList.Count == 1))
                {
                    for (int i = 0; i < workItems.Count; i++)
                    {
                        IWorkItem wi = workItems[i];
                        wi.claim();
                    }
                }
            }
        }

        public List<String> getActorIdsList()
        {
            return actorIdsList;
        }

        public void setActorIdsList(List<String> actorIdsList)
        {
            this.actorIdsList = actorIdsList;
        }

        public Boolean isNeedClaim()
        {
            return needClaim;
        }

        public void setNeedClaim(Boolean needSign)
        {
            this.needClaim = needSign;
        }
    }
}
