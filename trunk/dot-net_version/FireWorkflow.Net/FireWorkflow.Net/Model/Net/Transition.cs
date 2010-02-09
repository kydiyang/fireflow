using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>流程转移</summary>
    public class Transition : Edge
    {
        //        String fromSn = null;
        //        String toSn = null;
        public Transition()
        {
        }

        public Transition(WorkflowProcess workflowProcess, String name)
            : base(workflowProcess, name)
        {
        }

        public Transition(WorkflowProcess workflowProcess, String name, Node fromNode, Node toNode)
            : base(workflowProcess, name)
        {
            this.fromNode = fromNode;
            this.toNode = toNode;
        }
        //	public String getFromNodeId() {
        //		return fromNodeId;
        //	}
        //
        //	public void setFromNodeId(String fromNodeId) {
        //		this.fromNodeId = fromNodeId;
        //	}
        //
        //	public String getToNodeId() {
        //		return toNodeId;
        //	}
        //
        //	public void setToNodeId(String toNodeId) {
        //		this.toNodeId = toNodeId;
        //	}


        //    public String getFromSn() {
        //        return fromSn;
        //    }
        //
        //    public void setFromSn(String fromSn) {
        //        this.fromSn = fromSn;
        //    }
        //
        //    public String getToSn() {
        //        return toSn;
        //    }
        //
        //    public void setToSn(String toSn) {
        //        this.toSn = toSn;
        //    }

    }
}
