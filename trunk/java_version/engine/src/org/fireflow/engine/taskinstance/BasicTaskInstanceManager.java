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
package org.fireflow.engine.taskinstance;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.kernel.IActivityInstance;

/**
 * @author 非也
 * 
 */
public class BasicTaskInstanceManager extends AbstractTaskInstanceManager {

    @Override
    protected void afterWorkItemCreated(IWorkItem workItem) {

    }

    @Override
    protected void beforeTaskInstanceTaskInstanceStarted(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance) {

    }

    @Override
    protected void afterTaskInstanceCompleted(IWorkflowSession currentSession, IProcessInstance processInstance, ITaskInstance taskInstance, IActivityInstance targetActivityInstance) {

    }

}
