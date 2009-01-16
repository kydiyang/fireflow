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
package org.fireflow.engine.definition;

import java.util.List;
import org.fireflow.engine.IRuntimeContextAware;

/**
 * @author chennieyun
 *
 */
public interface IDefinitionService extends IRuntimeContextAware {
//	public WorkflowProcess getWorkflowProcessByName(String name);
//	public void setDefinitionFiles(List<String> definitionFileNames)throws IOException,FPDLParserException;
//	public List<WorkflowProcess> getAllWorkflowProcesses();
//        public WorkflowProcess getWorkflowProcessById(String id);
    
    public List<WorkflowDefinition> getAllLatestVersionOfWorkflowDefinitions();
    public WorkflowDefinition getWorkflowDefinitionByProcessIdAndVersion(String processId ,Integer version);
    public WorkflowDefinition getTheLatestVersionOfWorkflowDefinition(String processId);
}
