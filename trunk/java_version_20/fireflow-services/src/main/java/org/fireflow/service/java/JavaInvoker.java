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
package org.fireflow.service.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.model.binding.ServiceBinding;

/**
 * java bean服务，负责调用java bean；
 * 
 * @author 非也
 * @version 2.0
 */
public class JavaInvoker extends AbsServiceInvoker implements ServiceInvoker {
	private static final Log log = LogFactory.getLog(JavaInvoker.class);

	@Override
	public Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding) throws ServiceInvocationException {

		BeanFactory beanFactory = runtimeContext.getEngineModule(
				BeanFactory.class, activityInstance.getProcessType());

		JavaService javaService = (JavaService) serviceBinding.getService();
		String beanName = javaService.getJavaBeanName();
		String javaClass = javaService.getJavaClassName();
		Object bean = null;
		if (!StringUtils.isEmpty(beanName)) {
			bean = beanFactory.getBean(beanName);
		} else if (!StringUtils.isEmpty(javaClass)) {
			bean = beanFactory.createBean(javaClass);

		}

		if (bean == null) {
			throw new ServiceInvocationException(
					(!StringUtils.isEmpty(beanName) ? ("Service bean NOT found for " + beanName)
							: ("Service bean can NOT be instantiated for " + javaClass)));
		} else {
			return bean;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.invocation.AbsServiceInvoker#getParameterTypes(java
	 * .lang.String, java.lang.Object[])
	 */
	@Override
	protected Class[] getParameterTypes(Class serviceClass, String methodName,
			Object[] params)throws ServiceInvocationException {
		Object[] _params = params;
		if (_params == null) {
			_params = new Object[0];
		}
		Class[] parameterTypes = new Class[_params.length];
		boolean hasNullParam = false;
		for (int i = 0; i < _params.length; i++) {
			Object param = _params[i];
			if (param!=null){
				parameterTypes[i] = param.getClass();
			}else{
				parameterTypes[i] = NullClass.class;
				hasNullParam = true;
			}
			
		}
		if (!hasNullParam){
			return parameterTypes;
		}
		else{
			Method[] candidateMethods = getMethods(serviceClass,methodName,_params.length);
			if (candidateMethods==null || candidateMethods.length==0){
				throw new ServiceInvocationException("Operation[name="+methodName+
						"] is not found for service object[class="+serviceClass.getName()+
						"], the input parameter types is "+parameterTypesToString(parameterTypes));
			}
			parameterTypes = findExactParameterTypes(serviceClass,methodName,candidateMethods,parameterTypes);
			return parameterTypes;
		}
	}
	
	private String parameterTypesToString(Class[] parameterTypes){
		StringBuffer sBuf = new StringBuffer("[");
		for (int i=0;i<parameterTypes.length;i++){
			Class cls = parameterTypes[i];
			if(cls!=NullClass.class){
				sBuf.append(cls.getName());
			}else{
				sBuf.append("null");
			}
			
			if (i<parameterTypes.length-1){
				sBuf.append(",");
			}
		}
		sBuf.append("]");
		return sBuf.toString();
	}
	private Class[] findExactParameterTypes(Class serviceClass,String methodName,Method[] candidateMethods,Class[] parameterTypes)throws ServiceInvocationException{
		for (Method m : candidateMethods){
			Class[] candidateParamTypes = m.getParameterTypes();
			int paramLength = candidateParamTypes.length;
			boolean isTheSameParamTypes = true;
			for (int i=0;i<paramLength;i++){
				if (parameterTypes[i].getName().equals(NullClass.class.getName())){
					continue;
				}else if (!parameterTypes[i].getName().equals(candidateParamTypes[i].getName())){
					isTheSameParamTypes = false;
					break;
				}
			}
			if (isTheSameParamTypes){
				return m.getParameterTypes();
			}
		}
		throw new ServiceInvocationException("Operation[name="+methodName+
				"] is not found for service object[class="+serviceClass.getName()+
				"], the input parameter types is "+parameterTypesToString(parameterTypes));
	}
	/**
	 * 查找名字和参数数量相匹配的method
	 * @param methodName
	 * @param paramsLength
	 * @return
	 */
	private Method[] getMethods(Class serviceClass,String methodName,int paramsLength){
		Method[] allMethods = serviceClass.getMethods();
		List<Method> foundMethods = new ArrayList<Method>();
		for (Method m : allMethods){
			if (m.getName().equals(methodName)){
				Class[] paramTypes = m.getParameterTypes();
				int _tmpParamLength = 0;
				if (paramTypes!=null){
					_tmpParamLength = paramTypes.length;
				}
				if (_tmpParamLength == paramsLength){
					foundMethods.add(m);
				}
			}
		}
		return (Method[])foundMethods.toArray();
	}
	
	private class NullClass {

	}
	
}

