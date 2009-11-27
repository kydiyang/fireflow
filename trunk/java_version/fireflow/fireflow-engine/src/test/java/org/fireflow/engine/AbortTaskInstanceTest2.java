package org.fireflow.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.AssignToCurrentUserAndCompleteWorkItemHandler;
import org.fireflow.engine.taskinstance.DynamicAssignmentHandler;
import org.fireflow.engine.test.support.FireFlowAbstractTests;
import org.fireflow.kernel.KernelException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class AbortTaskInstanceTest2 extends FireFlowAbstractTests {
//    private final static String springConfigFile = "config/TestContext.xml";
//    private static ClassPathResource resource = null;
//    private static XmlBeanFactory beanFactory = null;
//    private static TransactionTemplate transactionTemplate = null;
//    private static RuntimeContext runtimeContext = null;

    //--------constant----------------------
    //客户电话，用于控制是否执行“发送手机短信通知客户收货”。通过设置mobile等于null和非null值分别进行测试。
//    private final static String mobile = null;//"123123123123";

    //-----variables-----------------
    static IProcessInstance currentProcessInstance = null;
    static String workItemAId = null;
    static String workItemBId = null;
    static String taskInstanceBId = null;
    static String workItemCId = null;
 
    @BeforeClass
    public static void setUpClass() throws Exception {
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
    }
    
    /**
     * 创建流程实例，并使之执行到B环节。
     */
    @Test
    public void testStartNewProcess() {
        System.out.println("--------------Start a new process ----------------");
        currentProcessInstance = (IProcessInstance) transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    //启动"/workflowdefinition/Jump.xml
                    IProcessInstance processInstance = workflowSession.createProcessInstance("AbortTaskInstance2",AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID);

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
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance2.A");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        

        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "AbortTaskInstance2", "AbortTaskInstance2.A.TaskA");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        workItemAId = ((IWorkItem) workItemList.get(0)).getId();
        
        //结束Activity A的工作项，使流程流转到B环节
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    IWorkItem wi = workflowSession.findWorkItemById(workItemAId);
                    wi.claim();
                    wi.complete();
                    return null;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance2.B");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());    
        taskInstanceBId = ((ITaskInstance)taskInstanceList.get(0)).getId();
        
        workItemList = persistenceService.findTodoWorkItems(AssignToCurrentUserAndCompleteWorkItemHandler.ACTOR_ID, "AbortTaskInstance2", "AbortTaskInstance2.B.TaskB");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.INITIALIZED), ((IWorkItem) workItemList.get(0)).getState());
        workItemBId = ((IWorkItem) workItemList.get(0)).getId();        
    }    
    
    /**
     * 中止B环节，使之流向D环节
     */
    @Test
    public void testAbortActivityB() {
        transactionTemplate.execute(new TransactionCallback() {

            public Object doInTransaction(TransactionStatus arg0) {
                try {
                    IWorkflowSession workflowSession = runtimeContext.getWorkflowSession();
                    ITaskInstance taskInstance = workflowSession.findTaskInstanceById(taskInstanceBId);
                    DynamicAssignmentHandler dynamicHandler = new DynamicAssignmentHandler();
                    List<String> actorsList = new ArrayList<String>();
                    actorsList.add("zhangsan");
                    dynamicHandler.setActorIdsList(actorsList);
                    taskInstance.abort("AbortTaskInstance2.D",dynamicHandler);
                    return null;
                } catch (EngineException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KernelException ex) {
                    Logger.getLogger(FireWorkflowEngineTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });    	
        IPersistenceService persistenceService = runtimeContext.getPersistenceService();
        
        List<ITaskInstance> taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance2.B");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        int taskInstanceState = ((ITaskInstance)taskInstanceList.get(0)).getState();
        assertEquals(ITaskInstance.CANCELED,taskInstanceState);           
        
        taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(currentProcessInstance.getId(), "AbortTaskInstance2.D");
        assertNotNull(taskInstanceList);
        assertEquals(1, taskInstanceList.size());
        taskInstanceState = ((ITaskInstance)taskInstanceList.get(0)).getState();
        assertEquals(ITaskInstance.RUNNING,taskInstanceState);
        
     
        
        List<IWorkItem> workItemList = persistenceService.findTodoWorkItems("zhangsan", "AbortTaskInstance2", "AbortTaskInstance2.D.TaskD");
        assertNotNull(workItemList);
        assertEquals(1, workItemList.size());
        assertEquals(new Integer(IWorkItem.RUNNING), ((IWorkItem) workItemList.get(0)).getState());
        workItemCId = ((IWorkItem) workItemList.get(0)).getId();          
    }
    @Test
    public void clear(){
    	fireWorkflowHelperDao.clearAllTables();
    }
}
