/**
 * Copyright 2004-2008 陈乜云（非也,Chen Nieyun）
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
package org.fireflow.kenel.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.fireflow.kenel.impl.AbstractNodeInstance;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.kenel.IActivityInstance;
import org.fireflow.kenel.INodeInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.ITransitionInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.kenel.event.NodeInstanceEvent;
//import org.fireflow.kenel.event.NodeInstanceEventType;

import org.fireflow.model.net.Activity;


/**
 * @author chennieyun
 * 
 */
public class ActivityInstance extends AbstractNodeInstance implements
		IActivityInstance {
	public transient static final Log log = LogFactory.getLog(ActivityInstance.class);
	
	public transient static final String Extension_Target_Name = "org.fireflow.kenel.ActivityInstance";
	public transient static List<String> Extension_Point_Names = new ArrayList<String>();
	public transient static final String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";
	static{
		Extension_Point_Names.add(Extension_Point_NodeInstanceEventListener);
	}
	private transient Activity activity = null;

	public ActivityInstance(Activity a) {
		activity = a;	
	}

	public String getId(){
		return activity.getId();
	}
	
	public void fire(IToken tk)
			throws KenelException {
		log.debug("The weight of the Entering TransitionInstance is "+tk.getValue());
		IToken token  = tk;
		token.setNodeId(this.getActivity().getId());

		//触发TokenEntered事件
		NodeInstanceEvent event1 = new NodeInstanceEvent(this);
		event1.setToken(tk);
		event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
		fireNodeLeavingEvent(event1);
		
		if(token.isAlive()){
			NodeInstanceEvent event = new NodeInstanceEvent(this);
			event.setToken(token);
			event.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
			fireNodeEnteredEvent(event);
			
			//如果没有task,即该activity是一个dummy activity，则直接complete
			if (this.getActivity().getTasks().size()==0){
				this.complete(token);
			}
		}
		else{
			this.complete(token);
		}
	}

	public void complete(IToken token) throws KenelException {
		if (token.isAlive()){
			NodeInstanceEvent event = new NodeInstanceEvent(this);
			event.setToken(token);
			event.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
			fireNodeLeavingEvent(event);
		}
		
		NodeInstanceEvent event2 = new NodeInstanceEvent(this);
		event2.setToken(token);
		event2.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
		fireNodeLeavingEvent(event2);		

		//按照定义，activity有且只有一个输出弧，所以此处只进行一次循环。
		for (int i = 0; leavingTransitionInstances != null
				&& i < leavingTransitionInstances.size(); i++) {
			ITransitionInstance transInst = leavingTransitionInstances
					.get(i);
			transInst.take(token);
		}
	}
	
	public String getExtensionTargetName(){
		return Extension_Target_Name;
	}
	public List<String> getExtensionPointNames(){
		return Extension_Point_Names;
	}
	
	//TODO extesion是单态还是多实例？单态应该效率高一些。
	public void registExtension(IKenelExtension extension)throws KenelException{
		if (!Extension_Target_Name.equals(extension.getExtentionTargetName())){
			throw new KenelException("Error:When construct the ActivityInstance,the Extension_Target_Name is mismatching");
		}
		if (Extension_Point_NodeInstanceEventListener.equals(extension.getExtentionPointName())){
			if (extension instanceof INodeInstanceEventListener){
				this.eventListeners.add((INodeInstanceEventListener)extension);
			}
			else{
				throw new  KenelException("Error:When construct the ActivityInstance,the extension MUST be a instance of INodeInstanceEventListener");
			}
		}
	}
	
	public String toString(){
		return "ActivityInstance_4_["+activity.getName()+"]";
	}
	
	public Activity getActivity(){
		return activity;
	}
}
