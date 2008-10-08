/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.properties.editor;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import javax.swing.JTabbedPane;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.model.net.Transition;
import org.openide.nodes.AbstractNode;

/**
 *
 * @author chennieyun
 */
public class TransitionEditorPane extends JTabbedPane {
    Transition transition  = null;
    FPDLDataObject fpdlDataObject = null;
    public TransitionEditorPane(AbstractNode node,Transition transition ,FPDLDataObject fpdlDataObj){
        this.transition = transition;
        this.fpdlDataObject = fpdlDataObj;
        init( node);
    }
    
        private void init(AbstractNode node) {
        EntityEditor entityEdit = new TransitionEditor(node,transition,
                fpdlDataObject);

        ExtendAttributeEditor xAttrEdt = new ExtendAttributeEditor(transition, fpdlDataObject);

        this.add(entityEdit,"Transition Attribute");
//      this.add("Activity Attribute", activityEdt);
//      this.add("Event",
//               eventEditor);

        this.add(xAttrEdt,"Extend Attribute" );

        this.setFont(Utilities.windowsUIFont);
    }
}
