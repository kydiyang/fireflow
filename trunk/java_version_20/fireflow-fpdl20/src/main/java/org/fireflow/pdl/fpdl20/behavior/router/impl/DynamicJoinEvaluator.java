/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.pdl.fpdl20.behavior.router.impl;

import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.pdl.fpdl20.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Synchronizer;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;

/**
 * 动态判断是否有活动的前驱来确定是否汇聚
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class DynamicJoinEvaluator implements JoinEvaluator {
	public static final String JOIN_DESCRIPTION = "汇聚逻辑：当任意输入Transition到达时，判断是否有活动的前驱结点，如果有则等待汇聚，否则执行后续分支。";
	public String getJoinDescription(){
		return JOIN_DESCRIPTION;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl20.behavior.router.JoinEvaluator#canBeFired()
	 */
	public boolean canBeFired(WorkflowSession session, Token token,
			Synchronizer node) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(
				PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();

		boolean multiEnteringTransitions = false;// 表示是否有多条输入边
		if (node.getEnteringTransitions() != null
				&& node.getEnteringTransitions().size() > 1) {
			multiEnteringTransitions = true;
		}

		// 1、通过elementInstanceId判断是否已经被执行
		if (multiEnteringTransitions) {
			Token tokenFromDB = tokenPersister.find(Token.class, token.getId());
			if (tokenFromDB.getElementInstanceId() != null
					&& !tokenFromDB.getElementInstanceId().trim().equals("")) {
				// 说明已经执行过
				tokenFromDB.setState(TokenState.COMPLETED);
				tokenPersister.saveOrUpdate(tokenFromDB);
				return false;
			}
		}

		// 2、判断汇聚是否完成
		boolean canBeFired = false;
		if (multiEnteringTransitions) {

			if (this.hasAlivePreviousNode(session, token, node)) {
				canBeFired = false;
			} else {
				canBeFired = true;
			}
		} else {// 只有一条输入边的synchronizer直接启动
			canBeFired = true;
		}



		return canBeFired;
	}
	
	/**
	 * 汇聚计算，这是fpdl1.0,和fpdl2.0本质区别的地方。
	 * @param session
	 * @param token
	 * @param thisNode
	 * @return
	 */
	protected boolean hasAlivePreviousNode(WorkflowSession session,Token token,Node thisNode){
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();
		
		List<Transition> enteringTransitions = thisNode.getEnteringTransitions();		
		if (enteringTransitions==null || enteringTransitions.size()==0){
			return false;
		}
		
		for (Transition transition:enteringTransitions){
			if (!transition.isLoop()){//排除循环的情况
				Node fromNode = transition.getFromNode();
				
				int aliveCount = tokenPersister.countAliveToken(token.getProcessInstanceId(), fromNode.getId(),token.getOperationContextName());
				if (aliveCount>0){
					return true;
				}
				
				boolean b = hasAlivePreviousNode(session,token,fromNode);
				
				if (b){
					return true;
				}
			}
		}
		
		return false;
	}
}
