using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Definition;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Resource;


namespace FireWorkflow.Net.Engine.Taskinstance
{
    public class DefaultSubflowTaskInstanceRunner : ITaskInstanceRunner
    {

        public void run(IWorkflowSession currentSession, RuntimeContext runtimeContext, IProcessInstance processInstance,
                ITaskInstance taskInstance)// throws EngineException, KernelException 
        {
            if (taskInstance.TaskType != TaskTypeEnum.SUBFLOW)
            {
                throw new EngineException(processInstance,
                        taskInstance.Activity,
                        "DefaultSubflowTaskInstanceRunner：TaskInstance的任务类型错误，只能为SUBFLOW类型");
            }
            Task task = taskInstance.Task;
            SubWorkflowProcess Subflow = ((SubflowTask)task).SubWorkflowProcess;

            WorkflowDefinition subWorkflowDef = runtimeContext.DefinitionService.GetTheLatestVersionOfWorkflowDefinition(Subflow.WorkflowProcessId);
            if (subWorkflowDef == null)
            {
                WorkflowProcess parentWorkflowProcess = taskInstance.WorkflowProcess;
                throw new EngineException(taskInstance.ProcessInstanceId, parentWorkflowProcess,
                        taskInstance.TaskId,
                        "系统中没有Id为" + Subflow.WorkflowProcessId + "的流程定义");
            }
            WorkflowProcess subWorkflowProcess = subWorkflowDef.getWorkflowProcess();

            if (subWorkflowProcess == null)
            {
                WorkflowProcess parentWorkflowProcess = taskInstance.WorkflowProcess;
                throw new EngineException(taskInstance.ProcessInstanceId, parentWorkflowProcess,
                        taskInstance.TaskId,
                        "系统中没有Id为" + Subflow.WorkflowProcessId + "的流程定义");
            }

            IPersistenceService persistenceService = runtimeContext.PersistenceService;

            ((TaskInstance)taskInstance).State=TaskInstanceStateEnum.RUNNING;
            ((TaskInstance)taskInstance).StartedTime = runtimeContext.getCalendarService().getSysDate();
            persistenceService.saveOrUpdateTaskInstance(taskInstance);


            IProcessInstance subProcessInstance = currentSession.createProcessInstance(subWorkflowProcess.Name, taskInstance);

            //初始化流程变量,从父实例获得初始值
            Dictionary<String, Object> processVars = ((TaskInstance)taskInstance).AliveProcessInstance.ProcessInstanceVariables;
            List<DataField> datafields = subWorkflowProcess.DataFields;
            for (int i = 0; datafields != null && i < datafields.Count; i++)
            {
                DataField df = (DataField)datafields[i];
                if (df.DataType == DataTypeEnum.STRING)
                {
                    if (processVars[df.Name] != null && (processVars[df.Name] is String))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, processVars[df.Name]);
                    }
                    else if (df.InitialValue != null)
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, df.InitialValue);
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, "");
                    }
                }
                else if (df.DataType == DataTypeEnum.INTEGER)
                {
                    if (processVars[df.Name] != null && (processVars[df.Name] is Int32))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, processVars[df.Name]);
                    }
                    else if (df.InitialValue != null)
                    {
                        try
                        {
                            Int32 intValue = Int32.Parse(df.InitialValue);
                            subProcessInstance.setProcessInstanceVariable(df.Name, intValue);
                        }
                        catch// (Exception e)
                        {
                           
                        }
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, (Int32)0);
                    }
                }
                else if (df.DataType == DataTypeEnum.FLOAT)
                {
                    if (processVars[df.Name] != null && (processVars[df.Name] is float))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, processVars[df.Name]);
                    }
                    else if (df.InitialValue != null)
                    {
                        float floatValue = float.Parse(df.InitialValue);
                        subProcessInstance.setProcessInstanceVariable(df.Name, floatValue);
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, (float)0);
                    }
                }
                else if (df.DataType == DataTypeEnum.BOOLEAN)
                {
                    if (processVars[df.Name] != null && (processVars[df.Name] is Boolean))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, processVars[df.Name]);
                    }
                    else if (df.InitialValue != null)
                    {
                        Boolean booleanValue = Boolean.Parse(df.InitialValue);
                        subProcessInstance.setProcessInstanceVariable(df.Name, booleanValue);
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.Name, false);
                    }
                }
                else if (df.DataType == DataTypeEnum.DATETIME)
                {
                    //TODO 需要完善一下
                }
            }

            runtimeContext.PersistenceService.saveOrUpdateProcessInstance(subProcessInstance);
            subProcessInstance.run();
        }
    }
}
