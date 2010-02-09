using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel.Impl
{
    public class NetInstance : INetInstance
    {

        private WorkflowProcess workflowProcess = null;
        private Int32? version = null;
        private StartNodeInstance startNodeInstance = null;
        //	private List<EndNodeInstance> endNodeInstances = new ArrayList<EndNodeInstance>();
        private Dictionary<String, Object> wfElementInstanceMap = new Dictionary<String, Object>();
        //	private IRuntimeContext runtimeContext = null;
        protected List<INodeInstanceEventListener> eventListeners = new List<INodeInstanceEventListener>();

        public NetInstance(WorkflowProcess process, Dictionary<String, List<IKernelExtension>> kenelExtensions)
        {
            this.workflowProcess = process;

            StartNode startNode = workflowProcess.getStartNode();
            startNodeInstance = new StartNodeInstance(startNode);
            List<IKernelExtension> extensionList = kenelExtensions[startNodeInstance.getExtensionTargetName()];
            for (int i = 0; extensionList != null && i < extensionList.Count; i++)
            {
                IKernelExtension extension = extensionList[i];
                startNodeInstance.registExtension(extension);
            }
            this.setStartNodeInstance(startNodeInstance);
            wfElementInstanceMap.Add(startNode.getId(), startNodeInstance);


            List<Activity> activities = workflowProcess.getActivities();
            for (int i = 0; i < activities.Count; i++)
            {
                Activity activity = (Activity)activities[i];
                ActivityInstance activityInstance = new ActivityInstance(activity);
                extensionList = kenelExtensions[activityInstance.getExtensionTargetName()];
                for (int j = 0; extensionList != null && j < extensionList.Count; j++)
                {
                    IKernelExtension extension = extensionList[j];
                    activityInstance.registExtension(extension);
                }
                wfElementInstanceMap.Add(activity.getId(), activityInstance);
            }

            List<Synchronizer> synchronizers = workflowProcess.getSynchronizers();
            for (int i = 0; i < synchronizers.Count; i++)
            {
                Synchronizer synchronizer = (Synchronizer)synchronizers[i];
                SynchronizerInstance synchronizerInstance = new SynchronizerInstance(synchronizer);
                extensionList = kenelExtensions[synchronizerInstance.getExtensionTargetName()];
                for (int j = 0; extensionList != null && j < extensionList.Count; j++)
                {
                    IKernelExtension extension = extensionList[j];
                    synchronizerInstance.registExtension(extension);
                }
                wfElementInstanceMap.Add(synchronizer.getId(), synchronizerInstance);
            }

            List<EndNode> endNodes = workflowProcess.getEndNodes();
            //        List<EndNodeInstance> endNodeInstances = netInstance.getEndNodeInstances();
            for (int i = 0; i < endNodes.Count; i++)
            {
                EndNode endNode = endNodes[i];
                EndNodeInstance endNodeInstance = new EndNodeInstance(endNode);
                //            endNodeInstances.add(endNodeInstance);
                extensionList = kenelExtensions[endNodeInstance.getExtensionTargetName()];
                for (int j = 0; extensionList != null && j < extensionList.Count; j++)
                {
                    IKernelExtension extension = extensionList[j];
                    endNodeInstance.registExtension(extension);
                }
                wfElementInstanceMap.Add(endNode.getId(), endNodeInstance);
            }

            List<Transition> transitions = workflowProcess.getTransitions();
            for (int i = 0; i < transitions.Count; i++)
            {
                Transition transition = (Transition)transitions[i];
                TransitionInstance transitionInstance = new TransitionInstance(transition);

                String fromNodeId = transition.getFromNode().getId();
                if (fromNodeId != null)
                {
                    INodeInstance enteringNodeInstance = (INodeInstance)wfElementInstanceMap[fromNodeId];
                    if (enteringNodeInstance != null)
                    {
                        enteringNodeInstance.addLeavingTransitionInstance(transitionInstance);
                        transitionInstance.setEnteringNodeInstance(enteringNodeInstance);
                    }
                }

                String toNodeId = transition.getToNode().getId();
                if (toNodeId != null)
                {
                    INodeInstance leavingNodeInstance = (INodeInstance)wfElementInstanceMap[toNodeId];
                    if (leavingNodeInstance != null)
                    {
                        leavingNodeInstance.addEnteringTransitionInstance(transitionInstance);
                        transitionInstance.setLeavingNodeInstance(leavingNodeInstance);
                    }
                }
                extensionList = kenelExtensions[transitionInstance.getExtensionTargetName()];
                for (int j = 0; extensionList != null && j < extensionList.Count; j++)
                {
                    IKernelExtension extension = extensionList[j];
                    transitionInstance.registExtension(extension);
                }
                wfElementInstanceMap.Add(transitionInstance.getId(), transitionInstance);
            }

            List<Loop> loops = workflowProcess.getLoops();
            for (int i = 0; i < loops.Count; i++)
            {
                Loop loop = (Loop)loops[i];
                LoopInstance loopInstance = new LoopInstance(loop);

                String fromNodeId = loop.getFromNode().getId();
                if (fromNodeId != null)
                {
                    INodeInstance enteringNodeInstance = (INodeInstance)wfElementInstanceMap[fromNodeId];
                    if (enteringNodeInstance != null)
                    {

                        enteringNodeInstance.addLeavingLoopInstance(loopInstance);
                        loopInstance.setEnteringNodeInstance(enteringNodeInstance);
                    }
                }

                String toNodeId = loop.getToNode().getId();
                if (toNodeId != null)
                {
                    INodeInstance leavingNodeInstance = (INodeInstance)wfElementInstanceMap[toNodeId];
                    if (leavingNodeInstance != null)
                    {
                        leavingNodeInstance.addEnteringLoopInstance(loopInstance);
                        loopInstance.setLeavingNodeInstance(leavingNodeInstance);
                    }
                }
                extensionList = kenelExtensions[loopInstance.getExtensionTargetName()];
                for (int j = 0; extensionList != null && j < extensionList.Count; j++)
                {
                    IKernelExtension extension = extensionList[j];
                    loopInstance.registExtension(extension);
                }
                wfElementInstanceMap.Add(loopInstance.getId(), loopInstance);
            }
        }

        public String getId()
        {
            return this.workflowProcess.getId();
        }

        public Int32? getVersion()
        {
            return version;
        }

        public void setVersion(Int32 v)
        {
            this.version = v;
        }

        public void run(IProcessInstance processInstance)
        {
            if (startNodeInstance == null)
            {
                KernelException exception = new KernelException(processInstance,
                        this.getWorkflowProcess(),
                        "Error:NetInstance is illegal ，the startNodeInstance can NOT be NULL ");
                throw exception;
            }

            Token token = new Token();
            token.setAlive(true);
            token.setProcessInstance(processInstance);
            token.setValue(startNodeInstance.getVolume());
            token.setStepNumber(0);
            token.setFromActivityId(IToken.FROM_START_NODE);

            //processevent应该放在processInstance中去触发
            //        ProcessInstanceEvent event = new ProcessInstanceEvent();
            //        event.setToken(token);
            //        this.fireBeforeProcessInstanceRunEvent(event);//??

            startNodeInstance.fire(token);
        }

        /**
         * 结束流程实例，如果流程状态没有达到终态，则直接返回。
         * @throws RuntimeException
         */
        public void complete()
        {
            //1、判断是否所有的EndeNodeInstance都到达终态，如果没有到达，则直接返回。
            //2、执行compelete操作，
            //3、触发after complete事件
            //4、返回主流程
        }


        //	public IRuntimeContext getRtCxt() {
        //		return runtimeContext;
        //	}
        //
        //	public void setRtCxt(IRuntimeContext rtCxt) {
        //		this.runtimeContext = rtCxt;
        //	}

        public WorkflowProcess getWorkflowProcess()
        {
            return workflowProcess;
        }

        //	public void setWorkflowProcess(WorkflowProcess workflowProcess) {
        //		this.workflowProcess = workflowProcess;
        //
        //	}
        public StartNodeInstance getStartNodeInstance()
        {
            return startNodeInstance;
        }

        public void setStartNodeInstance(StartNodeInstance startNodeInstance)
        {
            this.startNodeInstance = startNodeInstance;
        }

        //    public List<EndNodeInstance> getEndNodeInstances() {
        //        return endNodeInstances;
        //    }

        //	public void setEndNodeInstances(List endNodeInstances) {
        //		EndNodeInstances = endNodeInstances;
        //	}
        //    protected void fireBeforeProcessInstanceRunEvent(ProcessInstanceEvent event) throws KenelException {
        //        for (int i = 0; i < this.eventListeners.Count; i++) {
        //            IProcessInstanceEventListener listener = (IProcessInstanceEventListener) this.eventListeners[i);
        //            listener.onProcessInstanceFired(event);
        //        }
        //    }
        //
        //    protected void fireAfterProcessInstanceCompleteEvent(ProcessInstanceEvent event) throws KenelException {
        //        for (int i = 0; i < this.eventListeners.Count; i++) {
        //            IProcessInstanceEventListener listener = (IProcessInstanceEventListener) this.eventListeners[i);
        //            listener.onProcessInstanceFired(event);
        //        }
        //    }
        //	public void setRuntimeContext(IRuntimeContext rtCtx){
        //		runtimeContext = rtCtx;
        //	}
        //	
        //	public IRuntimeContext getRuntimeContext(){
        //		return runtimeContext;
        //	}

        public Object getWFElementInstance(String wfElementId)
        {
            return wfElementInstanceMap[wfElementId];
        }

        #region INetInstance 成员


        int INetInstance.getVersion()
        {
            throw new NotImplementedException();
        }

        #endregion
    }

}
