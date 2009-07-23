/**
 * Copyright 2007-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.kernel.impl.NetInstance;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.model.WorkflowProcess;

/**
 * @author 非也
 *
 */
public class KernelManager implements IRuntimeContextAware {

//    private HashMap<String,Object> wfElementInstanceMap = new HashMap<String,Object>();
    private HashMap<String, INetInstance> netInstanceMap = new HashMap<String, INetInstance>();
    private Map<String, List<IKernelExtension>> kernelExtensions = new HashMap<String, List<IKernelExtension>>();
    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
        if (this.kernelExtensions != null && this.kernelExtensions.size()>0) {
            Iterator values = this.kernelExtensions.values().iterator();
            while (values != null && values.hasNext()) {
                List extensionList = (List) values.next();
                for (int i = 0; extensionList != null && i < extensionList.size(); i++) {
                    IKernelExtension extension = (IKernelExtension) extensionList.get(i);
                    ((IRuntimeContextAware) extension).setRuntimeContext(rtCtx);
                }
            }
        }
    }

    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }

    public INetInstance getNetInstance(String processId, Integer version) throws KernelException {
        INetInstance netInstance = this.netInstanceMap.get(processId + "_V_" + version);
        if (netInstance == null) {

            WorkflowDefinition def = rtCtx.getDefinitionService().getWorkflowDefinitionByProcessIdAndVersionNumber(processId, version);
            netInstance = this.createNetInstance(def);
        }
        return netInstance;
    }

    public void clearAllNetInstance() {
        netInstanceMap.clear();
    }

    public INetInstance createNetInstance(WorkflowDefinition workflowDef) throws KernelException {
        if (workflowDef==null)return null;
        WorkflowProcess workflowProcess = null;
        workflowProcess = workflowDef.getWorkflowProcess();

//		Map nodeInstanceMap = new HashMap();
        if (workflowProcess == null ){
        	throw new KernelException(null,null,"The WorkflowProcess property of WorkflowDefinition[processId="+workflowDef.getProcessId()+"] is null. ");
        }
        String validateMsg =  workflowProcess.validate();
        if (validateMsg != null){
        	throw new KernelException(null,null,validateMsg);
        }
        NetInstance netInstance = new NetInstance(workflowProcess, kernelExtensions);
//        netInstance.setWorkflowProcess(workflowProcess);
        netInstance.setVersion(workflowDef.getVersion());

        netInstanceMap.put(workflowDef.getProcessId() + "_V_" + workflowDef.getVersion(), netInstance);

//		netInstance.setRtCxt(new RuntimeContext());
        return netInstance;
    }

    public Map<String, List<IKernelExtension>> getKernelExtensions() {
        return kernelExtensions;
    }

    public void setKernelExtensions(Map<String, List<IKernelExtension>> arg0) {
        this.kernelExtensions = arg0;
    }
}
