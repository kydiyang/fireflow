/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.datamodel.element;

import java.awt.Component;
import java.util.Map;
import javax.swing.Action;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.properties.editor.WorkflowProcessEditorPane;
import org.fireflow.model.WorkflowProcess;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.nodes.Node;

/**
 *
 * @author chennieyun
 */
public class WorkflowProcessElement extends AbstractNode implements IFPDLElement {

    private Component editor = null;

    public IFPDLElement createChild(String childType, Map<String, String> extendAtributes) {
        if (IFPDLElement.ACTIVITY.equals(childType)) {

            Node activitiesElement = this.getChildren().findChild(IFPDLElement.ACTIVITIES);
            return ((IFPDLElement) activitiesElement).createChild(childType, extendAtributes);
        } else if (IFPDLElement.DATAFIELD.equals(childType)) {
            Node activitiesElement = this.getChildren().findChild(IFPDLElement.DATAFIELDS);
            return ((IFPDLElement) activitiesElement).createChild(childType, extendAtributes);
        } else if (IFPDLElement.SYNCHRONIZER.equals(childType)) {
            Node activitiesElement = this.getChildren().findChild(IFPDLElement.SYNCHRONIZERS);
            return ((IFPDLElement) activitiesElement).createChild(childType, extendAtributes);
        } else if (IFPDLElement.END_NODE.equals(childType)) {
            Node activitiesElement = this.getChildren().findChild(IFPDLElement.END_NODES);
            return ((IFPDLElement) activitiesElement).createChild(childType, extendAtributes);
        } else if (IFPDLElement.TRANSITION.equals(childType)) {
            Node activitiesElement = this.getChildren().findChild(IFPDLElement.TRANSITIONS);
            return ((IFPDLElement) activitiesElement).createChild(childType, extendAtributes);
        }
        return null;
    }

    public void deleteSelf() {

    }

    public Object getContent() {
        return this.getLookup().lookup(WorkflowProcess.class);
    }
//    FPDLDataObject fpdlDataObject = null;
//    public WorkflowProcessElement(FPDLDataObject argFpdlDataObject,WorkflowProcess workflowProcess) {
//        this(new WorkflowProcessElementChildren(), Lookups.singleton(workflowProcess));
//        this.fpdlDataObject = argFpdlDataObject;
//        
//        
//    }
    public WorkflowProcessElement(Lookup lookup) {
        super(new WorkflowProcessElementChildren(), lookup);
        WorkflowProcess workflowProcess = lookup.lookup(WorkflowProcess.class);
    }

    public String getElementType() {
        return IFPDLElement.WORKFLOWPROCESS;
    }

    @Override
    public String getName() {
        return this.getLookup().lookup(WorkflowProcess.class).getName();
    }

    @Override
    public String getDisplayName() {
        return this.getLookup().lookup(WorkflowProcess.class).toString();
    }

    public Component getEditor() {
        if (editor == null) {
            editor = new WorkflowProcessEditorPane(this,this.getLookup().lookup(WorkflowProcess.class),
                    this.getLookup().lookup(FPDLDataObject.class));
        }
        return editor;
    }

    /*
    @Override
    protected Sheet createSheet() {
    WorkflowProcess workflowProcess = this.getLookup().lookup(WorkflowProcess.class);
    Sheet sheet = Sheet.createDefault();
    Sheet.Set basicPropertiesSet = Sheet.createPropertiesSet();
    basicPropertiesSet.setName("Basic Properties");
    basicPropertiesSet.setDisplayName("Basic Properties");
    sheet.put(basicPropertiesSet);
    //        Sheet.Set eventPropertiesSet = Sheet.createPropertiesSet();
    //        eventPropertiesSet.setName("Event");
    //        eventPropertiesSet.setDisplayName("Event");
    //        sheet.put(eventPropertiesSet);
    //
    //        Sheet.Set extendedAttributePropertiesSet = Sheet.createPropertiesSet();
    //        extendedAttributePropertiesSet.setName("Extended Properties");
    //        extendedAttributePropertiesSet.setDisplayName("Extended Properties");
    //        sheet.put(extendedAttributePropertiesSet);
    try {
    PropertySupport.Reflection nameProp = new PropertySupport.Reflection(workflowProcess, String.class, "getName", "setName");
    PropertySupport.Reflection descriptionProp = new PropertySupport.Reflection(workflowProcess, String.class, "getDescription", "setDescription");
    PropertySupport.Reflection displayNameProp = new PropertySupport.Reflection(workflowProcess, String.class, "getDisplayName","setDisplayName");
    PropertySupport.Reflection resourceFileProp = new PropertySupport.Reflection(workflowProcess, String.class, "getResourceFile","setResourceFile");
    PropertySupport.Reflection resourceManagerProp = new PropertySupport.Reflection(workflowProcess, String.class, "getResourceManager","setResourceManager");
    ExtendedAttributesProperty extendedAttrProp = new ExtendedAttributesProperty(workflowProcess.getExtendedAttributes());
    nameProp.setName("名称");
    descriptionProp.setName("描述");
    displayNameProp.setName("标签");
    resourceManagerProp.setName("资源管理器类名");
    resourceFileProp.setName("资源文件URL");
    extendedAttrProp.setName("扩展属性");
    basicPropertiesSet.put(nameProp);
    basicPropertiesSet.put(descriptionProp);
    basicPropertiesSet.put(displayNameProp);
    basicPropertiesSet.put(resourceManagerProp);
    basicPropertiesSet.put(resourceFileProp);
    basicPropertiesSet.put(extendedAttrProp);
    } catch (NoSuchMethodException ex) {
    ErrorManager.getDefault();
    }
    return sheet;
    }
     */
    /*
    public void createActivity() {
    WorkflowProcess workflowProcess = this.getLookup().lookup(WorkflowProcess.class);
    int size = workflowProcess.getActivities().size() + 1;
    Activity activity = new Activity(workflowProcess, "activity" + size);
    workflowProcess.getActivities().add(activity);
    System.out.println("activities size is " + workflowProcess.getActivities().size());
    ((IChildrenOperation) this.getChildren().findChild("Activities").getChildren()).rebuildChildren();
    }
     */
    @Override
    public Action[] getActions(boolean popup) {
        FPDLDataObject dataObj = this.getLookup().lookup(FPDLDataObject.class);
        if (dataObj!=null)return dataObj.getActions();
        return new Action[]{};
    }

    @Override
    public String toString() {
        return this.getLookup().lookup(WorkflowProcess.class).toString();
    }
}
