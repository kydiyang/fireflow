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

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.pdl.fpdl20.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Synchronizer;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pvm.kernel.Token;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class AndJoinEvaluator implements JoinEvaluator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl20.behavior.router.JoinEvaluator#canBeFired(org.
	 * fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token,
	 * org.fireflow.pdl.fpdl20.process.Synchronizer)
	 */
	public boolean canBeFired(WorkflowSession session, Token token,
			Synchronizer node) {
		List<Transition> enteringTransitionsList = node
				.getEnteringTransitions();
		// 仅有一条边的情况下，直接返回true
		if (enteringTransitionsList == null
				|| enteringTransitionsList.size() == 0
				|| enteringTransitionsList.size() == 1) {
			return true;
		}

		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(
				PersistenceService.class, FpdlConstants.PROCESS_TYPE);
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();
		
		List<Token> siblings = tokenPersister.findSiblings(token);
		
		if (siblings==null || siblings.size()==0){
			return false;
		}
		
		if (siblings.size()<enteringTransitionsList.size()){
			return false;
		}else{
			return true;//表示汇聚完毕
		}
	}

}
