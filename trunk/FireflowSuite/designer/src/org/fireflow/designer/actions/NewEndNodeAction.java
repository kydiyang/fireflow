package org.fireflow.designer.editpanel.graph.actions;

import java.awt.event.ActionEvent;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;

import javax.swing.Icon;
import org.fireflow.designer.actions.INewWfElementPosition;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
import org.fireflow.designer.util.DesignerConstant;
import org.fireflow.model.ExtendedAttributeNames;
import org.openide.explorer.ExplorerManager;

public class NewEndNodeAction extends AbstractAction implements INewWfElementPosition {

    Point2D newWfElementPoition;
    ExplorerManager explorerManager = null;
    WorkflowProcessElement workflowProcessElement = null;

    public NewEndNodeAction(ExplorerManager explMgr, String title, Icon image) {
        super(title, image);
        this.explorerManager = explMgr;
        this.workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];
    }

    public void actionPerformed(ActionEvent e) {
        Map<String, String> attributeMap = new HashMap<String, String>();
        attributeMap.put(ExtendedAttributeNames.BOUNDS_WIDTH, DesignerConstant.DEFAULT_SYNCHRONIZER_RADIUS);
        attributeMap.put(ExtendedAttributeNames.BOUNDS_HEIGHT, DesignerConstant.DEFAULT_SYNCHRONIZER_RADIUS);
        if (this.newWfElementPoition != null) {
            attributeMap.put(ExtendedAttributeNames.BOUNDS_X, Integer.toString((int)this.newWfElementPoition.getX()));
            attributeMap.put(ExtendedAttributeNames.BOUNDS_Y, Integer.toString((int)this.newWfElementPoition.getY()));
        }else{
            attributeMap.put(ExtendedAttributeNames.BOUNDS_X, Integer.toString(0));
            attributeMap.put(ExtendedAttributeNames.BOUNDS_Y, Integer.toString(0));            
        }
        workflowProcessElement.createChild(IFPDLElement.END_NODE, attributeMap);
        this.newWfElementPoition = null;
    }

    public void setPosition(Point2D p) {
        this.newWfElementPoition = p;
    }

    public Point2D getPosition() {
        return this.newWfElementPoition;
    }
}
