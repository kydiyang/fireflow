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
package org.fireflow.engine.kenelextensions;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.kenel.event.NodeInstanceEvent;
//import org.fireflow.kenel.event.NodeInstanceEventType;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.kenel.impl.SynchronizerInstance;

/**
 * @author chennieyun
 *
 */
public class SynchronizerInstanceExtension implements IKenelExtension,
        INodeInstanceEventListener ,IRuntimeContextAware{
    protected RuntimeContext rtCtx = null;
    
    public void setRuntimeContext(RuntimeContext ctx){
        this.rtCtx = ctx;
    }    
    public RuntimeContext getRuntimeContext(){
        return this.rtCtx;
    }    
    
    /* (non-Javadoc)
     * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionPointName()
     */
    public String getExtentionPointName() {
        // TODO Auto-generated method stub
        return SynchronizerInstance.Extension_Point_NodeInstanceEventListener;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionTargetName()
     */
    public String getExtentionTargetName() {
        // TODO Auto-generated method stub
        return SynchronizerInstance.Extension_Target_Name;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kenel.event.INodeInstanceEventListener#onNodeInstanceEventFired(org.fireflow.kenel.event.NodeInstanceEvent)
     */
    public void onNodeInstanceEventFired(NodeInstanceEvent e)
            throws KenelException,EngineException {
        // TODO Auto-generated method stub
        if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED) {
            IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
            persistenceService.saveOrUpdateToken(e.getToken());
        }
    }
}
