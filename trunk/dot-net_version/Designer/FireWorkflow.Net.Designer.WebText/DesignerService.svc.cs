using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using System.IO;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Definition;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Io;

namespace FireWorkflow.Net.Designer.WebText
{
    // 注意: 使用“重构”菜单上的“重命名”命令，可以同时更改代码、svc 和配置文件中的类名“DesignerService”。
    public class DesignerService : IDesignerService
    {
        /// <summary>
        /// 根据数据存储ID获取流程Xml字符串
        /// </summary>
        /// <param name="id">数据存储ID</param>
        public String GetWorkflowProcessXml(String id)
        {
            WorkflowDefinition wd = RuntimeContextFactory.getRuntimeContext().PersistenceService.FindWorkflowDefinitionById(id);
            if (wd != null) return wd.ProcessContent;
            else return "";
        }

        /// <summary>
        /// 根据流程ID获取流程Xml字符串
        /// </summary>
        /// <param name="processID">流程ID</param>
        /// <param name="version">流程版本</param>
        public String GetWorkflowProcessXmlProcessIdOrVersion(String processID, int version)
        {
            if (version <= 0)
            {
                WorkflowDefinition wd = RuntimeContextFactory.getRuntimeContext().PersistenceService.FindTheLatestVersionOfWorkflowDefinitionByProcessId(processID);
                if (wd != null) return wd.ProcessContent;
            }
            else
            {
                WorkflowDefinition wd = RuntimeContextFactory.getRuntimeContext().PersistenceService.FindWorkflowDefinitionByProcessIdAndVersionNumber(processID, version);
                if (wd != null) return wd.ProcessContent;
            }
            return "";
        }

        /// <summary>
        /// 获取流程步骤列表
        /// </summary>
        /// <param name="processInstanceId">流程实例ID.</param>
        public List<ProcessInstanceTrace> GetProcessInstanceTraceXml(String processInstanceId)
        {
            List<ProcessInstanceTrace> pit = RuntimeContextFactory.getRuntimeContext().PersistenceService.FindProcessInstanceTraces(processInstanceId);
            return pit;
        }

        /// <summary>返回所有流程的最新版本</summary>
        public List<WorkflowDefinition> GetAllLatestVersionsOfWorkflowDefinition()
        {
            return RuntimeContextFactory.getRuntimeContext().DefinitionService.GetAllLatestVersionsOfWorkflowDefinition();
        }

        /// <summary>
        /// 保存流程定义，如果同一个ProcessId的流程定义已经存在，则版本号自动加1。
        /// </summary>
        /// <param name="workflowProcessXml">保存的WorkflowProcess XML 文本.</param>
        /// <param name="version">保存的版本，当版本为小于等于0时，添加新流程，如存在相同ProcessId则版本号加1</param>
        /// <param name="isState">是否发布</param>
        public bool SaveOrUpdateWorkflowProcess(string workflowProcessXml, int version, bool isState)
        {
            Dom4JFPDLParser parser = new Dom4JFPDLParser();
            MemoryStream msin = new MemoryStream(Encoding.UTF8.GetBytes(workflowProcessXml));
            WorkflowProcess workflowProcess = parser.parse(msin);
            if (workflowProcess == null) return false;

            WorkflowDefinition wd = RuntimeContextFactory.getRuntimeContext().PersistenceService.FindWorkflowDefinitionByProcessIdAndVersionNumber(workflowProcess.Id, version);
            if (wd == null)
            {
                wd = new WorkflowDefinition();
                wd.PublishTime = DateTime.Now;
                wd.PublishUser = "admin";
            }

            wd.UploadTime = DateTime.Now;
            wd.UploadUser = "admin";
            wd.State = isState;


            wd.Name = workflowProcess.Name;
            wd.DisplayName = workflowProcess.DisplayName;
            wd.Description = workflowProcess.Description;
            wd.setWorkflowProcess(workflowProcess);

            if (RuntimeContextFactory.getRuntimeContext().PersistenceService.SaveOrUpdateWorkflowDefinition(wd))
            {
                return true;
            }

            return false;
        }
    }
}
