using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Definition;

namespace FireWorkflow.Net.Designer.WebText
{
    // 注意: 使用“重构”菜单上的“重命名”命令，可以同时更改代码和配置文件中的接口名“IDesignerService”。
    [ServiceContract]
    public interface IDesignerService
    {
        /// <summary>
        /// 根据数据存储ID获取流程Xml字符串
        /// </summary>
        /// <param name="id">数据存储ID</param>
        /// <returns></returns>
        [OperationContract]
        String GetWorkflowProcessXml(String id);

        /// <summary>
        /// 根据流程ID获取流程Xml字符串
        /// </summary>
        /// <param name="processID">流程ID</param>
        /// <param name="version">流程版本</param>
        [OperationContract]
        String GetWorkflowProcessXmlProcessIdOrVersion(String processID, int version);

        /// <summary>获取流程步骤列表</summary>
        /// <param name="processInstanceId">流程实例ID.</param>
        [OperationContract]
        List<ProcessInstanceTrace> GetProcessInstanceTraceXml(String processInstanceId);

        /// <summary>返回所有流程的最新版本</summary>
        [OperationContract]
        List<WorkflowDefinition> GetAllLatestVersionsOfWorkflowDefinition();


        /// <summary>
        /// 保存流程定义，如果同一个ProcessId的流程定义已经存在，则版本号自动加1。
        /// </summary>
        /// <param name="workflowProcessXml">保存的WorkflowProcess XML 文本.</param>
        /// <param name="version">保存的版本，当版本为小于等于0时，添加型流程，如存在着版本号加1</param>
        /// <param name="isState">是否发布</param>
        [OperationContract]
        bool SaveOrUpdateWorkflowProcess(String workflowProcessXml, int version, bool isState);
    }
}
