using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Condition;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Kernel.Event;
using FireWorkflow.Net.Kernel.Impl;
using FireWorkflow.Net.Kernel.Plugin;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Engine.Kernelextensions
{
    public class TransitionInstanceExtension : IKernelExtension,
        IEdgeInstanceEventListener, IRuntimeContextAware
    {

        protected RuntimeContext rtCtx = null;

        public void setRuntimeContext(RuntimeContext ctx)
        {
            this.rtCtx = ctx;
        }

        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
        }

        /// <summary>
        /// 执行分支判断策略，即设置token的alive属性 首先，如果this.alive==false,则所有的token的Alive属性皆为false
        /// 然后，如果在nexttransitionList中有值，则直接执行列表中的tansition
        /// 否则，通过计算Transition的表达式来确定下一个transition,
        /// </summary>
        /// <param name="vars"></param>
        /// <param name="condition"></param>
        /// <returns></returns>
        private Boolean determineTheAliveOfToken(Dictionary<String, Object> vars, String condition)
        {
            //        System.out.println("Inside SynchronizerInstance.DeterminTheAliveOfToken():: joinPoint.getAlive =" + joinPoint.getAlive());
            //        if (!joinPoint.getAlive()) {
            //            return false;
            //        }
            //        Set<String> nextTransitionInstanceNames = joinPoint.getAppointedTransitionNames();
            //        if (nextTransitionInstanceNames.Count > 0) {
            //            Iterator nextTransNamesIterator = nextTransitionInstanceNames.iterator();
            //            while (nextTransNamesIterator.hasNext()) {
            //                String transName = (String) nextTransNamesIterator.next();
            //                if (transName.Equals(transInst.getTransition().Name)) {
            //                    return true;
            //                }
            //            }
            //        }
            //
            //        if (transInst.getTransition().getCondition() == null || transInst.getTransition().getCondition().trim().Equals("")) {
            //            return true;
            //        }

            // TODO通过计算transition上的表达式来确定alive的值

            IConditionResolver elResolver = this.rtCtx.getConditionResolver();
            Boolean b = elResolver.resolveBooleanExpression(vars, condition);

            return b;
        }

        public void calculateTheAliveValue(IToken token, String condition)
        {

            if (!token.isAlive())
            {
                return;//如果token是dead状态，表明synchronizer的joinpoint是dead状态，不需要重新计算。
            }

            //1、如果没有转移条件，默认为true
            if (condition == null || condition.Trim().Equals(""))
            {
                token.setAlive(true);
                return;
            }
            //2、default类型的不需要计算其alive值，该值由synchronizer决定
            if (condition.Trim().Equals(ConditionConstant.DEFAULT))
            {
                return;
            }

            //3、计算EL表达式
            try
            {
                Boolean alive = determineTheAliveOfToken(token.getProcessInstance().getProcessInstanceVariables(), condition);
                token.setAlive(alive);
            }
            catch (Exception ex)
            {
                throw new EngineException(token.getProcessInstanceId(), token.getProcessInstance().getWorkflowProcess(), token.getNodeId(), ex.Message);
            }

        }

        public String getExtentionTargetName()
        {
            return TransitionInstance.Extension_Target_Name;
        }

        public String getExtentionPointName()
        {
            return TransitionInstance.Extension_Point_TransitionInstanceEventListener;
        }

        public void onEdgeInstanceEventFired(EdgeInstanceEvent e)
        {
            if (e.getEventType() == EdgeInstanceEvent.ON_TAKING_THE_TOKEN)
            {
                IToken token = e.getToken();
                ITransitionInstance transInst = (ITransitionInstance)e.getSource();
                String condition = transInst.getTransition().Condition;
                calculateTheAliveValue(token, condition);

                if (rtCtx.isEnableTrace() && token.isAlive())
                {
                    Transition transition = transInst.getTransition();
                    IWFElement fromNode = transition.FromNode;
                    int minorNumber = 1;
                    if (fromNode is Activity)
                    {
                        minorNumber = 2;
                    }
                    else
                    {
                        minorNumber = 1;
                    }

                    ProcessInstanceTrace trace = new ProcessInstanceTrace();
                    trace.setProcessInstanceId(e.getToken().getProcessInstanceId());
                    trace.setStepNumber(e.getToken().getStepNumber());
                    trace.setType(ProcessInstanceTrace.TRANSITION_TYPE);
                    trace.setFromNodeId(transInst.getTransition().FromNode.Id);
                    trace.setToNodeId(transInst.getTransition().ToNode.Id);
                    trace.setEdgeId(transInst.getTransition().Id);
                    trace.setMinorNumber(minorNumber);
                    rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(trace);
                }
            }

        }
    }
}
