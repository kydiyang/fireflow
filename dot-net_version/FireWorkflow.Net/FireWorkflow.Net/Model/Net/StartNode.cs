using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>开始节点</summary>
    public class StartNode : Synchronizer
    {
        public const String name = "START_NODE";

        public StartNode()
        {
        }

        public StartNode(WorkflowProcess workflowProcess)
            : base(workflowProcess, name)
        {
            // TODO Auto-generated constructor stub
        }

        /**
         * 返回null值，表示无输入弧
         */
        public override List<Transition> getEnteringTransitions()
        {
            return null;
        }
    }
}
