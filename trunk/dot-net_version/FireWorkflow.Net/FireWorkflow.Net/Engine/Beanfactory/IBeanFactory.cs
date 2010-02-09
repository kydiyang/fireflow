using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FireWorkflow.Net.Engine.Beanfactory
{
    /// <summary>
    /// <para>Engine把创建bean实例的工作委派给该服务。</para>
    /// <para>Engine在如下情况下需要获得相关Bean的实例(未全部枚举)。</para>
    /// <para>1)Tool类型的Task,Engine通过该服务获得ApplicationHandler的实例然后调用其方法IApplicationHandler.execute(ITaskInstance taskInstace)</para>
    /// <para>2)Engine在触发事件时，需要获得相关Listener的实例</para>
    /// <para>3)在分配工作项的时候需要获得IAssignmentHandler的实例。</para>
    /// </summary>
    public interface IBeanFactory
    {

        /// <summary>
        /// 根据bean的名字返回bean的实例
        /// </summary>
        /// <param name="beanName">bean name具体含义是什么由IBeanFactory的实现类来决定</param>
        /// <returns></returns>
        Object getBean(String beanName);
    }
}
