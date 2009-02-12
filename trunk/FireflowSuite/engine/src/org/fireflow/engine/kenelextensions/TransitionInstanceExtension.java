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
package org.fireflow.engine.kenelextensions;

import java.util.Map;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.condition.ConditionConstant;
import org.fireflow.kenel.ITransitionInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.engine.condition.IConditionResolver;
import org.fireflow.kenel.event.ITransitionInstanceEventListener;
import org.fireflow.kenel.event.TransitionInstanceEvent;
import org.fireflow.kenel.impl.TransitionInstance;
import org.fireflow.kenel.plugin.IKenelExtension;

/**
 *
 * @author chennieyun
 */
public class TransitionInstanceExtension implements IKenelExtension,
        ITransitionInstanceEventListener, IRuntimeContextAware {

    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }

    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }

    /**
     * 执行分支判断策略，即设置token的alive属性 首先，如果this.alive==false,则所有的token的Alive属性皆为false
     * 然后，如果在nexttransitionList中有值，则直接执行列表中的tansition
     * 否则，通过计算Transition的表达式来确定下一个transition,
     * 
     * @param transInst
     * @return
     */
    private boolean determineTheAliveOfToken(Map vars, String condition) {
//        System.out.println("Inside SynchronizerInstance.DeterminTheAliveOfToken():: joinPoint.getAlive =" + joinPoint.getAlive());
//        if (!joinPoint.getAlive()) {
//            return false;
//        }
//        Set<String> nextTransitionInstanceNames = joinPoint.getAppointedTransitionNames();
//        if (nextTransitionInstanceNames.size() > 0) {
//            Iterator nextTransNamesIterator = nextTransitionInstanceNames.iterator();
//            while (nextTransNamesIterator.hasNext()) {
//                String transName = (String) nextTransNamesIterator.next();
//                if (transName.equals(transInst.getTransition().getName())) {
//                    return true;
//                }
//            }
//        }
//
//        if (transInst.getTransition().getCondition() == null || transInst.getTransition().getCondition().trim().equals("")) {
//            return true;
//        }

        // TODO通过计算transition上的表达式来确定alive的值

        IConditionResolver elResolver = this.rtCtx.getConditionResolver();
        Boolean b = elResolver.resolveBooleanExpression(vars, condition);

        return b;
    }

    public String getExtentionTargetName() {
        return TransitionInstance.Extension_Target_Name;
    }

    public String getExtentionPointName() {
        return TransitionInstance.Extension_Point_TransitionInstanceEventListener;
    }

    public void onTransitionInstanceEventFired(TransitionInstanceEvent e) throws KenelException {
        if (e.getEventType() == TransitionInstanceEvent.ON_TAKING_THE_TOKEN) {
            if (!e.getToken().isAlive()) {
                return;//如果token是dead状态，表明synchronizer的joinpoint是dead状态，不需要重新计算。
            //计算token的alive值
            }
            ITransitionInstance transInst = (ITransitionInstance) e.getSource();
            String condition = transInst.getTransition().getCondition();

            //1、如果没有转移条件，默认为true
            if (condition == null || condition.trim().equals("")) {
                e.getToken().setAlive(true);
                return;
            }
            //2、default类型的不需要计算其alive值，该值由synchronizer决定
            if (condition.trim().equals(ConditionConstant.DEFAULT)) {
                return;
            }

            //3、计算EL表达式
            boolean alive = determineTheAliveOfToken(e.getToken().getProcessInstance().getProcessInstanceVariables(), condition);
            e.getToken().setAlive(alive);
        }
    }
}
