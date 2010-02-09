using System;
using System.Collections.Generic;
using System.Text;
using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Engine.Condition;
using ISM.FireWorkflow.Engine.Impl;
using ISM.FireWorkflow.Kernel;
using ISM.FireWorkflow.Kernel.Event;
using ISM.FireWorkflow.Kernel.Plugin;
using ISM.FireWorkflow.Kernel.Impl;

namespace ISM.FireWorkflow.Engine.Kernelextensions
{
    public class LoopInstanceExtension : IKernelExtension, IEdgeInstanceEventListener, IRuntimeContextAware
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

        public String getExtentionTargetName()
        {
            return LoopInstance.Extension_Target_Name;
        }

        public String getExtentionPointName()
        {
            return LoopInstance.Extension_Point_LoopInstanceEventListener;
        }

        private Boolean determineTheAliveOfToken(Dictionary<String, Object> vars, String condition)
        {
            // TODO通过计算transition上的表达式来确定alive的值

            IConditionResolver elResolver = this.rtCtx.getConditionResolver();
            Boolean b = elResolver.resolveBooleanExpression(vars, condition);

            return b;
        }

        public void calculateTheAliveValue(IToken token, String condition)
        {
            if (!token.isAlive())
            {
                return;// 如果token是dead状态，表明synchronizer的joinpoint是dead状态，不需要重新计算。
            }

            // 1、如果没有循环条件，默认为false
            if (condition == null || condition.Trim().Equals(""))
            {
                token.setAlive(false);
                return;
            }
            // 3、计算EL表达式
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

        public void onEdgeInstanceEventFired(EdgeInstanceEvent e)
        {

            if (e.getEventType() == EdgeInstanceEvent.ON_TAKING_THE_TOKEN)
            {
                IToken token = e.getToken();
                // 计算token的alive值
                ILoopInstance transInst = (ILoopInstance)e.getSource();
                String condition = transInst.getLoop().getCondition();

                calculateTheAliveValue(token, condition);

                if (rtCtx.isEnableTrace() && token.isAlive())
                {
                    ProcessInstanceTrace trace = new ProcessInstanceTrace();
                    trace.setProcessInstanceId(e.getToken().getProcessInstanceId());
                    trace.setStepNumber(e.getToken().getStepNumber() + 1);
                    trace.setType(ProcessInstanceTrace.LOOP_TYPE);
                    trace.setFromNodeId(transInst.getLoop().getFromNode().getId());
                    trace.setToNodeId(transInst.getLoop().getToNode().getId());
                    trace.setEdgeId(transInst.getLoop().getId());
                    rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(trace);
                }
            }
        }
    }
}
