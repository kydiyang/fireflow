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

import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
/**
 * @author chennieyun
 * 
 */
import org.fireflow.kenel.IActivityInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.kenel.event.NodeInstanceEvent;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.kenel.impl.ActivityInstance;
//import org.fireflow.kenel.event.NodeInstanceEventType;

public class ActivityInstanceExtension implements IKenelExtension,
		INodeInstanceEventListener {

	public String getExtentionPointName() {
		// TODO Auto-generated method stub
		return ActivityInstance.Extension_Point_NodeInstanceEventListener;
	}

	public String getExtentionTargetName() {
		// TODO Auto-generated method stub
		return ActivityInstance.Extension_Target_Name;
	}

	public void onNodeInstanceEventFired(NodeInstanceEvent e)
			throws KenelException {
		// TODO Auto-generated method stub
		if (e.getEventType()==NodeInstanceEvent.NODEINSTANCE_FIRED){
			RuntimeContext ctx = RuntimeContext.getInstance();
			IPersistenceService persistenceService = ctx.getPersistenceService();
			persistenceService.saveToken(e.getToken());
			ctx.getTaskInstanceManager().createTaskInstances(e.getToken(), (IActivityInstance)e.getSource());			
		}else if (e.getEventType()==NodeInstanceEvent.NODEINSTANCE_COMPLETED){
//			RuntimeContext.getInstance()
//			.getTaskInstanceManager()
//			.archiveTaskInstances((IActivityInstance)e.getSource());
		}
	}

}
