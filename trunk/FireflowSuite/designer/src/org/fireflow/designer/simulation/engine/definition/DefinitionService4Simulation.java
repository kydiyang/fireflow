/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.engine.definition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fireflow.engine.definition.DefinitionService;
import org.fireflow.model.DataField;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Transition;

/**
 *
 * @author chennieyun
 */
public class DefinitionService4Simulation extends DefinitionService {

    public void setWorkflowProcess(WorkflowProcess process) {
        workflowProcessMap.put(process.getName(), process);
    }

    public void reloadWorkflowProcess() {
        this.activityMap.clear();
        this.dataFieldMap.clear();
        this.taskMap.clear();
        this.transitionMap.clear();

        List<WorkflowProcess> workflowProcessList = this.getAllWorkflowProcesses();
        for (int i = 0; i < workflowProcessList.size(); i++) {
            WorkflowProcess process = workflowProcessList.get(i);

            List<Activity> activities = process.getActivities();
            for (int k = 0; activities != null && k < activities.size(); k++) {
                Activity activity = activities.get(k);
                activityMap.put(activity.getId(), activity);

                List<Task> tasks = activity.getTasks();
                for (int j = 0; tasks != null && j < tasks.size(); j++) {
                    Task task = tasks.get(j);
                    taskMap.put(task.getId(), task);
                }
            }

            List<Transition> transitions = process.getTransitions();
            for (int k = 0; transitions != null && k < transitions.size(); k++) {
                Transition trans = transitions.get(k);
                transitionMap.put(trans.getId(), trans);
            }

            List<DataField> datafields = process.getDataFields();
            for (int k = 0; datafields != null && k < datafields.size(); k++) {
                DataField df = datafields.get(k);
                dataFieldMap.put(df.getId(), df);
            }
        }
    }
}
