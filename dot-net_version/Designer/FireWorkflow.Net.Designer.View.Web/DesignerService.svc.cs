using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Definition;

namespace FireWorkflow.Net.Designer.View.Web
{
    // 注意: 如果更改此处的类名 "DesignerService"，也必须更新 Web.config 中对 "DesignerService" 的引用。
    public class DesignerService : IDesignerService
    {
        public String GetWorkflowProcessXml(String id)
        {
            //return TestProcessContentXml;
            WorkflowDefinition wd = RuntimeContextFactory.getRuntimeContext().PersistenceService.FindWorkflowDefinitionById(id);
            if (wd != null) return wd.ProcessContent;
            else return "";
        }

        public String GetWorkflowProcessXmlProcessIdOrVersion(String processID, int version)
        {
           // return TestProcessContentXml;

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
        public List<ProcessInstanceTrace> GetProcessInstanceTraceXml(String processInstanceId)
        {
            List<ProcessInstanceTrace> pit = RuntimeContextFactory.getRuntimeContext().PersistenceService.FindProcessInstanceTraces(processInstanceId);
            return pit;
        }


       
        #region 临时测试数据
        public String TestProcessContentXml
        {
            get
            {
                return
                    @"<?xml version=""1.0"" encoding=""UTF-8"" standalone=""no""?>
<!DOCTYPE fpdl:WorkflowProcess PUBLIC ""-//Nieyun Chen//ProcessDefinition//CN"" ""FireFlow_Process_Definition_Language.dtd"">
<fpdl:WorkflowProcess DisplayName=""某银行贷款流程"" Id=""LoanProcess"" Name=""LoanProcess"" ResourceFile="""" ResourceManager="""" TaskInstanceCreator=""Web.Example.loan_process.workflowextension, Web"" xmlns:fpdl=""http://www.fireflow.org/Fireflow_Process_Definition_Language"">
  <fpdl:Description/>
  <fpdl:DataFields>
    <fpdl:DataField DataType=""BOOLEAN"" DisplayName=""风险标志"" Id=""LoanProcess.RiskFlag"" InitialValue=""true"" Name=""RiskFlag"">
      <fpdl:Description>1标示有风险，0表示无风险</fpdl:Description>
    </fpdl:DataField>
    <fpdl:DataField DataType=""BOOLEAN"" DisplayName=""审批标志"" Id=""LoanProcess.Decision"" InitialValue=""false"" Name=""Decision"">
      <fpdl:Description>1表示审批通过，0表示审批不通过</fpdl:Description>
    </fpdl:DataField>
  </fpdl:DataFields>
  <fpdl:StartNode DisplayName="""" Id=""LoanProcess.START_NODE"" Name=""START_NODE"">
    <fpdl:ExtendedAttributes>
      <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""20""/>
      <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""20""/>
      <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""6""/>
      <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""123""/>
    </fpdl:ExtendedAttributes>
  </fpdl:StartNode>
  <fpdl:Tasks>
    <fpdl:Task CompletionStrategy=""ANY"" DefaultView=""EDITFORM"" DisplayName=""录入申请资料"" Id=""LoanProcess.Submit_appliation_info_task"" LoopStrategy=""REDO"" Name=""Submit_appliation_info_task"" Priority=""1"" Type=""FORM"">
      <fpdl:Description/>
      <fpdl:Performer DisplayName=""信贷员"" Name=""Loanteller"">
        <fpdl:Description/>
        <fpdl:AssignmentHandler>org.fireflow.example.workflowextension.RoleBasedAssignmentHandler</fpdl:AssignmentHandler>
      </fpdl:Performer>
      <fpdl:EditForm DisplayName=""贷款申请资料录入页面"" Name=""SubmitApplicationInfo"">
        <fpdl:Description/>
        <fpdl:Uri>org/fireflow/example/loan_process/SubmitApplicationInfo.xhtml</fpdl:Uri>
      </fpdl:EditForm>
    </fpdl:Task>
    <fpdl:Task CompletionStrategy=""ANY"" DefaultView=""EDITFORM"" DisplayName=""核查信用和还款能力"" Id=""LoanProcess.Evaluate_risk_task"" LoopStrategy=""REDO"" Name=""Evaluate_risk_task"" Priority=""1"" Type=""FORM"">
      <fpdl:Description/>
      <fpdl:Performer DisplayName=""风险核查员"" Name=""RiskEvaluator"">
        <fpdl:Description/>
        <fpdl:AssignmentHandler>org.fireflow.example.workflowextension.RoleBasedAssignmentHandler</fpdl:AssignmentHandler>
      </fpdl:Performer>
      <fpdl:EditForm DisplayName=""风险核查信息界面"" Name=""RiskEvaluateInfo"">
        <fpdl:Description/>
        <fpdl:Uri>org/fireflow/example/loan_process/RiskEvaluateInfo.xhtml</fpdl:Uri>
      </fpdl:EditForm>
    </fpdl:Task>
    <fpdl:Task CompletionStrategy=""ALL"" DefaultView=""EDITFORM"" DisplayName=""审批"" Id=""LoanProcess.Approve_application_task"" LoopStrategy=""REDO"" Name=""Approve_application_task"" Priority=""1"" TaskInstanceCompletionEvaluator=""org.fireflow.example.loan_process.workflowextension.ApproveApplicationTaskCompletionEvaluator"" Type=""FORM"">
      <fpdl:Description/>
      <fpdl:Performer DisplayName=""审批人"" Name=""Approver"">
        <fpdl:Description/>
        <fpdl:AssignmentHandler>org.fireflow.example.workflowextension.RoleBasedAssignmentHandler</fpdl:AssignmentHandler>
      </fpdl:Performer>
      <fpdl:EditForm DisplayName=""审批界面"" Name=""ApproveInfo"">
        <fpdl:Description/>
        <fpdl:Uri>org/fireflow/example/loan_process/ApproveInfo.xhtml</fpdl:Uri>
      </fpdl:EditForm>
      <fpdl:EventListeners>
        <fpdl:EventListener ClassName=""org.fireflow.example.loan_process.workflowextension.ApproveTaskInstanceEventListener""/>
      </fpdl:EventListeners>
    </fpdl:Task>
    <fpdl:Task CompletionStrategy=""ANY"" DefaultView=""EDITFORM"" DisplayName=""放款"" Id=""LoanProcess.Lend_money_task"" LoopStrategy=""REDO"" Name=""Lend_money_task"" Priority=""1"" Type=""FORM"">
      <fpdl:Description/>
      <fpdl:Performer DisplayName=""放款操作员"" Name=""LendMoneyOfficer"">
        <fpdl:Description/>
        <fpdl:AssignmentHandler>org.fireflow.example.workflowextension.RoleBasedAssignmentHandler</fpdl:AssignmentHandler>
      </fpdl:Performer>
      <fpdl:EditForm DisplayName=""放宽信息录入界面"" Name=""LendMoneyInfo"">
        <fpdl:Description/>
        <fpdl:Uri>org/fireflow/example/loan_process/LendMoneyInfo.xhtml</fpdl:Uri>
      </fpdl:EditForm>
    </fpdl:Task>
    <fpdl:Task CompletionStrategy=""ANY"" DefaultView=""EDITFORM"" DisplayName=""拒绝贷款申请"" Id=""LoanProcess.Reject_task"" LoopStrategy=""REDO"" Name=""Reject_task"" Priority=""1"" Type=""FORM"">
      <fpdl:Description/>
      <fpdl:Performer DisplayName=""信贷员"" Name=""Loanteller"">
        <fpdl:Description/>
        <fpdl:AssignmentHandler>org.fireflow.example.workflowextension.ProcessInstanceCreator</fpdl:AssignmentHandler>
      </fpdl:Performer>
      <fpdl:EditForm DisplayName=""拒绝贷款信息录入页面"" Name=""RejectInfo"">
        <fpdl:Description/>
        <fpdl:Uri>org/fireflow/example/loan_process/RejectInfo.xhtml</fpdl:Uri>
      </fpdl:EditForm>
    </fpdl:Task>
  </fpdl:Tasks>
  <fpdl:Activities>
    <fpdl:Activity CompletionStrategy=""ALL"" DisplayName=""提交申请"" Id=""LoanProcess.Submit_application_activity"" Name=""Submit_application_activity"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""60""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""100""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""54""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""107""/>
      </fpdl:ExtendedAttributes>
      <fpdl:Tasks/>
      <fpdl:TaskRefs>
        <fpdl:TaskRef Reference=""LoanProcess.Submit_appliation_info_task""/>
      </fpdl:TaskRefs>
    </fpdl:Activity>
    <fpdl:Activity CompletionStrategy=""ALL"" DisplayName=""风险核查"" Id=""LoanProcess.Evaluate_risk_activity"" Name=""Evaluate_risk_activity"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""60""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""166""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""231""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""107""/>
      </fpdl:ExtendedAttributes>
      <fpdl:Tasks/>
      <fpdl:TaskRefs>
        <fpdl:TaskRef Reference=""LoanProcess.Evaluate_risk_task""/>
      </fpdl:TaskRefs>
    </fpdl:Activity>
    <fpdl:Activity CompletionStrategy=""ALL"" DisplayName=""审批"" Id=""LoanProcess.Approve_application_activity"" Name=""Approve_application_activity"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""60""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""100""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""551""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""107""/>
      </fpdl:ExtendedAttributes>
      <fpdl:Tasks/>
      <fpdl:TaskRefs>
        <fpdl:TaskRef Reference=""LoanProcess.Approve_application_task""/>
      </fpdl:TaskRefs>
    </fpdl:Activity>
    <fpdl:Activity CompletionStrategy=""ALL"" DisplayName=""放款"" Id=""LoanProcess.Lend_money_acitivty"" Name=""Lend_money_acitivty"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""60""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""100""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""804""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""109""/>
      </fpdl:ExtendedAttributes>
      <fpdl:Tasks/>
      <fpdl:TaskRefs>
        <fpdl:TaskRef Reference=""LoanProcess.Lend_money_task""/>
      </fpdl:TaskRefs>
    </fpdl:Activity>
    <fpdl:Activity CompletionStrategy=""ALL"" DisplayName=""拒绝"" Id=""LoanProcess.Reject_activity_1"" Name=""Reject_activity_1"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""54""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""129""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""218""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""232""/>
      </fpdl:ExtendedAttributes>
      <fpdl:Tasks/>
      <fpdl:TaskRefs>
        <fpdl:TaskRef Reference=""LoanProcess.Reject_task""/>
      </fpdl:TaskRefs>
    </fpdl:Activity>
    <fpdl:Activity CompletionStrategy=""ALL"" DisplayName=""拒绝"" Id=""LoanProcess.Reject_activity_2"" Name=""Reject_activity_2"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""59""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""115""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""758""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""18""/>
      </fpdl:ExtendedAttributes>
      <fpdl:Tasks/>
      <fpdl:TaskRefs>
        <fpdl:TaskRef Reference=""LoanProcess.Reject_task""/>
      </fpdl:TaskRefs>
    </fpdl:Activity>
  </fpdl:Activities>
  <fpdl:Synchronizers>
    <fpdl:Synchronizer DisplayName="""" Id=""LoanProcess.Synchronizer1"" Name=""Synchronizer1"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""185""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""128""/>
      </fpdl:ExtendedAttributes>
    </fpdl:Synchronizer>
    <fpdl:Synchronizer DisplayName="""" Id=""LoanProcess.Synchronizer2"" Name=""Synchronizer2"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""414""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""127""/>
      </fpdl:ExtendedAttributes>
    </fpdl:Synchronizer>
    <fpdl:Synchronizer DisplayName="""" Id=""LoanProcess.Synchronizer3"" Name=""Synchronizer3"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""668""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""127""/>
      </fpdl:ExtendedAttributes>
    </fpdl:Synchronizer>
  </fpdl:Synchronizers>
  <fpdl:EndNodes>
    <fpdl:EndNode DisplayName="""" Id=""LoanProcess.EndNode1"" Name=""EndNode1"">
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""20""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""944""/>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""129""/>
      </fpdl:ExtendedAttributes>
    </fpdl:EndNode>
  </fpdl:EndNodes>
  <fpdl:Transitions>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.START_NODE"" Id=""LoanProcess.Transition1"" Name=""Transition1"" To=""LoanProcess.Submit_application_activity"">
      <fpdl:Condition/>
    </fpdl:Transition>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.Submit_application_activity"" Id=""LoanProcess.Transition2"" Name=""Transition2"" To=""LoanProcess.Synchronizer1"">
      <fpdl:Condition/>
    </fpdl:Transition>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.Synchronizer1"" Id=""LoanProcess.Transition3"" Name=""Transition3"" To=""LoanProcess.Evaluate_risk_activity"">
      <fpdl:Condition/>
    </fpdl:Transition>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.Evaluate_risk_activity"" Id=""LoanProcess.Transition4"" Name=""Transition4"" To=""LoanProcess.Synchronizer2"">
      <fpdl:Condition/>
    </fpdl:Transition>
    <fpdl:Transition DisplayName=""RiskFlag==false"" From=""LoanProcess.Synchronizer2"" Id=""LoanProcess.Transition5"" Name=""Transition5"" To=""LoanProcess.Approve_application_activity"">
      <fpdl:Condition>RiskFlag==false</fpdl:Condition>
    </fpdl:Transition>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.Approve_application_activity"" Id=""LoanProcess.Transition6"" Name=""Transition6"" To=""LoanProcess.Synchronizer3"">
      <fpdl:Condition/>
    </fpdl:Transition>
    <fpdl:Transition DisplayName=""Decision==true"" From=""LoanProcess.Synchronizer3"" Id=""LoanProcess.Transition7"" Name=""Transition7"" To=""LoanProcess.Lend_money_acitivty"">
      <fpdl:Condition>Decision==true</fpdl:Condition>
    </fpdl:Transition>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.Lend_money_acitivty"" Id=""LoanProcess.Transition8"" Name=""Transition8"" To=""LoanProcess.EndNode1"">
      <fpdl:Condition/>
    </fpdl:Transition>
    <fpdl:Transition DisplayName=""Decision==false"" From=""LoanProcess.Synchronizer3"" Id=""LoanProcess.Transition10"" Name=""Transition10"" To=""LoanProcess.Reject_activity_2"">
      <fpdl:Condition>Decision==false</fpdl:Condition>
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.edgePointList"" Value=""(696,48)""/>
      </fpdl:ExtendedAttributes>
    </fpdl:Transition>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.Reject_activity_2"" Id=""LoanProcess.Transition11"" Name=""Transition11"" To=""LoanProcess.EndNode1"">
      <fpdl:Condition/>
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.edgePointList"" Value=""(885,49)""/>
      </fpdl:ExtendedAttributes>
    </fpdl:Transition>
    <fpdl:Transition DisplayName=""RiskFlag==true"" From=""LoanProcess.Synchronizer2"" Id=""LoanProcess.Transition12"" Name=""Transition12"" To=""LoanProcess.Reject_activity_1"">
      <fpdl:Condition>RiskFlag==true</fpdl:Condition>
      <fpdl:ExtendedAttributes>
        <fpdl:ExtendedAttribute Name=""FIRE_FLOW.edgePointList"" Value=""(392,259)""/>
      </fpdl:ExtendedAttributes>
    </fpdl:Transition>
    <fpdl:Transition DisplayName="""" From=""LoanProcess.Reject_activity_1"" Id=""LoanProcess.Transition13"" Name=""Transition13"" To=""LoanProcess.Synchronizer1"">
      <fpdl:Condition/>
    </fpdl:Transition>
  </fpdl:Transitions>
  <fpdl:ExtendedAttributes>
    <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.height"" Value=""20""/>
    <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.width"" Value=""20""/>
    <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.x"" Value=""6""/>
    <fpdl:ExtendedAttribute Name=""FIRE_FLOW.bounds.y"" Value=""123""/>
  </fpdl:ExtendedAttributes>
</fpdl:WorkflowProcess>";
            }
        }
        #endregion
    }
}
