/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.awt.Component;
import java.util.List;
import java.util.UUID;
import javax.swing.Action;
import javax.swing.JPanel;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;

import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;

/**
 *
 * @author chennieyun
 */
public class ActivitiesElement extends AbstractNode implements IFPDLElement {

    public ActivitiesElement(Lookup lookup) {
        super(new ActivitiesElementChildren(), lookup);
    }

    public String getElementType() {
        return IFPDLElement.ACTIVITIES;
    }

    @Override
    public String getName() {
        return IFPDLElement.ACTIVITIES;
    }

    @Override
    public String getDisplayName() {
        return "流程环节";
    }

    public IFPDLElement createChild(String childType, java.util.Map<String,String> extendAtributes) {
//        System.out.println("==========Inside ActivitiesElement.createChild ,extendAttributes size is "+extendAtributes==null?0:extendAtributes.size());
        WorkflowProcess process = this.getLookup().lookup(WorkflowProcess.class);
        List activitiesList = process.getActivities();

        int num = activitiesList.size() ;
        String activityName = null;
        boolean haveSameName = false;
        do {
            haveSameName = false;         
            num++;
            activityName = "Activity" + num;            
//            System.out.println("activityName is "+activityName);
//            System.out.println("activitiesList.size() is "+activitiesList.size());
            for (int i = 0; i < activitiesList.size(); i++) {
                Activity act = (Activity) activitiesList.get(i);
                if (act.getName().equals(activityName)) {
                    haveSameName = true;
                    break;
                }
            }
        } while (haveSameName);



        Activity activity = new Activity(process, activityName);
        activity.setSn(UUID.randomUUID().toString());

        if (extendAtributes != null) {
            activity.getExtendedAttributes().putAll(extendAtributes);
        }

        process.getActivities().add(activity);

        ((IChildrenOperation) this.getChildren()).rebuildChildren();

        return (IFPDLElement) this.getChildren().findChild(activityName);

    }

    public void deleteSelf() {
    //activities 不允许delete self。
    }

    public Object getContent() {
        return this.getLookup().lookup(List.class);
    }

    public Component getEditor() {
        return new JPanel();
    }

    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        
        if (dataObj!=null)  return dataObj.getActions();
        return new Action[]{};
    }
}
