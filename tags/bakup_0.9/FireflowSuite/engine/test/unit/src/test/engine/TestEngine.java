package test.engine;

import java.util.HashMap;
import java.util.List;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IFireflowSession;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.StartNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;

import junit.framework.TestCase;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

//import org.springframework.core.io.support.

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

/**
 * @author chennieyun
 * 
 */
public class TestEngine extends TestCase {

	IFireflowSession fireflowSession = null;
	SessionFactory sessionFactory = null;

	static ClassPathResource resource = new ClassPathResource(
			"/test/engine/testContext.xml");
	static XmlBeanFactory beanFactory = new XmlBeanFactory(resource);

	public TestEngine() {
	}

	protected void initSession() {

		fireflowSession = (IFireflowSession) beanFactory
				.getBean("fireflowSession");

		sessionFactory = (SessionFactory) beanFactory.getBean("sessionFactory");
	}

	protected void setUp() {
		initSession();
	}

//	public void testStartNewProcess() {
//		try {
//			Session session = sessionFactory.openSession();
//			session.beginTransaction();
//			fireflowSession.begin(session);
//			IProcessInstance processInstance = fireflowSession
//					.createProcessInstance("process1");
//			processInstance
//					.setProcessInstanceVariable("jine", new Integer(800));
//			processInstance.run();
//			fireflowSession.end();
//			session.getTransaction().commit();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}

//	public void testAcceptWorkItem() throws EngineException {
//		Session session = sessionFactory.openSession();
//
//		try {
//
//			String id = "402881ee19c3b39e0119c3b3a2540005";
//
//			session.beginTransaction();
//
//			fireflowSession.begin(session);
//			IWorkItem wi = fireflowSession.findWorkItemById(id);
//
//			this.assertEquals(wi.getId(), id);
//			// this.assertEquals(wi.getState(), new Integer(0));
//			wi.accept();
//			fireflowSession.end();
//
//			session.getTransaction().commit();
//			session.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			session.getTransaction().rollback();
//		}
//	}
//
	public void testCompleteWorkItem() throws EngineException {
		String id = "402881ee19c3b39e0119c3b3a2540005";
		Session session = sessionFactory.openSession();
		Transaction transaction = session.getTransaction();
		try {
			transaction.begin();
			fireflowSession.begin(session);
			IWorkItem wi = fireflowSession.findWorkItemById(id);

			this.assertNotNull(wi);
			this.assertEquals(wi.getId(), id);
			this.assertEquals(wi.getState(), new Integer(1));
			wi.complete();
			fireflowSession.end();
			transaction.commit();
			session.close();
		} catch (Exception ex) {
			transaction.rollback();
			ex.printStackTrace();
		}

	}

	// public void testCompleteTask()throws Exception{
	// String id = "402881ee186edae201186edae7100002";
	// Session session = sessionFactory.openSession();
	// Transaction transaction = session.getTransaction();
	// try {
	// transaction.begin();
	// fireflowSession.begin(session);
	// ITaskInstance taskInstance = fireflowSession.findTaskInstanceById(id);
	//
	// taskInstance.start();
	// taskInstance.complete();
	// fireflowSession.end();
	// transaction.commit();
	//
	// } catch (Exception ex) {
	// transaction.rollback();
	// ex.printStackTrace();
	// }
	// }

	// public void testProcessInstanceVar() {
	// try {
	// Session session = sessionFactory.openSession();
	// session.beginTransaction();
	// fireflowSession.begin(session);
	// IProcessInstance processInstance = fireflowSession
	// .createProcessInstance("process1");
	// processInstance.run();
	// HashMap map = new HashMap();
	// map.put("jine", new Float("123.1"));
	// map.put("userName", "张三");
	//		
	// processInstance.setProcessInstanceVariables(map);
	//			
	// processInstance.setProcessInstanceVariable("userName", "里斯");
	// processInstance.setProcessInstanceVariable("test", new Integer(100));
	//			
	// fireflowSession.end();
	// session.getTransaction().commit();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }
}
