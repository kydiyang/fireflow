package org.fireflow.service.human;

import org.fireflow.model.data.Expression;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;

public class HumanService extends AbstractServiceDef implements ServiceDef {
	public static final String DEFAULT_INVOKER_CLASS_NAME = "org.fireflow.pdl.fpdl20.enginemodules.workitem.WorkItemManagerFpdl20Impl";
	public static final String DEFAULT_INVOKER_BEAN_NAME="workItemManager";
	/**
	 * 
	 */
	private static final long serialVersionUID = -9117600403085062844L;
	
	String formUrl = null;
	
	/**
	 * 工作项摘要
	 */
	Expression workItemSubject = null;

	public HumanService(){
		this.invokerBeanName = DEFAULT_INVOKER_BEAN_NAME;
//		this.invokerClassName = DEFAULT_INVOKER_CLASS_NAME;
		this.parserClassName = HumanServiceParser.class.getName();
	}
	
	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}

	public Expression getWorkItemSubject() {
		return workItemSubject;
	}

	public void setWorkItemSubject(Expression workItemSubj) {
		this.workItemSubject = workItemSubj;
	}
	
	
}
