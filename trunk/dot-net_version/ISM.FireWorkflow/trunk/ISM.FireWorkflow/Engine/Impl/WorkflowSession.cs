using System;
using System.Collections.Generic;
using System.Text;
using ISM.FireWorkflow.Model;
using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Engine.Taskinstance;
using ISM.FireWorkflow.Engine.Definition;
using ISM.FireWorkflow.Engine.Persistence;
using ISM.FireWorkflow.Kernel;

namespace ISM.FireWorkflow.Engine.Impl
{
    public class WorkflowSession : IWorkflowSession, IRuntimeContextAware
    {

        protected RuntimeContext runtimeContext = null;
        protected DynamicAssignmentHandler dynamicAssignmentHandler = null;
        protected Boolean inWithdrawOrRejectOperation = false;
        protected Dictionary<String, Object> attributes = new Dictionary<String, Object>();

        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.runtimeContext = ctx;
        }

        public WorkflowSession(RuntimeContext ctx)
        {
            this.runtimeContext = ctx;
        }

        public void setCurrentDynamicAssignmentHandler(
                DynamicAssignmentHandler handler)
        {
            this.dynamicAssignmentHandler = handler;
        }

        public DynamicAssignmentHandler consumeCurrentDynamicAssignmentHandler()
        {
            DynamicAssignmentHandler handler = this.dynamicAssignmentHandler;
            this.dynamicAssignmentHandler = null;
            return handler;
        }

        /**
         * @deprecated
         */
        public IProcessInstance createProcessInstance(String workflowProcessName)
        {
            return _createProcessInstance(workflowProcessName, null, null, null);
        }

        public IProcessInstance createProcessInstance(String workflowProcessName, ITaskInstance parentTaskInstance)
        {
            return _createProcessInstance(workflowProcessName, parentTaskInstance
                    .getId(), parentTaskInstance.getProcessInstanceId(),
                    parentTaskInstance.getId());
        }

        protected IProcessInstance _createProcessInstance(String workflowProcessId, String creatorId, String parentProcessInstanceId, String parentTaskInstanceId)
        {
            // TODO Auto-generated method stub
            String wfprocessId = workflowProcessId;
            return (IProcessInstance)this.execute(new WorkflowSessionIProcessInstance(wfprocessId, creatorId, parentProcessInstanceId, parentTaskInstanceId));
        }

        public IProcessInstance createProcessInstance(String workflowProcessId, String creatorId)
        {
            return _createProcessInstance(workflowProcessId, creatorId, null, null);
        }

        public IWorkItem findWorkItemById(String id)
        {
            String workItemId = id;
            try
            {
                return (IWorkItem)this.execute(new WorkflowSessionIWorkItem(id));
            }
            catch// (EngineException ex) 
            {
                //ex.printStackTrace();
                return null;
            }
            //catch (KernelException ex) {
            //ex.printStackTrace();
            //return null;
            // }
        }

        /**
         * WorkflowSession采用了模板模式，以便将来有需要时可以在本方法中进行扩展。
         * 
         * @param callback
         * @return
         * @throws org.fireflow.engine.EngineException
         * @throws org.fireflow.kenel.KenelException
         */
        public Object execute(IWorkflowSessionCallback callback)
        {
            try
            {
                Object result = callback.doInWorkflowSession(runtimeContext);
                if (result != null)
                {
                    if (result is IRuntimeContextAware)
                    {
                        ((IRuntimeContextAware)result).setRuntimeContext(this.runtimeContext);
                    }
                    if (result is IWorkflowSessionAware)
                    {
                        ((IWorkflowSessionAware)result).setCurrentWorkflowSession(this);
                    }

                    if (result is List<Object>)
                    {
                        List<Object> l = (List<Object>)result;
                        for (int i = 0; i < l.Count; i++)
                        {
                            Object item = l[i];
                            if (item is IRuntimeContextAware)
                            {
                                ((IRuntimeContextAware)item).setRuntimeContext(runtimeContext);
                                if (item is IWorkflowSessionAware)
                                {
                                    ((IWorkflowSessionAware)item).setCurrentWorkflowSession(this);
                                }
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                return result;
            }
            finally
            {

            }
        }

        public RuntimeContext getRuntimeContext()
        {
            return runtimeContext;
        }

        public ITaskInstance findTaskInstanceById(String id)
        {
            String taskInstanceId = id;
            try
            {
                return (ITaskInstance)this.execute(new WorkflowSessionITaskInstance(taskInstanceId));
            }
            catch //(EngineException ex)
            {
                //ex.printStackTrace();
                return null;
            }
            //catch (KernelException ex)
            //{
            //    ex.printStackTrace();
            //    return null;
            //}
        }

        public List<IWorkItem> findMyTodoWorkItems(String actorId)
        {
            List<IWorkItem> result = null;
            try
            {
                result = (List<IWorkItem>)this.execute(new WorkflowSessionIWorkItems(actorId));
            }
            catch //(EngineException ex)
            {
                //Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,null, ex);
            }
            //catch (KernelException ex)
            //{
            //    Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,null, ex);
            //}
            return result;
        }

        public List<IWorkItem> findMyTodoWorkItems(String actorId,String processInstanceId)
        {
            List<IWorkItem> result = null;
            try
            {
                result = (List<IWorkItem>)this.execute(new WorkflowSessionIWorkItems(actorId, processInstanceId));
            }
            catch //(EngineException ex)
            {
                //Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,null, ex);
            }
            //catch (KernelException ex)
            //{
            //    Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,null, ex);
            //}
            return result;
        }

        public List<IWorkItem> findMyTodoWorkItems(String actorId, String processId, String taskId)
        {
            List<IWorkItem> result = null;
            try {
                result = (List<IWorkItem>)this.execute(new WorkflowSessionIWorkItems(actorId, processId, taskId));

            }
            catch //(EngineException ex)
            {
                //Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,null, ex);
            }
            //catch (KernelException ex)
            //{
            //    Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,null, ex);
            //}
            return result;
        }

        public void setWithdrawOrRejectOperationFlag(Boolean b)
        {
            this.inWithdrawOrRejectOperation = b;
        }

        public Boolean isInWithdrawOrRejectOperation()
        {
            return this.inWithdrawOrRejectOperation;
        }

        public void setDynamicAssignmentHandler(
                DynamicAssignmentHandler dynamicAssignmentHandler)
        {
            this.dynamicAssignmentHandler = dynamicAssignmentHandler;
        }

        public IProcessInstance abortProcessInstance(String processInstanceId)
        {
            IProcessInstance processInstance = this.findProcessInstanceById(processInstanceId);
            processInstance.abort();
            return processInstance;
        }

        //	 public IWorkItem claimWorkItem(final String workItemId,
        //			final String taskInstanceId) throws EngineException,
        //			KernelException {
        //		IWorkItem result = null;
        //		try {
        //			result = (IWorkItem) this.execute(new IWorkflowSessionCallback() {
        //
        //				public Object doInWorkflowSession(RuntimeContext ctx)
        //						throws EngineException, KernelException {
        //					ITaskInstanceManager taskInstanceMgr = ctx
        //							.getTaskInstanceManager();
        //					IWorkItem workItem = taskInstanceMgr.claimWorkItem(
        //							workItemId, taskInstanceId);
        //					return workItem;
        //				}
        //			});
        //		} catch (EngineException ex) {
        //			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
        //					null, ex);
        //		} catch (KernelException ex) {
        //			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
        //					null, ex);
        //		}
        //		return result;
        //	}

        public IWorkItem claimWorkItem(String workItemId)
        {
            IWorkItem result = null;
            IWorkItem wi = this.findWorkItemById(workItemId);
            result = wi.claim();
            return result;
        }

        public void completeWorkItem(String workItemId)
        {
            IWorkItem wi = this.findWorkItemById(workItemId);
            wi.complete();

        }

        public void completeWorkItem(String workItemId, String comments)
        {
            IWorkItem wi = this.findWorkItemById(workItemId);
            wi.complete(comments);
        }

        public void completeWorkItem(String workItemId,DynamicAssignmentHandler dynamicAssignmentHandler, String comments)
        {
            IWorkItem wi = this.findWorkItemById(workItemId);
            wi.complete(dynamicAssignmentHandler, comments);

        }

        public void completeWorkItemAndJumpTo(String workItemId,
                String targetActivityId)
        {
            IWorkItem wi = this.findWorkItemById(workItemId);
            wi.jumpTo(targetActivityId);

        }

        public void completeWorkItemAndJumpTo(String workItemId,
                String targetActivityId, String comments)
        {
            IWorkItem wi = this.findWorkItemById(workItemId);
            wi.jumpTo(targetActivityId, comments);
        }

        public void completeWorkItemAndJumpTo(String workItemId,
                String targetActivityId,
                DynamicAssignmentHandler dynamicAssignmentHandler, String comments)
        {
            IWorkItem wi = this.findWorkItemById(workItemId);
            wi.jumpTo(targetActivityId, dynamicAssignmentHandler, comments);

        }

        public IProcessInstance findProcessInstanceById(String id)
        {
            try
            {
                return (IProcessInstance)this.execute(new WorkflowSessionIProcessInstance1(id));
            }
            catch// (EngineException ex)
            {
                //ex.printStackTrace();
                return null;
            }
            //catch (KernelException ex)
            //{
            //    //    ex.printStackTrace();
            //    return null;
            //}
        }

        public List<IProcessInstance> findProcessInstancesByProcessId(String processId)
        {
            try {
                return (List<IProcessInstance>) this.execute(new WorkflowSessionIProcessInstances(processId));
            }
            catch// (EngineException ex)
            {
                //ex.printStackTrace();
                return null;
            }
            //catch (KernelException ex)
            //{
            //    //    ex.printStackTrace();
            //    return null;
            //}
        }

        public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(String processId, Int32 version)
        {
            try {
                return (List<IProcessInstance>) this.execute(new WorkflowSessionIProcessInstances(processId, version));

            }
            catch// (EngineException ex)
            {
                //ex.printStackTrace();
                return null;
            }
            //catch (KernelException ex)
            //{
            //    //    ex.printStackTrace();
            //    return null;
            //}
        }

        public List<ITaskInstance> findTaskInstancesForProcessInstance(String processInstanceId, String activityId)
        {
            try
            {
                return (List<ITaskInstance>)this.execute(new WorkflowSessionITaskInstances(processInstanceId, activityId));
            }
            catch// (EngineException ex)
            {
                //ex.printStackTrace();
                return null;
            }
            //catch (KernelException ex)
            //{
            //    //    ex.printStackTrace();
            //    return null;
            //}
        }

        public IWorkItem reasignWorkItemTo(String workItemId, String actorId)
        {
            IWorkItem workItem = this.findWorkItemById(workItemId);
            return workItem.reasignTo(actorId);

        }

        public IWorkItem reasignWorkItemTo(String workItemId, String actorId, String comments)
        {
            IWorkItem workItem = this.findWorkItemById(workItemId);
            return workItem.reasignTo(actorId, comments);
        }

        public void rejectWorkItem(String workItemId)
        {
            IWorkItem workItem = this.findWorkItemById(workItemId);
            workItem.reject();

        }

        public void rejectWorkItem(String workItemId, String comments)
        {
            IWorkItem workItem = this.findWorkItemById(workItemId);
            workItem.reject(comments);
        }

        public IProcessInstance restoreProcessInstance(String processInstanceId)
        {
            IProcessInstance processInstance = this
                    .findProcessInstanceById(processInstanceId);
            processInstance.restore();
            return processInstance;
        }

        public ITaskInstance restoreTaskInstance(String taskInstanceId)
        {
            ITaskInstance taskInst = this.findTaskInstanceById(taskInstanceId);
            taskInst.restore();
            return taskInst;
        }

        public IProcessInstance suspendProcessInstance(String processInstanceId)
        {
            IProcessInstance processInstance = this
                    .findProcessInstanceById(processInstanceId);
            processInstance.suspend();
            return processInstance;
        }

        public ITaskInstance suspendTaskInstance(String taskInstanceId)
        {
            ITaskInstance taskInst = this.findTaskInstanceById(taskInstanceId);
            taskInst.suspend();
            return taskInst;
        }

        public IWorkItem withdrawWorkItem(String workItemId)
        {
            IWorkItem wi = this.findWorkItemById(workItemId);
            return wi.withdraw();
        }

        public void clearAttributes()
        {
            this.attributes.Clear();

        }

        public Object getAttribute(String name)
        {
            return this.attributes[name];
        }

        public void removeAttribute(String name)
        {
            this.attributes.Remove(name);
        }

        public void setAttribute(String name, Object attr)
        {
            this.attributes.Add(name, attr);

        }
    }
}
