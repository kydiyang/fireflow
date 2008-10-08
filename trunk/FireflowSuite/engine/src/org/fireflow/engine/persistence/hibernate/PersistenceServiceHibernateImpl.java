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
package org.fireflow.engine.persistence.hibernate;

import java.util.List;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.impl.JoinPoint;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.impl.Token;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

/**
 * @author chennieyun
 *
 */
public class PersistenceServiceHibernateImpl implements IPersistenceService {
//	private ProcessInstanceDAO processInstanceDAO = null;
//	private TaskInstanceDAO taskInstanceDAO = null;
//	private WorkItemDAO workItemDAO = null;
//	private JoinPointDAO joinPointDAO = null;
//	private TokenDAO tokenDAO = null;
    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveJoinPoint(org.fireflow.kenel.IJoinPoint)
     */
    public void saveJoinPoint(IJoinPoint joinPoint) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        session.save(joinPoint);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveProcessInstance(org.fireflow.engine.IProcessInstance)
     */
    public void saveProcessInstance(IProcessInstance processInstance) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        session.save(processInstance);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveTaskInstance(org.fireflow.engine.ITaskInstance)
     */
    public void saveTaskInstance(ITaskInstance taskInstance) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        session.save(taskInstance);
    }
//
	/* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveWorkItem(org.fireflow.engine.IWorkItem)
     */
    public void saveWorkItem(IWorkItem workitem) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        session.save(workitem);
    }

    public void saveToken(IToken token) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        session.save(token);
    }
//
//	public ProcessInstanceDAO getProcessInstanceDAO() {
//		return processInstanceDAO;
//	}
//
//	public void setProcessInstanceDAO(ProcessInstanceDAO processInstanceDAO) {
//		this.processInstanceDAO = processInstanceDAO;
//	}
//
//	public TaskInstanceDAO getTaskInstanceDAO() {
//		return taskInstanceDAO;
//	}
//
//	public void setTaskInstanceDAO(TaskInstanceDAO taskInstanceDAO) {
//		this.taskInstanceDAO = taskInstanceDAO;
//	}
//
//	public WorkItemDAO getWorkItemDAO() {
//		return workItemDAO;
//	}
//
//	public void setWorkItemDAO(WorkItemDAO workItemDAO) {
//		this.workItemDAO = workItemDAO;
//	}
//
//	public JoinPointDAO getJoinPointDAO() {
//		return joinPointDAO;
//	}
//
//	public void setJoinPointDAO(JoinPointDAO joinPointDAO) {
//		this.joinPointDAO = joinPointDAO;
//	}
//
//	public TokenDAO getTokenDAO() {
//		return tokenDAO;
//	}
//
//	public void setTokenDAO(TokenDAO tokenDAO) {
//		this.tokenDAO = tokenDAO;
//	}
    public IJoinPoint findJoinPoint(IProcessInstance processInstance, String synchronizerId) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        Criteria criteria = session.createCriteria(JoinPoint.class);
        criteria.add(Expression.eq("processInstance.id", processInstance.getId()));
        criteria.add(Expression.eq("synchronizerId", synchronizerId));

        return (IJoinPoint) criteria.uniqueResult();
    }

    public List<ITaskInstance> findTaskInstances(IProcessInstance processInstance, String activityId) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        Criteria criteria = session.createCriteria(TaskInstance.class);
        criteria.add(Expression.eq("processInstance.id", processInstance.getId()));
        criteria.add(Expression.eq("activityId", activityId));

        return (List<ITaskInstance>) criteria.list();
    }

    /**
     * 
     */
    public List<IToken> findTokens(IProcessInstance processInstance, String nodeId) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        Criteria criteria = session.createCriteria(Token.class);

        criteria.add(Expression.eq("processInstance.id", processInstance.getId()));
        criteria.add(Expression.eq("nodeId", nodeId));

        return (List<IToken>) criteria.list();
    }

    public void updateWorkItem(IWorkItem workItem) {
        //在hibernate中，update操作无需处理？
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();

        session.update(workItem);
    }

    public void updateTaskInstance(ITaskInstance taskInstance) {
        //在hibernate中，update操作无需处理？
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();

        session.update(taskInstance);

    }

    public IWorkItem findWorkItemById(String id) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();

        return (IWorkItem) session.get(WorkItem.class, id);
    }

    public ITaskInstance findTaskInstanceById(String id) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();

        return (ITaskInstance) session.get(TaskInstance.class, id);
    }

    /**
     * 查询出属于同一个taskInstance的所有的workItem
     */
    public List<IWorkItem> findWorkItemsForTaskInstance(TaskInstance taskInstance) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();

        Criteria criteria = session.createCriteria(WorkItem.class);
        criteria.add(Expression.eq("taskInstance.id", taskInstance.getId()));
        List<IWorkItem> result = criteria.list();

        return result;
    }

    public List<IWorkItem> findWorkItem4Task(String taskid) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();

        Criteria criteria = session.createCriteria(WorkItem.class);
        criteria.add(Expression.eq("taskInstance.taskId", taskid));
        List<IWorkItem> result = criteria.list();

        return result;
    }

    public List<IToken> findTokens(IProcessInstance processInstance) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
        Criteria criteria = session.createCriteria(Token.class);

        criteria.add(Expression.eq("processInstance.id", processInstance.getId()));

        return (List<IToken>) criteria.list();
    }

    public List<IProcessInstance> findProcessInstanceByProcessId(String processId) {
        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();

        Criteria criteria = session.createCriteria(ProcessInstance.class);

        criteria.add(Expression.eq("processId", processId));
        List<IProcessInstance> result = criteria.list();

        return result;
    }
}
