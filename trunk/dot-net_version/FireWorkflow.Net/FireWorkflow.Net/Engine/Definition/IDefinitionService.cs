using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Definition
{

    /// <summary>流程定义服务。</summary>
    public interface IDefinitionService : IRuntimeContextAware
    {
        //	public WorkflowProcess getWorkflowProcessByName(String name);
        //	public void setDefinitionFiles(List<String> definitionFileNames)throws IOException,FPDLParserException;
        //	public List<WorkflowProcess> getAllWorkflowProcesses();
        //        public WorkflowProcess getWorkflowProcessById(String id);

        /// <summary>返回所有流程的最新版本</summary>
        /// <returns></returns>
         List<WorkflowDefinition> getAllLatestVersionsOfWorkflowDefinition();


        /// <summary>根据流程Id和版本号查找流程定义</summary>
        WorkflowDefinition getWorkflowDefinitionByProcessIdAndVersionNumber(String processId, Int32? version);

        /// <summary>通过流程Id查找其最新版本的流程定义</summary>
        WorkflowDefinition getTheLatestVersionOfWorkflowDefinition(String processId);
    }
}
