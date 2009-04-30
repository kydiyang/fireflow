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
package org.fireflow.engine.kernelextensions;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
/**
 * @author chennieyun
 * 
 */
import org.fireflow.kernel.IActivityInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.event.INodeInstanceEventListener;
import org.fireflow.kernel.event.NodeInstanceEvent;
import org.fireflow.kernel.plugin.IKernelExtension;
import org.fireflow.kernel.impl.ActivityInstance;
//import org.fireflow.kenel.event.NodeInstanceEventType;
public class ActivityInstanceExtension implements IKernelExtension,
        INodeInstanceEventListener,IRuntimeContextAware {
    protected RuntimeContext rtCtx = null;
    
    public void setRuntimeContext(RuntimeContext ctx){
        this.rtCtx = ctx;
    }    
    public RuntimeContext getRuntimeContext(){
        return this.rtCtx;
    }    
    public String getExtentionPointName() {
        // TODO Auto-generated method stub
        return ActivityInstance.Extension_Point_NodeInstanceEventListener;
    }

    public String getExtentionTargetName() {
        // TODO Auto-generated method stub
        return ActivityInstance.Extension_Target_Name;
    }

    
    public void onNodeInstanceEventFired(NodeInstanceEvent e)
            throws KernelException {
        // TODO Auto-generated method stub
        if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_FIRED) {
            
            IPersistenceService persistenceService = rtCtx.getPersistenceService();
            persistenceService.saveOrUpdateToken(e.getToken());
            rtCtx.getTaskInstanceManager().createTaskInstances(e.getToken(), (IActivityInstance) e.getSource());
        } else if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_COMPLETED) {
//			RuntimeContext.getInstance()
//			.getTaskInstanceManager()
//			.archiveTaskInstances((IActivityInstance)e.getSource());
        }
    }
}
