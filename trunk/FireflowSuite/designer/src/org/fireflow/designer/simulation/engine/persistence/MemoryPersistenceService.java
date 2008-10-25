/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.engine.persistence;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.impl.JoinPoint;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.impl.Token;

/**
 *
 * @author chennieyun
 */
public class MemoryPersistenceService implements IPersistenceService {

    List<IStorageChangeListener> listeners = new ArrayList<IStorageChangeListener>();
    private Hashtable<String, IProcessInstance> processInstanceStorage = new Hashtable<String, IProcessInstance>();
    private Hashtable<String, ITaskInstance> taskInstanceStorage = new Hashtable<String, ITaskInstance>();
    private Hashtable<String, IWorkItem> workItemStorage = new Hashtable<String, IWorkItem>();
    private Hashtable<String, IJoinPoint> joinPointStorage = new Hashtable<String, IJoinPoint>();
    private Hashtable<String, IToken> tokenStorage = new Hashtable<String, IToken>();

    public void saveOrUpdateProcessInstance(IProcessInstance processInstance) {
        if (processInstance.getId() == null || processInstance.getId().trim().equals("")) {
            String id = UUID.randomUUID().toString();
            ((ProcessInstance) processInstance).setId(id);
            processInstanceStorage.put(id, processInstance);
        } else {
            processInstanceStorage.remove(processInstance.getId());
            processInstanceStorage.put(processInstance.getId(), processInstance);
        }
        StorageChangedEvent e = new StorageChangedEvent(this);
        e.setProcessInstance(processInstance);
        e.setProcessId(processInstance.getProcessId());
        e.setProcessInstanceId(processInstance.getId());
        this.fireStorageChangedEvent(e);
    }

    public void saveOrUpdateTaskInstance(ITaskInstance taskInstance) {
        if (taskInstance.getId() == null || taskInstance.getId().trim().equals("")) {
            String id = UUID.randomUUID().toString();
            ((TaskInstance) taskInstance).setId(id);
            taskInstanceStorage.put(id, taskInstance);
        } else {
            taskInstanceStorage.remove(taskInstance.getId());
            taskInstanceStorage.put(taskInstance.getId(), taskInstance);
        }
        StorageChangedEvent e = new StorageChangedEvent(this);
        e.setProcessInstance(taskInstance.getProcessInstance());
        e.setProcessId(taskInstance.getProcessInstance().getProcessId());
        e.setProcessInstanceId(taskInstance.getProcessInstance().getId());
        this.fireStorageChangedEvent(e);
    }

    public void saveOrUpdateWorkItem(IWorkItem workitem) {
        if (workitem.getId() == null || workitem.getId().trim().equals("")) {
            String id = UUID.randomUUID().toString();
            ((WorkItem) workitem).setId(id);
            workItemStorage.put(id, workitem);
        } else {
            workItemStorage.remove(workitem.getId());
            workItemStorage.put(workitem.getId(), workitem);
        }

        StorageChangedEvent e = new StorageChangedEvent(this);
        e.setProcessInstance(workitem.getTaskInstance().getProcessInstance());
        e.setProcessId(workitem.getTaskInstance().getProcessInstance().getProcessId());
        e.setProcessInstanceId(workitem.getTaskInstance().getProcessInstance().getId());
        this.fireStorageChangedEvent(e);
    }

    public void saveOrUpdateJoinPoint(IJoinPoint joinPoint) {
        if (((JoinPoint) joinPoint).getId() == null ||
                ((JoinPoint) joinPoint).getId().trim().equals("")) {
            String id = UUID.randomUUID().toString();
            ((JoinPoint) joinPoint).setId(id);
            this.joinPointStorage.put(id, joinPoint);

        } else {
            joinPointStorage.remove(((JoinPoint) joinPoint).getId());
            joinPointStorage.put(((JoinPoint) joinPoint).getId(), joinPoint);
        }

        StorageChangedEvent e = new StorageChangedEvent(this);
        e.setProcessInstance(joinPoint.getProcessInstance());
        e.setProcessId(joinPoint.getProcessInstance().getProcessId());
        e.setProcessInstanceId(joinPoint.getProcessInstance().getId());
        this.fireStorageChangedEvent(e);
    }

    public void saveOrUpdateToken(IToken token) {
        if (token.getId() == null || token.getId().trim().equals("")) {
            String id = UUID.randomUUID().toString();
            ((Token) token).setId(id);
            this.tokenStorage.put(id, token);
        } else {
            tokenStorage.remove(token.getId());
            tokenStorage.put(token.getId(), token);
        }
        StorageChangedEvent e = new StorageChangedEvent(this);
        e.setProcessInstance(token.getProcessInstance());
        e.setProcessId(token.getProcessInstance().getProcessId());
        e.setProcessInstanceId(token.getProcessInstance().getId());
        this.fireStorageChangedEvent(e);
    }

    public void updateWorkItem(IWorkItem workItem) {
        String id = workItem.getId();
        this.workItemStorage.put(id, workItem);

        StorageChangedEvent e = new StorageChangedEvent(this);
        e.setProcessInstance(workItem.getTaskInstance().getProcessInstance());
        e.setProcessId(workItem.getTaskInstance().getProcessInstance().getProcessId());
        e.setProcessInstanceId(workItem.getTaskInstance().getProcessInstance().getId());
        this.fireStorageChangedEvent(e);

    }

    public void updateTaskInstance(ITaskInstance taskInstance) {
        String id = taskInstance.getId();
        this.taskInstanceStorage.put(id, taskInstance);

        StorageChangedEvent e = new StorageChangedEvent(this);
        e.setProcessInstance(taskInstance.getProcessInstance());
        e.setProcessId(taskInstance.getProcessInstance().getProcessId());
        e.setProcessInstanceId(taskInstance.getProcessInstance().getId());
        this.fireStorageChangedEvent(e);
    }

    /**
     * 
     * @param processInstance
     * @param synchronizerId
     * @return
     */
    public IJoinPoint findJoinPointsForProcessInstance(String processInstanceId, String synchronizerId) {
//        System.out.println("===this.joinPointStorage.size is " + this.joinPointStorage.size());
        Iterator itr = this.joinPointStorage.values().iterator();
        while (itr.hasNext()) {
            JoinPoint joinPoint = (JoinPoint) itr.next();
//            System.out.println("====joinpoint.getProcessInstance is " + joinPoint.getProcessInstance());
            if (joinPoint.getProcessInstance() != null && joinPoint.getProcessInstance().getId().equals(processInstance.getId()) && synchronizerId.equals(joinPoint.getSynchronizerId())) {
                return joinPoint;
            }
        }

        return null;
    }

    public List<IJoinPoint> findJoinPoint4ProcessInstance(IProcessInstance processInstance) {
        Iterator itr = this.joinPointStorage.values().iterator();
        List<IJoinPoint> result = new ArrayList<IJoinPoint>();
        while (itr.hasNext()) {
            JoinPoint joinPoint = (JoinPoint) itr.next();
            if (joinPoint.getProcessInstance() != null && joinPoint.getProcessInstance().getId().equals(processInstance.getId())) {
                result.add(joinPoint);
            }
        }

        return result;
    }

    /**
     * 返回实例processInstance的且activityId等于输入参数的所有TaskInstance
     * 如果activityId等于null，则返回processInstance的所有taskInstance
     * @param processInstance
     * @param activityID
     * @return
     */
    @Override
    public List<ITaskInstance> findTaskInstancesForProcessInstance(java.lang.String processInstanceId, String activityID) {
        Iterator itr = this.taskInstanceStorage.values().iterator();
        List<ITaskInstance> taskInstances = new ArrayList<ITaskInstance>();
        while (itr.hasNext()) {
            TaskInstance taskInst = (TaskInstance) itr.next();
//            System.out.println("============Inside MemoryPersistenceService.findTaskInstance:: taskInstance.getProcessInstance is " + taskInst.getProcessInstance());
            if (taskInst.getProcessInstance() != null && taskInst.getProcessInstance().getId().equals(processInstance.getId())) {
                if (activityID != null && !activityID.equals("") && taskInst.getActivityId().equals(activityID)) {
                    taskInstances.add(taskInst);
                } else if (activityID == null || activityID.equals("")) {
                    taskInstances.add(taskInst);
                }
            }
        }

        return taskInstances;

    }

//    public List<ITaskInstance> findTaskInstances(String activityID) {
//        Iterator itr = this.taskInstanceStorage.values().iterator();
//        List<ITaskInstance> taskInstances = new ArrayList<ITaskInstance>();
//        while (itr.hasNext()) {
//            TaskInstance taskInst = (TaskInstance) itr.next();
//
//            if (activityID != null && !activityID.equals("") && taskInst.getActivityId().equals(activityID)) {
//                taskInstances.add(taskInst);
//            }
//        }
//        return taskInstances;
//    }
    @Override
    public List<IToken> findTokensForProcessInstance(String processInstanceId, String nodeId) {
        Iterator itr = this.tokenStorage.values().iterator();
        List<IToken> tokens = new ArrayList<IToken>();
        while (itr.hasNext()) {
            Token token = (Token) itr.next();
            if (token.getProcessInstance() != null && token.getProcessInstance().getId().equals(processInstance.getId()) && token.getNodeId().equals(nodeId)) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    public List<IWorkItem> findWorkItemsForTaskInstance(String taskInstanceId) {
        Iterator itr = this.workItemStorage.values().iterator();
        List<IWorkItem> workitems = new ArrayList<IWorkItem>();
        while (itr.hasNext()) {
            WorkItem wi = (WorkItem) itr.next();
            if (wi.getTaskInstance() != null && wi.getTaskInstance().getId().equals(taskInstance.getId())) {
                workitems.add(wi);
            }

        }
        return workitems;
    }

    public IWorkItem findWorkItemById(String id) {

        return this.workItemStorage.get(id);
    }

    public List<IWorkItem> findWorkItemForTask(String taskid) {
//        WorkItem wi = ;
        List<ITaskInstance> taskInstList = this.findTaskInstanceByTaskId(taskid);
        List<IWorkItem> result = new Vector<IWorkItem>();
        for (int i = 0; i < taskInstList.size(); i++) {
            ITaskInstance taskInst = taskInstList.get(i);
            List<IWorkItem> tmp = this.findWorkItemsForTaskInstance(null,(TaskInstance) taskInst);
            result.addAll(tmp);
        }
        return result;
    }

    public List<ITaskInstance> findTaskInstanceByTaskId(String taskId) {
        Iterator taskInstanceIterator = this.taskInstanceStorage.values().iterator();
        List<ITaskInstance> result = new Vector<ITaskInstance>();
        while (taskInstanceIterator.hasNext()) {
            TaskInstance taskInst = (TaskInstance) taskInstanceIterator.next();
            if (taskInst.getTaskId().equals(taskId)) {
                result.add(taskInst);
            }
        }
        return result;
    }

    public ITaskInstance findTaskInstanceById(String id) {
        return this.taskInstanceStorage.get(id);
    }

    public void addStorageChangeListenser(IStorageChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removeStorageChangeListener(IStorageChangeListener listener) {
        this.listeners.remove(listener);
    }

    private void fireStorageChangedEvent(StorageChangedEvent e) {
        for (int i = 0; i < listeners.size(); i++) {
            IStorageChangeListener listener = listeners.get(i);
            listener.onStorageChanged(e);
        }
    }

    public Hashtable getJoinPointStorage() {
        return joinPointStorage;
    }

    public Hashtable getProcessInstanceStorage() {
        return processInstanceStorage;
    }

    public Hashtable getTaskInstanceStorage() {
        return taskInstanceStorage;
    }

    public Hashtable getTokenStorage() {
        return tokenStorage;
    }

    public Hashtable getWorkItemStorage() {
        return workItemStorage;
    }

    public void clearAll() {
        this.joinPointStorage.clear();
        this.processInstanceStorage.clear();
        this.taskInstanceStorage.clear();
        this.tokenStorage.clear();
        this.workItemStorage.clear();

        StorageChangedEvent e = new StorageChangedEvent(this);
        this.fireStorageChangedEvent(e);
    }

    /**
     * 删除同一个process的所有实例，子流程实例除外
     * @param processId
     */
    public void clearAll4Process(String processId) {
        IProcessInstance processInstance = null;
        List<IProcessInstance> processInstanceList = findProcessInstanceByProcessId(processId);
        for (int i = 0; i < processInstanceList.size(); i++) {
            processInstance = processInstanceList.get(i);
            if (processInstance.getParentTaskInstanceId() != null && !processInstance.getParentTaskInstanceId().trim().equals("")) {
                continue;
            }
            clearAll4ProcessInstance(processInstance);
        }
        StorageChangedEvent e = new StorageChangedEvent(this);
        this.fireStorageChangedEvent(e);
    }

    private void clearAll4ProcessInstance(IProcessInstance processInstance) {
        this.processInstanceStorage.remove(processInstance.getId());

        List<ITaskInstance> taskInstanceList = this.findTaskInstancesForProcessInstance( null,null);
        for (int j = 0; j < taskInstanceList.size(); j++) {
            ITaskInstance taskInstance = taskInstanceList.get(j);
            this.taskInstanceStorage.remove(taskInstance.getId());

            List<IWorkItem> workitemList = this.findWorkItemsForTaskInstance(null,(TaskInstance) taskInstance);
            for (int k = 0; k < workitemList.size(); k++) {
                this.workItemStorage.remove(workitemList.get(k).getId());
            }
            List<IProcessInstance> childProcessInstanceList = findChildProcessInstance(taskInstance);
            for (int i = 0; i < childProcessInstanceList.size(); i++) {
                clearAll4ProcessInstance(childProcessInstanceList.get(i));
            }
        }


        List<IJoinPoint> joinpointList = this.findJoinPoint4ProcessInstance(processInstance);
        for (int m = 0; m < joinpointList.size(); m++) {
            this.joinPointStorage.remove(joinpointList.get(m));
        }

        List<IToken> tokenList = this.findTokens(processInstance);
        for (int n = 0; n < tokenList.size(); n++) {
            this.tokenStorage.remove(tokenList.get(n));
        }
    }

    private List<IProcessInstance> findChildProcessInstance(ITaskInstance taskInstance) {
        Iterator<IProcessInstance> iter = this.processInstanceStorage.values().iterator();
        List<IProcessInstance> result = new ArrayList<IProcessInstance>();
        while (iter.hasNext()) {
            IProcessInstance procInst = iter.next();
            if (procInst.getParentTaskInstanceId() != null && procInst.getParentTaskInstanceId().equals(taskInstance.getId())) {
                result.add(procInst);
            }
        }
        return result;
    }

    public List<IToken> findTokens(IProcessInstance processInstance) {
//        throw new UnsupportedOperationException("Not supported yet.");
        List<IToken> resultList = new ArrayList<IToken>();
        Iterator tokensIter = this.tokenStorage.values().iterator();
        while (tokensIter.hasNext()) {
            IToken token = (IToken) tokensIter.next();
            if (token.getProcessInstance().getId().equals(processInstance.getId())) {
                resultList.add(token);
            }
        }
        return resultList;
    }

//    public void tempDebugMethod() {
//        Iterator keyIterator = this.processInstanceStorage.keySet().iterator();
//        while (keyIterator.hasNext()) {
//            String id = (String) keyIterator.next();
//            IProcessInstance processInstance = (IProcessInstance) this.processInstanceStorage.get(id);
//            System.out.println("ProcessInstance:" + processInstance);
//        }
//    }
    public List<IProcessInstance> findProcessInstanceByProcessId(String processId) {
        Iterator keyIterator = this.processInstanceStorage.keySet().iterator();
        List<IProcessInstance> result = new ArrayList<IProcessInstance>();
        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            IProcessInstance processInstance = (IProcessInstance) this.processInstanceStorage.get(key);
            if (processInstance.getProcessId().equals(processId)) {
                result.add(processInstance);
            }
        }
        return result;
    }
}
