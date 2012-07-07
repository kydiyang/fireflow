using System;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.Collections.Generic;
using FireWorkflow.Net.Engine.Impl;

namespace FireWorkflow.Net.Designer.View.Web
{
    // 注意: 如果更改此处的接口名称 "IDesignerService"，也必须更新 Web.config 中对 "IDesignerService" 的引用。
    [ServiceContract]
    public interface IDesignerService
    {
        [OperationContract]
        String GetWorkflowProcessXml(String id);
        [OperationContract]
        String GetWorkflowProcessXmlProcessIdOrVersion(String processID, int version);

        [OperationContract]
        List<ProcessInstanceTrace> GetProcessInstanceTraceXml(String processInstanceId);
        
    }
}
