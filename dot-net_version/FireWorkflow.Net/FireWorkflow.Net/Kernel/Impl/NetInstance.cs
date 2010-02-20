/**
 * Copyright 2003-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 * @author 非也,nychen2000@163.com
 * @Revision to .NET 无忧 lwz0721@gmail.com 2010-02
 */
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
        private Int32 version = 0;
        private StartNodeInstance startNodeInstance = null;
        //	private List<EndNodeInstance> endNodeInstances = new ArrayList<EndNodeInstance>();
        private Dictionary<String, Object> wfElementInstanceMap = new Dictionary<String, Object>();
        //	private IRuntimeContext runtimeContext = null;
        protected List<INodeInstanceEventListener> eventListeners = new List<INodeInstanceEventListener>();

        /// <summary>wangmj  初始化一个工作流网实例,将引擎的扩展属性，注入到对应的工作流元素中</summary>
        /// <param name="process"></param>
        /// <param name="kenelExtensions"></param>
        public NetInstance(WorkflowProcess process, Dictionary<String, List<IKernelExtension>> kenelExtensions)
        {
            this.workflowProcess = process;

            //开始节点
            StartNode startNode = workflowProcess.StartNode;
            startNodeInstance = new StartNodeInstance(startNode);
            List<IKernelExtension> extensionList = kenelExtensions[startNodeInstance.getExtensionTargetName()];
            for (int i = 0; extensionList != null && i < extensionList.Count; i++)
            {
                IKernelExtension extension = extensionList[i];
                startNodeInstance.registExtension(extension);
            }
            this.setStartNodeInstance(startNodeInstance);
            wfElementInstanceMap.Add(startNode.Id, startNodeInstance);

            //活动节点activity
            List<Activity> activities = workflowProcess.Activities;
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
                wfElementInstanceMap.Add(activity.Id, activityInstance);
            }

            //同步器节点
            List<Synchronizer> synchronizers = workflowProcess.Synchronizers;
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
                wfElementInstanceMap.Add(synchronizer.Id, synchronizerInstance);
            }

            //结束节点
            List<EndNode> endNodes = workflowProcess.EndNodes;
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
                wfElementInstanceMap.Add(endNode.Id, endNodeInstance);
            }

            //转移线
            List<Transition> transitions = workflowProcess.Transitions;
            for (int i = 0; i < transitions.Count; i++)
            {
                Transition transition = (Transition)transitions[i];
                TransitionInstance transitionInstance = new TransitionInstance(transition);

                String fromNodeId = transition.FromNode.Id;
                if (fromNodeId != null)
                {
                    INodeInstance enteringNodeInstance = (INodeInstance)wfElementInstanceMap[fromNodeId];
                    if (enteringNodeInstance != null)
                    {
                        enteringNodeInstance.AddLeavingTransitionInstance(transitionInstance);
                        transitionInstance.setEnteringNodeInstance(enteringNodeInstance);
                    }
                }

                String toNodeId = transition.ToNode.Id;
                if (toNodeId != null)
                {
                    INodeInstance leavingNodeInstance = (INodeInstance)wfElementInstanceMap[toNodeId];
                    if (leavingNodeInstance != null)
                    {
                        leavingNodeInstance.AddEnteringTransitionInstance(transitionInstance);
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

            //循环线
            List<Loop> loops = workflowProcess.Loops;
            for (int i = 0; i < loops.Count; i++)
            {
                Loop loop = (Loop)loops[i];
                LoopInstance loopInstance = new LoopInstance(loop);

                String fromNodeId = loop.FromNode.Id;
                if (fromNodeId != null)
                {
                    INodeInstance enteringNodeInstance = (INodeInstance)wfElementInstanceMap[fromNodeId];
                    if (enteringNodeInstance != null)
                    {

                        enteringNodeInstance.AddLeavingLoopInstance(loopInstance);
                        loopInstance.setEnteringNodeInstance(enteringNodeInstance);
                    }
                }

                String toNodeId = loop.ToNode.Id;
                if (toNodeId != null)
                {
                    INodeInstance leavingNodeInstance = (INodeInstance)wfElementInstanceMap[toNodeId];
                    if (leavingNodeInstance != null)
                    {
                        leavingNodeInstance.AddEnteringLoopInstance(loopInstance);
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
            return this.workflowProcess.Id;
        }

        public Int32 getVersion()
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

            Token token = new Token();//初始化token
            token.IsAlive = true;//活动的
            token.ProcessInstance = processInstance;//对应流程实例
            token.Value = startNodeInstance.Volume;//token容量
            token.StepNumber = 0;//步骤号，开始节点的第一步默认为0
            token.FromActivityId = TokenFrom.FROM_START_NODE;//从哪个节点来 "FROM_START_NODE" 规定的节点。

            //注意这里并没有保存token
            startNodeInstance.fire(token);//启动开始节点
        }

        /// <summary>结束流程实例，如果流程状态没有达到终态，则直接返回。</summary>
        public void complete()
        {
            //1、判断是否所有的EndeNodeInstance都到达终态，如果没有到达，则直接返回。
            //2、执行compelete操作，
            //3、触发after complete事件
            //4、返回主流程
        }

        public WorkflowProcess getWorkflowProcess()
        {
            return workflowProcess;
        }

        public StartNodeInstance getStartNodeInstance()
        {
            return startNodeInstance;
        }

        public void setStartNodeInstance(StartNodeInstance startNodeInstance)
        {
            this.startNodeInstance = startNodeInstance;
        }

        public Object getWFElementInstance(String wfElementId)
        {
            return wfElementInstanceMap[wfElementId];
        }
    }
}
