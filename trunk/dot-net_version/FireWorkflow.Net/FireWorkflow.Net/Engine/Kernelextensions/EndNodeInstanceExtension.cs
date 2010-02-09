using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Impl;

namespace FireWorkflow.Net.Engine.Kernelextensions
{
    public class EndNodeInstanceExtension : SynchronizerInstanceExtension
    {

        public override String getExtentionPointName()
        {
            // TODO Auto-generated method stub
            return EndNodeInstance.Extension_Point_NodeInstanceEventListener;
        }

        /* (non-Javadoc)
         * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionTargetName()
         */
        public override String getExtentionTargetName()
        {
            // TODO Auto-generated method stub
            return EndNodeInstance.Extension_Target_Name;
        }

        public override void onNodeInstanceEventFired(NodeInstanceEvent e)
        {

            if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_COMPLETED)
            {
                // 执行ProcessInstance的complete操作

                IToken tk = e.getToken();

                EndNodeInstance syncInst = (EndNodeInstance)e.getSource();
                IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
                persistenceService.deleteTokensForNode(e.getToken().getProcessInstanceId(), syncInst.getSynchronizer().getId());
                ProcessInstance currentProcessInstance = (ProcessInstance)tk.getProcessInstance();
                currentProcessInstance.complete();
            }
        }
    }
}
