package org.fireflow.designer.properties.editor;

import javax.swing.JTabbedPane;


import org.fireflow.designer.properties.editor.ExtendAttributeEditor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import org.fireflow.designer.datamodel.FPDLDataObject;
//import org.fireflow.designer.properties.editor.ActivityEditor;
import org.fireflow.model.net.Activity;
import org.openide.nodes.AbstractNode;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ActivityEditorPane
      extends JTabbedPane {
   Activity activity;
   FPDLDataObject fpdlDataObject;
   AbstractNode theNode;
//   WorkflowProcess workflowProcess;

   public ActivityEditorPane(AbstractNode node ,Activity argActivity,
                             FPDLDataObject fpdlDataObj) {
       theNode = node;
      activity = argActivity;
      fpdlDataObject = fpdlDataObj;
//      workflowProcess = wkflwProcess;
      init();
   }

   void init() {
      ActivityEditor activityEdt = new ActivityEditor(theNode,
            activity, fpdlDataObject);
//      ActivityEventEditor eventEditor = new
//            ActivityEventEditor(activity, theMediator);
      ExtendAttributeEditor xAttrEdt = new
            ExtendAttributeEditor(activity, fpdlDataObject);
      this.add( activityEdt,"Activity Attribute");
//      this.add("Event",
//               eventEditor);

      this.add( xAttrEdt,"Extend Attribute");

      this.setFont(Utilities.windowsUIFont);
   }


   public Activity getActivity() {
      return this.activity;
   }
}