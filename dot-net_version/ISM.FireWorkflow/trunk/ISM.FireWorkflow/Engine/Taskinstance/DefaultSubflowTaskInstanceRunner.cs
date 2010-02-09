using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Engine.Definition;
using ISM.FireWorkflow.Engine.Impl;
using ISM.FireWorkflow.Engine.Persistence;
using ISM.FireWorkflow.Kernel;
using ISM.FireWorkflow.Model;
using ISM.FireWorkflow.Model.Resource;


namespace ISM.FireWorkflow.Engine.Taskinstance
{
    public class DefaultSubflowTaskInstanceRunner : ITaskInstanceRunner
    {

        public void run(IWorkflowSession currentSession, RuntimeContext runtimeContext, IProcessInstance processInstance,
                ITaskInstance taskInstance)// throws EngineException, KernelException 
        {
            if (!Task.SUBFLOW.Equals(taskInstance.getTaskType()))
            {
                throw new EngineException(processInstance,
                        taskInstance.getActivity(),
                        "DefaultSubflowTaskInstanceRunner：TaskInstance的任务类型错误，只能为SUBFLOW类型");
            }
            Task task = taskInstance.getTask();
            SubWorkflowProcess Subflow = ((SubflowTask)task).getSubWorkflowProcess();

            WorkflowDefinition subWorkflowDef = runtimeContext.getDefinitionService().getTheLatestVersionOfWorkflowDefinition(Subflow.getWorkflowProcessId());
            if (subWorkflowDef == null)
            {
                WorkflowProcess parentWorkflowProcess = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), parentWorkflowProcess,
                        taskInstance.getTaskId(),
                        "系统中没有Id为" + Subflow.getWorkflowProcessId() + "的流程定义");
            }
            WorkflowProcess subWorkflowProcess = subWorkflowDef.getWorkflowProcess();

            if (subWorkflowProcess == null)
            {
                WorkflowProcess parentWorkflowProcess = taskInstance.getWorkflowProcess();
                throw new EngineException(taskInstance.getProcessInstanceId(), parentWorkflowProcess,
                        taskInstance.getTaskId(),
                        "系统中没有Id为" + Subflow.getWorkflowProcessId() + "的流程定义");
            }

            IPersistenceService persistenceService = runtimeContext.getPersistenceService();

            ((TaskInstance)taskInstance).setState(ITaskInstance.RUNNING);
            ((TaskInstance)taskInstance).setStartedTime(runtimeContext.getCalendarService().getSysDate());
            persistenceService.saveOrUpdateTaskInstance(taskInstance);


            IProcessInstance subProcessInstance = currentSession.createProcessInstance(subWorkflowProcess.getName(), taskInstance);

            //初始化流程变量,从父实例获得初始值
            Dictionary<String, Object> processVars = ((TaskInstance)taskInstance).getAliveProcessInstance().getProcessInstanceVariables();
            List<DataField> datafields = subWorkflowProcess.getDataFields();
            for (int i = 0; datafields != null && i < datafields.Count; i++)
            {
                DataField df = (DataField)datafields[i];
                if (df.getDataType().Equals(DataField.STRING))
                {
                    if (processVars[df.getName()] != null && (processVars[df.getName()] is String))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), processVars[df.getName()]);
                    }
                    else if (df.getInitialValue() != null)
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), df.getInitialValue());
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), "");
                    }
                }
                else if (df.getDataType().Equals(DataField.INTEGER))
                {
                    if (processVars[df.getName()] != null && (processVars[df.getName()] is Int32))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), processVars[df.getName()]);
                    }
                    else if (df.getInitialValue() != null)
                    {
                        try
                        {
                            Int32 intValue = Int32.Parse(df.getInitialValue());
                            subProcessInstance.setProcessInstanceVariable(df.getName(), intValue);
                        }
                        catch// (Exception e)
                        {
                           
                        }
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), (Int32)0);
                    }
                }
                else if (df.getDataType().Equals(DataField.FLOAT))
                {
                    if (processVars[df.getName()] != null && (processVars[df.getName()] is float))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), processVars[df.getName()]);
                    }
                    else if (df.getInitialValue() != null)
                    {
                        float floatValue = float.Parse(df.getInitialValue());
                        subProcessInstance.setProcessInstanceVariable(df.getName(), floatValue);
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), (float)0);
                    }
                }
                else if (df.getDataType().Equals(DataField.BOOLEAN))
                {
                    if (processVars[df.getName()] != null && (processVars[df.getName()] is Boolean))
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), processVars[df.getName()]);
                    }
                    else if (df.getInitialValue() != null)
                    {
                        Boolean booleanValue = Boolean.Parse(df.getInitialValue());
                        subProcessInstance.setProcessInstanceVariable(df.getName(), booleanValue);
                    }
                    else
                    {
                        subProcessInstance.setProcessInstanceVariable(df.getName(), false);
                    }
                }
                else if (df.getDataType().Equals(DataField.DATETIME))
                {
                    //TODO 需要完善一下
                }
            }

            runtimeContext.getPersistenceService().saveOrUpdateProcessInstance(subProcessInstance);
            subProcessInstance.run();
        }
    }
}
