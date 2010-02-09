using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Engine;

namespace FireWorkflow.Net.Engine.Definition
{
    /// <summary>
    /// 从关系数据库表T_FF_DF_WORKFLOWDEF中读取流程定义文件，该表保存了同一个流程的各个版本。
    /// 该类用于系统的实施阶段。
    /// </summary>
    public class DefinitionService4DBMS : IDefinitionService
    {
        protected RuntimeContext rtCtx = null;


        public List<WorkflowDefinition> getAllLatestVersionsOfWorkflowDefinition()
        {
            return rtCtx.getPersistenceService().findAllTheLatestVersionsOfWorkflowDefinition();

        }

        public WorkflowDefinition getWorkflowDefinitionByProcessIdAndVersionNumber(String id, Int32 version)
        {
            return rtCtx.getPersistenceService().findWorkflowDefinitionByProcessIdAndVersionNumber(id, version);
        }

        public WorkflowDefinition getTheLatestVersionOfWorkflowDefinition(String processId)
        {
            return rtCtx.getPersistenceService().findTheLatestVersionOfWorkflowDefinitionByProcessId(processId);
        }

        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.rtCtx = ctx;
        }
        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
        }

        #region IDefinitionService 成员


        public WorkflowDefinition getWorkflowDefinitionByProcessIdAndVersionNumber(string processId, int? version)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}
