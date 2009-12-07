package org.fireflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.AssignmentHandlerMock;
import org.fireflow.engine.taskinstance.CurrentUserAssignmentHandlerMock;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.KernelException;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class SkipTest extends FireFlowAbstractTests {
//    private final static String springConfigFile = "config/TestContext.xml";
//    private static ClassPathResource resource = null;
//    private static XmlBeanFactory beanFactory = null;
//    private static TransactionTemplate transactionTemplate = null;
//    private static RuntimeContext runtimeContext = null;

        //-----variables-----------------
    static IProcessInstance currentProcessInstance = null;
    static String workItem1Id = null;
    static String workItem2Id = null;
    static String workItem3Id = null;
    static String workItem5Id = null;
    
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        resource = new ClassPathResource(springConfigFile);
//        beanFactory = new XmlBeanFactory(resource);
//        transactionTemplate = (TransactionTemplate) beanFactory.getBean("transactionTemplate");
//        runtimeContext = (RuntimeContext) beanFactory.getBean("runtimeContext");
//
//        //首先将表中的数据清除
//        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus arg0) {
//            	IFireWorkflowHelperDao helperDao = (IFireWorkflowHelperDao) beanFactory.getBean("FireWorkflowHelperDao");
//                helperDao.clearAllTables();
//            }
//        });
//    }

    /**
     * 创建流程实例，并执行实例的run方法。
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Loop.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("skip2",AssignmentHandlerMock.ACTOR_ID);
                    processInstance.setProcessInstanceVariable("flag", "skip");
                    processInstance.run();
                    return processInstance;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        assertNotNull(currentProcessInstance);

        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "skip2.Activity1");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(1,taskInst.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "skip2", "skip2.Activity1.Task1");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(1,token.getStepNumber().intValue());

        workItem1Id = ((IWorkItem) workItemList.get(0)).getId();
    }
    
    @Test
    public void testCompleteWorkItem1(){
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();

                    IWorkItem workItem = workflowSession.findWorkItemById(workItem1Id);
                    workItem.complete();
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        
        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        
        List taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "skip2.Activity2");
        if (taskInstanceList==null){taskInstanceList = new ArrayList();}
        assertEquals(0,taskInstanceList.size());
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "skip2.Activity4");
        if (taskInstanceList==null){taskInstanceList = new ArrayList();}
        assertEquals(0,taskInstanceList.size());
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "skip2.Activity3");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        ITaskInstance taskInst = ((ITaskInstance) taskInstanceList.get(0));
        assertEquals(new Integer(ITaskInstance.RUNNING),taskInst .getState());
        assertEquals(3,taskInst.getStepNumber().intValue());

        List workItemList = persistenceService.findTodoWorkItems(CurrentUserAssignmentHandlerMock.ACTOR_ID, "skip2", "skip2.Activity3.Task3");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());

        List tokensList = persistenceService.findTokensForProcessInstance(currentProcessInstance.getId(), null);
        assertNotNull(tokensList);
        assertEquals(1, tokensList.size());
        IToken token = (IToken)tokensList.get(0);
        assertEquals(3,token.getStepNumber().intValue());        
    }
    @Test
    public void clear(){
    	fireWorkflowHelperDao.clearAllTables();
    }
}
