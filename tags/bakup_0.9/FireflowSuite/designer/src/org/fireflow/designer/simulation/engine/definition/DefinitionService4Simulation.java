/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.engine.definition;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.definition.DefinitionService4FileSystem;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.model.WorkflowProcess;

/**
 *
 * @author chennieyun
 */
public class DefinitionService4Simulation extends DefinitionService4FileSystem {

    public WorkflowDefinition setWorkflowProcess(WorkflowProcess process)throws EngineException {
//        workflowDefinitionMap.put(process.getName(), process);
        WorkflowDefinition4Simulation workflowDef = new WorkflowDefinition4Simulation();
        workflowDef.setVersion(new Integer(1));

        workflowDef.setWorkflowProcess(process);

        String latestVersionKey = process.getId() + "_V_" + workflowDef.getVersion();
        workflowDefinitionMap.put(latestVersionKey, workflowDef);
        latestVersionKeyMap.put(process.getId(), latestVersionKey);
        return workflowDef;
    }

//    public void reloadWorkflowProcess() {
//        this.activityMap.clear();
//        this.dataFieldMap.clear();
//        this.taskMap.clear();
//        this.transitionMap.clear();
//
//        List<WorkflowProcess> workflowProcessList = this.getAllWorkflowProcesses();
//        for (int i = 0; i < workflowProcessList.size(); i++) {
//            WorkflowProcess process = workflowProcessList.get(i);
//
//            List<Activity> activities = process.getActivities();
//            for (int k = 0; activities != null && k < activities.size(); k++) {
//                Activity activity = activities.get(k);
//                activityMap.put(activity.getId(), activity);
//
//                List<Task> tasks = activity.getTasks();
//                for (int j = 0; tasks != null && j < tasks.size(); j++) {
//                    Task task = tasks.get(j);
//                    taskMap.put(task.getId(), task);
//                }
//            }
//
//            List<Transition> transitions = process.getTransitions();
//            for (int k = 0; transitions != null && k < transitions.size(); k++) {
//                Transition trans = transitions.get(k);
//                transitionMap.put(trans.getId(), trans);
//            }
//
//            List<DataField> datafields = process.getDataFields();
//            for (int k = 0; datafields != null && k < datafields.size(); k++) {
//                DataField df = datafields.get(k);
//                dataFieldMap.put(df.getId(), df);
//            }
//        }
//    }
}
