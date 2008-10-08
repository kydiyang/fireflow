/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.fireflow.designer.datamodel.element.TaskElement;
import org.fireflow.designer.simulation.FireflowSimulator;
import org.fireflow.model.Task;
import org.openide.nodes.Node;

/**
 *
 * @author chennieyun
 */
public class CompleteWorkitemAction extends AbstractAction {

    FireflowSimulator fireflowSimulator = null;

    public CompleteWorkitemAction(FireflowSimulator simulator, String title, Icon image) {
        super(title, image);
        fireflowSimulator = simulator;
    }

    public void actionPerformed(ActionEvent e) {
        Node node = (Node) fireflowSimulator.getSelectedNode();
        if (node==null)return;
        if (!(node instanceof TaskElement))return;
        
        TaskElement taskElement = (TaskElement) node;
        Task task = (Task) taskElement.getContent();

        fireflowSimulator.completeWorkItem4Task(task.getId());
    }
}
