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
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.condition.Constant;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.ISynchronizerInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.ITransitionInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.condition.IConditionResolver;
import org.fireflow.kenel.event.INodeInstanceEventListener;
import org.fireflow.kenel.event.NodeInstanceEvent;
//import org.fireflow.kenel.event.NodeInstanceEventType;
import org.fireflow.kenel.plugin.IKenelExtension;
import org.fireflow.model.net.Synchronizer;

/**
 * @author chennieyun
 * 
 */
public class SynchronizerInstance extends AbstractNodeInstance implements
        ISynchronizerInstance {

    public transient static final Log log = LogFactory.getLog(SynchronizerInstance.class);
    public transient static final String Extension_Target_Name = "org.fireflow.kenel.SynchronizerInstance";
    public transient static List<String> Extension_Point_Names = new ArrayList<String>();
    public transient static final String Extension_Point_NodeInstanceEventListener = "NodeInstanceEventListener";

    static {
        Extension_Point_Names.add(Extension_Point_NodeInstanceEventListener);
    }
    private int volume = 0;// 即节点的容量
    // private int value = 0;
    private transient Synchronizer synchronizer = null;

    // private boolean alive = false;
    // private List<String> nextTransitionInstanceNames = new
    // ArrayList<String>();
    public String getId() {
        return this.synchronizer.getId();
    }

    public SynchronizerInstance(Synchronizer s) {
        synchronizer = s;
        int a = synchronizer.getEnteringTransitions().size();
        int b = synchronizer.getLeavingTransitions().size();
        volume = a * b;

//		System.out.println("synchronizer "+synchronizer.getName()+"'s volume is "+volume);
    }

    public void fire(IToken tk) throws KenelException {
        tk.setNodeId(this.getSynchronizer().getId());
        log.debug("The weight of the Entering TransitionInstance is " + tk.getValue());

        IJoinPoint joinPoint = null;
        synchronized (this) {

            joinPoint = ((ProcessInstance) tk.getProcessInstance()).createJoinPoint(this, tk);// JoinPoint由谁生成比较好？
            tk.setAlive(false);// tk的生命周期结束了！

            // 触发TokenEntered事件
            NodeInstanceEvent event1 = new NodeInstanceEvent(this);
            event1.setToken(tk);
            event1.setEventType(NodeInstanceEvent.NODEINSTANCE_TOKEN_ENTERED);
            fireNodeLeavingEvent(event1);

            int value = joinPoint.getValue();

            log.debug("The volume of " + this.toString() + " is " + volume);
            log.debug("The value of " + this.toString() + " is " + value);
            if (value > volume) {
                throw new KenelException("FireFlow引擎内核执行发生异常，同步器实例[" + this.toString() + "]的token数量超过其容量");
            }

            if (value < volume) {// 如果Value小于容量则继续等待其他弧的汇聚。
                return;
            }
        }

        // Synchronize的fire条件应该只与joinPoint的value有关（value==volume），与alive无关
        NodeInstanceEvent event2 = new NodeInstanceEvent(this);
        event2.setToken(tk);
        event2.setEventType(NodeInstanceEvent.NODEINSTANCE_FIRED);
        fireNodeEnteredEvent(event2);

        NodeInstanceEvent event3 = new NodeInstanceEvent(this);
        event3.setToken(tk);
        event3.setEventType(NodeInstanceEvent.NODEINSTANCE_COMPLETED);
        fireNodeLeavingEvent(event3);

        NodeInstanceEvent event4 = new NodeInstanceEvent(this);
        event4.setToken(tk);
        event4.setEventType(NodeInstanceEvent.NODEINSTANCE_LEAVING);
        fireNodeLeavingEvent(event4);

        boolean activiateDefaultCondition = true;
        ITransitionInstance defaultTransInst = null;
        for (int i = 0; leavingTransitionInstances != null && i < leavingTransitionInstances.size(); i++) {
            ITransitionInstance transInst = leavingTransitionInstances.get(i);
            String condition = transInst.getTransition().getCondition();
            if (condition != null && condition.equals(Constant.DEFAULT)) {
                defaultTransInst = transInst;
                continue;
            }

            Token token = new Token(); // 产生新的token
            
            boolean alive = determineTheAliveOfToken(tk.getProcessInstance().getProcessInstanceVariables(), joinPoint, transInst);
            if (alive) {
                activiateDefaultCondition = false;
            }
            token.setAlive(alive);
            // token.setRuntimeContext(tk.getRuntimeContext());
            token.setProcessInstance(tk.getProcessInstance());
            transInst.take(token);
        }
        if (activiateDefaultCondition && defaultTransInst != null) {
            Token token = new Token();
            token.setAlive(true);
            token.setProcessInstance(tk.getProcessInstance());
            defaultTransInst.take(token);
        }
    }

    /**
     * 执行分支判断策略，即设置token的alive属性 首先，如果this.alive==false,则所有的token的Alive属性皆为false
     * 然后，如果在nexttransitionList中有值，则直接执行列表中的tansition
     * 否则，通过计算Transition的表达式来确定下一个transition,
     * 
     * @param transInst
     * @return
     */
    private boolean determineTheAliveOfToken(Map vars, IJoinPoint joinPoint,
            ITransitionInstance transInst) {
        System.out.println("Inside SynchronizerInstance.DeterminTheAliveOfToken():: joinPoint.getAlive =" + joinPoint.getAlive());
        if (!joinPoint.getAlive()) {
            return false;
        }
        Set<String> nextTransitionInstanceNames = joinPoint.getAppointedTransitionNames();
        if (nextTransitionInstanceNames.size() > 0) {
            Iterator nextTransNamesIterator = nextTransitionInstanceNames.iterator();
            while (nextTransNamesIterator.hasNext()) {
                String transName = (String) nextTransNamesIterator.next();
                if (transName.equals(transInst.getTransition().getName())) {
                    return true;
                }
            }
        }

        if (transInst.getTransition().getCondition() == null || transInst.getTransition().getCondition().trim().equals("")) {
            return true;
        }

        // TODO通过计算transition上的表达式来确定alive的值

        IConditionResolver elResolver = RuntimeContext.getInstance().getConditionResolver();
        Boolean b = elResolver.resolveBooleanExpression(vars, transInst.getTransition().getCondition());

        return b;
    }

    public void setVolume(int arg) {
        volume = arg;
    }

    public int getVolume() {
        return volume;
    }

    // public int getValue() {
    // return value;
    // }
    //
    // public void setValue(int tokens) {
    // this.value = tokens;
    // }
    public String getExtensionTargetName() {
        return Extension_Target_Name;
    }

    public List<String> getExtensionPointNames() {
        return Extension_Point_Names;
    }

    // TODO extesion是单态还是多实例？单态应该效率高一些。
    public void registExtension(IKenelExtension extension)
            throws KenelException {
        if (!Extension_Target_Name.equals(extension.getExtentionTargetName())) {
            throw new KenelException(
                    "Error:When construct the ActivityInstance,the Extension_Target_Name is mismatching");
        }
        if (Extension_Point_NodeInstanceEventListener.equals(extension.getExtentionPointName())) {
            if (extension instanceof INodeInstanceEventListener) {
                this.eventListeners.add((INodeInstanceEventListener) extension);
            } else {
                throw new KenelException(
                        "Error:When construct the ActivityInstance,the extension MUST be a instance of INodeInstanceEventListener");
            }
        }
    }

    public String toString() {
        return "SynchronizerInstance_4_[" + synchronizer.getId() + "]";
    }

    public Synchronizer getSynchronizer() {
        return this.synchronizer;
    }
}
