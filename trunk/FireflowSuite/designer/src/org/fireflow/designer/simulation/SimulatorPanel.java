/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.designer.simulation;

import java.awt.Color;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fireflow.designer.datamodel.element.ActivityElement;
import org.fireflow.designer.editpanel.graph.ProcessGraphModel;
import org.fireflow.designer.simulation.engine.persistence.IStorageChangeListener;
import org.fireflow.designer.simulation.engine.persistence.StorageChangedEvent;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kenel.IJoinPoint;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphSelectionModel;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphSelectionModel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author chennieyun
 */
public class SimulatorPanel extends JGraph implements GraphSelectionListener,IStorageChangeListener {
    public static final int RUNNING = 1;
    public static final int WAITING = 2;
    public static final int COMPLETED = 3;
    public static final int INITSTATE = 0;
    
    private IProcessInstance currentProcessInstance = null;
    
    private HashMap taskColorProps = new HashMap();

    public SimulatorPanel(ProcessGraphModel model, GraphLayoutCache layoutCache) {
        super(model, layoutCache);
        init();
    }

    public void init() {
        this.setHighlightColor(Color.WHITE);
        // this.setSelectNewCells(true);
        this.setAutoscrolls(true);
        this.setCloneable(false);
        this.setDisconnectable(false);
        // this.setBendable(true);
        this.setEditable(false);
        this.setMoveable(false);

        this.addGraphSelectionListener(this);

        GraphSelectionModel selectionModel = new DefaultGraphSelectionModel(this);
        selectionModel.setSelectionMode(GraphSelectionModel.SINGLE_GRAPH_SELECTION);
        this.setSelectionModel(selectionModel);

    }

    public HashMap getTaskColorProps(){
        return this.taskColorProps;
    }
    
    public void valueChanged(GraphSelectionEvent event) {
        try {
            DefaultGraphCell cell = (DefaultGraphCell) event.getCell();
            AbstractNode workflowElement = (AbstractNode) cell.getUserObject();
            ProcessGraphModel model = (ProcessGraphModel) this.getModel();

            model.getExplorerManager().setSelectedNodes(new Node[]{workflowElement});

            if (workflowElement instanceof ActivityElement) {
                this.setEditable(true);
            } else {
                this.setEditable(false);
            }
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public IProcessInstance getCurrentProcessInstance() {
        return currentProcessInstance;
    }

    public void setCurrentProcessInstance(IProcessInstance currentProcessInstance) {
        this.currentProcessInstance = currentProcessInstance;
    }
    
    public String toString(){
        return ((ProcessGraphModel)this.getModel()).getWorkflowProcessElement().getDisplayName();
    }
    
    public Node getSelectedNode(){
        ProcessGraphModel model = (ProcessGraphModel)this.getModel();
        Node[] nodes = model.getExplorerManager().getSelectedNodes();
        if (nodes!=null && nodes.length>0) return nodes[0];
        return null;
    }

    public void onStorageChanged(StorageChangedEvent e) {
        ProcessGraphModel graphModel = (ProcessGraphModel) this.graphLayoutCache.getModel();        
        WorkflowProcess workflowProcess = (WorkflowProcess)graphModel.getWorkflowProcessElement().getContent();
        if (e.getProcessId()==null || !e.getProcessId().equals(workflowProcess.getId())){
            return;
        }
        
        if (this.currentProcessInstance==null){
            this.currentProcessInstance = e.getProcessInstance();
        }
        
        IPersistenceService persistenceService = RuntimeContext.getInstance().getPersistenceService();
        
                
        //开始节点
        String startNodeSN = workflowProcess.getStartNode().getSn();
        this.updateTheWorkflowGraph(startNodeSN, COMPLETED);
        
        //activity
        List<String> completedTransitionIds = new ArrayList<String>();
        List<Activity> activities = workflowProcess.getActivities();
        for (int i=0;i<activities.size();i++){
            Activity activity = activities.get(i);
            
            List<ITaskInstance> taskInstList = persistenceService.findTaskInstancesForProcessInstance(null, activity.getId());
//            System.err.println("=====activity "+activity.getId()+"; taskList size is "+taskInstList.size());
            if (taskInstList==null || taskInstList.size()==0) continue;
            
            int state = COMPLETED ;
            for (int m=0;m<taskInstList.size();m++){
                ITaskInstance taskInst = taskInstList.get(m);
                if (taskInst.getState()!=ITaskInstance.COMPLETED && taskInst.getState()!=ITaskInstance.CANCELED){
                    state = RUNNING;
                    if (taskInst.getState()==ITaskInstance.INITIALIZED){
                        taskColorProps.put(taskInst.getTaskId(), Color.MAGENTA);
                    }else{
                        taskColorProps.put(taskInst.getTaskId(), Color.GREEN);
                    }
                }else{
                    taskColorProps.put(taskInst.getTaskId(), Color.BLUE);
                }
            }
            
            String sn = activity.getSn();
            updateTheWorkflowGraph(sn,state);
            
            sn = activity.getEnteringTransition().getSn();
            updateTheWorkflowGraph(sn,COMPLETED);
            completedTransitionIds.add(activity.getEnteringTransition().getId());
            
            if (state==COMPLETED){
                sn = activity.getLeavingTransition().getSn();
                updateTheWorkflowGraph(sn,state);
                completedTransitionIds.add(activity.getLeavingTransition().getId());
            }
        }
        
        //synchronizer
        List<Synchronizer> synchronizers = workflowProcess.getSynchronizers();
        for (int i=0;i<synchronizers.size();i++){
            Synchronizer synchronizer = synchronizers.get(i);
            
            if (synchronizer.getEnteringTransitions().size()==1){
                if (completedTransitionIds.contains(synchronizer.getEnteringTransitions().get(0).getId())){
                    updateTheWorkflowGraph(synchronizer.getSn(),COMPLETED);
                }
                continue;
            }
            
            int volum = synchronizer.getEnteringTransitions().size()*synchronizer.getLeavingTransitions().size();
            IJoinPoint joinPoint = persistenceService.findJoinPointsForProcessInstance(null, synchronizer.getId());
            
            if (joinPoint==null) continue;
//            System.out.println(" synchronizer is "+synchronizer.getId()+"; volum is "+volum+"; value is "+joinPoint.getValue()+";alive is "+joinPoint.getAlive());
            if (joinPoint.getValue()<volum && joinPoint.getAlive()){
                updateTheWorkflowGraph(synchronizer.getSn(),WAITING);
            }else if (joinPoint.getValue()==volum && joinPoint.getAlive()){
                updateTheWorkflowGraph(synchronizer.getSn(),COMPLETED);
            }
        }
        
        //和endnode
        List<EndNode> endnodes = workflowProcess.getEndNodes();
        for (int i=0;i<endnodes.size();i++){
            EndNode endnode = endnodes.get(i);
            if (endnode.getEnteringTransitions().size()==1){
                if (completedTransitionIds.contains(endnode.getEnteringTransitions().get(0).getId())){
                    updateTheWorkflowGraph(endnode.getSn(),COMPLETED);
                }
                continue;
            }            
            int volum = endnode.getEnteringTransitions().size();
            IJoinPoint joinPoint = persistenceService.findJoinPointsForProcessInstance(null, endnode.getId());
            if (joinPoint==null) continue;
            if (joinPoint.getValue()<volum && joinPoint.getAlive()){
                updateTheWorkflowGraph(endnode.getSn(),WAITING);
            }else if (joinPoint.getValue()==volum && joinPoint.getAlive()){
                updateTheWorkflowGraph(endnode.getSn(),COMPLETED);
            }
        } 
    }
    
    private void updateTheWorkflowGraph(String sn,int state){
        ProcessGraphModel graphModel = (ProcessGraphModel) this.graphLayoutCache.getModel();
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

        }else if (state==INITSTATE){
            GraphConstants.setLineColor(attribute, Color.BLACK);
            GraphConstants.setForeground(attribute, Color.BLACK);
            GraphConstants.setBorderColor(attribute, Color.BLACK);            
        }
        this.graphLayoutCache.update(cellView);
        this.repaint();        
    }
    
    public String getWorkflowProcessId(){
        ProcessGraphModel graphModel = (ProcessGraphModel) this.graphLayoutCache.getModel();        
        WorkflowProcess workflowProcess = (WorkflowProcess)graphModel.getWorkflowProcessElement().getContent();
        return workflowProcess.getId();
    }
    
    public void initTheWorkflowGraph(){
        ProcessGraphModel graphModel = (ProcessGraphModel) this.graphLayoutCache.getModel();        
        WorkflowProcess workflowProcess = (WorkflowProcess)graphModel.getWorkflowProcessElement().getContent();
        
        this.updateTheWorkflowGraph(workflowProcess.getStartNode().getSn(), INITSTATE);
        
        List<Activity> activities = workflowProcess.getActivities();
        for (int i=0;i<activities.size();i++){
            this.updateTheWorkflowGraph(activities.get(i).getSn(), INITSTATE);
        }
        
        List<Synchronizer> synchronizers = workflowProcess.getSynchronizers();
        for (int i=0;i<synchronizers.size();i++){
            this.updateTheWorkflowGraph(synchronizers.get(i).getSn(), INITSTATE);
        }
        
        List<EndNode> endnodes = workflowProcess.getEndNodes();
        for (int i=0;i<endnodes.size();i++){
            this.updateTheWorkflowGraph(endnodes.get(i).getSn(), INITSTATE);
        } 
        
        List<Transition> transitions = workflowProcess.getTransitions();
        for (int i=0;i<transitions.size();i++){
            this.updateTheWorkflowGraph(transitions.get(i).getSn(), INITSTATE);
        }         
        
        this.currentProcessInstance = null;
    }
}