using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Impl;

namespace FireWorkflow.Net.Engine.Kernelextensions
{
    public class StartNodeInstanceExtension : SynchronizerInstanceExtension
    {

        public override String getExtentionPointName()
        {
            // TODO Auto-generated method stub
            return StartNodeInstance.Extension_Point_NodeInstanceEventListener;
        }

        /* (non-Javadoc)
         * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionTargetName()
         */
        public override String getExtentionTargetName()
        {
            // TODO Auto-generated method stub
            return StartNodeInstance.Extension_Target_Name;
        }

        public override void onNodeInstanceEventFired(NodeInstanceEvent e)
        {
            //        System.out.println("==Inside StartNode Extension....");
        }
    }
}
