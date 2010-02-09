using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>同步器</summary>
    public class Synchronizer : Node
    {
        /// <summary>输入转移的列表</summary>
        List<Transition> enteringTransitions = new List<Transition>();

        /// <summary>输出转移的列表</summary>
        List<Transition> leavingTransitions = new List<Transition>();

        /// <summary>输入循环的列表</summary>
        List<Loop> enteringLoops = new List<Loop>();

        /// <summary>输出循环的列表</summary>
        List<Loop> leavingLoops = new List<Loop>();

        public Synchronizer()
        {
        }

        public Synchronizer(WorkflowProcess workflowProcess, String name)
            : base(workflowProcess, name)
        {
            // TODO Auto-generated constructor stub
        }

        /// <summary>返回输入Transition集合</summary>
        public virtual List<Transition> getEnteringTransitions()
        {
            return enteringTransitions;
        }

        /// <summary>返回输出transition集合</summary>
        public virtual List<Transition> getLeavingTransitions()
        {
            return leavingTransitions;
        }

        /// <summary>返回输入Loop集合</summary>
        public List<Loop> getEnteringLoops()
        {
            return this.enteringLoops;
        }

        /// <summary>返回输出Loop集合</summary>
        public List<Loop> getLeavingLoops()
        {
            return this.leavingLoops;
        }
    }
}
