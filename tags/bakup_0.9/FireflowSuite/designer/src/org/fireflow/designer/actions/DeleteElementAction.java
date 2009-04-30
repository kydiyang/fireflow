/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fireflow.designer.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.fireflow.designer.datamodel.IFPDLElement;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

/**
 *
 * @author chennieyun
 */
public class DeleteElementAction  extends AbstractAction {
    private ExplorerManager explorerManager = null;
    private WorkflowProcessElement workflowProcessElement = null;
    public DeleteElementAction(ExplorerManager explMgr, String title, Icon image) {
        super(title, image);
        this.explorerManager = explMgr;
        this.workflowProcessElement = (WorkflowProcessElement) explorerManager.getRootContext().getChildren().getNodes()[0];
    }
    public void actionPerformed(ActionEvent e) {
        Node[] selectedNodes = explorerManager.getSelectedNodes();
        for (int i=0;selectedNodes!=null && i<selectedNodes.length;i++){
               Node node = selectedNodes[i];
                if (node!=null && (node instanceof IFPDLElement)) {
                    IFPDLElement fpdlElement = (IFPDLElement) node;
                    fpdlElement.deleteSelf();

                }            
        }
    }

}
