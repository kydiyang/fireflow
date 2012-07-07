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

namespace FireWorkflow.Net.Designer
{
    public partial class SaveWindow : ChildWindow
    {
        /// <summary>true 保存为新的版本.</summary>
        public bool IsNew { get { return cBIsNew.IsChecked == true; } }

        /// <summary>true 发布.</summary>
        public bool IsState { get { return cBIsState.IsChecked == true; } }

        public SaveWindow(String title)
        {
            InitializeComponent();

            label.Content = title;
        }

        private void OKButton_Click(object sender, RoutedEventArgs e)
        {
            this.DialogResult = true;
        }

        private void CancelButton_Click(object sender, RoutedEventArgs e)
        {
            this.DialogResult = false;
        }
    }
}

