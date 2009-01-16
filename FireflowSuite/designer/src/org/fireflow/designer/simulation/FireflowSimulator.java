/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation;

import cn.bestsolution.tools.resourcesmanager.util.Utilities;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.fireflow.designer.simulation.engine.definition.WorkflowDefinition4Simulation;
import org.fireflow.designer.simulation.engine.persistence.MemoryPersistenceService;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.designer.util.DesignerConstant;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kenel.KenelException;
import org.fireflow.model.WorkflowProcess;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * 该类起到Mediator的作用
 * @author chennieyun
 */
public class FireflowSimulator {
    private static FireflowSimulator currentSimulator = null;



    private WorkflowProcess workflowProcess = null;
    private IWorkflowSession fireflowSession = null;
//    private MemoryPersistenceService persistenceService = null;
//    private GraphLayoutCache graphLayoutCache = null;
    
//    private ExplorerManager explorerManager = null;
    
    private SimulatorPanel mainSimulatorPanel = null;
    private SimulatorPanel currentSimulatorPanel = null;
    private List<SimulatorPanel> simulatorPanelList = new ArrayList<SimulatorPanel>();
    private JScrollPane simulatorContainer = null;

    public static void setCurrrentSimulator(FireflowSimulator simulator){
        currentSimulator = simulator;
    }
    
    public static FireflowSimulator getCurrentSimulator(){
        return currentSimulator;
    }
    
    
    public FireflowSimulator(IWorkflowSession session, WorkflowProcess workflowProcess, SimulatorPanel simulatorPanel,JScrollPane simulatorContainer) {
//        explorerManager = explorerMgr;
//        this.graphLayoutCache = layoutCache;
        fireflowSession = session;
        this.workflowProcess = workflowProcess;
        this.currentSimulatorPanel = simulatorPanel;
        simulatorPanelList.add(simulatorPanel);
        mainSimulatorPanel = simulatorPanel;
        this.simulatorContainer = simulatorContainer;
        
     
    }

//    public ExplorerManager getExplorerManager() {
//        return explorerManager;
//    }

    /**
     * 当某个工作流元素处于running,complete,wait状态时，重绘模拟器界面上的图形
     * @param workflowElementID
     */
    public void repaintSimulatorPanel(String workflowElementID, int state) {
        /*
        ProcessGraphModel graphModel = (ProcessGraphModel) this.graphLayoutCache.getModel();
        String sn = workflowProcess.findSnById(workflowElementID);
        System.out.println("Inside FireflowSimulator.. sn is " + sn);
        GraphCell cell = null;
        try {
            cell = graphModel.getGraphCellByWFElementSn(sn);
        } catch (Exception e) {

        }
        if (cell == null) {
            return;
        }
//        System.out.println("============------------repaintSimulatorPanel:the cell id is "+workflowElementID);
        CellView cellView = this.graphLayoutCache.getMapping(cell, false);
        Map attributeMap = new HashMap();
//        Map attribute = cell.getAttributes();
        Map attribute = cellView.getAttributes();
        if (state == this.COMPLETED) {
            GraphConstants.setLineColor(attribute, Color.BLUE);
            GraphConstants.setForeground(attribute, Color.BLUE);
            GraphConstants.setBorderColor(attribute, Color.BLUE);
        } else if (state == this.RUNNING) {
            GraphConstants.setLineColor(attribute, Color.GREEN);
            GraphConstants.setForeground(attribute, Color.GREEN);
            GraphConstants.setBorderColor(attribute, Color.GREEN);
        } else if (state == this.WAITING) {
            GraphConstants.setLineColor(attribute, Color.YELLOW);
            GraphConstants.setForeground(attribute, Color.YELLOW);
            GraphConstants.setBorderColor(attribute, Color.YELLOW);

        }
        this.graphLayoutCache.update(cellView);
        currentSimulatorPanel.repaint();
//        attributeMap.put(cell, attribute);
//        this.graphLayoutCache.edit(attributeMap);
    */
    }


    //==================下面是是对engine API的调用================================//
    /**
     * 模拟应用程序启动流程实例
     */
    public void run() {
        String validateResult = this.workflowProcess.validate();
        if (validateResult!=null){
            JOptionPane.showMessageDialog(currentSimulatorPanel, validateResult,"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        initAll();//清空上一次的测试数据
        
        try {
            WorkflowDefinition workflowDef = new WorkflowDefinition4Simulation();
            workflowDef.setWorkflowProcess(workflowProcess);
            workflowDef.setVersion(1);
            fireflowSession.getRuntimeContext().getKenelManager().createNetInstance(workflowDef);
            IProcessInstance processInstance = fireflowSession.createProcessInstance(workflowProcess.getName());
//            processInstance.setProcessInstanceVariable("jine", new Integer(800));
            processInstance.run();
            
            mainSimulatorPanel.setCurrentProcessInstance(processInstance);
            
        } catch (EngineException ex) {
            InputOutput inputOutput = IOProvider.getDefault().getIO(DesignerConstant.FIREFLOW_OUTPUT, false);
            inputOutput.getErr().println(Utilities.errorStackToString(ex));
            inputOutput.setOutputVisible(true);
            inputOutput.setErrVisible(true);
            inputOutput.select();
        } catch (KenelException ex) {
            InputOutput inputOutput = IOProvider.getDefault().getIO(DesignerConstant.FIREFLOW_OUTPUT, false);
            inputOutput.getErr().println(Utilities.errorStackToString(ex));
            inputOutput.setOutputVisible(true);
            inputOutput.setErrVisible(true);
            inputOutput.select();
        }

    }

    public void acceptWorkItem4Task(String taskId) {
        IPersistenceService persistenceService = RuntimeContext.getInstance().getPersistenceService();
        List<IWorkItem> workItemList = persistenceService.findWorkItemForTask(taskId);
        for (int i = 0; i < workItemList.size(); i++) {
            IWorkItem wi = workItemList.get(i);
            if (wi.getState() == IWorkItem.INITIALIZED) {
                try {
                    wi.sign();
                } catch (EngineException ex) {
                    InputOutput inputOutput = IOProvider.getDefault().getIO(DesignerConstant.FIREFLOW_OUTPUT, false);
                    inputOutput.getErr().println(Utilities.errorStackToString(ex));
                    inputOutput.setOutputVisible(true);
                    inputOutput.setErrVisible(true);
                    inputOutput.select();
                } catch (KenelException ex) {
                    InputOutput inputOutput = IOProvider.getDefault().getIO(DesignerConstant.FIREFLOW_OUTPUT, false);
                    inputOutput.getErr().println(Utilities.errorStackToString(ex));
                    inputOutput.setOutputVisible(true);
                    inputOutput.setErrVisible(true);
                    inputOutput.select();
                }
            }
        }
    }

    public void completeWorkItem4Task(String taskId) {
        IPersistenceService persistenceService = RuntimeContext.getInstance().getPersistenceService();
        List<IWorkItem> workItemList = persistenceService.findWorkItemForTask(taskId);
        for (int i = 0; i < workItemList.size(); i++) {
            IWorkItem wi = workItemList.get(i);
            if (wi.getState() == IWorkItem.STARTED) {
                try {
                    wi.complete();
                } catch (EngineException ex) {
                    InputOutput inputOutput = IOProvider.getDefault().getIO(DesignerConstant.FIREFLOW_OUTPUT, false);
                    inputOutput.getErr().println(Utilities.errorStackToString(ex));
                    inputOutput.setOutputVisible(true);
                    inputOutput.setErrVisible(true);
                    inputOutput.select();
                } catch (KenelException ex) {
                    InputOutput inputOutput = IOProvider.getDefault().getIO(DesignerConstant.FIREFLOW_OUTPUT, false);
                    inputOutput.getErr().println(Utilities.errorStackToString(ex));
                    inputOutput.setOutputVisible(true);
                    inputOutput.setErrVisible(true);
                    inputOutput.select();
                }
            }
        }
    }
    
    public void setWorkflowVariable(String name,Object value){
        IPersistenceService persistenceService = RuntimeContext.getInstance().getPersistenceService();
        currentSimulatorPanel.getCurrentProcessInstance().setProcessInstanceVariable(name, value);
        persistenceService.saveOrUpdateProcessInstance(currentSimulatorPanel.getCurrentProcessInstance());
    }

    public void initAll(){
        try {
            RuntimeContext ctx  = RuntimeContext.getInstance();
            IPersistenceService persistenceService = ctx.getPersistenceService();
            ((MemoryPersistenceService) persistenceService).clearAll4Process(this.workflowProcess.getId());

            mainSimulatorPanel.initTheWorkflowGraph();
            this.setCurrentSimulatorPanel(mainSimulatorPanel);

            for (int i = 0; i < this.simulatorPanelList.size(); i++) {
                ((MemoryPersistenceService) persistenceService).removeStorageChangeListener(simulatorPanelList.get(i));
//                simulatorPanelList.get(i).initTheWorkflowGraph();
            }
            this.simulatorPanelList.clear();
            this.simulatorPanelList.add(mainSimulatorPanel);
            mainSimulatorPanel.getTaskColorProps().clear();
            ((MemoryPersistenceService) persistenceService).addStorageChangeListenser(mainSimulatorPanel);
            
            ctx.initAllNetInstances();

        } catch (KenelException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public List getSimulatorPanelList(){
        return this.simulatorPanelList;
    }
    
    public void addSimulatorPanel(SimulatorPanel simulatorPanel){
        this.simulatorPanelList.add(simulatorPanel);
        this.currentSimulatorPanel = simulatorPanel;
        this.simulatorContainer.getViewport().removeAll();
        this.simulatorContainer.getViewport().add(this.currentSimulatorPanel);
        this.simulatorContainer.validate();
    }
    
   
    public void setCurrentSimulatorPanel(SimulatorPanel panel){
        this.currentSimulatorPanel = panel;
        this.simulatorContainer.getViewport().removeAll();
        this.simulatorContainer.getViewport().add(this.currentSimulatorPanel);
        this.simulatorContainer.validate();        
    }
    
    public SimulatorPanel getSimulatorPanel4Process(String processId){
        for (int i=0;i<this.simulatorPanelList.size();i++){
            SimulatorPanel panel = this.simulatorPanelList.get(i);
            if (panel.getWorkflowProcessId().equals(processId)){
                return panel;
            }
        }
        return null;
    }
    
    /**
     * 获得当前模拟界面中的选中的节点
     * @return
     */
    public Node getSelectedNode(){
        return this.currentSimulatorPanel.getSelectedNode();
    }    
//    public void star
}
