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
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;

/**
 *
 * @author chennieyun
 */
public class AcceptWorkitemAction extends AbstractAction {

    FireflowSimulator fireflowSimulator = null;

    public AcceptWorkitemAction(FireflowSimulator simulator, String title, Icon image) {
        super(title, image);
        fireflowSimulator = simulator;
    }

    public void actionPerformed(ActionEvent e) {
        Node node = (Node) fireflowSimulator.getSelectedNode();
        if (node==null) return;
        if (!(node instanceof TaskElement))return;
        
        TaskElement taskElement = (TaskElement) node;
        Task task = (Task) taskElement.getContent();
        fireflowSimulator.acceptWorkItem4Task(task.getId());
        
    }

//    @Override
//    public boolean isEnabled() {
//        Node[] nodes = fireflowSimulator.getExplorerManager().getSelectedNodes();
//        AbstractNode node = null;
//        System.out.println("Inside AcceptWorkitemAction:: nodes.size is "+nodes==null?"null":nodes.length);
//        if (nodes != null && nodes.length > 0) {
//            node = (AbstractNode) nodes[0];
//            System.out.println("=====Inside AcceptWorkitemAction :: node is " + node == null?"null" : node.getClass());
//            if ((node instanceof TaskElement)) {// || (node instanceof ActivityElement)){
//                return true;
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//
//    }
}
