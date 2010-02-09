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
using FireWorkflow.Net.Base;

namespace FireWorkflow.Net.Engine.Condition
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
