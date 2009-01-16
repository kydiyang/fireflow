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

import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.NodeInstanceEvent;
import org.fireflow.kenel.impl.EndNodeInstance;

/**
 * @author chennieyun
 *
 */
public class EndNodeInstanceExtension extends SynchronizerInstanceExtension {
  
    public String getExtentionPointName() {
        // TODO Auto-generated method stub
        return EndNodeInstance.Extension_Point_NodeInstanceEventListener;
    }

    /* (non-Javadoc)
     * @see org.fireflow.kenel.plugin.IKenelExtension#getExtentionTargetName()
     */
    public String getExtentionTargetName() {
        // TODO Auto-generated method stub
        return EndNodeInstance.Extension_Target_Name;
    }

    public void onNodeInstanceEventFired(NodeInstanceEvent e)
            throws KenelException {
        super.onNodeInstanceEventFired(e);
        System.out.println("=====Inside EndNodeInstanceExtension.onNodeInstanceEventFired()::e.getEventType is "+e.getEventType());
        if (e.getEventType() == NodeInstanceEvent.NODEINSTANCE_COMPLETED) {
            // 执行ProcessInstance的complete操作
            
            IToken tk = e.getToken();
            ProcessInstance currentProcessInstance = (ProcessInstance) tk.getProcessInstance();
            currentProcessInstance.complete();
        }
    }
}
