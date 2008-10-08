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
package org.fireflow.engine.condition;

import java.util.Map;

import org.fireflow.kenel.condition.IConditionResolver;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.JexlContext;
/**
 * @author chennieyun
 *
 */
public class ConditionResolver implements IConditionResolver {

	/* (non-Javadoc)
	 * @see org.fireflow.kenel.condition.IConditionResolver#resolveBooleanExpression(java.lang.String)
	 */
	public boolean resolveBooleanExpression(Map vars,String elExpression) {
		// TODO Auto-generated method stub
		try{
			Expression expression = ExpressionFactory.createExpression(elExpression);
			JexlContext jexlCtx = JexlHelper.createContext();
			jexlCtx.setVars(vars);
			Object obj = expression.evaluate(jexlCtx);
			return (Boolean)obj;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}

}
