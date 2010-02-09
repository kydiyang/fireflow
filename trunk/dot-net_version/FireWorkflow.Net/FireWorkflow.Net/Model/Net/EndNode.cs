using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>结束节点</summary>
    public class EndNode : Synchronizer
    {
        public EndNode()
        {
        }

        public EndNode(WorkflowProcess workflowProcess, String name)
            : base(workflowProcess, name)
        {
            // TODO Auto-generated constructor stub
        }

        /// <summary>返回null。表示无输出弧。</summary>
        public override List<Transition> getLeavingTransitions()
        {
            return null;
        }
    }
}
