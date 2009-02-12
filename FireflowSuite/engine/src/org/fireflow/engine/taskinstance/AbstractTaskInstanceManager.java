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
package org.fireflow.engine.taskinstance;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkflowSessionAware;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.ou.IAssignmentHandler;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kenel.IActivityInstance;
import org.fireflow.kenel.IToken;
import org.fireflow.kenel.KenelException;
import org.fireflow.model.net.Activity;
import org.fireflow.model.Task;

import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.calendar.ICalendarService;
import org.fireflow.engine.impl.WorkflowSession;
import org.fireflow.engine.ou.DynamicAssignmentHandler;
import org.fireflow.engine.ou.IAssignable;
import org.fireflow.model.Duration;
import org.fireflow.model.reference.Participant;

/**
 * @author chennieyun
 * 
 */
public abstract class AbstractTaskInstanceManager implements
        ITaskInstanceManager {

    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }

    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#archiveTaskInstances(org.fireflow.kenel.IActivityInstance)
     */

    public void archiveTaskInstances(IActivityInstance activityInstance) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#createTaskInstances(org.fireflow.kenel.IActivityInstance)
     */
    public void createTaskInstances(IToken token,
            IActivityInstance activityInstance) throws EngineException, KenelException {

        // TODO Auto-generated method stub
        Activity activity = activityInstance.getActivity();
        IPersistenceService persistenceService = rtCtx.getPersistenceService();
        ICalendarService calService = rtCtx.getCalendarService();
        
        IProcessInstance processInstance = token.getProcessInstance();
        WorkflowSession workflowSession = (WorkflowSession) ((IWorkflowSessionAware) processInstance).getCurrentWorkflowSession();
//        System.out.println("====.... workflowSession of processInstance is "+workflowSession+"; hashCode is "+(workflowSession==null?0:workflowSession.hashCode()));
//        System.out.println("====....token is "+token.hashCode());

        if (workflowSession==null){
                      throw new EngineException(
                            "The workflow session in process instance can NOT be null");
        }

        for (int i = 0; i < activity.getTasks().size(); i++) {
            Task task = activity.getTasks().get(i);
            // 1、创建Task实例，并设置工作流系统定义的属性
            ITaskInstance taskInstance = this.createTaskInstance(token, task,
                    activity);
            
            ((TaskInstance) taskInstance).setProcessInstanceId(processInstance.getId());
            ((TaskInstance) taskInstance).setProcessId(processInstance.getProcessId());
            ((TaskInstance) taskInstance).setVersion(processInstance.getVersion());
            ((TaskInstance) taskInstance).setActivityId(activity.getId());
            ((TaskInstance) taskInstance).setAssignmentStrategy(task.getAssignmentStrategy());
            ((TaskInstance) taskInstance).setCreatedTime(calService.getSysDate());
            ((TaskInstance) taskInstance).setDisplayName(task.getDisplayName());
            ((TaskInstance) taskInstance).setName(task.getName());

            ((TaskInstance) taskInstance).setState(TaskInstance.INITIALIZED);

            ((TaskInstance) taskInstance).setTaskId(task.getId());
            ((TaskInstance) taskInstance).setTaskType(task.getType());
            
            ((IRuntimeContextAware) taskInstance).setRuntimeContext(rtCtx);
            ((IWorkflowSessionAware)taskInstance).setCurrentWorkflowSession(workflowSession);
            //计算超时
            Duration duration = task.getDuration();

            if (duration != null && calService != null) {
                ((TaskInstance) taskInstance).setExpiredTime(calService.dateAfter(calService.getSysDate(), duration));
            }

            // 2、保存实例taskInstance
            persistenceService.saveOrUpdateTaskInstance(taskInstance);
//			token.getProcessInstance().getTaskInstances().add(taskInstance);

            // 3、分配任务

            if (task.getType().equals(Task.FORM)) {
                DynamicAssignmentHandler dynamicAssignmentHandler = workflowSession.consumeCurrentDynamicAssignmentHandler();
                // performer(id,name,type,handler)
                Participant performer = task.getPerformer();
                if (performer == null || performer.getAssignmentHandler().trim().equals("")) {
                    throw new EngineException(
                            "流程定义错误，Form类型的 task必须指定performer及其AssignmentHandler");
                }
//                System.out.println("====Inside abstractTaskInstanceManager: dynamicAssignmentHandler is "+dynamicAssignmentHandler);
                assign(taskInstance, performer, dynamicAssignmentHandler);
            }

            // 4、如果startmode=auto，则启动任务,仅对subflow和Tool类型的task有效
            // 对于 manual类型的task ，只能够自动签收
            // startMode是否可以去掉？没有什么意义。mual类型的task的自动起动可以通过workitem的eventhandler实现

            if (Task.SUBFLOW.equals(task.getType()) || Task.TOOL.equals(task.getType())) {
                ((TaskInstance) taskInstance).start();
//                task.get
            }

        }
    }

    public abstract ITaskInstance createTaskInstance(IToken token, Task task,
            Activity activity) throws EngineException;

    // TODO asignmentHandler是否可以缓存？
    protected void assign(ITaskInstance taskInstance, Participant part, DynamicAssignmentHandler dynamicAssignmentHandler) {

        try {
            if (dynamicAssignmentHandler != null) {
                dynamicAssignmentHandler.assign((IAssignable) taskInstance, part.getName());
            } else {
                String asignmentHandlerClassName = part.getAssignmentHandler();
                Class clz = Class.forName(asignmentHandlerClassName);
                Object assignmentHandler = clz.newInstance();
                ((IAssignmentHandler) assignmentHandler).assign((IAssignable) taskInstance, part.getName());
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EngineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KenelException e) {
            e.printStackTrace();
        }

    }
}
