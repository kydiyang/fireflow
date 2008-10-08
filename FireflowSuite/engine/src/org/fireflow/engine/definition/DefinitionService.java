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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fireflow.model.DataField;
import org.fireflow.model.Task;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.fireflow.model.io.FPDLParserException;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Transition;

/**
 * @author chennieyun
 * 
 */
public class DefinitionService implements IDefinitionService {

    protected HashMap<String, WorkflowProcess> workflowProcessMap = new HashMap<String, WorkflowProcess>();// 流程名到流程定义的map
    protected HashMap<String, Activity> activityMap = new HashMap<String, Activity>();
    protected HashMap<String, Task> taskMap = new HashMap<String, Task>();
    protected HashMap<String, DataField> dataFieldMap = new HashMap<String, DataField>();
    protected HashMap<String, Transition> transitionMap = new HashMap<String, Transition>();
    /*
     * (non-Javadoc)
     * 
     * @see org.fireflow.engine.definition.IDefinitionService#getWorkflowProcess(java.lang.String)
     */

    public WorkflowProcess getWorkflowProcessByName(String name) {
        // TODO Auto-generated method stub
        return workflowProcessMap.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.fireflow.engine.definition.IDefinitionService#setDefinitionFiles(java.util.List)
     */
    public void setDefinitionFiles(List<String> definitionFileNames)
            throws IOException, FPDLParserException {
        if (definitionFileNames != null) {
            Dom4JFPDLParser parser = new Dom4JFPDLParser();
            for (int i = 0; i < definitionFileNames.size(); i++) {
                InputStream inStream = this.getClass().getResourceAsStream(
                        definitionFileNames.get(i).trim());
                if (inStream == null) {
                    throw new IOException("没有找到名称为" + definitionFileNames.get(i) + "的流程定义文件");
                }
                WorkflowProcess workflowProcess = parser.parse(inStream);
                workflowProcessMap.put(workflowProcess.getName(), workflowProcess);

                List<Activity> activities = workflowProcess.getActivities();
                for (int k = 0; activities != null && k < activities.size(); k++) {
                    Activity activity = activities.get(k);
                    activityMap.put(activity.getId(), activity);

                    List<Task> tasks = activity.getTasks();
                    for (int j = 0; tasks != null && j < tasks.size(); j++) {
                        Task task = tasks.get(j);
                        taskMap.put(task.getId(), task);
                    }
                }

                List<Transition> transitions = workflowProcess.getTransitions();
                for (int k = 0; transitions != null && k < transitions.size(); k++) {
                    Transition trans = transitions.get(k);
                    transitionMap.put(trans.getId(), trans);
                }

                List<DataField> datafields = workflowProcess.getDataFields();
                for (int k = 0; datafields != null && k < datafields.size(); k++) {
                    DataField df = datafields.get(k);
                    dataFieldMap.put(df.getId(), df);
                }

            }
        }

    }

    public List<WorkflowProcess> getAllWorkflowProcesses() {
        return new ArrayList(workflowProcessMap.values());
    }

    public WorkflowProcess getWorkflowProcessById(String id) {
        return workflowProcessMap.get(id);
    }

    public Activity getActivityById(String id) {
        return activityMap.get(id);
    }

    public Task getTaskById(String id) {
        return taskMap.get(id);
    }

    public Transition getTransitionById(String id) {
        return transitionMap.get(id);
    }

    public DataField getDataFieldById(String id) {
        return dataFieldMap.get(id);
    }
}
