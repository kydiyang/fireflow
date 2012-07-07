using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.IO;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Windows.Browser;
using System.Collections.ObjectModel;
using System.ServiceModel.Channels;
using System.ServiceModel;
using FireWorkflow.Net.Designer.Model.Designer;
using FireWorkflow.Net.Designer.Model.Net;
using FireWorkflow.Net.Designer.Model.Io;
using FireWorkflow.Net.Designer.Model;
using FireWorkflow.Net.Designer.View.DesignerServiceReference;

namespace FireWorkflow.Net.Designer.View
{
    public partial class MainPage : UserControl, IMyMainPage
    {
        /// <summary>当前控件集合</summary>
        public Dictionary<string, IDesigner> UserControls { get; set; }//

        /// <summary>当前操作的WorkflowProcess业务流程</summary>
        public WorkflowProcess WorkflowProcessCurrent { get; set; }

        LoadWorkflow loadWorkflow;
        IDictionary<string, string> queryString;
        string address;

        public MainPage()
        {
            InitializeComponent();
            UserControls = new Dictionary<string, IDesigner>();
            queryString = HtmlPage.Document.QueryString;

            loadWorkflow = new LoadWorkflow(this, canvas, UserControls);

            string absolutePath = HtmlPage.Document.DocumentUri.AbsoluteUri;
            address = absolutePath.Substring(0, absolutePath.LastIndexOf('/')) + "/DesignerService.svc";


            GetXMLWorkflowProcessWebClient();
        }

        public bool SetUserControlsKey(string oldKey, string newKey)
        {
            throw new NotImplementedException();
        }

        #region WCF服务调用
        //加载数据
        private void GetXMLWorkflowProcessWebClient()
        {

            if (queryString.ContainsKey("WorkflowProcessId"))
            {
                GetWorkflowProcessXml(queryString["WorkflowProcessId"]);
            }
            else if (queryString.ContainsKey("ProcessId"))
            {
                GetWorkflowProcessXmlProcessIdOrVersion(queryString["ProcessId"], ((queryString.ContainsKey("Version")) ? int.Parse(queryString["Version"]) : 0));
            }
        }

        /// <summary>
        /// 根据数据存储ID获取流程Xml字符串
        /// </summary>
        /// <param name="id">数据存储ID</param>
        public void GetWorkflowProcessXml(String id)
        {
            DesignerServiceClient dsc = new DesignerServiceClient(new BasicHttpBinding(), new EndpointAddress(address));
            dsc.GetWorkflowProcessXmlCompleted += new EventHandler<GetWorkflowProcessXmlCompletedEventArgs>(dsc_GetWorkflowProcessXmlCompleted);
            dsc.GetWorkflowProcessXmlAsync(id);
        }
        void dsc_GetWorkflowProcessXmlCompleted(object sender, GetWorkflowProcessXmlCompletedEventArgs e)
        {
            if (String.IsNullOrEmpty(e.Result)) return;
            //流程图初始化
            MemoryStream msin = new MemoryStream(Encoding.UTF8.GetBytes(e.Result));
            Dom4JFPDLParser djp = new Dom4JFPDLParser();
            this.WorkflowProcessCurrent = djp.parse(msin);
            loadWorkflow.LoadWorkflowProcessToFrom(this.WorkflowProcessCurrent);

            //流程跟踪
            if (queryString.ContainsKey("ProcessInstanceId"))
            {
                GetProcessInstanceTraceXml(queryString["ProcessInstanceId"]);
            }

        }

        /// <summary>
        /// 根据流程ID获取流程Xml字符串
        /// </summary>
        /// <param name="processID">流程ID</param>
        /// <param name="version">流程版本</param>
        public void GetWorkflowProcessXmlProcessIdOrVersion(String processID, int version)
        {
            DesignerServiceClient dsc = new DesignerServiceClient(new BasicHttpBinding(), new EndpointAddress(address));
            dsc.GetWorkflowProcessXmlProcessIdOrVersionCompleted += new EventHandler<GetWorkflowProcessXmlProcessIdOrVersionCompletedEventArgs>(dsc_GetWorkflowProcessXmlProcessIdOrVersionCompleted);
            dsc.GetWorkflowProcessXmlProcessIdOrVersionAsync(processID, version);
        }
        void dsc_GetWorkflowProcessXmlProcessIdOrVersionCompleted(object sender, GetWorkflowProcessXmlProcessIdOrVersionCompletedEventArgs e)
        {
            if (String.IsNullOrEmpty(e.Result)) return;
            //流程图初始化
            MemoryStream msin = new MemoryStream(Encoding.UTF8.GetBytes(e.Result));
            Dom4JFPDLParser djp = new Dom4JFPDLParser();
            this.WorkflowProcessCurrent = djp.parse(msin);
            loadWorkflow.LoadWorkflowProcessToFrom(this.WorkflowProcessCurrent);

            //流程跟踪
            if (queryString.ContainsKey("ProcessInstanceId"))
            {
                GetProcessInstanceTraceXml(queryString["ProcessInstanceId"]);
            }
        }


        /// <summary>
        /// 获取流程步骤列表
        /// </summary>
        /// <param name="processInstanceId">流程实例ID.</param>
        public void GetProcessInstanceTraceXml(String processInstanceId)
        {
            DesignerServiceClient dsc = new DesignerServiceClient(new BasicHttpBinding(), new EndpointAddress(address));
            dsc.GetProcessInstanceTraceXmlCompleted += new EventHandler<GetProcessInstanceTraceXmlCompletedEventArgs>(dsc_GetProcessInstanceTraceXmlCompleted);
            dsc.GetProcessInstanceTraceXmlAsync(processInstanceId);
        }
        void dsc_GetProcessInstanceTraceXmlCompleted(object sender, GetProcessInstanceTraceXmlCompletedEventArgs e)
        {
            //流程跟踪
            ObservableCollection<ProcessInstanceTrace> pits = e.Result;

            foreach (ProcessInstanceTrace item in pits)
            {
                if (UserControls.ContainsKey(item.FromNodeId)) UserControls[item.FromNodeId].SetState(StateEnum.COMPLETED);
                if (UserControls.ContainsKey(item.EdgeId)) UserControls[item.EdgeId].SetState(StateEnum.COMPLETED);
                if (UserControls.ContainsKey(item.ToNodeId)) UserControls[item.ToNodeId].SetState(StateEnum.RUNNING);
            }

            for (int i = pits.Count - 1, j = 0; i >= 0 && j < 4; i--, j++)
            {
                if (UserControls.ContainsKey(pits[i].EdgeId)) UserControls[pits[i].EdgeId].SetState(StateEnum.RUNNING);
                if (UserControls.ContainsKey(pits[i].FromNodeId) && UserControls[pits[i].FromNodeId] is ActivityControl)
                {
                    break;
                }
            }
        }
        #endregion
        
         /// <summary>添加移动事件</summary>
        /// <param name="uc"></param>
        public void AddHandle(UserControl uc)
        {
        }
        





    }
}
