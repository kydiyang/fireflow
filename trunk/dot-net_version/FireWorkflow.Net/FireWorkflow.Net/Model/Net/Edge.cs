using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Model.Net
{
    /// <summary>工作流网的边。</summary>
    public class Edge : AbstractWFElement
    {

        /// <summary>
        /// 转移(或者循环)的源节点。
        /// 转移的源节点可以是StartNode、 Activity或者Synchronizer。
        /// 循环的源节点必须是Synchronizer或者EndNode，同时循环的目标节点必须是循环源节点的前驱。
        /// </summary>
        protected Node fromNode = null;

        /// <summary>
        /// 转移(或者循环)的目标节点。
        /// 转移的终止目标可以是EndNode、 Activity或者Synchronizer。
        /// 循环的目标节点必须是Synchronizer或者StartNode。
        /// </summary>
        protected Node toNode = null;

        /// <summary>转移（或者循环）的启动条件</summary>

        protected String condition = null;

        public Edge()
        {

        }

        public Edge(WorkflowProcess workflowProcess, String name)
            : base(workflowProcess, name)
        {
        }


        /// <summary>返回转移(或者循环)的启动条件，转移（循环）启动条件是一个EL表达式</summary>
        public String getCondition()
        {
            return condition;
        }

        /// <summary>设置转移(或者循环)条件</summary>
        public void setCondition(String condition)
        {
            this.condition = condition;
        }


        /// <summary>返回转移(或者循环)的源节点</summary>
        public Node getFromNode()
        {
            return fromNode;
        }

        public void setFromNode(Node fromNode)
        {
            this.fromNode = fromNode;
        }


        /// <summary>返回转移(或者循环)的目标节点</summary>
        public Node getToNode()
        {
            return toNode;
        }

        public void setToNode(Node toNode)
        {
            this.toNode = toNode;
        }
    }
}
