using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Engine.Impl;

namespace FireWorkflow.Net.Engine.Taskinstance
{
    public class DefaultToolTaskInstanceRunner : ITaskInstanceRunner
    {

        public void run(IWorkflowSession currentSession, RuntimeContext runtimeContext, IProcessInstance processInstance, ITaskInstance taskInstance)// throws EngineException, KernelException 
        {
            if (taskInstance.getTaskType() != TaskTypeEnum.TOOL)
            {
                throw new EngineException(processInstance,
                        taskInstance.getActivity(),
                        "DefaultToolTaskInstanceRunner：TaskInstance的任务类型错误，只能为TOOL类型");
            }
            Task task = taskInstance.getTask();
            if (task == null)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "The Task is null,can NOT start the taskinstance,");
            }
            if (((ToolTask)task).Application == null || ((ToolTask)task).Application.Handler == null)
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "The task.getApplication() is null or task.getApplication().getHandler() is null,can NOT start the taskinstance,");
            }

            Object obj = runtimeContext.getBeanByName(((ToolTask)task).Application.Handler);

            if (obj == null || !(obj is IApplicationHandler))
            {
                WorkflowProcess process = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), process,
                        taskInstance.getTaskId(),
                        "Run tool task instance error! Not found the instance of " + ((ToolTask)task).Application.Handler + " or the instance not implements IApplicationHandler");

            }

            try
            {
                ((IApplicationHandler)obj).execute(taskInstance);
            }
            catch (Exception )
            {
                //TODO, 对tool类型的task抛出的错误应该怎么处理？
            }

            ITaskInstanceManager taskInstanceManager = runtimeContext.TaskInstanceManager;
            taskInstanceManager.completeTaskInstance(currentSession, processInstance, taskInstance, null);
            //        taskInstanceManager.completeTaskInstance(taskInstance, null);
        }

    }

}
