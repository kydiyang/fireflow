using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Definition;
using FireWorkflow.Net.Kernel.Impl;
using FireWorkflow.Net.Kernel.Plugin;


namespace FireWorkflow.Net.Kernel
{
    public class KernelManager : IRuntimeContextAware
    {

        //    private HashMap<String,Object> wfElementInstanceMap = new HashMap<String,Object>();
        private Dictionary<String, INetInstance> netInstanceMap = new Dictionary<String, INetInstance>();
        private Dictionary<String, List<IKernelExtension>> kernelExtensions = new Dictionary<String, List<IKernelExtension>>();
        protected RuntimeContext rtCtx = null;

        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.rtCtx = ctx;
            if (this.kernelExtensions != null && this.kernelExtensions.Count > 0)
            {
                foreach (List<IKernelExtension> item in this.kernelExtensions.Values)
                {
                    for (int i = 0; item != null && i < item.Count; i++)
                    {
                        IKernelExtension extension = (IKernelExtension)item[i];
                        ((IRuntimeContextAware)extension).setRuntimeContext(rtCtx);
                    }
                }

            }
        }

        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
        }

        public INetInstance getNetInstance(String processId, Int32? version)
        {
            INetInstance netInstance = this.netInstanceMap[processId + "_V_" + version];
            if (netInstance == null)
            {

                WorkflowDefinition def = rtCtx.getDefinitionService().getWorkflowDefinitionByProcessIdAndVersionNumber(processId, version);
                netInstance = this.createNetInstance(def);
            }
            return netInstance;
        }

        public void clearAllNetInstance()
        {
            netInstanceMap.Clear();
        }

        public INetInstance createNetInstance(WorkflowDefinition workflowDef)
        {
            if (workflowDef == null) return null;
            WorkflowProcess workflowProcess = null;
            workflowProcess = workflowDef.getWorkflowProcess();

            //		Map nodeInstanceMap = new HashMap();
            if (workflowProcess == null)
            {
                throw new KernelException(null, null, "The WorkflowProcess property of WorkflowDefinition[processId=" + workflowDef.getProcessId() + "] is null. ");
            }
            String validateMsg = workflowProcess.validate();
            if (validateMsg != null)
            {
                throw new KernelException(null, null, validateMsg);
            }
            NetInstance netInstance = new NetInstance(workflowProcess, kernelExtensions);
            //        netInstance.setWorkflowProcess(workflowProcess);
            netInstance.setVersion(workflowDef.getVersion());

            netInstanceMap.Add(workflowDef.getProcessId() + "_V_" + workflowDef.getVersion(), netInstance);

            //		netInstance.setRtCxt(new RuntimeContext());
            return netInstance;
        }

        public Dictionary<String, List<IKernelExtension>> getKernelExtensions()
        {
            return kernelExtensions;
        }

        public void setKernelExtensions(Dictionary<String, List<IKernelExtension>> arg0)
        {
            this.kernelExtensions = arg0;
        }
    }
}
