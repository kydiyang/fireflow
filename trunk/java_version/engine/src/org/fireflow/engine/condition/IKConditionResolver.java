package org.fireflow.engine.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.datameta.Variable;

public class IKConditionResolver implements IConditionResolver {

	public boolean resolveBooleanExpression(Map vars, String elExpression)
			throws Exception {
		System.out.println("-----====------IK Expression---Expression is "+elExpression);
		List<Variable> variables = new ArrayList<Variable>();
		Object[] keys = vars.keySet().toArray();
		for (int i=0;keys!=null && i<keys.length;i++){
			Object key =keys[i];
			variables.add(Variable.createVariable(key.toString(), vars.get(key)));
		}

		Object result = ExpressionEvaluator.evaluate(elExpression,variables);
		
		Boolean b = (Boolean)result;
		return b.booleanValue();
	}

}
