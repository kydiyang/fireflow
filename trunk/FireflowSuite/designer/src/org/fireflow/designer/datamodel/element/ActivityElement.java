/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.awt.Component;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import javax.swing.Action;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.properties.editor.ActivityEditorPane;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.Transition;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class ActivityElement extends AbstractNode implements IFPDLElement {

    Component editor = null;

    public IFPDLElement createChild(String childType, Map<String, String> extendAtributes) {
        Activity activity = this.getLookup().lookup(Activity.class);
        List tasksList = activity.getTasks();
        int num = activity.getTasks().size();
        String taskName = null;
        boolean haveSameName = false;
        do {
            haveSameName = false;
            num++;
            taskName = "Task" + num;
            for (int i = 0; i < tasksList.size(); i++) {
                Task tsk = (Task) tasksList.get(i);
                if (tsk.getName().equals(taskName)) {
                    haveSameName = true;
                    break;
                }
            }
        } while (haveSameName);

        Task task = new Task(activity, taskName);
        task.setSn(UUID.randomUUID().toString());
        if (IFPDLElement.TOOL_TASK.equals(childType)) {
            task.setType(Task.TOOL);
        } else if (IFPDLElement.SUBFLOW_TASK.equals(childType)) {
            task.setType(Task.SUBFLOW);
        } else {
            task.setType(Task.FORM);
        }

        if (extendAtributes != null) {
            task.getExtendedAttributes().putAll(extendAtributes);
        }

        activity.getTasks().add(task);
        
        ((IChildrenOperation) this.getChildren()).rebuildChildren();

        return (IFPDLElement) this.getChildren().findChild(taskName);
    }

    public void deleteSelf() {
//        System.out.println("====删除Activity Element33");
        Activity activity = (Activity) this.getContent();
        WorkflowProcess workflowProcess = (WorkflowProcess) activity.getParent();
        workflowProcess.getActivities().remove(activity);

//        ExplorerManager explorerManager = this.getLookup().lookup(ExplorerManager.class);
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        ExplorerManager explorerManager = dataObj.getExplorerManager();
        WorkflowProcessElement workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];
        Node transitionsElement = workflowProcessElement.getChildren().findChild(IFPDLElement.TRANSITIONS);

        List transitionList = workflowProcess.getTransitions();
        List<TransitionElement> trans4Delete = new Vector<TransitionElement>();
        for (int i = 0; transitionList != null && i < transitionList.size(); i++) {
            Transition trans = (Transition) transitionList.get(i);
            if (trans.getFromNode().getId().equals(activity.getId()) || trans.getToNode().getId().equals(activity.getId())) {
                TransitionElement transitionElement = (TransitionElement) transitionsElement.getChildren().findChild(trans.getName());
                if (transitionElement != null) {
                    trans4Delete.add(transitionElement);
                }
            }
        }
        for (int i = 0; i < trans4Delete.size(); i++) {
            TransitionElement transitionElement = trans4Delete.get(i);
            transitionElement.deleteSelf();
        }

        Node parent = this.getParentNode();
        ((IChildrenOperation) parent.getChildren()).rebuildChildren();


    }

    public Object getContent() {
        return this.getLookup().lookup(Activity.class);
    }

    public ActivityElement(Lookup lookup) {
        super(new ActivityElementChildren(), lookup);
    }

    public String getElementType() {
        return IFPDLElement.ACTIVITY;
    }

    @Override
    public String getName() {
        Activity activity = this.getLookup().lookup(Activity.class);
        return activity.getName();
    }

    @Override
    public String getDisplayName() {
        Activity activity = this.getLookup().lookup(Activity.class);
        return activity.toString();
    }

    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        
        if (dataObj!=null)return dataObj.getActions();
        return new Action[]{};
    }

//    private class MyAction extends AbstractAction {
//
//        public MyAction() {
//            putValue(NAME, "Do Something");
//        }
//
//        public void actionPerformed(ActionEvent e) {
//
//            JOptionPane.showMessageDialog(null, "Hello from ");
//        }
//    }
    @Override
    public String toString() {
        Activity activity = this.getLookup().lookup(Activity.class);
        return activity.toString();
    }

    public Component getEditor() {
        if (editor == null) {
            editor = new ActivityEditorPane(this,this.getLookup().lookup(Activity.class),
                    this.getLookup().lookup(FPDLDataObject.class));
        }
        return editor;
    }
}
