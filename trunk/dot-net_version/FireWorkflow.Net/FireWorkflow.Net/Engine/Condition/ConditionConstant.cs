using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Condition
{
    /// <summary>
    /// 条件表达式相关的常量
    /// </summary>
    public class ConditionConstant
    {
        /// <summary>
        /// 如果某个条件表达式是DEFAUT,则表示：如果他的兄弟的转移条件计算结果都是false，则执行本转移
        /// </summary>
        public const String DEFAULT = "DEFAULT";
    }
}
