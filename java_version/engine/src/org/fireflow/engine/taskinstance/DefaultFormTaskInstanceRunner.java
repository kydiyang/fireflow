/*
 * Copyright 2007-2009 非也
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

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package org.fireflow.engine.taskinstance;

import java.util.List;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.beanfactory.IBeanFactory;
import org.fireflow.engine.impl.WorkflowSession;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.FormTask;
import org.fireflow.model.Task;
import org.fireflow.model.resource.Participant;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 21, 2009
 */
public class DefaultFormTaskInstanceRunner implements ITaskInstanceRunner {

    public void run(IWorkflowSession currentSession, RuntimeContext runtimeContext, IProcessInstance processInstance,
            ITaskInstance taskInstance) throws EngineException, KernelException {
                if (!Task.FORM.equals(taskInstance.getTaskType())){
                    throw new EngineException(processInstance,
                            taskInstance.getActivity(),
                            "DefaultFormTaskInstanceRunner：TaskInstance的任务类型错误，只能为FORM类型");
                }

                DynamicAssignmentHandler dynamicAssignmentHandler = ((WorkflowSession)currentSession).consumeCurrentDynamicAssignmentHandler();
                FormTask task = (FormTask)taskInstance.getTask();
                // performer(id,name,type,handler)
                Participant performer = task.getPerformer();
                if (performer == null || performer.getAssignmentHandler().trim().equals("")) {
                    throw new EngineException(processInstance,
                            taskInstance.getActivity(),
                            "流程定义错误，Form类型的 task必须指定performer及其AssignmentHandler");
                }
                assign(currentSession,processInstance,runtimeContext,taskInstance, task, performer, dynamicAssignmentHandler);
    }

    protected void assign(IWorkflowSession currentSession,IProcessInstance processInstance, RuntimeContext runtimeContext,ITaskInstance taskInstance, FormTask formTask, Participant part, DynamicAssignmentHandler dynamicAssignmentHandler) throws EngineException, KernelException {
        //如果有指定的Actor，则按照指定的Actor分配任务
        if (dynamicAssignmentHandler != null) {

            dynamicAssignmentHandler.assign((IAssignable) taskInstance, part.getName());

        }
        else {

        	IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        	List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(taskInstance.getProcessInstanceId(), taskInstance.getActivityId());
        	ITaskInstance theLastCompletedTaskInstance = null;

        	for (int i=0;taskInstanceList!=null && i<taskInstanceList.size();i++){
        		ITaskInstance tmp = (ITaskInstance)taskInstanceList.get(i);
        		if (tmp.getId().equals(taskInstance.getId()))continue;
                if (!tmp.getTaskId().equals(taskInstance.getTaskId()))continue;
                if (tmp.getState().intValue()!=ITaskInstance.COMPLETED)continue;
        		if ( theLastCompletedTaskInstance==null){
        			theLastCompletedTaskInstance = tmp;
        		}else{
        			if (theLastCompletedTaskInstance.getStepNumber().intValue()<tmp.getStepNumber().intValue()){
        				theLastCompletedTaskInstance=tmp;
        			}
        		}
        	}

        	//如果是循环且LoopStrategy==REDO，则分配个上次完成该工作的操作员
            if (theLastCompletedTaskInstance!=null && (FormTask.REDO.equals(formTask.getLoopStrategy()) || currentSession.isInWithdrawOrRejectOperation())) {
            	List workItemList = persistenceService.findCompletedWorkItemsForTaskInstance(theLastCompletedTaskInstance.getId());
            	ITaskInstanceManager taskInstanceMgr = runtimeContext.getTaskInstanceManager();
                for (int k = 0; k < workItemList.size(); k++) {
                    IWorkItem completedWorkItem = (IWorkItem) workItemList.get(k);

                    IWorkItem newFromWorkItem = taskInstanceMgr.createWorkItem(currentSession,processInstance,taskInstance, completedWorkItem.getActorId());
                    newFromWorkItem.claim();
                }
            } else {
                IBeanFactory beanFactory = runtimeContext.getBeanFactory();
                IAssignmentHandler assignmentHandler = (IAssignmentHandler) beanFactory.getBean(part.getAssignmentHandler());
                ((IAssignmentHandler) assignmentHandler).assign((IAssignable) taskInstance, part.getName());
            }
        }


    }
}
