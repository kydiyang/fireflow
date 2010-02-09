using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>工作流网的节点。</summary>
    public class Node : AbstractWFElement
    {
        public Node()
        {
        }

        public Node(WorkflowProcess workflowProcess, String name) :
            base(workflowProcess, name)
        {
            // TODO Auto-generated constructor stub
        }
    }
}
