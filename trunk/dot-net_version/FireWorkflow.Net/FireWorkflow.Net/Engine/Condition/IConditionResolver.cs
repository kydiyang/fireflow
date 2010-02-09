using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Condition
{
    /// <summary>
    /// 转移条件表达式解析器
    /// </summary>
    public interface IConditionResolver
    {
        /// <summary>
        /// 解析条件表达式。条件表达是必须是一个值为Boolean类型的EL表达式
        /// </summary>
        /// <param name="vars">变量列表</param>
        /// <param name="elExpression">条件表达式</param>
        /// <returns>返回条件表达式的计算结果</returns>
        Boolean resolveBooleanExpression(Dictionary<String, Object> vars, String elExpression);// throws Exception;
    }
}
