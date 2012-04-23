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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl20.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl20.behavior.router.SplitEvaluator;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Synchronizer;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsNodeBehavior implements WorkflowBehavior {
	protected Map<String,JoinEvaluator> joinEvaluatorRegistry = new HashMap<String,JoinEvaluator>();
	protected Map<String,SplitEvaluator> splitEvaluatorRegistry = new HashMap<String,SplitEvaluator>();

	public CompensationHandler getCompensationHandler(String compensationCode) {
		return null;
	}

	// （2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	// public CancellationHandler getCancellationHandler(){
	// return null;
	// }

	public FaultHandler getFaultHandler(String errorCode) {
		return null;
	}
	
	protected abstract List<String> determineNextTransitions(
			WorkflowSession session, Token token4Node, Node node);

	protected List<PObjectKey> determineNextPObjectKeys(
			WorkflowSession session, Token token, Object workflowElement) {
		String targetActivityId = (String) session
				.removeAttribute(WorkItemManager.TARGET_ACTIVITY_ID);
		if (targetActivityId != null && !targetActivityId.trim().equals("")) {
			List<PObjectKey> nextPObjectKeys = new ArrayList<PObjectKey>();
			PObjectKey pobjKey = new PObjectKey(token.getProcessId(),
					token.getVersion(), token.getProcessType(),
					targetActivityId);
			nextPObjectKeys.add(pobjKey);
			return nextPObjectKeys;
		}

		Node node = (Node) workflowElement;
		List<PObjectKey> nextPObjectKeys = new ArrayList<PObjectKey>();

		List<String> nextTransitionIdList = this.determineNextTransitions(session, token, node);
		for (String transitionId : nextTransitionIdList){
			PObjectKey pobjKey = new PObjectKey(token.getProcessId(),
					token.getVersion(), token.getProcessType(),
					transitionId);
			nextPObjectKeys.add(pobjKey);
		}

		return nextPObjectKeys;
	}

}
