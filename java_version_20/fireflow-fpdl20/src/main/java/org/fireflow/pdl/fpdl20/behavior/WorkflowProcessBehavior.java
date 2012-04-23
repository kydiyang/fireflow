/**
 * Copyright 2007-2010 非也
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
package org.fireflow.pdl.fpdl20.behavior;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fireflow.engine.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.impl.InternalSessionAttributeKeys;
import org.fireflow.engine.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.data.Property;
import org.fireflow.pdl.fpdl20.misc.FpdlConstants;
import org.fireflow.pdl.fpdl20.process.Subflow;
import org.fireflow.pdl.fpdl20.process.Node;
import org.fireflow.pdl.fpdl20.process.WorkflowProcess;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 * @deprecated WorkflowProcess对象不具备执行功能，具体执行应该交给subflow
 */
public class WorkflowProcessBehavior implements WorkflowBehavior {

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#prepare(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#getCompensationHandler(java.lang.String)
	 */
	public CompensationHandler getCompensationHandler(String compensationCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * （2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#getCancellationHandler()
	 */
//	public CancellationHandler getCancellationHandler() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#getFaultHandler(java.lang.String)
	 */
	public FaultHandler getFaultHandler(String errorCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		// TODO Auto-generated method stub
		
	}

}
