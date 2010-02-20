using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Engine.Beanfactory;
using FireWorkflow.Net.Engine.Calendar;
using FireWorkflow.Net.Engine.Definition;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Persistence;
using FireWorkflow.Net.Engine.Taskinstance;
using FireWorkflow.Net.Kernel;
using FireWorkflow.Net.Engine.Condition;

namespace FireWorkflow.Net.Engine
{

    /// <summary>
    /// 在没有spring环境下构建RuntimeContext实例。<br/>
    /// (暂未实现)
    /// </summary>
    public class RuntimeContextFactory
    {
        private static RuntimeContext ctx ;

        public static RuntimeContext getRuntimeContext()
        {
            if (ctx == null)
            {
                ctx = new RuntimeContext();
                ctx.EnableTrace = true;
                //转移条件表达式解析服务

                ctx.ConditionResolver = new FireWorkflow.Net.Engine.Condition.ConditionResolver();
                //实例对象存取服务
                Type type = Type.GetType("FireWorkflow.Net.Persistence.OracleDAL.PersistenceServiceDAL, FireWorkflow.Net.Persistence.OracleDAL");
                if (type != null)
                {
                    ctx.PersistenceService = (IPersistenceService)Activator.CreateInstance(type, new object[] { "OracleServer" });
                }
                else throw new Exception("默认FireWorkflow.Net.Persistence.OracleDAL程序集没有引入！");
                //流程定义服务，通过该服务获取流程定义
                ctx.DefinitionService = new FireWorkflow.Net.Engine.Definition.DefinitionService4FileSystem();
                
                //ctx.setCalendarService(new DefaultCalendarService());
                //ctx.setConditionResolver(new ConditionResolver());
                //ctx.setDefinitionService(new DefinitionService4DBMS());
            }
            return ctx;
        }
    }
}
