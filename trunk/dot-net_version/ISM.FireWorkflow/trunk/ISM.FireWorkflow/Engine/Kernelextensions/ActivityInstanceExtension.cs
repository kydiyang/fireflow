using System;
using System.Collections.Generic;

using System.Text;
using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Engine.Persistence;
using ISM.FireWorkflow.Kernel;
using ISM.FireWorkflow.Kernel.Event;
using ISM.FireWorkflow.Kernel.Impl;
using ISM.FireWorkflow.Kernel.Plugin;

namespace ISM.FireWorkflow.Engine.Kernelextensions
{
    //import org.fireflow.kenel.event.NodeInstanceEventType;
    public class ActivityInstanceExtension : IKernelExtension,
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
        public String getExtentionPointName()
        {
            // TODO Auto-generated method stub
            return ActivityInstance.Extension_Point_NodeInstanceEventListener;
        }

        public String getExtentionTargetName()
        {
            // TODO Auto-generated method stub
            return ActivityInstance.Extension_Target_Name;
        }


        public void onNodeInstanceEventFired(NodeInstanceEvent e)
        {
            // TODO Auto-generated method stub
            if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_FIRED)
            {

                IPersistenceService persistenceService = rtCtx.getPersistenceService();
                persistenceService.saveOrUpdateToken(e.getToken());
                rtCtx.getTaskInstanceManager().createTaskInstances(e.getToken(), (IActivityInstance)e.getSource());
            }
            else if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_COMPLETED)
            {
                //			RuntimeContext.getInstance()
                //			.getTaskInstanceManager()
                //			.archiveTaskInstances((IActivityInstance)e.getSource());
            }
        }
    }
}
