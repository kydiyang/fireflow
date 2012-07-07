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

namespace FireWorkflow.Net.Designer
{
    public partial class OpenWindow : ChildWindow
    {
        public WorkflowDefinition selectWorkflowDefinition { get; set; }
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
    }
}

