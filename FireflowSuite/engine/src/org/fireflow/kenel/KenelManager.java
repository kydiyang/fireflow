/**
 * Copyright 2007-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.kenel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.kenel.impl.NetInstance;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.model.WorkflowProcess;

/**
 * @author chennieyun
 *
 */
public class KenelManager implements IRuntimeContextAware {

//    private HashMap<String,Object> wfElementInstanceMap = new HashMap<String,Object>();
    private HashMap<String, INetInstance> netInstanceMap = new HashMap<String, INetInstance>();
    private Map<String, List<IKenelExtension>> kenelExtensions = new HashMap<String, List<IKenelExtension>>();
    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
        if (this.kenelExtensions != null && this.kenelExtensions.size()>0) {
            Iterator values = this.kenelExtensions.values().iterator();
            while (values != null && values.hasNext()) {
                List extensionList = (List) values.next();
                for (int i = 0; extensionList != null && i < extensionList.size(); i++) {
                    IKenelExtension extension = (IKenelExtension) extensionList.get(i);
                    ((IRuntimeContextAware) extension).setRuntimeContext(rtCtx);
                }
            }
        }
    }

    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }

    public INetInstance getNetInstance(String processId, Integer version) throws KenelException {
        INetInstance netInstance = this.netInstanceMap.get(processId + "_V_" + version);
        if (netInstance == null) {

            WorkflowDefinition def = rtCtx.getDefinitionService().getWorkflowDefinitionByProcessIdAndVersion(processId, version);
            netInstance = this.createNetInstance(def);
        }
        return netInstance;
    }

    public void clearAllNetInstance() {
        netInstanceMap.clear();
    }

    public INetInstance createNetInstance(WorkflowDefinition workflowDef) throws KenelException {

        WorkflowProcess workflowProcess = null;
        workflowProcess = workflowDef.getWorkflowProcess();

//		Map nodeInstanceMap = new HashMap();
        if (workflowProcess == null || workflowProcess.validate() != null) {
            return null;
        }
        NetInstance netInstance = new NetInstance(workflowProcess, kenelExtensions);
//        netInstance.setWorkflowProcess(workflowProcess);
        netInstance.setVersion(workflowDef.getVersion());

        netInstanceMap.put(workflowDef.getProcessId() + "_V_" + workflowDef.getVersion(), netInstance);

//		netInstance.setRtCxt(new RuntimeContext());
        return netInstance;
    }

    public Map<String, List<IKenelExtension>> getKenelExtensions() {
        return kenelExtensions;
    }

    public void setKenelExtensions(Map<String, List<IKenelExtension>> kenelExtensions) {
        this.kenelExtensions = kenelExtensions;
    }
}
