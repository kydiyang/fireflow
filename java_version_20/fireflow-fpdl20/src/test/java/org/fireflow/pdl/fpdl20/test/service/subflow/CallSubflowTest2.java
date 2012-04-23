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
package org.fireflow.pdl.fpdl20.test.service.subflow;

import javax.xml.namespace.QName;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.engine.WorkflowQuery;
import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.WorkflowSessionFactory;
import org.fireflow.engine.WorkflowStatement;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.engine.query.Restrictions;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Subflow;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pdl.fpdl20.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl20.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl20.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl20.process.impl.WorkflowProcessImpl;
import org.fireflow.service.subflow.SubflowService;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CallSubflowTest2  extends FireWorkflowJunitEnviroment{
	protected static final String processName = "TheCallSubflowProcess2";
	protected static final String processDisplayName = "调用外部流程的subflow";
	protected static final String description = "调用外部流程的subflow";
	protected static final String bizId = "biz_123";
	
	@Test
	public void testStartProcess(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(FpdlConstants.PROCESS_TYPE);
		
		//首先发布被调用的流程：process2
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {

				//构建流程定义
				WorkflowProcess process = createWorkflowProcess2();
				
				//启动流程
				try {
					stmt.uploadProcess(process, Boolean.TRUE, null);
					
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				return null;
			}
			
		});
		//执行主流程
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {

				//构建流程定义
				WorkflowProcess process = getWorkflowProcess();
				
				//启动流程
				try {
					ProcessInstance processInstance = stmt.startProcess(process, bizId, null);
					
					if (processInstance!=null){
						processInstanceId = processInstance.getId();
					}
					
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WorkflowProcessNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		assertResult(session);
	}

	public void assertResult(WorkflowSession session) {
		super.assertResult(session);
		
		//主流程变量
		WorkflowQuery<Variable> varQuery = session.createWorkflowQuery(Variable.class);
		varQuery.add(Restrictions.eq(VariableProperty.PROCESS_ELEMENT_ID, processName+WorkflowProcess.ID_SEPARATOR+WorkflowProcess.MAIN_FLOW_NAME))
			.add(Restrictions.eq(VariableProperty.NAME, "response1"));
		Variable var = varQuery.unique();
		Assert.assertNotNull(var);
		Assert.assertEquals("It is OK", var.getPayload());
		
		varQuery.reset();
		varQuery.add(Restrictions.eq(VariableProperty.PROCESS_ELEMENT_ID, processName+WorkflowProcess.ID_SEPARATOR+WorkflowProcess.MAIN_FLOW_NAME))
		.add(Restrictions.eq(VariableProperty.NAME, "response2"));
		var = varQuery.unique();
		Assert.assertNotNull(var);
		Assert.assertEquals(10, var.getPayload());
		
		//子流程变量
		varQuery.reset();
		varQuery.add(Restrictions.eq(VariableProperty.PROCESS_ELEMENT_ID, "Process2"+WorkflowProcess.ID_SEPARATOR+WorkflowProcess.MAIN_FLOW_NAME))
		.add(Restrictions.eq(VariableProperty.NAME, "id"));
		var = varQuery.unique();
		Assert.assertNotNull(var);
		Assert.assertEquals(bizId, var.getPayload());
		
		varQuery.reset();
		varQuery.add(Restrictions.eq(VariableProperty.PROCESS_ELEMENT_ID, "Process2"+WorkflowProcess.ID_SEPARATOR+WorkflowProcess.MAIN_FLOW_NAME))
		.add(Restrictions.eq(VariableProperty.NAME, "result"));
		var = varQuery.unique();
		Assert.assertNotNull(var);
		Assert.assertEquals("This is the result!", var.getPayload());
	}
	
	/* 
	 * 
	 * 主流程：Start-->Activity1(call subflow)-->End
	 * 
	 * 子流程: Start-->Activity2-->End
	 * 
	 * @see org.fireflow.FireWorkflowJunitEnviroment#createWorkflowProcess()
	 */
	@Override
	public WorkflowProcess createWorkflowProcess() {
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDisplayName);
		process.setDescription(description);
		
		//一、创建主流程
		Subflow mainflow = process.getMainflow();
		
		PropertyImpl property = new PropertyImpl(mainflow,"id");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString(bizId);
		mainflow.getProperties().add(property);
		
		property = new PropertyImpl(mainflow,"response1");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("OK");
		mainflow.getProperties().add(property);
		
		property = new PropertyImpl(mainflow,"response2");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property.setInitialValueAsString("1");
		mainflow.getProperties().add(property);
		
		mainflow.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode = new StartNodeImpl(process.getMainflow(),"Start");
		
		ActivityImpl activity = new ActivityImpl(process.getMainflow(),"Activity1");
		activity.setDuration(new Duration(6,Duration.DAY));
		property = new PropertyImpl(activity,"approveResult");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("This is the result!");
		activity.getProperties().add(property);
		
		EndNodeImpl endNode = new EndNodeImpl(process.getMainflow(),"End");
		
		mainflow.setEntry(startNode);
		mainflow.getStartNodes().add(startNode);
		mainflow.getActivities().add(activity);
		mainflow.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(process.getMainflow(),"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity);
		startNode.getLeavingTransitions().add(transition1);
		activity.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(process.getMainflow(),"activity2end");
		transition2.setFromNode(activity);
		transition2.setToNode(endNode);
		activity.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		mainflow.getTransitions().add(transition1);
		mainflow.getTransitions().add(transition2);
		
		
		//三、创建SubflowService 并绑定到activity1
		SubflowService subflowService = new SubflowService();
		subflowService.setName("call_subflow2");
		subflowService.setTargetNamespaceUri("http://CallSubflowTest1");
		subflowService.setProcessId("Process2");
		subflowService.setSubflowId("Process2.main_flow");
		
		CommonInterfaceDef commonInterface = new CommonInterfaceDef();
		commonInterface.setName("call_subflow2_interface");
		subflowService.setInterface(commonInterface);
		
		OperationDefImpl op = new OperationDefImpl();
		op.setOperationName("callSubflow");
		commonInterface.getOperations().add(op);
		
		InputImpl input = new InputImpl();
		input.setName("id");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		op.getInputs().add(input);
		
		input = new InputImpl();
		input.setName("result");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		op.getInputs().add(input);
		
		OutputImpl output = new OutputImpl();
		output.setName("reply1");
		output.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		op.getOutputs().add(output);
		
		output = new OutputImpl();
		output.setName("reply2");
		output.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		op.getOutputs().add(output);
		
		
		//绑定
		ServiceBindingImpl svcBinding = new ServiceBindingImpl();
		svcBinding.setService(subflowService);
		svcBinding.setServiceId(subflowService.getId());
		svcBinding.setOperation(op);
		svcBinding.setOperationName(op.getOperationName());
		
		//io输入映射
		// processVars.id-->inputs.id
		AssignmentImpl assignment = new AssignmentImpl();
		ExpressionImpl from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/id");
		from.setLanguage("XPATH");
		from.getNamespaceMap().put("ns0",subflowService.getTargetNamespaceUri());
		assignment.setFrom(from);
		
		ExpressionImpl to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.INPUTS+"/"+"id");
		to.setLanguage("XPATH");
		assignment.setTo(to);
		svcBinding.getInputAssignments().add(assignment);
		
		// activityVars.approveResult-->inputs.result
		assignment = new AssignmentImpl();
		from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.ACTIVITY_VARIABLES+"/"+"approveResult");
		from.setLanguage("XPATH");
		from.getNamespaceMap().put("ns0",subflowService.getTargetNamespaceUri());
		assignment.setFrom(from);
		
		to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.INPUTS+"/result");
		to.setLanguage("XPATH");
		assignment.setTo(to);
		svcBinding.getInputAssignments().add(assignment);
		
		//io输出映射
		// output.reply1-->processVars.response1
		assignment = new AssignmentImpl();
		from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.OUTPUTS+"/reply1");
		from.setLanguage("XPATH");
		assignment.setFrom(from);
		
		to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/response1");
		to.setLanguage("XPATH");
		to.getNamespaceMap().put("ns0", subflowService.getTargetNamespaceUri());
		assignment.setTo(to);
		svcBinding.getOutputAssignments().add(assignment);
		
		// output.reply2-->processVars.response2
		assignment = new AssignmentImpl();
		from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.OUTPUTS+"/reply2");
		from.setLanguage("XPATH");
		assignment.setFrom(from);
		
		to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/response2");
		to.setLanguage("XPATH");
		to.getNamespaceMap().put("ns0", subflowService.getTargetNamespaceUri());
		assignment.setTo(to);
		svcBinding.getOutputAssignments().add(assignment);
		
		//设置到activity和workflowprocess
		process.addService(subflowService);
		activity.setServiceBinding(svcBinding);
		
		return process;
	}
	
	public WorkflowProcess createWorkflowProcess2(){
		WorkflowProcessImpl process = new WorkflowProcessImpl("Process2","Process2");
		
		Subflow subflow2 = process.getMainflow();
		
		PropertyImpl property4Subflow2 = new PropertyImpl(subflow2,"id");//流程变量x
		property4Subflow2.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property4Subflow2.setInitialValueAsString("");
		subflow2.getProperties().add(property4Subflow2);
		
		property4Subflow2 = new PropertyImpl(subflow2,"result");//流程变量x
		property4Subflow2.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property4Subflow2.setInitialValueAsString("OK");
		subflow2.getProperties().add(property4Subflow2);
		
		property4Subflow2 = new PropertyImpl(subflow2,"reply1");//流程变量x
		property4Subflow2.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property4Subflow2.setInitialValueAsString("It is OK");
		subflow2.getProperties().add(property4Subflow2);
		
		property4Subflow2 = new PropertyImpl(subflow2,"reply2");//流程变量x
		property4Subflow2.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property4Subflow2.setInitialValueAsString("10");
		subflow2.getProperties().add(property4Subflow2);		
		
		subflow2.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode2 = new StartNodeImpl(subflow2,"Start2");
		
		ActivityImpl activity2 = new ActivityImpl(subflow2,"Activity2");
		activity2.setDuration(new Duration(6,Duration.DAY));
		
		EndNodeImpl endNode2 = new EndNodeImpl(subflow2,"End2");
		
		subflow2.setEntry(startNode2);
		subflow2.getStartNodes().add(startNode2);
		subflow2.getActivities().add(activity2);
		subflow2.getEndNodes().add(endNode2);
		
		TransitionImpl transition1_subflow2 = new TransitionImpl(subflow2,"start2activity");
		transition1_subflow2.setFromNode(startNode2);
		transition1_subflow2.setToNode(activity2);
		startNode2.getLeavingTransitions().add(transition1_subflow2);
		activity2.getEnteringTransitions().add(transition1_subflow2);
		
		TransitionImpl transition2_subflow2 = new TransitionImpl(subflow2,"activity2end");
		transition2_subflow2.setFromNode(activity2);
		transition2_subflow2.setToNode(endNode2);
		activity2.getLeavingTransitions().add(transition2_subflow2);
		endNode2.getEnteringTransitions().add(transition2_subflow2);
		
		subflow2.getTransitions().add(transition1_subflow2);
		subflow2.getTransitions().add(transition2_subflow2);
		
		return process;
	}
}
