/**
 * Copyright 2007-2008 非也
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

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import java.util.Vector;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.impl.Token;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The hibernate implementation of persistence service
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public class PersistenceServiceHibernateImpl extends HibernateDaoSupport implements IPersistenceService {

    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }

    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }

    /**
     * Save processInstance
     * @param processInstance
     */
    public void saveOrUpdateProcessInstance(IProcessInstance processInstance) {
        this.getHibernateTemplate().saveOrUpdate(processInstance);
    }

    /**
     * Save joinpoint
     * @param joinPoint
     */
//    public void saveOrUpdateJoinPoint(IJoinPoint joinPoint) {
//        this.getHibernateTemplate().saveOrUpdate(joinPoint);
//    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveTaskInstance(org.fireflow.engine.ITaskInstance)
     */
    public void saveOrUpdateTaskInstance(ITaskInstance taskInstance) {
        this.getHibernateTemplate().saveOrUpdate(taskInstance);
    }
//
	/* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveWorkItem(org.fireflow.engine.IWorkItem)
     */

    public void saveOrUpdateWorkItem(IWorkItem workitem) {
        this.getHibernateTemplate().saveOrUpdate(workitem);
    }

    public void saveOrUpdateToken(IToken token) {
        this.getHibernateTemplate().saveOrUpdate(token);
    }

//    public List<IJoinPoint> findJoinPointsForProcessInstance(final String processInstanceId, final String synchronizerId) {
//        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {
//
//            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
//                Criteria criteria = arg0.createCriteria(JoinPoint.class);
//                criteria.add(Expression.eq("processInstanceId", processInstanceId));
//                if (synchronizerId != null && !synchronizerId.trim().equals("")) {
//                    criteria.add(Expression.eq("synchronizerId", synchronizerId));
//                }
//                return criteria.list();
//            }
//        });
//
//        return result;
//
//    }
    public Integer getAliveTokenCountForNode(final String processInstanceId, final String nodeId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(Token.class);

                criteria.add(Expression.eq("processInstanceId", processInstanceId));

                criteria.add(Expression.eq("nodeId", nodeId));

                criteria.add(Expression.eq("alive", java.lang.Boolean.TRUE));

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    public Integer getCompletedTaskInstanceCountForTask(final String processInstanceId, final String taskId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("taskId", taskId.trim()));
                criteria.add(Expression.eq("processInstanceId", processInstanceId));

                Criterion cri2 = Expression.eq("state", new Integer(ITaskInstance.COMPLETED));

                criteria.add(cri2);

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    public Integer getAliveTaskInstanceCountForActivity(final String processInstanceId, final String activityId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId.trim()));

                criteria.add(Expression.eq("activityId", activityId.trim()));

                Criterion cri1 = Expression.eq("state", new Integer(ITaskInstance.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(ITaskInstance.RUNNING));
//                Criterion cri3 = Expression.eq("state", new Integer(ITaskInstance.SUSPENDED));
                Criterion cri_or = Expression.or(cri1, cri2);
//                Criterion cri_or = Expression.or(cri_tmp, cri3);

                criteria.add(cri_or);

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    /**
     * 获得同一个Token的所有状态为Initialized的TaskInstance
     * @param tokenId
     * @return
     */
    public List<ITaskInstance> findInitializedTaskInstancesListForToken(final String processInstanceId, final String tokenId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId));

                criteria.add(Expression.eq("tokenId", tokenId.trim()));

                criteria.add(Expression.eq("state", new Integer(0)));

                return (List<ITaskInstance>) criteria.list();
            }
        });
        return result;
    }

    public List<ITaskInstance> findTaskInstancesForProcessInstance(final java.lang.String processInstanceId,
            final String activityId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId.trim()));
                if (activityId != null && !activityId.trim().equals("")) {
                    criteria.add(Expression.eq("activityId", activityId.trim()));
                }
                return (List<ITaskInstance>) criteria.list();
            }
        });
        return result;
    }

    public List<ITaskInstance> findTaskInstancesForProcessInstanceByStepNumber(final String processInstanceId, final Integer stepNumber) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId.trim()));

                if (stepNumber != null) {
                    criteria.add(Expression.eq("stepNumber", stepNumber));
                }
                return (List<ITaskInstance>) criteria.list();
            }
        });
        return result;
    }

    /*
    public List<ITaskInstance> findTaskInstancesForProcessInstanceByFromActivityId(final String processInstanceId, final String fromActivityId) {
    List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

    public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

    Criteria criteria = arg0.createCriteria(TaskInstance.class);
    criteria.add(Expression.eq("processInstanceId", processInstanceId.trim()));

    if (fromActivityId != null && !fromActivityId.trim().equals("")) {
    criteria.add(Expression.eq("fromActivityId", fromActivityId.trim()));
    }
    return (List<ITaskInstance>) criteria.list();
    }
    });
    return result;
    }
     */
//    public IToken findDeadTokenById(final String id){
//       IToken result = (IToken) this.getHibernateTemplate().execute(new HibernateCallback() {
//
//            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
//
//                Criteria criteria = arg0.createCriteria(Token.class);
//
//
//                criteria.add(Expression.eq("id", id));
//
//                criteria.add(Expression.eq("alive",java.lang.Boolean.FALSE));
//
//                return criteria.uniqueResult();
//            }
//        });
//        return result;
//    }
    public IToken findTokenById(String id) {
        return (IToken) this.getHibernateTemplate().get(Token.class, id);
    }

    public void deleteTokensForNodes(final String processInstanceId, final List nodeIdsList) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "delete from org.fireflow.kernel.impl.Token as model where model.processInstanceId=:processInstanceId and model.nodeId in (:nodeId)";
                Query query = arg0.createQuery(hql);
                query.setString("processInstanceId", processInstanceId);
                query.setParameterList("nodeId", nodeIdsList);
                return query.executeUpdate();
            }
        });
    }

    public void deleteTokensForNode(final String processInstanceId, final String nodeId) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "delete from org.fireflow.kernel.impl.Token as model where model.processInstanceId=:processInstanceId and model.nodeId=:nodeId";
                Query query = arg0.createQuery(hql);
                query.setString("processInstanceId", processInstanceId);
                query.setString("nodeId", nodeId);
                return query.executeUpdate();
            }
        });
    }

    public void deleteToken(IToken token) {
        this.getHibernateTemplate().delete(token);
    }

    /**
     * 
     */
    public List<IToken> findTokensForProcessInstance(final String processInstanceId, final String nodeId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(Token.class);

                criteria.add(Expression.eq("processInstanceId", processInstanceId));
                if (nodeId != null && !nodeId.trim().equals("")) {
                    criteria.add(Expression.eq("nodeId", nodeId));
                }

                return (List<IToken>) criteria.list();
            }
        });
        return result;
    }

//    public void updateWorkItem(IWorkItem workItem) {
//        //在hibernate中，update操作无需处理？
//        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
//
//        session.update(workItem);
//    }

//    public void updateTaskInstance(ITaskInstance taskInstance) {
//        //在hibernate中，update操作无需处理？
//        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
//
//        session.update(taskInstance);
//
//    }
    public IWorkItem findWorkItemById(String id) {
        return (IWorkItem) this.getHibernateTemplate().get(WorkItem.class, id);
    }

    public ITaskInstance findAliveTaskInstanceById(final String id) {
        ITaskInstance result = (ITaskInstance) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(TaskInstance.class);


                Criterion cri1 = Expression.eq("state", new Integer(0));
                Criterion cri2 = Expression.eq("state", new Integer(1));
                Criterion cri3 = Expression.eq("state", new Integer(3));
                Criterion cri_tmp = Expression.or(cri1, cri2);
                Criterion cri_or = Expression.or(cri_tmp, cri3);

                Criterion cri0 = Expression.eq("id", id);
                Criterion cri_and = Expression.and(cri0, cri_or);
                criteria.add(cri_and);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    public ITaskInstance findTaskInstanceById(String id) {
        return (ITaskInstance) this.getHibernateTemplate().get(TaskInstance.class, id);
    }


    /*
    public List<IWorkItem> findWorkItemsForTaskInstance(final String taskInstanceId) {
    List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

    public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
    Criteria criteria = arg0.createCriteria(WorkItem.class);
    criteria.add(Expression.eq("taskInstance.id", taskInstanceId));
    List<IWorkItem> _result = criteria.list();

    return _result;
    }
    });
    return result;

    }
     */
//    public List<IWorkItem> findAliveWorkItemsWithoutJoinForTaskInstance(final String taskInstanceId) {
//        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {
//
//            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
//                String hql = "From org.fireflow.engine.impl.WorkItem m Where m.taskInstance.id=:taskInstanceId And (m.state=0 Or m.state=1 Or m.state=3)";
//                Query query = arg0.createQuery(hql);
//
//                query.setString("taskInstanceId", taskInstanceId);
//
//                return query.list();
//            }
//        });
//        System.out.println("===================================");
//        return result;
//    }
    public void abortTaskInstance(final TaskInstance taskInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Date now = rtCtx.getCalendarService().getSysDate();
                //首先Cancel TaskInstance
                taskInstance.setState(ITaskInstance.CANCELED);
                taskInstance.setEndTime(now);
                taskInstance.setCanBeWithdrawn(Boolean.FALSE);
                arg0.update(taskInstance);


                String hql = "Update org.fireflow.engine.impl.WorkItem m set m.state=:state ,m.endTime=:endTime Where m.taskInstance.id=:taskInstanceId And (m.state=0 Or m.state=1)";
                Query query = arg0.createQuery(hql);
                query.setInteger("state", IWorkItem.CANCELED);
                query.setDate("endTime", now);
                query.setString("taskInstanceId", taskInstance.getId());

                query.executeUpdate();

                return null;
            }
        });
    }

    public Integer getAliveWorkItemCountForTaskInstance(final String taskInstanceId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "select count(*) From org.fireflow.engine.impl.WorkItem m Where m.taskInstance.id=:taskInstanceId And (m.state=0 Or m.state=1 Or m.state=3)";
                Query query = arg0.createQuery(hql);
                query.setString("taskInstanceId", taskInstanceId);

                return query.uniqueResult();
            }
        });
        return result;
    }

    public List<IWorkItem> findDeadWorkItemsWithoutJoinForTaskInstance(final String taskInstanceId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "From org.fireflow.engine.impl.WorkItem m Where m.taskInstance.id=:taskInstanceId And (m.state=7 Or m.state=9)";
                Query query = arg0.createQuery(hql);
                query.setString("taskInstanceId", taskInstanceId);

                return query.list();
            }
        });
        System.out.println("===================================");
        return result;
    }

    public List<IWorkItem> findWorkItemForTask(final String taskid) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);

                criteria.createAlias("taskInstance", "taskInstance");
                criteria.add(Expression.eq("taskInstance.taskId", taskid));
                List<IWorkItem> _result = criteria.list();

                return _result;
            }
        });
        return result;
    }

//    public List<IToken> findTokens(IProcessInstance processInstance) {
//        Session session = (Session) RuntimeContext.getInstance().getCurrentDBSession();
//        Criteria criteria = session.createCriteria(Token.class);
//
//        criteria.add(Expression.eq("processInstance.id", processInstance.getId()));
//
//        return (List<IToken>) criteria.list();
//    }
    public List<IProcessInstance> findProcessInstanceByProcessId(final String processId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);

                criteria.add(Expression.eq("processId", processId));
                List<IProcessInstance> _result = criteria.list();

                return _result;
            }
        });
        return result;
    }

    public List<IProcessInstance> findProcessInstanceByProcessIdAndVersion(final String processId, final Integer version) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);

                criteria.add(Expression.eq("processId", processId));
                criteria.add(Expression.eq("version", version));
                List<IProcessInstance> _result = criteria.list();

                return _result;
            }
        });
        return result;
    }

    public IProcessInstance findProcessInstanceById(String id) {
        return (IProcessInstance) this.getHibernateTemplate().get(ProcessInstance.class, id);
    }

    public IProcessInstance findAliveProcessInstanceById(final String id) {
        IProcessInstance result = (IProcessInstance) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);


                Criterion cri1 = Expression.eq("state", new Integer(IProcessInstance.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IProcessInstance.RUNNING));
//                Criterion cri3 = Expression.eq("state", new Integer(IProcessInstance.SUSPENDED));
                Criterion cri_or = Expression.or(cri1, cri2);
//                Criterion cri_or = Expression.or(cri_tmp, cri3);

                Criterion cri0 = Expression.eq("id", id);
                Criterion cri_and = Expression.and(cri0, cri_or);
                criteria.add(cri_and);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

//    public IJoinPoint findJoinPointById(String id) {
//        return (IJoinPoint) this.getHibernateTemplate().get(JoinPoint.class, id);
//    }
    public void saveOrUpdateWorkflowDefinition(WorkflowDefinition workflowDef) {
        if (workflowDef.getId() == null || workflowDef.getId().equals("")) {
            Integer latestVersion = findTheLatestVersionNumber(workflowDef.getProcessId());
            if (latestVersion != null) {
                workflowDef.setVersion(new Integer(latestVersion.intValue() + 1));
            } else {
                workflowDef.setVersion(new Integer(1));
            }
        }
        this.getHibernateTemplate().saveOrUpdate(workflowDef);
    }

    public Integer findTheLatestVersionNumber(final String processId) {
        //取得当前最大的version值
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Query q = arg0.createQuery("select max(m.version) from WorkflowDefinition m where m.processId=:processId");
                q.setString("processId", processId);
                Object obj = q.uniqueResult();
                if (obj != null) {
                    Integer latestVersion = (Integer) obj;
                    return latestVersion;
                } else {
                    return null;
                }
            }
        });
        return result;
    }

    public WorkflowDefinition findWorkflowDefinitionById(String id) {
        return (WorkflowDefinition) this.getHibernateTemplate().get(WorkflowDefinition.class, id);
    }

    public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersionNumber(final String processId, final int version) {
        WorkflowDefinition workflowDef = (WorkflowDefinition) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria c = arg0.createCriteria(WorkflowDefinition.class);
                c.add(Expression.eq("processId", processId));
                c.add(Expression.eq("version", version));
                return (WorkflowDefinition) c.uniqueResult();
            }
        });
        return workflowDef;
    }

    public WorkflowDefinition findTheLatestVersionOfWorkflowDefinitionByProcessId(String processId) {
        Integer latestVersion = this.findTheLatestVersionNumber(processId);
        return this.findWorkflowDefinitionByProcessIdAndVersionNumber(processId, latestVersion);
    }

    public List<WorkflowDefinition> findWorkflowDefinitionsByProcessId(final String processId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria c = arg0.createCriteria(WorkflowDefinition.class);
                c.add(Expression.eq("processId", processId));
                return c.list();
            }
        });

        return result;
    }

    public List<WorkflowDefinition> findAllTheLatestVersionsOfWorkflowDefinition() {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "select distinct model.processId from WorkflowDefinition model ";
                Query query = arg0.createQuery(hql);
                List processIdList = query.list();
                List _result = new Vector<WorkflowDefinition>();
                for (int i = 0; i < processIdList.size(); i++) {
                    WorkflowDefinition wfDef = findTheLatestVersionOfWorkflowDefinitionByProcessId((String) processIdList.get(i));
                    _result.add(wfDef);
                }
                return _result;
            }
        });
        return result;
    }

    public List<IWorkItem> findTodoWorkItems(final String actorId) {
        return findTodoWorkItems(actorId, null);
    }

    public List<IWorkItem> findTodoWorkItems(final String actorId, final String processInstanceId) {

        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);

                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.RUNNING));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processInstanceId != null && !processInstanceId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processInstanceId", processInstanceId));
                }

                return criteria.list();
            }
        });
        return result;
    }

    public List<IWorkItem> findTodoWorkItems(final String actorId, final String processId, final String taskId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);


                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.RUNNING));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processId != null && !processId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processId", processId));
                }

                if (taskId != null && !taskId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.taskId", taskId));
                }
                return criteria.list();


            }
        });
        return result;
    }

    public List<IWorkItem> findHaveDoneWorkItems(final String actorId) {
        return findHaveDoneWorkItems(actorId, null);
    }

    public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processInstanceId) {

        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);

                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.COMPLETED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.CANCELED));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processInstanceId != null && !processInstanceId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processInstanceId", processInstanceId));
                }

                return criteria.list();
            }
        });
        return result;
    }

    public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processId, final String taskId) {
        List result = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);


                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.COMPLETED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.CANCELED));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processId != null && !processId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processId", processId));
                }

                if (taskId != null && !taskId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.taskId", taskId));
                }
                return criteria.list();


            }
        });
        return result;
    }

    public void deleteWorkItemsInInitializedState(final String taskInstanceId) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "delete from org.fireflow.engine.impl.WorkItem as model where model.taskInstance.id=? and model.state=0";
                Query query = arg0.createQuery(hql);
                query.setString(0, taskInstanceId);
                return query.executeUpdate();
            }
        });
    }

    public Integer getAliveProcessInstanceCountForParentTaskInstance(final String taskInstanceId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);
                criteria.add(Expression.eq("parentTaskInstanceId", taskInstanceId));

                Criterion cri1 = Expression.eq("state", new Integer(IProcessInstance.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IProcessInstance.RUNNING));
//                Criterion cri3 = Expression.eq("state", new Integer(IProcessInstance.SUSPENDED));
                Criterion cri_or = Expression.or(cri1, cri2);
//                Criterion cri_or = Expression.or(cri_tmp, cri3);

                criteria.add(cri_or);

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    public void suspendProcessInstance(final ProcessInstance processInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                processInstance.setSuspended(Boolean.TRUE);
                arg0.update(processInstance);

                String hql1 = "Update org.fireflow.engine.impl.TaskInstance m Set m.suspended=:suspended Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
                Query query1 = arg0.createQuery(hql1);
                query1.setBoolean("suspended", Boolean.TRUE);
                query1.setString("processInstanceId", processInstance.getId());
                query1.executeUpdate();

                return null;
            }
        });
    }

    public void restoreProcessInstance(final ProcessInstance processInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                processInstance.setSuspended(Boolean.FALSE);
                arg0.update(processInstance);

                String hql1 = "Update org.fireflow.engine.impl.TaskInstance m Set m.suspended=:suspended Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
                Query query1 = arg0.createQuery(hql1);
                query1.setBoolean("suspended", Boolean.FALSE);
                query1.setString("processInstanceId", processInstance.getId());
                query1.executeUpdate();

                return null;
            }
        });
    }

    public void abortProcessInstance(final ProcessInstance processInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Date now = rtCtx.getCalendarService().getSysDate();
                processInstance.setState(IProcessInstance.CANCELED);
                processInstance.setEndTime(now);
                arg0.update(processInstance);

                String hql1 = "Update org.fireflow.engine.impl.TaskInstance as m set m.state=:state,m.endTime=:endTime,m.canBeWithdrawn=:canBewithdrawn Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
                Query query1 = arg0.createQuery(hql1);
                query1.setInteger("state", ITaskInstance.CANCELED);
                query1.setDate("endTime", now);
                query1.setBoolean("canBewithdrawn", Boolean.FALSE);
                query1.setString("processInstanceId", processInstance.getId());
                query1.executeUpdate();


                String hql2 = "Update org.fireflow.engine.impl.WorkItem as m set m.state=:state,m.endTime=:endTime Where m.taskInstance in (From org.fireflow.engine.impl.TaskInstance n  Where n.processInstanceId=:processInstanceId)   And (m.state=0 Or m.state=1)";
                Query query2 = arg0.createQuery(hql2);
                query2.setInteger("state", IWorkItem.CANCELED);
                query2.setDate("endTime", now);
                query2.setString("processInstanceId", processInstance.getId());
                query2.executeUpdate();

                String hql3 = "Delete org.fireflow.kernel.impl.Token where processInstanceId=:processInstanceId";
                Query query3 = arg0.createQuery(hql3);
                query3.setString("processInstanceId", processInstance.getId());
                query3.executeUpdate();

                return null;
            }
        });
    }
    /*
    public List<IWorkItem> findHaveDoneWorkItems(final String processInstanceId,final String activityId){
    if (processInstanceId==null || processInstanceId.trim().equals("")||
    activityId==null || activityId.trim().equals("")){
    return null;
    }
    Object result = this.getHibernateTemplate().execute(new HibernateCallback() {
    public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
    String hql = " from org.fireflow.engine.impl.WorkItem as model where model.taskInstance.processInstance.id=? and model.activityId=? and model.state=2";
    Query query = arg0.createQuery(hql);
    query.setString(0, processInstanceId);
    query.setString(1, activityId);
    return query.list();
    }
    });

    return (List)result;
    }
     */

    public void saveOrUpdateProcessInstanceTrace(ProcessInstanceTrace processInstanceTrace) {
        this.getHibernateTemplate().saveOrUpdate(processInstanceTrace);
    }
    public List findProcessInstanceTraces(final String processInstanceId){
		List l = (List)this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				String hql = "From org.fireflow.engine.impl.ProcessInstanceTrace as m Where m.processInstanceId=:processInstanceId Order by m.stepNumber,m.minorNumber";
				Query q = arg0.createQuery(hql);
				q.setString("processInstanceId", processInstanceId);

				return q.list();
			}
		});

		return l;
    }
}
