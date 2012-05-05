/**
 * Copyright 2007-2010 非也
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
package org.fireflow.pdl.fpdl20.behavior;

import java.util.List;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.pdl.fpdl20.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl20.behavior.router.impl.DynamicJoinEvaluator;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.EndNode;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Synchronizer;
import org.fireflow.pdl.fpdl20.process.features.Feature;
import org.fireflow.pdl.fpdl20.process.features.endnode.NormalEndFeature;
import org.fireflow.pdl.fpdl20.process.features.endnode.ThrowCompensationFeature;
import org.fireflow.pdl.fpdl20.process.features.endnode.ThrowFaultFeature;
import org.fireflow.pdl.fpdl20.process.features.endnode.ThrowTerminationFeature;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.ContinueDirection;



/**
 * @author 非也
 * @version 2.0
 */
public class EndNodeBehavior extends AbsSynchronizerBehavior {
	public Boolean canBeFired(WorkflowSession session, Token token,
			Synchronizer synchronizer){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE);
		
		String className = DynamicJoinEvaluator.class.getName();
		JoinEvaluator joinEvaluator = this.joinEvaluatorRegistry.get(className);
		if (joinEvaluator==null){
			joinEvaluator = (JoinEvaluator)beanFactory.createBean(className);
			joinEvaluatorRegistry.put(className, joinEvaluator);
		}
		return joinEvaluator.canBeFired(session, token, synchronizer);
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	@Override
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		
		EndNode endNode = (EndNode)workflowElement;
		Feature decorator = endNode.getFeature();
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		
		if (decorator!=null && decorator instanceof ThrowTerminationFeature){
			Token token4ProcessInstance = kernelManager.getParentToken(token);
			kernelManager.fireTerminationEvent(session, token4ProcessInstance,token);
		}else if (decorator!=null && decorator instanceof ThrowFaultFeature){
			ThrowFaultFeature throwExceptionDecorator = (ThrowFaultFeature)decorator;
			Token token4ProcessInstance = kernelManager.getParentToken(token);
			kernelManager.fireFaultEvent(session, token4ProcessInstance,token, throwExceptionDecorator.getErrorCode());
			
		}else if (decorator!=null && decorator instanceof ThrowCompensationFeature){
			ThrowCompensationFeature throwCompensationDecorator = (ThrowCompensationFeature)decorator;
			Token token4ProcessInstance = kernelManager.getParentToken(token);
			kernelManager.fireCompensationEvent(session, token4ProcessInstance,token,throwCompensationDecorator.getCompensationCodes());
		}

		return ContinueDirection.closeMe();
	}

	protected List<String> determineNextTransitions(
			WorkflowSession session, Token token4Node, Node node){
		return null;
	}
}
