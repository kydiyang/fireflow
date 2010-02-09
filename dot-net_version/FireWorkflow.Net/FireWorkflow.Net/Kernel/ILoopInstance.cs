using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model.Net;

namespace FireWorkflow.Net.Kernel
{
    public interface ILoopInstance : IEdgeInstance
    {
        Loop getLoop();

        /**
         * 计算循环条件的值，如果计算结果为true则执行该循环，否则不执行循环。
         * 如果循环条件表达式为null或者""值则其结果定义为false。
         * @return
         */
        //    public Boolean evaluateCondition();
    }
}
