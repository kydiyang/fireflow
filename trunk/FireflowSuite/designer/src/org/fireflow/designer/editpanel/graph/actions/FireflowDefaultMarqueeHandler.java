/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.editpanel.graph.actions;

import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.fireflow.designer.actions.INewWfElementPosition;
import org.fireflow.designer.datamodel.FPDLDataObject;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
import org.fireflow.designer.editpanel.graph.ProcessDesignMediator;
import org.fireflow.designer.editpanel.graph.ProcessDesignPanel;
import org.fireflow.designer.editpanel.graph.ProcessGraphModel;
import org.jgraph.graph.BasicMarqueeHandler;

/**
 *
 * @author chennieyun
 */
public class FireflowDefaultMarqueeHandler extends BasicMarqueeHandler {

    private ProcessDesignPanel designPanel = null;
    private ProcessDesignMediator mediator = null;
    private JPopupMenu thePopupMenu = null;

    public FireflowDefaultMarqueeHandler(ProcessDesignMediator md,
            ProcessDesignPanel designPanel) {
        mediator = md;
        this.designPanel = designPanel;
    }

    public boolean isForceMarqueeEvent(MouseEvent event) {
        if (event.isShiftDown() || event.isAltDown() || event.isControlDown()) {
            return false;
        }
        // If Right Mouse Button we want to Display the PopupMenu
        if (SwingUtilities.isRightMouseButton(event)) // Return Immediately
        {
            return true;
        }
        return super.isForceMarqueeEvent(event);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            ProcessGraphModel model = (ProcessGraphModel) mediator.getDesignPanel().getModel();
            WorkflowProcessElement workflowProcessElement = model.getWorkflowProcessElement();
            FPDLDataObject dataObj = workflowProcessElement.getLookup().lookup(FPDLDataObject.class);
            Action[] actions = dataObj.getActions();

            if (thePopupMenu == null) {
                thePopupMenu = new JPopupMenu();
                for (int i = 0; actions != null && i < actions.length; i++) {
                    Action action = actions[i];
                    if (action != null) {
                        thePopupMenu.add(action);
                    } else {
                        thePopupMenu.addSeparator();
                    }
                }
            }
            for (int i = 0; actions != null && i < actions.length; i++) {
                Action action = actions[i];
                if (action instanceof INewWfElementPosition) {
                    ((INewWfElementPosition) action).setPosition(mouseEvent.getPoint());
                }
            }
            thePopupMenu.show(mediator.getDesignPanel(), mouseEvent.getX(), mouseEvent.getY());
        } else {
            super.mousePressed(mouseEvent);
        }
    }
}
