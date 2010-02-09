using System;
using System.Collections.Generic;
using System.Text;
using ISM.FireWorkflow.Engine.Beanfactory;
using ISM.FireWorkflow.Engine.Calendar;
using ISM.FireWorkflow.Engine.Definition;
using ISM.FireWorkflow.Engine.Impl;
using ISM.FireWorkflow.Engine.Persistence;
using ISM.FireWorkflow.Engine.Taskinstance;
using ISM.FireWorkflow.Kernel;
using ISM.FireWorkflow.Engine.Condition;

namespace ISM.FireWorkflow.Engine
{
    /// <summary>
    /// RuntimeContext是Fire workflow Engine的总线。所有的服务都挂接在这个总线上，并通过这个总线获取。<br/>
    /// RuntimeContext也是业务代码调用工作流引擎的入口，通过runtimeContext.getWorkflowSession()获得IWorkflowSession 对象，
    /// 然后通过IWorkflowSession调用各种工作流实例对象及其API。<br/>
    /// </summary>
    public class RuntimeContext
    {
        //    private static RuntimeContext instance = null;
        /// <summary>是否已经初始化</summary>
        private Boolean isInitialized = false;
        //context管理的各种服务

        /// <summary>转移条件表达式解析服务</summary>
        private IConditionResolver conditionResolver = null;

        /// <summary>实例对象存取服务</summary>
        private IPersistenceService persistenceService = null;

        /// <summary>流程定义服务，通过该服务获取流程定义</summary>
        private IDefinitionService definitionService = null;

        /// <summary>内核管理器</summary>
        private KernelManager kernelManager = null;

        /// <summary>TaskInstance 管理器，负责TaskInstance的创建、运行、结束。</summary>
        private ITaskInstanceManager taskInstanceManager = null;

        /// <summary>日历服务</summary>
        private ICalendarService calendarService = null;

        /// <summary>bean工厂，fire workflow默认使用spring作为其实现</summary>
        private IBeanFactory beanFactory = null;

        /// <summary>是否打开流程跟踪，如果打开，则会往T_FF_HIST_TRACE表中插入纪录。</summary>
        private Boolean enableTrace = false;

        //    private ThreadLocal<Object> currentDBSession = new ThreadLocal<Object>();
        //	private ThreadLocal<IFireflowSession> currentFireflowSession = new ThreadLocal<IFireflowSession>();
        //保持一个内部用的beanFactory,类似spring的beanfactory,用于注册Listener,ApplicationHandler,AssignmentHandler的
        //单态实例，可以考虑这个beanfactory是否可以引用spring的beanfactory
        //将Bean的创建工作委派给IBeanFactory,20090228
        //    private Map<String, Object> beanCache = new HashMap<String, Object>();


        public RuntimeContext()
        {
        }


        /// <summary>
        /// 根据bean的name返回bean的实例。<br/>
        /// Fire workflow RuntimeContext将该工作委派给org.fireflow.engine.beanfactory.IBeanFatory
        /// </summary>
        /// <param name="beanName">Bean Name具体指什么是由IBeanFatory的实现类来决定的。</param>
        /// <returns></returns>
        public Object getBeanByName(String beanName)
        {
            if (beanFactory != null)
            {
                return beanFactory.getBean(beanName);
            }
            else
            {
                throw new NullReferenceException("The RuntimeContext's beanFactory  can NOT be null");
            }
        }

        //DBSession 放在哪里比较好些？
        //    public void setCurrentDBSession(Object dbSession) {
        //        currentDBSession.set(dbSession);
        //    }
        //
        //    public Object getCurrentDBSession() {
        //        return currentDBSession.get();
        //    }

        /// <summary>返回条件解析器</summary>
        public IConditionResolver getConditionResolver()
        {
            return conditionResolver;
        }

        /// <summary>设置条件解析器</summary>
        public void setConditionResolver(IConditionResolver resolver)
        {
            this.conditionResolver = resolver;
            if (this.conditionResolver is IRuntimeContextAware)
            {
                ((IRuntimeContextAware)this.conditionResolver).setRuntimeContext(this);
            }
        }

        /// <summary>返回TaskInstance管理器</summary>
        public ITaskInstanceManager getTaskInstanceManager()
        {
            return taskInstanceManager;
        }

        public void setTaskInstanceManager(ITaskInstanceManager taskInstanceManager)
        {
            this.taskInstanceManager = taskInstanceManager;
            this.taskInstanceManager.setRuntimeContext(this);
        }

        public IPersistenceService getPersistenceService()
        {
            return this.persistenceService;
        }

        public void setPersistenceService(IPersistenceService persistenceService)
        {
            this.persistenceService = persistenceService;
            this.persistenceService.setRuntimeContext(this);
        }

        public IDefinitionService getDefinitionService()
        {
            return definitionService;
        }

        public void setDefinitionService(IDefinitionService service)
        {
            this.definitionService = service;
            this.definitionService.setRuntimeContext(this);
        }

        public KernelManager getKernelManager()
        {
            return kernelManager;
        }

        public void setKernelManager(KernelManager arg0)
        {
            this.kernelManager = arg0;
            this.kernelManager.setRuntimeContext(this);
        }

        public ICalendarService getCalendarService()
        {
            return calendarService;
        }

        public void setCalendarService(ICalendarService calendarService)
        {
            this.calendarService = calendarService;
            this.calendarService.setRuntimeContext(this);
        }

        public IWorkflowSession getWorkflowSession()
        {
            return new WorkflowSession(this);
        }

        public Boolean isIsInitialized()
        {
            return isInitialized;
        }

        public IBeanFactory getBeanFactory()
        {
            return beanFactory;
        }

        public void setBeanFactory(IBeanFactory beanFactory)
        {
            this.beanFactory = beanFactory;
        }

        /// <summary>初始化方法</summary>
        public void initialize()// throws EngineException, KernelException 
        {
            //		System.out.println("执行initialize(),the isInitialized="+this.isInitialized);
            //		System.out.println("看看有没有 受理环节实例"+this.getKenelManager().getWFElementInstance("受理"));
            if (!isInitialized)
            {
                //			initKenelExtensions();
                initAllNetInstances();
                isInitialized = true;
            }
        }

        protected void initAllNetInstances()// throws KernelException
        {
            /*
            List<WorkflowProcess> allWfProcess = definitionService.getAllWorkflowProcesses();
            this.getKenelManager().clearAllNetInstance();
            for(int i=0;allWfProcess!=null && i<allWfProcess.Count;i++){
            WorkflowProcess process = allWfProcess.get(i);
            this.getKenelManager().createNetInstance( process);
            }
             */
        }

        public Boolean isEnableTrace()
        {
            return enableTrace;
        }

        public void setEnableTrace(Boolean enableTrace)
        {
            this.enableTrace = enableTrace;
        }
    }

}
