package org.fireflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.AssignToCurrentUserAndCompleteWorkItemHandler;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.junit.Test;

public class CompleteWorkItemInAssignmentHandlerTest extends FireFlowAbstractTests {
   
    /**
     * 创建流程实例，并执行实例的run方法。
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        IProcessInstance currentProcessInstance = null;
        try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Jump.xml
            currentProcessInstance = workflowSession.createProcessInstance("CompleteWorkItemInAssignmentHandler",AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID);

            currentProcessInstance.run();

        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "CompleteWorkItemInAssignmentHandler.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getTaskId().equals("CompleteWorkItemInAssignmentHandler.Activity1.Task1")){
        	assertEquals(new Integer(ITaskInstance.COMPLETED), taskInst.getState());
        }else{
            assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        }
        taskInst = ((ITaskInstance) taskInstanceList.get(1));
        if (taskInst.getTaskId().equals("CompleteWorkItemInAssignmentHandler.Activity1.Task2")){
        	assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        }else{
            assertEquals(new Integer(ITaskInstance.COMPLETED), taskInst.getState());
        }
        
        List<IWorkItem> workItemList = persistenceService.findHaveDoneWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "CompleteWorkItemInAssignmentHandler", "CompleteWorkItemInAssignmentHandler.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.COMPLETED), ((IWorkItem) workItemList.get(0)).getState());
        //String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
        workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "CompleteWorkItemInAssignmentHandler", "CompleteWorkItemInAssignmentHandler.Activity1.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        //String workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
        
        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());


    }
    
    @Test
    public void testClaimAndCompleteWorkItem2(){
    	IProcessInstance currentProcessInstance = null;
        try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            //启动"/workflowdefinition/Jump.xml
            currentProcessInstance = workflowSession.createProcessInstance("CompleteWorkItemInAssignmentHandler",AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID);

            currentProcessInstance.run();

        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "CompleteWorkItemInAssignmentHandler.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(2, taskInstanceList.size());
        
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        if (taskInst.getTaskId().equals("CompleteWorkItemInAssignmentHandler.Activity1.Task1")){
        	assertEquals(new Integer(ITaskInstance.COMPLETED), taskInst.getState());
        }else{
            assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        }
        taskInst = ((ITaskInstance) taskInstanceList.get(1));
        if (taskInst.getTaskId().equals("CompleteWorkItemInAssignmentHandler.Activity1.Task2")){
        	assertEquals(new Integer(ITaskInstance.RUNNING), taskInst.getState());
        }else{
            assertEquals(new Integer(ITaskInstance.COMPLETED), taskInst.getState());
        }
        
        List<IWorkItem> workItemList = persistenceService.findHaveDoneWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "CompleteWorkItemInAssignmentHandler", "CompleteWorkItemInAssignmentHandler.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.COMPLETED), ((IWorkItem) workItemList.get(0)).getState());
        //String workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
        
        workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "CompleteWorkItemInAssignmentHandler", "CompleteWorkItemInAssignmentHandler.Activity1.Task2");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        String workItem2Id = ((IWorkItem) workItemList.get(0)).getId();
        
        List<IToken> tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());
    	
        //--------------------------------------       
        
        try {
            IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
            IWorkItem workItem2 = workflowSession.findWorkItemById(workItem2Id);
            workItem2.claim();
            workItem2.complete();
        } catch (EngineException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KernelException ex) {
            Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        IWorkItem wi = persistenceService.findWorkItemById(workItem2Id);
        assertEquals(new Integer(IWorkItem.COMPLETED), wi.getState());

        tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());

        workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "CompleteWorkItemInAssignmentHandler", "CompleteWorkItemInAssignmentHandler.Activity2.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        //String workItem3Id = ((IWorkItem) workItemList.get(0)).getId();
        
    }

}
