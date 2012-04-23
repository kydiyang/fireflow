package org.fireflow.service.human.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.config.ReassignConfig;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ReassignConfigPersister;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.service.human.HumanService;

public abstract class AbsAssignmentHandler implements AssignmentHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = 433878253467183907L;
	

	public abstract List<User> getPotentialOwners(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance);
	public abstract List<User> getReaders(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance);
	public abstract List<User> getAdministrators(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance);
	public abstract WorkItemAssignmentStrategy getAssignmentStrategy(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity);
	
	public List<WorkItem> assign(WorkflowSession session,
			ActivityInstance activityInstance,WorkItemManager workItemManager,Object theActivity,
			ServiceBinding serviceBinding, ResourceBinding resourceBinding)
			throws EngineException {

		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessInstance processInstance = session.getCurrentProcessInstance();

		List<WorkItem> result = new ArrayList<WorkItem>();
		
		Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();

		List<User> potentialOwners =  this.getPotentialOwners(session,resourceBinding,theActivity,processInstance,activityInstance);		
		if (potentialOwners==null || potentialOwners.size()==0){
			//通知业务领导进行处理
			List<User> administrators = this.getAdministrators(session,resourceBinding,theActivity,processInstance,activityInstance);	
			if (administrators==null || administrators.size()==0){
				//TODO 赋值给Fireflow内置用户，并记录警告信息
				WorkItem wi = workItemManager.createWorkItem(session, processInstance, activityInstance, FireWorkflowSystem.getInstance(),theActivity, null);
				result.add(wi);
			}else{
				//这种情况下，ASSIGNMENT_STRATEGY固定为WorkItem.ASSIGNMENT_ANY
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, WorkItemAssignmentStrategy.ASSIGN_TO_ANY);
				
				for (User user : administrators) {					
					WorkItem wi = workItemManager.createWorkItem(session,
							processInstance, activityInstance, user, theActivity,values);

					result.add(wi);
					
					List<User> agents = findReassignTo(runtimeContext, activityInstance
							.getProcessId(), activityInstance.getProcessType(),
							activityInstance.getNodeId(), user.getId());
					if (agents != null && agents.size() != 0) {

						List<WorkItem> agentWorkItems = workItemManager
								.reassignWorkItemTo(session, wi, agents,
										WorkItem.REASSIGN_AFTER_ME,
										WorkItemAssignmentStrategy.ASSIGN_TO_ANY,theActivity);
						
						result.addAll(agentWorkItems);
					}

				}
			}
		}else{
			if (this.getAssignmentStrategy(session,resourceBinding,theActivity)!=null){
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, this.getAssignmentStrategy(session,resourceBinding,theActivity));
			}else{
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, WorkItemAssignmentStrategy.ASSIGN_TO_ANY);
			}
			for (User user : potentialOwners) {
				
				WorkItem wi = workItemManager.createWorkItem(session,
						processInstance, activityInstance, user,theActivity, values);
				result.add(wi);
				
				List<User> agents = findReassignTo(runtimeContext, activityInstance
						.getProcessId(), activityInstance.getProcessType(),
						activityInstance.getNodeId(), user.getId());
				if (agents != null && agents.size() != 0) {

					List<WorkItem> agentWorkItems = workItemManager
							.reassignWorkItemTo(session, wi, agents,
									WorkItem.REASSIGN_AFTER_ME,
									WorkItemAssignmentStrategy.ASSIGN_TO_ANY,theActivity);
					
					result.addAll(agentWorkItems);
				}				
			}			
		}
		

		List<User> readers = this.getReaders(session,resourceBinding,theActivity,processInstance,activityInstance);
		if (readers != null && readers.size() > 0) {
			values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, WorkItemAssignmentStrategy.ASSIGN_TO_ANY);
			values.put(WorkItemProperty.STATE, WorkItemState.READONLY);
			for (User user : readers) {
				WorkItem wi = workItemManager.createWorkItem(session,
						processInstance, activityInstance, user,theActivity, values);

				result.add(wi);
			}
		}
		return result;
	}
	protected List<User> findReassignTo(RuntimeContext rtCtx,String processId,String processType,String activityId,String userId){
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, processType);
		ReassignConfigPersister persister = persistenceService.getReassignConfigPersister();
		
		List<ReassignConfig> configs = persister.findReassignConfig(processId, processType, activityId,userId);
		
		if (configs==null || configs.size()==0) return null;
		
		List<User> agents = new ArrayList<User>();
		OUSystemConnector ousystem = rtCtx.getEngineModule(OUSystemConnector.class, processType);
		for (ReassignConfig config : configs){
			User u = ousystem.findUserById(config.getAgentId());
			agents.add(u);
		}
		return agents;
	}	

}
