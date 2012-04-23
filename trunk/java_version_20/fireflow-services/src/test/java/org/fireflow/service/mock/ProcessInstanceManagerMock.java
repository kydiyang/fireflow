package org.fireflow.service.mock;

import java.util.Date;
import java.util.Map;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.instancemanager.impl.AbsProcessInstanceManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.InvalidModelException;

public class ProcessInstanceManagerMock extends AbsProcessInstanceManager {

	public ProcessInstance createProcessInstance(WorkflowSession session,
			Object workflowProcess, String bizId, ProcessDescriptor descriptor,
			ActivityInstance parentActivityInstance) {
//		WorkflowProcess fpdl20Process = (WorkflowProcess)workflowProcess;
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		CalendarService calendarService = context.getDefaultEngineModule(CalendarService.class);
		User u = sessionLocal.getCurrentUser();
		
		ProcessInstanceImpl processInstance = new ProcessInstanceImpl();
		processInstance.setProcessId(descriptor.getProcessId());
		processInstance.setVersion(descriptor.getVersion());
		processInstance.setProcessType(descriptor.getProcessType());
		processInstance.setBizId(bizId);
		processInstance.setProcessName(descriptor.getName());
		String displayName = descriptor.getDisplayName();
		processInstance.setProcessDisplayName((displayName==null||displayName.trim().equals(""))?descriptor.getName():displayName);
//		processInstance.setBizCategory(fpdl20Process.getBizCategory());
		processInstance.setState(ProcessInstanceState.INITIALIZED);

		Date now = calendarService.getSysDate();
		processInstance.setCreatedTime(now);
		processInstance.setCreatorId(u.getId());
		processInstance.setCreatorName(u.getName());
		processInstance.setCreatorDeptId(u.getDeptId());
		processInstance.setCreatorDeptName(u.getDeptName());
		
		if (parentActivityInstance!=null){
			processInstance.setParentActivityInstanceId(parentActivityInstance.getId());
			processInstance.setParentProcessInstanceId(parentActivityInstance.getProcessInstanceId());
			processInstance.setParentScopeId(parentActivityInstance.getScopeId());
		}
		
//		if (fpdl20Process.getDuration()!=null && fpdl20Process.getDuration().getValue()>0){
//			Date expiredDate = calendarService.dateAfter(now, fpdl20Process.getDuration());
//			processInstance.setExpiredTime(expiredDate);
//		}

//		processInstance.setExpiredTime(time);
		
		return processInstance;
	}

	public void fireProcessInstanceEvent(WorkflowSession session,
			ProcessInstance processInstance, Object workflowElement,
			ProcessInstanceEventTrigger eventType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.instancemanager.ProcessInstanceManager#startProcess(org.fireflow.engine.WorkflowSession, java.lang.String, int, java.lang.String, java.lang.String, java.util.Map)
	 */
	public ProcessInstance startProcess(WorkflowSession session,
			String workflowProcessId, int version, String processType,
			String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		// TODO Auto-generated method stub
		return null;
	}

}
