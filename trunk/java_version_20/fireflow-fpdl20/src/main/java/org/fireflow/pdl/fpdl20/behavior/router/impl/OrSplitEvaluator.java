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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl20.behavior.router.SplitEvaluator;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.Transition;
import org.fireflow.pvm.kernel.Token;

/**
 * 仅有一个满足条件的输出
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class OrSplitEvaluator implements SplitEvaluator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl20.behavior.router.SplitEvaluator#
	 * determineNextTransitions(org.fireflow.engine.WorkflowSession,
	 * org.fireflow.pvm.kernel.Token, org.fireflow.pdl.fpdl20.process.Node)
	 */
	public List<String> determineNextTransitions(WorkflowSession session,
			Token token4Node, Node node) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();

		Map<String, Object> fireflowVariableContext = ScriptEngineHelper
				.fulfillScriptContext(session, ctx,
						session.getCurrentProcessInstance(),
						session.getCurrentActivityInstance());

		List<String> result = new ArrayList<String>();

		List<Transition> leavingTransitions = node.getLeavingTransitions();
		Transition defaultTransition = null;
		if (leavingTransitions != null) {
			for (Transition transition : leavingTransitions) {
				if (transition.isDefault()) {
					defaultTransition = transition;
					continue;
				}
				boolean b = true;

				Expression expression = transition.getCondition();
				if (expression != null && expression.getBody() != null
						&& !expression.getBody().trim().equals("")) {

					Object obj = ScriptEngineHelper.evaluateExpression(ctx,
							expression, fireflowVariableContext);
					if (obj instanceof Boolean) {
						b = ((Boolean) obj).booleanValue();
					}

				}

				if (b) {
					result.add(transition.getId());
					break;//找到任何一个满足条件的转移便退出循环。
				}
			}
		}

		if (result.size() == 0) {
			if (defaultTransition != null) {
				result.add(defaultTransition.getId());
			}
		}
		return result;
	}

}
