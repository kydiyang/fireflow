/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation.taskinstance;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.fireflow.designer.datamodel.element.WorkflowProcessElement;
import org.fireflow.designer.editpanel.graph.MyViewFactory;
import org.fireflow.designer.editpanel.graph.ProcessGraphModel;
import org.fireflow.designer.simulation.FireflowSimulator;
import org.fireflow.designer.simulation.SimulatorPanel;
import org.fireflow.designer.simulation.engine.definition.DefinitionService4Simulation;
import org.fireflow.designer.simulation.engine.persistence.MemoryPersistenceService;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.kenel.KenelException;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.io.Dom4JFPDLParser;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author chennieyun
 */
public class TaskInstance4Simulation extends TaskInstance {

    /**
     * tool类型的task直接结束，不调用其application handler
     * @throws org.fireflow.engine.EngineException
     */
    protected void startToolTask() throws EngineException {
        if (this.getState().intValue() == ITaskInstance.INITIALIZED ||
                this.getState().intValue() == ITaskInstance.STARTED) {
            try {
                this.complete();
            } catch (KenelException ex) {
                Logger.getLogger(TaskInstance.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @Override
    protected void startSubflowTask() throws EngineException, KenelException {
        //检查自流程的定义文件是否已经装载，如果没有则弹出兑换框，让操作员选择
        String workflowProcessId = this.getTask().getSubWorkflowProcess().getWorkflowProcessId();

        RuntimeContext ctx = RuntimeContext.getInstance();
        WorkflowProcess workflowProcess = ctx.getDefinitionService().getWorkflowProcessById(workflowProcessId);
        String validateResult = null;
        if (workflowProcess == null) {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new FileFilter() {

                public boolean accept(File pathname) {
                    if (pathname.getName().endsWith(".xml")) {
                        return true;
                    } else if (pathname.isDirectory()) {
                        return true;
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    return "FPDL File.";
                }
            };
            chooser.setFileFilter(filter);

            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File workflowProcessDefFile = chooser.getSelectedFile();
                try {
                    FileInputStream fIn = new FileInputStream(workflowProcessDefFile);
                    Dom4JFPDLParser parser = new Dom4JFPDLParser();
                    WorkflowProcess subWorkflow = parser.parse(fIn);
                    if (!subWorkflow.getId().equals(workflowProcessId)) {
                        throw new EngineException("The workflowprocess's ID is NOT equal to " + workflowProcessId + ", which defined in the Task[" + this.getTask().getDisplayName() + "]");
                    }

                    validateResult = subWorkflow.validate();
                    if (validateResult != null) {
                        JOptionPane.showMessageDialog(null, validateResult, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    DefinitionService4Simulation defService = (DefinitionService4Simulation) ctx.getDefinitionService();
                    defService.setWorkflowProcess(subWorkflow);

                    ctx.getKenelManager().createNetInstance(subWorkflow);
                    workflowProcess = subWorkflow;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new EngineException(e.getMessage());
                }
            } else {
                throw new EngineException("Not found the workflow process definition");
            }
        }
        validateResult = workflowProcess.validate();
        if (validateResult != null) {
            JOptionPane.showMessageDialog(null, validateResult, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //创建新的模拟面板
        FireflowSimulator simulator = FireflowSimulator.getCurrentSimulator();
//        SimulatorPanel panel = simulator.getSimulatorPanel4Process(workflowProcess.getId());
//        if (panel == null) {
        InstanceContent lookupContent = new InstanceContent();
        AbstractLookup lookup = new AbstractLookup(lookupContent);
        lookupContent.add(workflowProcess);

        WorkflowProcessElement workflowProcessElement = new WorkflowProcessElement(lookup);

        Children.Array array = new Children.Array();
        array.add(new Node[]{workflowProcessElement});
        AbstractNode root = new AbstractNode(array) {

            public Action[] getActions(boolean b) {
                return new Action[]{};
            }
            };

        ExplorerManager explorerManager = new ExplorerManager();
        explorerManager.setRootContext(root);
        lookupContent.add(explorerManager);

        ProcessGraphModel graphModel = new ProcessGraphModel(explorerManager);
        GraphLayoutCache layoutCache = new GraphLayoutCache(graphModel, new MyViewFactory(), false);
        HashSet<String> localAttributes = new HashSet<String>();
        localAttributes.add(GraphConstants.BORDERCOLOR);
        localAttributes.add(GraphConstants.FOREGROUND);
        localAttributes.add(GraphConstants.LINECOLOR);
        layoutCache.setLocalAttributes(localAttributes);
        SimulatorPanel graph = new SimulatorPanel(graphModel, layoutCache);


        simulator.addSimulatorPanel(graph);
        MemoryPersistenceService persistenceService = (MemoryPersistenceService) ctx.getPersistenceService();
        persistenceService.addStorageChangeListenser(graph);
//        } else {
//            simulator.setCurrentSimulatorPanel(panel);
//        }

        super.startSubflowTask();

//        List<IProcessInstance> processInstList = ctx.getPersistenceService().findProcessInstanceByProcessId(workflowProcess.getId());//                    simulator.add
//        for (int i = 0; processInstList != null && i < processInstList.size(); i++) {
//            IProcessInstance processInst = processInstList.get(i);
//            if (this.getId().equals(processInst.getParentTaskInstanceId())) {
//                graph.setCurrentProcessInstance(processInst);
//                break;
//            }
//        }
    }
}
