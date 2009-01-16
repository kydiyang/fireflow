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
package org.fireflow.kenel.impl;

import java.util.ArrayList;
import java.util.List;
import org.fireflow.kenel.INodeInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.ITransitionInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.event.ITransitionInstanceEventListener;
import org.fireflow.kenel.event.TransitionInstanceEvent;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.kenel.plugin.IPlugable;
import org.fireflow.model.net.Transition;

/**
 * @author chennieyun
 *
 */
public class TransitionInstance implements ITransitionInstance, IPlugable {

    public transient static final String Extension_Target_Name = "org.fireflow.kenel.TransitionInstance";
    public transient static List<String> Extension_Point_Names = new ArrayList<String>();
    public transient static final String Extension_Point_TransitionInstanceEventListener = "TransitionInstanceEventListener";

    static {
        Extension_Point_Names.add(Extension_Point_TransitionInstanceEventListener);
    }
    private List<ITransitionInstanceEventListener> eventListeners = new ArrayList<ITransitionInstanceEventListener>();
    private INodeInstance leavingNodeInstance = null;
    private INodeInstance enteringNodeInstance = null;
    private transient Transition transition = null;
    private int weight = 0;

    public TransitionInstance(Transition t) {
        transition = t;
    }

    public String getId() {
        return this.transition.getId();
    }
//	private int weight = 0;
    public int getWeight() {
        if (weight == 0) {
            if (enteringNodeInstance instanceof StartNodeInstance) {
                return 1;
            } else if (leavingNodeInstance instanceof EndNodeInstance) {
                return 1;
            } else if (leavingNodeInstance instanceof ActivityInstance) {
                SynchronizerInstance synchronizerInstance = (SynchronizerInstance) enteringNodeInstance;
                int weight = synchronizerInstance.getVolume() / enteringNodeInstance.getLeavingTransitionInstances().size();
                return weight;

            } else if (leavingNodeInstance instanceof SynchronizerInstance) {
                SynchronizerInstance synchronizerInstance = (SynchronizerInstance) leavingNodeInstance;
                int weight = synchronizerInstance.getVolume() / leavingNodeInstance.getEnteringTransitionInstances().size();
                return weight;
            }
        }

        return weight;
    }
//	public void setWeight(int i){
//		this.weight = i;
//	}
    public INodeInstance getLeavingNodeInstance() {
        return leavingNodeInstance;
    }

    public void setLeavingNodeInstance(INodeInstance nodeInst) {
        this.leavingNodeInstance = nodeInst;
    }

    public INodeInstance getEnteringNodeInstance() {
        return enteringNodeInstance;
    }

    public void setEnteringNodeInstance(INodeInstance nodeInst) {
        this.enteringNodeInstance = nodeInst;
    }

    /**
     * 接受一个token，并移交给下一个节点
     */
    public boolean take(IToken token) throws KenelException {
        TransitionInstanceEvent e = new TransitionInstanceEvent(this);
        e.setToken(token);
        e.setEventType(TransitionInstanceEvent.ON_TAKING_THE_TOKEN);
        System.out.println("====Inside TransitionInstance::listeners is null?"+this.eventListeners+";   sizi is "+this.eventListeners.size());
        //在监听器中决定token的alive属性
        for (int i = 0; this.eventListeners != null && i < this.eventListeners.size(); i++) {
            ITransitionInstanceEventListener listener =  this.eventListeners.get(i);
            listener.onTransitionInstanceEventFired(e);
        }
        //然后将token转移给下一个节点
        INodeInstance nodeInst = this.getLeavingNodeInstance();
        token.setValue(this.getWeight());
        boolean alive = token.isAlive();
        
        nodeInst.fire(token);
        
        return alive;
    }

    public Transition getTransition() {
        return this.transition;
    }

    public String getExtensionTargetName() {
        return this.Extension_Target_Name;
    }

    public List<String> getExtensionPointNames() {
        return this.Extension_Point_Names;
    }

    public void registExtension(IKenelExtension extension) throws KenelException {
        if (!Extension_Target_Name.equals(extension.getExtentionTargetName())) {
            return;
//			throw new KenelException("Error:When construct the TansitionInstance,the Extension_Target_Name is mismatching");
        }
        if (Extension_Point_TransitionInstanceEventListener.equals(extension.getExtentionPointName())) {
            if (extension instanceof ITransitionInstanceEventListener) {
                this.eventListeners.add((ITransitionInstanceEventListener) extension);
            } else {
                throw new KenelException("Error:When construct the TransitionInstance,the extension MUST be a instance of ITransitionInstanceEventListener");
            }
        }
    }
}
