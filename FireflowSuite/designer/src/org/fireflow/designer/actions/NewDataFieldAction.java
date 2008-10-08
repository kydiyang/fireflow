package org.fireflow.designer.actions;

import cn.bestsolution.tools.resourcesmanager.actions.*;
import java.awt.event.*;
import cn.bestsolution.tools.resourcesmanager.*;

import java.awt.geom.Point2D;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.fireflow.designer.datamodel.*;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
import org.fireflow.designer.outline.*;
import org.openide.explorer.ExplorerManager;

public class NewDataFieldAction extends AbstractAction implements INewWfElementPosition {

    Point2D newWfElementPoition;
    ExplorerManager explorerManager = null;
    WorkflowProcessElement workflowProcessElement = null;

    public NewDataFieldAction(ExplorerManager explMgr, String title, Icon image) {
        super(title, image);
        this.explorerManager = explMgr;
        this.workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];
    }

    public void actionPerformed(ActionEvent evt) {
        IFPDLElement newDataFieldElement = workflowProcessElement.createChild(IFPDLElement.DATAFIELD, null);
    }

    public void setPosition(Point2D p) {
        this.newWfElementPoition = p;
    }

    public Point2D getPosition() {
        return this.newWfElementPoition;
    }
}
