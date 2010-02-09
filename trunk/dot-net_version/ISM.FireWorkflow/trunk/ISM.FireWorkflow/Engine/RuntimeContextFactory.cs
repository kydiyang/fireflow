using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISM.FireWorkflow.Engine
{

    /// <summary>
    /// 在没有spring环境下构建RuntimeContext实例。<br/>
    /// (暂未实现)
    /// </summary>
    public class RuntimeContextFactory
    {
        private static RuntimeContext ctx ;

        //    public static getRuntimeContext(){
        //        if (ctx==null){
        //            ctx = RuntimeContext.getInstance();
        //            ctx.setCalendarService(new DefaultCalendarService());
        //            ctx.setConditionResolver(new ConditionResolver());
        //            ctx.setDefinitionService(new DefinitionService4DBMS());
        //        }
        //    }
    }
}
