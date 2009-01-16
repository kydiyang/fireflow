package org.fireflow.engine;

import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.fireflow.engine.calendar.ICalendarService;
import org.fireflow.engine.definition.IDefinitionService;
import org.fireflow.engine.impl.WorkflowSession;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.ITaskInstanceManager;
import org.fireflow.kenel.KenelException;
import org.fireflow.kenel.KenelManager;
import org.fireflow.engine.condition.IConditionResolver;


/**
 * RuntimeContext提供所有流程实例运行环境
 * @author chennieyun
 *
 */
public class RuntimeContext {

    private static RuntimeContext instance = null;
    private boolean isInitialized = false;
    
    //context管理的各种服务
    private IConditionResolver conditionResolver = null;
    private IPersistenceService persistenceService = null;
    private IDefinitionService definitionService = null;
    private KenelManager kenelManager = null;
    private ITaskInstanceManager taskInstanceManager = null;
    private ICalendarService calendarService = null;
    
    private ThreadLocal<Object> currentDBSession = new ThreadLocal<Object>();
//	private ThreadLocal<IFireflowSession> currentFireflowSession = new ThreadLocal<IFireflowSession>();
    
    //TODO
    //保持一个内部用的beanFactory,类似spring的beanfactory,用于注册Listener,ApplicationHandler,AssignmentHandler的
    //单态实例，可以考虑这个beanfactory是否可以引用spring的beanfactory
    private Map<String, Object> beanCache = new HashMap<String, Object>();

    //为了使kenel能够用于IDE环境，runtimecontext不要采用此模式，而是通过token区获取。
//    public static RuntimeContext getInstance() {
//        if (instance == null) {
//            instance = new RuntimeContext();
//        }
//        return instance;
//    }


    public RuntimeContext() {
    }

    /**
     * 该方法用户缓存诸如事件监听器，applicationHandler实例等。
     * 不过，这种实现不是很漂亮。
     * @param beanClassName
     * @return
     */
    public Object getBeanByClassName(String beanClassName) {
        Object obj = beanCache.get(beanClassName);
        if (obj != null) {
            return obj;
        }
        Class clz;
        try {
            clz = Class.forName(beanClassName);
            obj = clz.newInstance();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RuntimeContext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(RuntimeContext.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RuntimeContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        beanCache.put(beanClassName, obj);
        return obj;
    }

    //DBSession 放在哪里比较好些？
//    public void setCurrentDBSession(Object dbSession) {
//        currentDBSession.set(dbSession);
//    }
//
//    public Object getCurrentDBSession() {
//        return currentDBSession.get();
//    }
    
    public IConditionResolver getConditionResolver() {
        return conditionResolver;
    }

    public void setConditionResolver(IConditionResolver resolver) {
        this.conditionResolver = resolver;
        if (this.conditionResolver instanceof IRuntimeContextAware){
            ((IRuntimeContextAware)this.conditionResolver).setRuntimeContext(this);
        }
    }

    public ITaskInstanceManager getTaskInstanceManager() {
        return taskInstanceManager;
    }

    public void setTaskInstanceManager(ITaskInstanceManager taskInstanceManager) {
        this.taskInstanceManager = taskInstanceManager;
        this.taskInstanceManager.setRuntimeContext(this);
    }

    public IPersistenceService getPersistenceService() {
        return this.persistenceService;
    }

    public void setPersistenceService(IPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
        this.persistenceService.setRuntimeContext(this);
    }

    public IDefinitionService getDefinitionService() {
        return definitionService;
    }

    public void setDefinitionService(IDefinitionService service) {
        this.definitionService = service;
        this.definitionService.setRuntimeContext(this);
    }

    public KenelManager getKenelManager() {
        return kenelManager;
    }

    public void setKenelManager(KenelManager kenelManager) {
        this.kenelManager = kenelManager;
        this.kenelManager.setRuntimeContext(this);
    }

    public ICalendarService getCalendarService() {
        return calendarService;
    }

    public void setCalendarService(ICalendarService calendarService) {
        this.calendarService = calendarService;
        this.calendarService.setRuntimeContext(this);
    }

    

    public void initialize() throws EngineException, KenelException {
//		System.out.println("执行initialize(),the isInitialized="+this.isInitialized);
//		System.out.println("看看有没有 受理环节实例"+this.getKenelManager().getWFElementInstance("受理"));
        if (!isInitialized) {
//			initKenelExtensions();
            initAllNetInstances();
            isInitialized = true;
        }
    }

 
    protected void initAllNetInstances() throws KenelException {
        /*
        List<WorkflowProcess> allWfProcess = definitionService.getAllWorkflowProcesses();
        this.getKenelManager().clearAllNetInstance();
        for(int i=0;allWfProcess!=null && i<allWfProcess.size();i++){
        WorkflowProcess process = allWfProcess.get(i);
        this.getKenelManager().createNetInstance( process);
        }
         */
    }
    
    public IWorkflowSession getWorkflowSession(){
        return new WorkflowSession(this);
    }

    public boolean isIsInitialized() {
        return isInitialized;
    }
    
    
}
