using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using FireWorkflow.Net.Designer.DesignerServiceReference;
using System.ServiceModel;
using System.Windows.Data;

namespace FireWorkflow.Net.Designer
{
    public partial class OpenWindow : ChildWindow
    {
        public WorkflowDefinition selectWorkflowDefinition { get; set; }
        public string Address { get; set; }
        public OpenWindow(string address)
        {
            InitializeComponent();
            this.Address = address;
            dataGrid.DoubleClick += new EventHandler<EventArgs>(dataGrid_DoubleClick);


        }

        public OpenWindow(WorkflowDefinition[] wds)
        {
            InitializeComponent();
            dataGrid.ItemsSource = null;
            dataGrid.ItemsSource = wds;

            dataGrid.DoubleClick += new EventHandler<EventArgs>(dataGrid_DoubleClick);
        }

        void dataGrid_DoubleClick(object sender, EventArgs e)
        {
            if (dataGrid.SelectedItem != null)
            {
                selectWorkflowDefinition = (WorkflowDefinition)dataGrid.SelectedItem;
                this.DialogResult = true;
            }
        }

        private void OKButton_Click(object sender, RoutedEventArgs e)
        {
            if (dataGrid.SelectedItem == null)
            {
                MessageBox.Show("请选择需要打开的流程！");
                return;
            }
            selectWorkflowDefinition = (WorkflowDefinition)dataGrid.SelectedItem;
            this.DialogResult = true;
        }

        private void CancelButton_Click(object sender, RoutedEventArgs e)
        {
            this.DialogResult = false;
        }

        private void bFind_Click(object sender, RoutedEventArgs e)
        {
            //查询
            BasicHttpBinding binding = new BasicHttpBinding();
            binding.MaxBufferSize = 2147483647;
            binding.MaxReceivedMessageSize = 2147483647;
            DesignerServiceClient dsc = new DesignerServiceClient(binding, new EndpointAddress(this.Address));
            dsc.GetPageLatestVersionsOfWorkflowDefinitionCompleted += new EventHandler<GetPageLatestVersionsOfWorkflowDefinitionCompletedEventArgs>(dsc_GetPageLatestVersionsOfWorkflowDefinitionCompleted);
            dsc.GetPageLatestVersionsOfWorkflowDefinitionAsync(tfName.Text, tfDisplayName.Text);
        }

        void dsc_GetPageLatestVersionsOfWorkflowDefinitionCompleted(object sender, GetPageLatestVersionsOfWorkflowDefinitionCompletedEventArgs e)
        {
            PagedCollectionView pcv = new PagedCollectionView(e.Result);
             
            //列表控件数据源绑定  
            dataGrid.ItemsSource = pcv;
          
            //分页控件数据源绑定  
            dataPager.DataContext = pcv;
        }
    }
}

