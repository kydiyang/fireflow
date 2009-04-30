/**
 * Copyright 2007-2008 陈乜云,Chen Nieyun
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation��
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.designer.actions;

import java.awt.event.ActionEvent;

import java.awt.geom.Point2D;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.datamodel.element.ActivityElement;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
//import org.fireflow.designer.outline.FPDLOutlineTree;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

/**
 * @author chennieyun
 *
 */
public class NewFormTask extends AbstractAction implements INewWfElementPosition {

    Point2D newWfElementPoition;
    ExplorerManager explorerManager = null;
    WorkflowProcessElement workflowProcessElement = null;

    /**
     * @param argTtitle
     * @param icon
     */
    public NewFormTask(ExplorerManager explMgr, String argTtitle, ImageIcon icon) {
        super(argTtitle, icon);
        this.explorerManager = explMgr;
        this.workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];
    // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see cn.bestsolution.tools.resourcesmanager.actions.UpdateAction#actionPerformed(java.awt.event.ActionEvent, cn.bestsolution.tools.resourcesmanager.MainMediator)
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length > 0) {
            Node firstNode = nodes[0];
            IFPDLElement workflowElm = (IFPDLElement) firstNode;
            if (workflowElm.getElementType().equals(IFPDLElement.ACTIVITY)) {
                ActivityElement activityElement = (ActivityElement) workflowElm;
                activityElement.createChild(IFPDLElement.FORM_TASK, null);
            }
        }

    }

    @Override
    public boolean isEnabled() {
        Node[] nodes = explorerManager.getSelectedNodes();
        if (nodes.length > 0) {
            Node firstNode = nodes[0];
            if (firstNode instanceof IFPDLElement) {
                IFPDLElement workflowElm = (IFPDLElement) firstNode;
                if (workflowElm.getElementType().equals(IFPDLElement.ACTIVITY)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setPosition(Point2D p) {
        this.newWfElementPoition = p;
    }

    public Point2D getPosition() {
        return this.newWfElementPoition;
    }
}
