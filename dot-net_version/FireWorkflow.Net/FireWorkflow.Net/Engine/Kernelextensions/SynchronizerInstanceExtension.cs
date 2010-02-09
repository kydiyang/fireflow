using System;
using System.Collections.Generic;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Kernel.Impl;



namespace FireWorkflow.Net.Engine.Kernelextensions
{
    public class SynchronizerInstanceExtension : IKernelExtension,
        INodeInstanceEventListener, IRuntimeContextAware
    {

        protected RuntimeContext rtCtx = null;

        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.rtCtx = ctx;
        }

        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
        }

        /* (non-Javadoc)
         * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionPointName()
         */
        public virtual String getExtentionPointName()
        {
            // TODO Auto-generated method stub
            return SynchronizerInstance.Extension_Point_NodeInstanceEventListener;
        }

        /* (non-Javadoc)
         * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionTargetName()
         */
        public virtual String getExtentionTargetName()
        {
            // TODO Auto-generated method stub
            return SynchronizerInstance.Extension_Target_Name;
        }

        /* (non-Javadoc)
         * @see org.fireflow.kenel.event.INodeInstanceEventListener#onNodeInstanceEventFired(org.fireflow.kenel.event.NodeInstanceEvent)
         */
        public virtual void onNodeInstanceEventFired(NodeInstanceEvent e)
        {
            // TODO Auto-generated method stub
            //此处注释掉，由ProcessInstance.createJoinPoint()决定是否保存，20090309
            //        if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED) {
            //            IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
            //            persistenceService.saveOrUpdateToken(e.getToken());
            //        }

            if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_LEAVING)
            {
                ISynchronizerInstance syncInst = (ISynchronizerInstance)e.getSource();
                IPersistenceService persistenceService = this.rtCtx.getPersistenceService();

                persistenceService.deleteTokensForNode(e.getToken().getProcessInstanceId(), syncInst.getSynchronizer().getId());

            }
        }
    }
}
