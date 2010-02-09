using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;





using ISM.FireWorkflow.Engine;
using ISM.FireWorkflow.Engine.Impl;
using ISM.FireWorkflow.Engine.Condition;
using ISM.FireWorkflow.Kernel;
using ISM.FireWorkflow.Kernel.Event;
using ISM.FireWorkflow.Kernel.Impl;
using ISM.FireWorkflow.Kernel.Plugin;
using ISM.FireWorkflow.Model;
using ISM.FireWorkflow.Model.Net;
using ISM.FireWorkflow.Base;

namespace ISM.FireWorkflow.Engine.Condition
{
    /// <summary>
    /// 实现条件表达式的解析。
    /// </summary>
    public class ConditionResolver : IConditionResolver, IRuntimeContextAware
    {
        protected RuntimeContext rtCtx = null;

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.fireflow.kenel.condition.IConditionResolver#resolveBooleanExpression
         * (java.lang.String)
         */
        public Boolean resolveBooleanExpression(Dictionary<String, Object> vars, String elExpression)//throws Exception
        {
            Evaluator evaluator = new Evaluator(typeof(bool), elExpression, "GetResolveBooleanExpression", vars);
            Object obj = evaluator.Evaluate("GetResolveBooleanExpression", vars);
            return (Boolean)obj;
        }

        public void setRuntimeContext(RuntimeContext ctx)
        {
            rtCtx = ctx;
        }

        public RuntimeContext getRuntimeContext()
        {
            return this.rtCtx;
        }
    }
}
