using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Taskinstance;
using FireWorkflow.Net.Engine.Definition;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Kernel;

namespace FireWorkflow.Net.Engine.Impl
{
    #region WorkflowSessionProcessInstance class
    public class WorkflowSessionIProcessInstance : IWorkflowSessionCallback
    {
        public String wfprocessId;
        public String creatorId;
        public String parentProcessInstanceId;
        public String parentTaskInstanceId;

        public WorkflowSessionIProcessInstance(String workflowProcessId, String creatorId, String parentProcessInstanceId, String parentTaskInstanceId)
        {
            this.wfprocessId = workflowProcessId;
            this.creatorId = creatorId;
            this.parentProcessInstanceId = parentProcessInstanceId;
            this.parentTaskInstanceId = parentTaskInstanceId;
        }
        public object doInWorkflowSession(RuntimeContext ctx)
        {

            WorkflowDefinition workflowDef = ctx.getDefinitionService()
                    .getTheLatestVersionOfWorkflowDefinition(wfprocessId);
            WorkflowProcess wfProcess = null;

            wfProcess = workflowDef.getWorkflowProcess();

            if (wfProcess == null)
            {
                throw new Exception("Workflow process NOT found,id=[" + wfprocessId + "]");
            }

            ProcessInstance processInstance = new ProcessInstance();
            processInstance.setCreatorId(creatorId);
            processInstance.setProcessId(wfProcess.Id);
            processInstance.setVersion(workflowDef.getVersion());
            processInstance.setDisplayName(wfProcess.DisplayName);
            processInstance.setName(wfProcess.Name);
            processInstance.setState(IProcessInstance.INITIALIZED);
            processInstance.setCreatedTime(ctx.getCalendarService().getSysDate());
            processInstance.setParentProcessInstanceId(parentProcessInstanceId);
            processInstance.setParentTaskInstanceId(parentTaskInstanceId);

            ctx.getPersistenceService().saveOrUpdateProcessInstance(
                    processInstance);

            // 初始化流程变量
            List<DataField> datafields = wfProcess.DataFields;
            for (int i = 0; datafields != null && i < datafields.Count; i++)
            {
                DataField df = (DataField)datafields[i];
                if (df.DataType == DataTypeEnum.STRING)
                {
                    if (df.InitialValue != null)
                    {
                        processInstance.setProcessInstanceVariable(df.Name, df.InitialValue);
                    }
                    else
                    {
                        processInstance.setProcessInstanceVariable(df.Name, "");
                    }
                }
                else if (df.DataType == DataTypeEnum.INTEGER)
                {
                    if (df.InitialValue != null)
                    {
                        try
                        {
                            Int32 intValue = Int32.Parse(df.InitialValue);
                            processInstance.setProcessInstanceVariable(df.Name, intValue);
                        }
                        catch (Exception )
                        {
                        }
                    }
                    else
                    {
                        processInstance.setProcessInstanceVariable(df.Name, (Int32)0);
                    }
                }
                else if (df.DataType == DataTypeEnum.LONG)
                {
                    if (df.InitialValue != null)
                    {
                        try
                        {
                            long longValue = long.Parse(df.InitialValue);
                            processInstance.setProcessInstanceVariable(df.Name, longValue);
                        }
                        catch (Exception )
                        {
                        }
                    }
                    else
                    {
                        processInstance.setProcessInstanceVariable(df.Name, (long)0);
                    }
                }
                else if (df.DataType == DataTypeEnum.FLOAT)
                {
                    if (df.InitialValue != null)
                    {
                        float floatValue = float.Parse(df.InitialValue);
                        processInstance.setProcessInstanceVariable(df.Name, floatValue);
                    }
                    else
                    {
                        processInstance.setProcessInstanceVariable(df.Name, (float)0);
                    }
                }
                else if (df.DataType == DataTypeEnum.DOUBLE)
                {
                    if (df.InitialValue != null)
                    {
                        Double doubleValue = Double.Parse(df.InitialValue);
                        processInstance.setProcessInstanceVariable(df
                                .Name, doubleValue);
                    }
                    else
                    {
                        processInstance.setProcessInstanceVariable(df.Name, (Double)0);
                    }
                }
                else if (df.DataType == DataTypeEnum.BOOLEAN)
                {
                    if (df.InitialValue != null)
                    {
                        Boolean booleanValue = Boolean.Parse(df.InitialValue);
                        processInstance.setProcessInstanceVariable(df.Name, booleanValue);
                    }
                    else
                    {
                        processInstance.setProcessInstanceVariable(df.Name, false);
                    }
                }
                else if (df.DataType == DataTypeEnum.DATETIME)
                {
                    // TODO 需要完善一下
                    if (df.InitialValue != null
                            && df.DataPattern != null)
                    {
                        try
                        {
                            //SimpleDateFormat dFormat = new SimpleDateFormat(df.DataPattern);
                            DateTime dateTmp = DateTime.Parse(df.InitialValue);
                            processInstance.setProcessInstanceVariable(df.Name, dateTmp);
                        }
                        catch (Exception )
                        {
                            processInstance.setProcessInstanceVariable(df.Name, null);
                            //e.printStackTrace();
                        }
                    }
                    else
                    {
                        processInstance.setProcessInstanceVariable(df.Name, null);
                    }
                }
            }

            ctx.getPersistenceService().saveOrUpdateProcessInstance(
                    processInstance);

            return processInstance;
        }
    }
    #endregion

    #region WorkflowSessionIWorkItem
    public class WorkflowSessionIWorkItem : IWorkflowSessionCallback
    {
        String workItemId;
        public WorkflowSessionIWorkItem(String workItemId)
        {
            this.workItemId = workItemId;
        }

        public Object doInWorkflowSession(RuntimeContext ctx)
        {
            IPersistenceService persistenceService = ctx.getPersistenceService();

            return persistenceService.findWorkItemById(workItemId);
        }
    }
    #endregion

    #region WorkflowSessionITaskInstance
    public class WorkflowSessionITaskInstance : IWorkflowSessionCallback
    {
        String taskInstanceId;
        public WorkflowSessionITaskInstance(String taskInstanceId)
        {
            this.taskInstanceId = taskInstanceId;
        }

        public Object doInWorkflowSession(RuntimeContext ctx)
        {
            IPersistenceService persistenceService = ctx.getPersistenceService();
            return persistenceService.findTaskInstanceById(taskInstanceId);
        }
    }
    #endregion

    #region WorkflowSessionIWorkItems
    public class WorkflowSessionIWorkItems : IWorkflowSessionCallback
    {
        String actorId;
        String processId;
        String taskId;
        char t;
        public WorkflowSessionIWorkItems(String actorId)
        {
            t='1';
            this.actorId = actorId;
        }

        public WorkflowSessionIWorkItems(String actorId, String processId)
        {
            t = '2';
            this.actorId = actorId;
            this.processId = processId;
        }
        public WorkflowSessionIWorkItems(String actorId, String processId, String taskId)
        {
            t = '3';
            this.actorId = actorId;
            this.processId = processId;
            this.taskId = taskId;
        }

        public Object doInWorkflowSession(RuntimeContext ctx)
        {
            switch (t)
            {
                case '2': return ctx.getPersistenceService().findTodoWorkItems(actorId, processId);
                case '3': return ctx.getPersistenceService().findTodoWorkItems(actorId, processId, taskId);
                default: return ctx.getPersistenceService().findTodoWorkItems(actorId);
            }
        }
    }

    #endregion

    #region WorkflowSessionIProcessInstance
    public class WorkflowSessionIProcessInstance1 : IWorkflowSessionCallback
    {
        String id;
        public WorkflowSessionIProcessInstance1(String id)
        {
            this.id = id;
        }

        public Object doInWorkflowSession(RuntimeContext ctx)
        {
            IPersistenceService persistenceService = ctx.getPersistenceService();
            return persistenceService.findProcessInstanceById(id);
        }
    }
    #endregion


    #region WorkflowSessionIProcessInstances
    public class WorkflowSessionIProcessInstances : IWorkflowSessionCallback
    {
        String processId;
        Int32 version;
        char t;
        public WorkflowSessionIProcessInstances(String processId)
        {
            t = '1';
            this.processId = processId;
        }
        public WorkflowSessionIProcessInstances(String processId,Int32 version)
        {
            t = '2';
            this.processId = processId;
            this.version=version; 
        }

        public Object doInWorkflowSession(RuntimeContext ctx)
        {
            switch (t)
            {
                case '2': return ctx.getPersistenceService().findProcessInstancesByProcessIdAndVersion(processId, version);
                //case '3': return ctx.getPersistenceService().findProcessInstancesByProcessId(actorId, processId, taskId);
                default: return ctx.getPersistenceService().findProcessInstancesByProcessId(processId);
            }

        }
    }
    #endregion

    #region WorkflowSessionITaskInstances
    public class WorkflowSessionITaskInstances : IWorkflowSessionCallback
    {
        String processInstanceId;
        String activityId;
        public WorkflowSessionITaskInstances(String processInstanceId, String activityId)
        {
            this.processInstanceId = processInstanceId;
            this.activityId = activityId;
        }

        public Object doInWorkflowSession(RuntimeContext ctx)
        {
            IPersistenceService persistenceService = ctx.getPersistenceService();
            return persistenceService.findTaskInstancesForProcessInstance(processInstanceId, activityId);
        }
    }
    #endregion


}
