using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Text;
using System.IO;
using System.Diagnostics;
using System.Windows.Browser;
using System.Collections.ObjectModel;
using System.ServiceModel;
using System.ServiceModel.Channels;
using FireWorkflow.Net.Designer.Model.Designer;
using FireWorkflow.Net.Designer.Model;
using FireWorkflow.Net.Designer.Model.Net;
using FireWorkflow.Net.Designer.Model.Io;
using FireWorkflow.Net.Designer.Model.Designer.Windows;
using FireWorkflow.Net.Designer.DesignerServiceReference;

namespace FireWorkflow.Net.Designer
{
    public partial class MainPage : UserControl, IMyMainPage
    {
        /// <summary>当前选中控件集合</summary>
        private List<IDesigner> selectionControls = new List<IDesigner>();

        /// <summary>当前控件集合</summary>
        public Dictionary<string, IDesigner> UserControls {get; set;}//

        /// <summary>鼠标下面的元素</summary>
        IDesignerNode mouseEnterNode;
        /// <summary>鼠标下面的元素</summary>
        IDesignerEdge mouseEnterEdge;

        /// <summary>是否开始画循环线条</summary>
        bool IsLoop = false;

        /// <summary>当前开始画的循环线条</summary>
        LoopControl currentLoopControl;

        /// <summary>是否开始画线条</summary>
        bool IsTransition = false;

        /// <summary>当前开始画的线条</summary>
        TransitionControl currentTransitionControl;

        /// <summary>鼠标当前Y轴位置</summary>
        private double mouseY; 

        /// <summary>鼠标当前X轴位置</summary>
        private double mouseX;

        /// <summary>当前操作的WorkflowProcess业务流程</summary>
        public WorkflowProcess WorkflowProcessCurrent { get; set; }


        /// <summary>当前打开修改的流程</summary>
        public WorkflowDefinition SelectWorkflowDefinition { get; set; }


        /// <summary>是否按下Shift</summary>
        private bool isKeyShift;

        /// <summary>是否按下Ctrl</summary>
        private bool isKeyCtrl;


        /// <summary>当前查询字符串参数</summary>
        private IDictionary<string, string> queryString;
        /// <summary>WCF服务地址</summary>
        private string address;

        //菜单
        private ContextMenu workflowProcessMenu;

        LoadWorkflow loadWorkflow;

        public MainPage()
        {
            InitializeComponent();
            EventViewer.SizeChanged += new SizeChangedEventHandler(EventViewer_SizeChanged);
            UserControls = new Dictionary<string, IDesigner>();
            queryString = HtmlPage.Document.QueryString;

            
            loadWorkflow = new LoadWorkflow(this, canvas, UserControls);
            loadWorkflow.IsEdit = true;

            string absolutePath = HtmlPage.Document.DocumentUri.AbsoluteUri;
            address = absolutePath.Substring(0, absolutePath.LastIndexOf('/')) + "/DesignerService.svc";//获取WCF服务地址
            
            SetIsEnabledTools(false);
            isKeyShift = false;
            isKeyCtrl = false;
            InitMenu();

            btnLocalSave.IsEnabled = false;
            btnSave.IsEnabled = false;
            listBox.IsEnabled = false;
            //GetXMLWorkflowProcessWebClient();

            if (this.WorkflowProcessCurrent == null) this.WorkflowProcessCurrent = new WorkflowProcess("NewWorkflowProcess");

        }

        /// <summary>更改控件集合Key</summary>
        /// <param name="oldKey">旧Key</param>
        /// <param name="newKey">新Key</param>
        public bool SetUserControlsKey(string oldKey, string newKey)
        {
            if (UserControls.ContainsKey(oldKey) && !UserControls.ContainsKey(newKey))
            {
                UserControls.Add(newKey, UserControls[oldKey]);
                UserControls.Remove(oldKey);
                return true;
            }
            return false;
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
            BasicHttpBinding binding = new BasicHttpBinding();
            binding.MaxBufferSize = 2147483647;
            binding.MaxReceivedMessageSize = 2147483647;
            DesignerServiceClient dsc = new DesignerServiceClient(binding, new EndpointAddress(address));
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
            selectionControls.Clear();
            loadWorkflow.LoadWorkflowProcessToFrom(this.WorkflowProcessCurrent);

            //流程跟踪
            if (queryString.ContainsKey("ProcessInstanceId"))
            {
                GetProcessInstanceTraceXml(queryString["ProcessInstanceId"]);
            }
            DataContext = false;

        }

        /// <summary>
        /// 根据流程ID获取流程Xml字符串
        /// </summary>
        /// <param name="processID">流程ID</param>
        /// <param name="version">流程版本</param>
        public void GetWorkflowProcessXmlProcessIdOrVersion(String processID, int version)
        {
            BasicHttpBinding binding = new BasicHttpBinding();
            binding.MaxBufferSize = 2147483647;
            binding.MaxReceivedMessageSize = 2147483647;
            DesignerServiceClient dsc = new DesignerServiceClient(binding, new EndpointAddress(address));
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
            selectionControls.Clear();
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
            BasicHttpBinding binding = new BasicHttpBinding();
            binding.MaxBufferSize = 2147483647;
            binding.MaxReceivedMessageSize = 2147483647;
            DesignerServiceClient dsc = new DesignerServiceClient(binding, new EndpointAddress(address));
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


        /// <summary>返回所有流程的最新版本</summary>
        public void GetAllLatestVersionsOfWorkflowDefinition()
        {
            BasicHttpBinding binding = new BasicHttpBinding();
            binding.MaxBufferSize = 2147483647;
            binding.MaxReceivedMessageSize = 2147483647;
            DesignerServiceClient dsc = new DesignerServiceClient(binding, new EndpointAddress(address));
            dsc.GetAllLatestVersionsOfWorkflowDefinitionCompleted += new EventHandler<GetAllLatestVersionsOfWorkflowDefinitionCompletedEventArgs>(dsc_GetAllLatestVersionsOfWorkflowDefinitionCompleted);
            dsc.GetAllLatestVersionsOfWorkflowDefinitionAsync();
        }

        void dsc_GetAllLatestVersionsOfWorkflowDefinitionCompleted(object sender, GetAllLatestVersionsOfWorkflowDefinitionCompletedEventArgs e)
        {
            ObservableCollection<WorkflowDefinition> WorkflowDefinitions = e.Result;
            if (WorkflowDefinitions!=null)
            {
                OpenWindow openWindow = new OpenWindow(WorkflowDefinitions.ToArray());
                openWindow.Closed += new EventHandler(openWindow_Closed);
                openWindow.Show();
            }
        }

        void openWindow_Closed(object sender, EventArgs e)
        {
            OpenWindow openWindow = (OpenWindow)sender;
            if (openWindow.DialogResult==true)
            {
                this.SelectWorkflowDefinition = openWindow.selectWorkflowDefinition;
                GetWorkflowProcessXml(this.SelectWorkflowDefinition.Id);

                btnLocalSave.IsEnabled = true;
                btnSave.IsEnabled = true;
                listBox.IsEnabled = true;
            }
            DataContext = false;
        }

        /// <summary>
        /// 保存流程定义，如果同一个ProcessId的流程定义已经存在，则版本号自动加1。
        /// </summary>
        /// <param name="workflowProcessXml">保存的WorkflowProcess XML 文本.</param>
        /// <param name="version">保存的版本，当版本为小于等于0时，添加新流程，如存在相同ProcessId则版本号加1</param>
        /// <param name="isState">是否发布</param>
        public bool SaveOrUpdateWorkflowProcess(string workflowProcessXml, int version, bool isState)
        {
            BasicHttpBinding　binding　　=　new　BasicHttpBinding();
            binding.MaxBufferSize = 2147483647;
            binding.MaxReceivedMessageSize = 2147483647;
            DesignerServiceClient dsc = new DesignerServiceClient(binding, new EndpointAddress(address));
            dsc.SaveOrUpdateWorkflowProcessCompleted += new EventHandler<SaveOrUpdateWorkflowProcessCompletedEventArgs>(dsc_SaveOrUpdateWorkflowProcessCompleted);
            dsc.SaveOrUpdateWorkflowProcessAsync(workflowProcessXml, version, isState);
            return false;
        }

        void dsc_SaveOrUpdateWorkflowProcessCompleted(object sender, SaveOrUpdateWorkflowProcessCompletedEventArgs e)
        {
            DataContext = false;
            if (!e.Result)
            {
                MessageBox.Show("保存失败！");
            }
        }



        #endregion

        #region 菜单
        private void InitMenu()
        {
            workflowProcessMenu = new ContextMenu();//新建右键菜单
            MenuItem miWorkflowProcess = new MenuItem();//新建右键菜单项
            miWorkflowProcess.Header = "流程属性";
            miWorkflowProcess.Click += new RoutedEventHandler(miWorkflowProcess_Click);
            // mi.Click += MenuItem_Click;//为菜单项注册事件      
            workflowProcessMenu.Items.Add(miWorkflowProcess);
            ContextMenuService.SetContextMenu(EventViewer, workflowProcessMenu);//为控件绑定右键菜单
        }


        /// <summary>流程属性</summary>
        void miWorkflowProcess_Click(object sender, RoutedEventArgs e)
        {
            WorkflowProcessWindow wpw = new WorkflowProcessWindow(this.WorkflowProcessCurrent);
            wpw.Show();
        }

        void mi_Click(object sender, RoutedEventArgs e)
        {
            //sender
        }
        private void LayoutRoot_MouseRightButtonDown(object sender, MouseButtonEventArgs e)
        {
            e.Handled = true;//屏蔽默认的右键菜单
        }

        #endregion



        #region 画布鼠标事件
        //鼠标单击
        private void Canvas_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (IsTransition)
            {
                listBox.SelectedIndex = 8;
                TransitionButtonDown(sender, e);
                return;
            }
            if (IsLoop)
            {
                listBox.SelectedIndex = 9;
                LoopButtonDown(sender, e);
                return;
            }
            bool isDown = mouseEnterNode == null && mouseEnterEdge == null;
            //鼠标单击
            if (!isMouseCaptured && sender is Canvas)
            {
                switch (listBox.SelectedIndex)
                {
                    case 0:
                        ArrowButtonDown(sender, e);
                        break;
                    case 1:
                        if (isDown) StartNodeButtonDown(sender, e);
                        break;
                    case 2:
                        if (isDown) ActivityButtonDown(sender, e);
                        break;
                    case 3:
                        if (isDown) ActivityFormTaskButtonDown(sender, e);
                        break;
                    case 4:
                        if (isDown) ActivityToolButtonDown(sender, e);
                        break;
                    case 5:
                        if (isDown) ActivitySubflowButtonDown(sender, e);
                        break;
                    case 6:
                        if (isDown) SynchronizerButtonDown(sender, e);
                        break;
                    case 7:
                        if (isDown) EndNodeButtonDown(sender, e);
                        break;
                    case 8:
                        TransitionButtonDown(sender, e);
                        break;
                    case 9:
                        LoopButtonDown(sender, e);
                        break;
                    default:
                        break;
                }
            }
        }

        /// <summary>在画布中鼠标移动</summary>
        private void Canvas_MouseMove(object sender, MouseEventArgs e)
        {
            if (IsTransition)
            {
                Point p = e.GetPosition(canvas);
                Point endPoint = GetMouseMoveEndPoint(currentTransitionControl.GetPoint(), p);
                currentTransitionControl.SetTransitionBegin(((IDesignerNode)UserControls[currentTransitionControl.Transition.FromNode.Id]).Bounds, false);
                currentTransitionControl.SetTransitionEnd(new Rect(endPoint.X, endPoint.Y, 0, 0), true);
            }
            else if (IsLoop)
            {
                Point p = e.GetPosition(canvas);
                Point endPoint = GetMouseMoveEndPoint(currentLoopControl.GetPoint(), p);
                currentLoopControl.SetTransitionBegin(((IDesignerNode)UserControls[currentLoopControl.Loop.FromNode.Id]).Bounds, false);
                currentLoopControl.SetTransitionEnd(new Rect(endPoint.X, endPoint.Y, 0, 0), true);
            }
        }


        private void Canvas_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            bool isDown = mouseEnterNode == null && mouseEnterEdge == null;
            if (!isMouseCaptured && isDown && !isKeyShift && !isKeyCtrl)
            {
                ClearSelection();
            }
        }

        #endregion

        #region 建立
        private void ArrowButtonDown(object sender, MouseButtonEventArgs e)
        {
            //选择
        }

        private void StartNodeButtonDown(object sender, MouseButtonEventArgs e)
        {
            //开始节点
            if (this.WorkflowProcessCurrent.StartNode == null)
            {
                StartNodeControl snc = new StartNodeControl(this, null, false);
                snc.InitNode();
                if (!canvas.Children.Contains(snc))
                {
                    snc.LoadNode();
                    //设置位置
                    Point p = e.GetPosition(canvas);
                    snc.SetPoint((double)(p.X - snc.Bounds.Width / 2), (double)(p.Y - snc.Bounds.Height / 2));

                    this.AddHandle(snc);
                    canvas.Children.Add(snc);
                    this.WorkflowProcessCurrent.StartNode = snc.StartNode;

                    UserControls.Add(snc.Id, snc);

                }
            }
            else
            {
                MainPage.Message("提示", "只能有一个开始节点。");
            }
            listBox.SelectedIndex = 0;
        }

        private void ActivityButtonDown(object sender, MouseButtonEventArgs e)
        {
            ActivityControl ac = new ActivityControl(this, null, false);
            ac.InitNode();
            if (!canvas.Children.Contains(ac))
            {
                ac.LoadNode();
                //设置位置
                Point p = e.GetPosition(canvas);
                ac.SetPoint((double)(p.X - ac.Bounds.Width / 2), (double)(p.Y - ac.Bounds.Height / 2));

                this.AddHandle(ac);
                canvas.Children.Add(ac);
                this.WorkflowProcessCurrent.Activities.Add(ac.Activity);
                UserControls.Add(ac.Id, ac);
            }
        }
        private void ActivityFormTaskButtonDown(object sender, MouseButtonEventArgs e)
        {
            ActivityControl ac = new ActivityControl(this, null, false);
            ac.InitNode();
            ac.AddFormTask();
            if (!canvas.Children.Contains(ac))
            {
                ac.LoadNode();
                //设置位置
                Point p = e.GetPosition(canvas);
                ac.SetPoint((double)(p.X - ac.Bounds.Width / 2), (double)(p.Y - ac.Bounds.Height / 2));

                this.AddHandle(ac);
                canvas.Children.Add(ac);
                this.WorkflowProcessCurrent.Activities.Add(ac.Activity);
                UserControls.Add(ac.Id, ac);
            }
        }
        private void ActivityToolButtonDown(object sender, MouseButtonEventArgs e)
        {
            ActivityControl ac = new ActivityControl(this, null, false);
            ac.InitNode();
            ac.AddToolTask();
            if (!canvas.Children.Contains(ac))
            {
                ac.LoadNode();
                //设置位置
                Point p = e.GetPosition(canvas);
                ac.SetPoint((double)(p.X - ac.Bounds.Width / 2), (double)(p.Y - ac.Bounds.Height / 2));

                this.AddHandle(ac);
                canvas.Children.Add(ac);
                this.WorkflowProcessCurrent.Activities.Add(ac.Activity);
                UserControls.Add(ac.Id, ac);
            }
        }
        private void ActivitySubflowButtonDown(object sender, MouseButtonEventArgs e)
        {
            ActivityControl ac = new ActivityControl(this, null, false);
            ac.InitNode();
            ac.AddSubflowTask();
            if (!canvas.Children.Contains(ac))
            {
                ac.LoadNode();
                //设置位置
                Point p = e.GetPosition(canvas);
                ac.SetPoint((double)(p.X - ac.Bounds.Width / 2), (double)(p.Y - ac.Bounds.Height / 2));

                this.AddHandle(ac);
                canvas.Children.Add(ac);
                this.WorkflowProcessCurrent.Activities.Add(ac.Activity);
                UserControls.Add(ac.Id, ac);
            }
        }

        private void SynchronizerButtonDown(object sender, MouseButtonEventArgs e)
        {
            SynchronizerControl sc = new SynchronizerControl(this, null,false);
            sc.InitNode();
            if (!canvas.Children.Contains(sc))
            {
                sc.LoadNode();
                //设置位置
                Point p = e.GetPosition(canvas);
                sc.SetPoint((double)(p.X - sc.Bounds.Width / 2), (double)(p.Y - sc.Bounds.Height / 2));

                this.AddHandle(sc);
                this.WorkflowProcessCurrent.Synchronizers.Add(sc.Synchronizer);
                canvas.Children.Add(sc);
                UserControls.Add(sc.Id, sc);
            }
            //listBox.SelectedIndex = 0;
        }

        private void EndNodeButtonDown(object sender, MouseButtonEventArgs e)
        {
            EndNodeControl enc = new EndNodeControl(this, null, false);
            enc.InitNode();
            if (!canvas.Children.Contains(enc))
            {
                enc.LoadNode();
                //设置位置
                Point p = e.GetPosition(canvas);
                enc.SetPoint((double)(p.X - enc.Bounds.Width / 2), (double)(p.Y - enc.Bounds.Height / 2));

                this.AddHandle(enc);
                canvas.Children.Add(enc);
                this.WorkflowProcessCurrent.EndNodes.Add(enc.EndNode);
                UserControls.Add(enc.Id, enc);
            }

            //listBox.SelectedIndex = 0;
        }

        //添加连接线
        private void TransitionButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (mouseEnterNode != null)
            {
                if (IsTransition == false)
                {
                    //只能存在一个输出口
                    if (mouseEnterNode.Node is Activity && ((Activity)mouseEnterNode.Node).LeavingTransition != null)
                    {
                        Debug.WriteLine("Activity只能存在一个输出口!");
                        return;
                    }


                    Debug.WriteLine("增加TransitionControl");
                    currentTransitionControl = new TransitionControl(this, null, false);
                    currentTransitionControl.InitEdge();
                    currentTransitionControl.Transition.FromNode = mouseEnterNode.Node;
                    if (mouseEnterNode.Node is Synchronizer)
                    {
                        ((Synchronizer)mouseEnterNode.Node).LeavingTransitions.Add(currentTransitionControl.Transition);
                    }
                    else if (mouseEnterNode.Node is Activity)
                    {
                        ((Activity)mouseEnterNode.Node).LeavingTransition = currentTransitionControl.Transition;
                    }
                    mouseEnterNode = null;

                    Point p = e.GetPosition(canvas);

                    currentTransitionControl.SetTransitionPosition(p, null, p);

                    if (!canvas.Children.Contains(currentTransitionControl))
                    {
                        //设置位置
                        canvas.Children.Add(currentTransitionControl);
                        UserControls.Add(currentTransitionControl.Id, currentTransitionControl);
                        this.WorkflowProcessCurrent.Transitions.Add(currentTransitionControl.Transition);
                    }
                    IsTransition = true;
                    return;
                }
            }

            if (IsTransition)
            {
                if (mouseEnterNode != null)
                {
                    //判断是否配对连接
                    if (currentTransitionControl.Transition.FromNode is Activity && !(mouseEnterNode.Node is Synchronizer))
                    {
                        //当源节点为Activity,目标节点必须是Synchronizer；否则退出
                        Debug.WriteLine("配对失败!当源节点为Activity,目标节点必须是Synchronizer;");
                        return;
                    }
                    else if(currentTransitionControl.Transition.FromNode is Synchronizer)
                    {
                        //当源节点为Synchronizer,目标节点必须是Activity，并且Activity没有输入项；否则退出
                        if (!(mouseEnterNode.Node is Activity) || ((Activity)mouseEnterNode.Node).EnteringTransition != null)
                        {
                            Debug.WriteLine("配对失败!当源节点为Synchronizer,目标节点必须是Activity，并且Activity没有输入项");
                            return;
                        }
                    }

                    currentTransitionControl.Transition.ToNode = mouseEnterNode.Node;
                    if (mouseEnterNode.Node is Synchronizer)
                    {
                        ((Synchronizer)mouseEnterNode.Node).EnteringTransitions.Add(currentTransitionControl.Transition);
                    }
                    else if (mouseEnterNode.Node is Activity)
                    {
                        ((Activity)mouseEnterNode.Node).EnteringTransition = currentTransitionControl.Transition;
                    }
                    mouseEnterNode = null;

                    currentTransitionControl.SavePoint();
                    currentTransitionControl.LoadEdge();
                    this.AddHandle(currentTransitionControl);
                    currentTransitionControl = null;
                    IsTransition = false;
                }
                else
                {
                    Point p = e.GetPosition(canvas);
                    currentTransitionControl.AddLinePoint(p);
                }
            }
        }

        //添加循环线
        private void LoopButtonDown(object sender, MouseButtonEventArgs e)
        {
            if (mouseEnterNode != null && mouseEnterNode.Node is Synchronizer && !(mouseEnterNode.Node is StartNode))
            {
                if (IsLoop == false)
                {
                    Debug.WriteLine("增加LoopControl");
                    currentLoopControl = new LoopControl(this, null, false);
                    currentLoopControl.InitEdge();
                    currentLoopControl.Loop.FromNode = mouseEnterNode.Node;

                    mouseEnterNode = null;

                    Point p = e.GetPosition(canvas);

                    currentLoopControl.SetLoopPosition(p, null, p);

                    if (!canvas.Children.Contains(currentLoopControl))
                    {
                        //设置位置
                        canvas.Children.Add(currentLoopControl);
                        UserControls.Add(currentLoopControl.Id, currentLoopControl);
                        this.WorkflowProcessCurrent.Loops.Add(currentLoopControl.Loop);
                    }
                    IsLoop = true;
                    return;
                }
            }

            if (IsLoop)
            {
                System.Threading.Thread.Sleep(300);//还没有获取mouseEnterNode就执行了。进行稍微延时已保证获取到了mouseEnterNode；

                if (mouseEnterNode != null)
                {
                    //判断是否配对连接
                    if (!(mouseEnterNode.Node is Synchronizer))
                    {
                        //目标节点必须是Synchronizer；否则退出
                        Debug.WriteLine("配对失败!目标节点必须是Synchronizer;");
                        return;
                    }
                    else if (currentLoopControl.Loop.FromNode.Id == mouseEnterNode.Node.Id)
                    {
                        //节点相同退出
                        Debug.WriteLine("配对失败!节点相同。");
                        return;

                    }

                    currentLoopControl.Loop.ToNode = mouseEnterNode.Node;
                    ((Synchronizer)mouseEnterNode.Node).LeavingLoops.Add(currentLoopControl.Loop);
                   
                    mouseEnterNode = null;

                    currentLoopControl.SavePoint();
                    currentLoopControl.LoadEdge();
                    this.AddHandle(currentLoopControl);
                    currentLoopControl = null;
                    IsLoop = false;

                }
                else
                {
                    Point p = e.GetPosition(canvas);
                    currentLoopControl.AddLinePoint(p);
                }
            }
        }



        private Point GetMouseMoveEndPoint(Point begin, Point end)
        {
            Point rPoint = new Point();
            if (end.X == begin.X)
            {
                if (end.Y == begin.Y) return rPoint;
            }
            if (end.X > begin.X) rPoint.X = end.X - 2;
            else rPoint.X = end.X + 2;


            if (end.Y == begin.Y) rPoint.Y = end.Y;
            else if (end.Y > begin.Y) rPoint.Y = end.Y - 2;
            else rPoint.Y = end.Y + 2;

            return rPoint;
        }


        #endregion

        #region 移动元素

        /// <summary>添加移动事件</summary>
        /// <param name="uc"></param>
        public void AddHandle(UserControl uc)
        {
            uc.MouseLeftButtonDown += new MouseButtonEventHandler(Handle_MouseDown);
            uc.MouseMove += new MouseEventHandler(Handle_MouseMove);
            uc.MouseLeftButtonUp += new MouseButtonEventHandler(Handle_MouseUp);
            uc.MouseEnter += new MouseEventHandler(Handle_MouseEnter);
            uc.MouseLeave += new MouseEventHandler(Handle_MouseLeave);
        }

        void Handle_MouseLeave(object sender, MouseEventArgs e)
        {
            //离开时发生
            Debug.WriteLine("离开");
            mouseEnterNode = null;
            mouseEnterEdge = null;
        }

        void Handle_MouseEnter(object sender, MouseEventArgs e)
        {
            //进入时发生
            if (sender is IDesignerNode)
            {
                Debug.WriteLine("进入时发生");
                mouseEnterNode = (IDesignerNode)sender;
            }
            else if (sender is IDesignerEdge)
            {
                mouseEnterEdge = (IDesignerEdge)sender;
            }
        }

        bool isMouseCaptured=false;
        bool isMouseMove=false;

        //鼠标按下
        public void Handle_MouseDown(object sender, MouseEventArgs args)
        {
            Debug.WriteLine("Handle_MouseDown");
            IDesigner item = (IDesigner)sender;

            if (IsLoop || IsTransition)
            {
                if (item is IDesignerNode)
                {
                    if (mouseEnterNode == null) mouseEnterNode = (IDesignerNode)item;
                }
                else if (item is IDesignerEdge)
                {
                    if (mouseEnterEdge == null) mouseEnterEdge = (IDesignerEdge)item;
                }
                return;
            }

            ///if (listBox.SelectedIndex==0 && item.ResizeStatus==EnumResizeStatus.Move)
            if (item.ResizeStatus == EnumResizeStatus.Move)
            {
                isMouseCaptured = true;
                ((UserControl)sender).CaptureMouse();

                mouseY = args.GetPosition(null).Y;
                mouseX = args.GetPosition(null).X;

                return;
            }
            if (listBox.SelectedIndex >=0 && listBox.SelectedIndex<=7)
            {
                if (item is IDesignerNode)
                {
                    if (mouseEnterNode == null) mouseEnterNode = (IDesignerNode)item;
                }
                else if(item is IDesignerEdge)
                {
                    if (mouseEnterEdge == null) mouseEnterEdge = (IDesignerEdge)item;
                }
                AddPickOn(item);
            }
        }

        //鼠标移动
        public void Handle_MouseMove(object sender, MouseEventArgs args)
        {
            if (isMouseCaptured)
            {
                //IDesignerNode item = (IDesignerNode)sender;
                // Calculate the current position of the object.
                double deltaV = args.GetPosition(null).Y - mouseY;
                double deltaH = args.GetPosition(null).X - mouseX;
                foreach (var control in selectionControls)
                {
                    MoveControl(control, deltaV, deltaH);
                }

                mouseY = args.GetPosition(null).Y;
                mouseX = args.GetPosition(null).X;
                isMouseMove = true;
            }
        }
        public void MoveControl(IDesigner control, double deltaV, double deltaH)
        {
            control.MoveDesigner(deltaV, deltaH);
            if (control is IDesignerNode)
            {
                SetTransitionTransitionPoint((IDesignerNode)control);
            }
        }

       
        /// <summary>设置相连线条位置</summary>
        /// <param name="item">调整的元素</param>
        public void SetTransitionTransitionPoint(IDesignerNode item)
        {
            //设置相关连线条位置
            if (item is IDesignerSynchronizer)
            {
                foreach (var tran in ((IDesignerSynchronizer)item).LeavingTransitions)
                {
                    TransitionControl tc = (TransitionControl)UserControls[tran.Id];
                    tc.SetTransitionBegin(item.Bounds, false);
                    tc.SetTransitionEnd(((IDesignerNode)UserControls[tc.Transition.ToNode.Id]).Bounds, true);
                }
                foreach (var tran in ((IDesignerSynchronizer)item).EnteringTransitions)
                {
                    TransitionControl tc = (TransitionControl)UserControls[tran.Id];
                    tc.SetTransitionBegin(((IDesignerNode)UserControls[tc.Transition.FromNode.Id]).Bounds, true);
                    tc.SetTransitionEnd(item.Bounds, false);
                }

                foreach (var tran in ((IDesignerSynchronizer)item).LeavingLoops)
                {
                    LoopControl loop = (LoopControl)UserControls[tran.Id];
                    loop.SetTransitionBegin(item.Bounds, false);
                    loop.SetTransitionEnd(((IDesignerNode)UserControls[loop.Loop.ToNode.Id]).Bounds, false);
                }
                foreach (var tran in ((IDesignerSynchronizer)item).EnteringLoops)
                {
                    LoopControl loop = (LoopControl)UserControls[tran.Id];
                    loop.SetTransitionBegin(((IDesignerNode)UserControls[loop.Loop.FromNode.Id]).Bounds, false);
                    loop.SetTransitionEnd(item.Bounds, false);
                }
            }
            else if (item is ActivityControl)
            {
                if (((ActivityControl)item).Activity.LeavingTransition != null)
                {
                    TransitionControl tc = (TransitionControl)UserControls[((ActivityControl)item).Activity.LeavingTransition.Id];
                    tc.SetTransitionBegin(item.Bounds, true);
                    tc.SetTransitionEnd(((IDesignerNode)UserControls[tc.Transition.ToNode.Id]).Bounds, false);
                }
                if (((ActivityControl)item).Activity.EnteringTransition != null)
                {
                    TransitionControl tc = (TransitionControl)UserControls[((ActivityControl)item).Activity.EnteringTransition.Id];
                    tc.SetTransitionBegin(((IDesignerNode)UserControls[tc.Transition.FromNode.Id]).Bounds, false);
                    tc.SetTransitionEnd(item.Bounds, true);
                }
            }
        }

        //鼠标弹起
        public void Handle_MouseUp(object sender, MouseEventArgs args)
        {
            Debug.WriteLine("鼠标弹起");
            if (isMouseCaptured)
            {
                IDesignerNode item = (IDesignerNode)sender;
                isMouseCaptured = false;
                ((UserControl)sender).ReleaseMouseCapture();

                double dLeft, dTop;
                bool isEdit=false;

                dLeft = item.Bounds.X;
                dTop = item.Bounds.Y;

                if (dLeft < 0) { dLeft = 10; isEdit = true; }
                if (dTop < 0){ dTop = 10;isEdit = true; }
                if (isEdit)
                {
                    item.SetPoint(dLeft, dTop);
                    SetTransitionTransitionPoint(item);
                }
                //调整画布
                canvas.Width = Math.Max(canvas.Width, item.Bounds.Right + 20);
                canvas.Height = Math.Max(canvas.Height, item.Bounds.Bottom + 20);

                mouseY = -1;
                mouseX = -1;

                isMouseMove = false;
            }
        }

        #endregion

        #region 消息提示
        public static void Message(String title,string message)
        {
            ChildWindow childWindow = new ChildWindow();
            childWindow.Title = title;
            childWindow.Content = message;
            childWindow.Show();
        }
        #endregion

        #region 全局事件
        
        //获取键盘输入
        private void keyDown(object sender, KeyEventArgs e)
        {
            switch (e.Key)
            {
                case Key.Shift: isKeyShift = true; break;
                case Key.Ctrl: isKeyCtrl = true; break;
                case Key.Delete: DelControl(); break;
                case Key.Escape: EscapeControl(); break;
                case Key.W: EscapeControl(); break;
            }
        }

        //释放键盘输入
        private void keyUP(object sender, KeyEventArgs e)
        {
            isKeyShift = false;
            isKeyCtrl = false;
        }

        //失去焦点
        private void lostFocus(object sender, RoutedEventArgs e)
        {
            isKeyShift = false;
            isKeyCtrl = false;
        }

        /// <summary>取消</summary>
        private void EscapeControl()
        {
            //取消绘制循环
            if (IsLoop)
            {
                if (DelLoop(currentLoopControl))
                {
                    currentLoopControl = null;
                    IsLoop = false;
                }
                return;
            }

            //取消绘制流程转移
            if (IsTransition)
            {
                if (DelTransition(currentTransitionControl))
                {
                    currentTransitionControl = null;
                    IsTransition = false;
                }
                return;
            }

            //取消选择
            ClearSelection();
        }

        void EventViewer_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            //控件大小发生变化时调整画布大小
            canvas.Width = Math.Max(canvas.Width, EventViewer.ActualWidth - 28);
            canvas.Height = Math.Max(canvas.Height, EventViewer.ActualHeight - 10);
        }

        #endregion

        #region 工具栏事件
        public void SetIsEnabledTools(bool enabled)
        {
            btnlayoutLeftAlign.IsEnabled = enabled;
            btnlayoutLevelCenterAlign.IsEnabled = enabled;
            btnlayoutRightAlign.IsEnabled = enabled;
            btnlayoutTopAlign.IsEnabled = enabled;
            btnlayoutVerticalCenterAlign.IsEnabled = enabled;
            btnlayoutBottomAlign.IsEnabled = enabled;
            btnlayoutLevelSpacingEqual.IsEnabled = enabled;
            btnlayoutLevelIncreaseSpacing.IsEnabled = enabled;
            btnlayoutLevelDecreaseSpacing.IsEnabled = enabled;
            btnlayoutVerticalSpacingEqual.IsEnabled = enabled;
            btnlayoutVerticalIncreaseSpacing.IsEnabled = enabled;
            btnlayoutVerticalDecreaseSpacing.IsEnabled = enabled;
        }
        private void btnNew_Checked(object sender, RoutedEventArgs e)
        {
            //新建
            SelectWorkflowDefinition = null;
            WorkflowProcess wp= new WorkflowProcess("NewWorkflowProcess");
            WorkflowProcessWindow wpw = new WorkflowProcessWindow(wp);
            wpw.Closed += new EventHandler(wpw_Closed);
            wpw.Show();


        }

        void wpw_Closed(object sender, EventArgs e)
        {
            WorkflowProcessWindow wpw = (WorkflowProcessWindow)sender;
            if (wpw.DialogResult==true)
            {
                this.WorkflowProcessCurrent = wpw.WorkflowProcess;
                btnLocalSave.IsEnabled = true;
                btnSave.IsEnabled = true;
                listBox.IsEnabled = true;

                canvas.Width = EventViewer.ActualWidth - 28;
                canvas.Height = EventViewer.ActualHeight - 10;

                canvas.Children.Clear();
                UserControls.Clear();
                selectionControls.Clear();
            }
        }
        private void btnLocalOpen_Checked(object sender, RoutedEventArgs e)
        {
            try
            {
                OpenFileDialog myOpenDialog = new OpenFileDialog();
                myOpenDialog.Filter = "流程文件*.xml | *.xml";
                //myOpenDialog.DefaultExt = "xml";

                bool? ret = myOpenDialog.ShowDialog();
                if (ret == true)
                {
                    using (StreamReader fs = (StreamReader)myOpenDialog.File.OpenText())
                    {
                        string xmlWorkflow = fs.ReadToEnd();
                        fs.Close();

                        MemoryStream msin = new MemoryStream(Encoding.UTF8.GetBytes(xmlWorkflow));
                        Dom4JFPDLParser djp = new Dom4JFPDLParser();

                        SelectWorkflowDefinition = null;
                        this.WorkflowProcessCurrent = djp.parse(msin);

                        btnLocalSave.IsEnabled = true;
                        btnSave.IsEnabled = true;
                        listBox.IsEnabled = true;

                        canvas.Width = EventViewer.ActualWidth - 28;
                        canvas.Height = EventViewer.ActualHeight - 10;

                        canvas.Children.Clear();
                        UserControls.Clear();
                        selectionControls.Clear();

                        loadWorkflow.LoadWorkflowProcessToFrom(this.WorkflowProcessCurrent);
                    }
                }



            }
            catch
            {
                throw;
            }
        }
        private void btnOpen_Checked(object sender, RoutedEventArgs e)
        {
            //打开
            //返回所有流程的最新版本
            DataContext = true;
            this.GetAllLatestVersionsOfWorkflowDefinition();
        }
        private void btnSave_Checked(object sender, RoutedEventArgs e)
        {
            //保存
            DataContext = true;
            SaveWindow sw = new SaveWindow(String.Format("流程名:{0}；\n显示名称:{1}；\n版本号：{2}；\n", WorkflowProcessCurrent.Name, WorkflowProcessCurrent.DisplayName, this.SelectWorkflowDefinition == null ? 0 : this.SelectWorkflowDefinition.Version));
            sw.Closed += new EventHandler(sw_Closed);
            sw.Show();

        }
        private void btnLocalSave_Checked(object sender, RoutedEventArgs e)
        {
            //保存
            try
            {
                SaveFileDialog mySaveDialog = new SaveFileDialog();
                mySaveDialog.Filter = "流程文件*.xml | *.xml";
                mySaveDialog.DefaultExt = "xml";
                bool? ret = mySaveDialog.ShowDialog();
                if (ret == true)
                {
                    using (Stream fs = (Stream)mySaveDialog.OpenFile())
                    {

                        Dom4JFPDLSerializer fpdl = new Dom4JFPDLSerializer();
                        MemoryStream so = new MemoryStream();

                        fpdl.serialize(WorkflowProcessCurrent, so);
                        byte[] bs = so.ToArray();

                        fs.Write(bs, 0, bs.Length);
                        fs.Close();
                    }
                }
            }
            catch
            {
                throw;
            }
        }

        void sw_Closed(object sender, EventArgs e)
        {
            SaveWindow sw = (SaveWindow)sender;
            if (sw.DialogResult == true)
            {
                Dom4JFPDLSerializer fpdl = new Dom4JFPDLSerializer();
                MemoryStream so = new MemoryStream();
                try
                {
                    fpdl.serialize(WorkflowProcessCurrent, so);
                    byte[] bs = so.ToArray();

                    String workflowProcessXml = Encoding.UTF8.GetString(bs, 0, bs.Count());

                    int version = 0;
                    if (!sw.IsNew)
                        version = this.SelectWorkflowDefinition == null ? 0 : this.SelectWorkflowDefinition.Version;
            

                    SaveOrUpdateWorkflowProcess(workflowProcessXml, version, sw.IsState);
                }
                catch
                {
                    throw;
                }
            }
            else DataContext = false;
        }

        #region 对齐控制
        private void btnlayoutLeftAlign_Checked(object sender, RoutedEventArgs e)
        {
            //左对齐
            IDesigner controls = selectionControls[selectionControls.Count - 1];
            if (controls is IDesignerNode)
            {
                double dAlign = ((IDesignerNode)controls).Bounds.X;
                foreach (var item in selectionControls)
                {
                    if (item is IDesignerNode)
                    {
                        IDesignerNode itemNode=(IDesignerNode)item;
                        itemNode.SetPoint(dAlign, itemNode.Bounds.Y);
                        if (itemNode is IDesignerNode)
                        {
                            SetTransitionTransitionPoint((IDesignerNode)itemNode);
                        }
                    }
                }
            }
        }
        private void btnlayoutVerticalCenterAlign_Checked(object sender, RoutedEventArgs e)
        {
            //垂直居中对齐
            IDesigner controls = selectionControls[selectionControls.Count - 1];
            if (controls is IDesignerNode)
            {
                double dAlign = ((IDesignerNode)controls).Bounds.X + ((IDesignerNode)controls).Bounds.Width / 2;
                foreach (var item in selectionControls)
                {
                    if (item is IDesignerNode)
                    {
                        IDesignerNode itemNode = (IDesignerNode)item;
                        itemNode.SetPoint(dAlign - itemNode.Bounds.Width / 2, itemNode.Bounds.Y);
                        if (itemNode is IDesignerNode)
                        {
                            SetTransitionTransitionPoint((IDesignerNode)itemNode);
                        }
                    }
                }
            }
        }
        private void btnlayoutRightAlign_Checked(object sender, RoutedEventArgs e)
        {
            //右对齐
            IDesigner controls = selectionControls[selectionControls.Count - 1];
            if (controls is IDesignerNode)
            {
                double dAlign = ((IDesignerNode)controls).Bounds.X + ((IDesignerNode)controls).Bounds.Width;
                foreach (var item in selectionControls)
                {
                    if (item is IDesignerNode)
                    {
                        IDesignerNode itemNode = (IDesignerNode)item;
                        itemNode.SetPoint(dAlign - itemNode.Bounds.Width, itemNode.Bounds.Y);
                        if (itemNode is IDesignerNode)
                        {
                            SetTransitionTransitionPoint((IDesignerNode)itemNode);
                        }
                    }
                }
            }
        }
        private void btnlayoutTopAlign_Checked(object sender, RoutedEventArgs e)
        {
            //顶端对齐
            IDesigner controls = selectionControls[selectionControls.Count - 1];
            if (controls is IDesignerNode)
            {
                double dAlign = ((IDesignerNode)controls).Bounds.Y;
                foreach (var item in selectionControls)
                {
                    if (item is IDesignerNode)
                    {
                        IDesignerNode itemNode = (IDesignerNode)item;
                        itemNode.SetPoint(itemNode.Bounds.X, dAlign);
                        if (itemNode is IDesignerNode)
                        {
                            SetTransitionTransitionPoint((IDesignerNode)itemNode);
                        }
                    }
                }
            }
        }
        private void btnlayoutLevelCenterAlign_Checked(object sender, RoutedEventArgs e)
        {
            //水平居中对齐
            IDesigner controls = selectionControls[selectionControls.Count - 1];
            if (controls is IDesignerNode)
            {
                double dAlign = ((IDesignerNode)controls).Bounds.Y + ((IDesignerNode)controls).Bounds.Height/2;
                foreach (var item in selectionControls)
                {
                    if (item is IDesignerNode)
                    {
                        IDesignerNode itemNode = (IDesignerNode)item;
                        itemNode.SetPoint(itemNode.Bounds.X, dAlign - itemNode.Bounds.Height / 2);
                        if (itemNode is IDesignerNode)
                        {
                            SetTransitionTransitionPoint((IDesignerNode)itemNode);
                        }
                    }
                }
            }
        }
        private void btnlayoutBottomAlign_Checked(object sender, RoutedEventArgs e)
        {
            //底端对齐
            IDesigner controls = selectionControls[selectionControls.Count - 1];
            if (controls is IDesignerNode)
            {
                double dAlign = ((IDesignerNode)controls).Bounds.Y + ((IDesignerNode)controls).Bounds.Height;
                foreach (var item in selectionControls)
                {
                    if (item is IDesignerNode)
                    {
                        IDesignerNode itemNode = (IDesignerNode)item;
                        itemNode.SetPoint(itemNode.Bounds.X, dAlign - itemNode.Bounds.Height);
                        if (itemNode is IDesignerNode)
                        {
                            SetTransitionTransitionPoint((IDesignerNode)itemNode);
                        }
                    }
                }
            }
        }
        private void btnlayoutLevelSpacingEqual_Checked(object sender, RoutedEventArgs e)
        {
            //使水平间距相等
            var items=from sc in  selectionControls
                      where sc is IDesignerNode
                      orderby sc.CentrePoint.X, sc.Id
                      select sc;

            int sl = items.Count();
            double dL=items.ElementAt(0).CentrePoint.X;
            double dR = items.ElementAt(sl - 1).CentrePoint.X;
            double pj = (dR - dL) / (sl-1);

            int i = 0;
            foreach (var item in items)
            {
                item.MoveDesigner(0, pj * i - (item.CentrePoint.X - dL));
                if (item is IDesignerNode)
                {
                    SetTransitionTransitionPoint((IDesignerNode)item);
                }
                i++;
            }
        }
        private void btnlayoutLevelIncreaseSpacing_Checked(object sender, RoutedEventArgs e)
        {
            //增加水平间距 //使水平间距相等
            var items = from sc in selectionControls
                        where sc is IDesignerNode
                        orderby sc.CentrePoint.X, sc.Id
                        select sc;

            int i = 0;
            foreach (var item in items)
            {
                item.MoveDesigner(0, 10 * i);
                if (item is IDesignerNode)
                {
                    SetTransitionTransitionPoint((IDesignerNode)item);
                }
                i++;
            }
        }
        private void btnlayoutLevelDecreaseSpacing_Checked(object sender, RoutedEventArgs e)
        {
            //减小水平间距
            //增加水平间距 //使水平间距相等
            var items = from sc in selectionControls
                        where sc is IDesignerNode
                        orderby sc.CentrePoint.X, sc.Id
                        select sc;

            int i = 0;
            foreach (var item in items)
            {
                item.MoveDesigner(0, -10 * i);
                if (item is IDesignerNode)
                {
                    SetTransitionTransitionPoint((IDesignerNode)item);
                }
                i++;
            }
        }
        private void btnlayoutVerticalSpacingEqual_Checked(object sender, RoutedEventArgs e)
        {
            //使垂直间距相等
            var items = from sc in selectionControls
                        where sc is IDesignerNode
                        orderby sc.CentrePoint.Y, sc.Id
                        select sc;

            int sl = items.Count();
            double dL = items.ElementAt(0).CentrePoint.Y;
            double dR = items.ElementAt(sl - 1).CentrePoint.Y;
            double pj = (dR - dL) / (sl - 1);

            int i = 0;
            foreach (var item in items)
            {
                item.MoveDesigner( pj * i - (item.CentrePoint.Y - dL),0);
                if (item is IDesignerNode)
                {
                    SetTransitionTransitionPoint((IDesignerNode)item);
                }
                i++;
            }
        }
        private void btnlayoutVerticalIncreaseSpacing_Checked(object sender, RoutedEventArgs e)
        {
            //增加垂直间距
            var items = from sc in selectionControls
                        where sc is IDesignerNode
                        orderby sc.CentrePoint.Y,sc.Id
                        select sc;

            int i = 0;
            foreach (var item in items)
            {
                item.MoveDesigner(10 * i, 0);
                if (item is IDesignerNode)
                {
                    SetTransitionTransitionPoint((IDesignerNode)item);
                }
                i++;
            }
        }
        private void btnlayoutVerticalDecreaseSpacing_Checked(object sender, RoutedEventArgs e)
        {
            //减小垂直间距
            var items = from sc in selectionControls
                        where sc is IDesignerNode
                        orderby sc.CentrePoint.Y, sc.Id
                        select sc;

            int i = 0;
            foreach (var item in items)
            {
                item.MoveDesigner(-10 * i, 0);
                if (item is IDesignerNode)
                {
                    SetTransitionTransitionPoint((IDesignerNode)item);
                }
                i++;
            }
        }
        #endregion

        #endregion

        #region 选中相关

        /// <summary>添加选中控件</summary>
        /// <param name="sender">控件</param>
        public void AddPickOn(IDesigner sender)
        {
            if (!isKeyShift && !isKeyCtrl)
            {
                ClearSelection();
            }
            else
            {

                foreach (var item in selectionControls)
                    item.IsSelectionState = false;
            }
            sender.IsSelection = true;
            sender.IsSelectionState = true;
            if (!this.selectionControls.Contains(sender))
            {
                this.selectionControls.Add(sender);
            }
            if (this.selectionControls.Count >= 2)
            {
                SetIsEnabledTools(true);
            }
            else SetIsEnabledTools(false);
        }

        /// <summary>清除所有选中</summary>
        public void ClearSelection()
        {
            foreach (var item in selectionControls)
            {
                item.IsSelection = false;
            }
            selectionControls.Clear();
            SetIsEnabledTools(false);
        }

        #endregion

        #region 删除对象
        public void DelControl()
        {
            if (selectionControls.Count <= 0) return;
            if (MessageBox.Show("是否要删除选中对象。", "提示", MessageBoxButton.OKCancel) == MessageBoxResult.OK)
            {
                //while (selectionControls.Count>0)
                //{
                //    IDesigner des = selectionControls[0];
                //}
                foreach (var item in selectionControls)
                {
                    if (item is ActivityControl) DelActivity((ActivityControl)item);
                    else if (item is StartNodeControl) DelStartNode((StartNodeControl)item);
                    else if (item is EndNodeControl) DelEndNode((EndNodeControl)item);
                    else if (item is SynchronizerControl) DelSynchronizer((SynchronizerControl)item);
                    else if (item is TransitionControl) DelTransition((TransitionControl)item);
                    else if (item is LoopControl) DelLoop((LoopControl)item);
                }
                selectionControls.Clear();
                return;
            }

        }

        public void DelStartNode(StartNodeControl con)
        {
            this.WorkflowProcessCurrent.StartNode = null;
            UserControls.Remove(con.Id);
            canvas.Children.Remove(con);
            foreach (var item in con.StartNode.LeavingTransitions)
            {
                item.FromNode = null;
            }
            foreach (var item in con.StartNode.EnteringLoops)
            {
                item.ToNode = null;
            }
        }

        public void DelActivity(ActivityControl con)
        {
            if (this.WorkflowProcessCurrent.Activities.Remove(con.Activity))
            {
                UserControls.Remove(con.Id);
                canvas.Children.Remove(con);
                if (con.Activity.EnteringTransition != null)
                {
                    con.Activity.EnteringTransition.ToNode = null;
                }
                if (con.Activity.LeavingTransition != null)
                {
                    con.Activity.LeavingTransition.FromNode = null;
                }
                currentTransitionControl = null;
                IsTransition = false;
            }
        }
        public void DelEndNode(EndNodeControl con)
        {
            if (this.WorkflowProcessCurrent.EndNodes.Remove(con.EndNode))
            {
                UserControls.Remove(con.Id);
                canvas.Children.Remove(con);
                foreach (var item in con.EndNode.EnteringTransitions)
                {
                    item.ToNode = null;
                }
                foreach (var item in con.EndNode.LeavingLoops)
                {
                    item.FromNode = null;
                }
            }
        }
        public void DelSynchronizer(SynchronizerControl con)
        {
            if (this.WorkflowProcessCurrent.Synchronizers.Remove(con.Synchronizer))
            {
                UserControls.Remove(con.Id);
                canvas.Children.Remove(con);
                foreach (var item in con.Synchronizer.LeavingTransitions)
                {
                    item.FromNode = null;
                }
                foreach (var item in con.Synchronizer.EnteringTransitions)
                {
                    item.ToNode = null;
                }
                foreach (var item in con.Synchronizer.LeavingLoops)
                {
                    item.FromNode = null;
                }
                foreach (var item in con.Synchronizer.EnteringLoops)
                {
                    item.ToNode = null;
                }
            }
        }
        public bool DelTransition(TransitionControl con)
        {
            if (this.WorkflowProcessCurrent.Transitions.Remove(con.Transition))
            {
                UserControls.Remove(con.Id);
                canvas.Children.Remove(con);
                if (con.Transition.FromNode != null)
                {
                    if (con.Transition.FromNode is Activity)
                    {
                        ((Activity)con.Transition.FromNode).LeavingTransition = null;
                    }
                    else if (con.Transition.FromNode is Synchronizer)
                    {
                        ((Synchronizer)con.Transition.FromNode).LeavingTransitions.Remove(con.Transition);
                    }
                }
                if (con.Transition.ToNode != null)
                {
                    if (con.Transition.ToNode is Activity)
                    {
                        ((Activity)con.Transition.ToNode).EnteringTransition = null;
                    }
                    else if (con.Transition.ToNode is Synchronizer)
                    {
                        ((Synchronizer)con.Transition.ToNode).EnteringTransitions.Remove(con.Transition);
                    }
                }
                return true;
            }
            return false;
        }
        public bool DelLoop(LoopControl con)
        {
            if (this.WorkflowProcessCurrent.Loops.Remove(con.Loop))
            {
                UserControls.Remove(con.Id);
                canvas.Children.Remove(con);
                if (con.Loop.FromNode != null)
                {
                    if (con.Loop.FromNode is Synchronizer)
                    {
                        ((Synchronizer)con.Loop.FromNode).LeavingLoops.Remove(con.Loop);
                    }
                }
                if (con.Loop.ToNode != null)
                {
                    if (con.Loop.ToNode is Synchronizer)
                    {
                        ((Synchronizer)con.Loop.ToNode).EnteringLoops.Remove(con.Loop);
                    }
                }
                return true;
            }
            return false;
        }
        #endregion

    }
}
