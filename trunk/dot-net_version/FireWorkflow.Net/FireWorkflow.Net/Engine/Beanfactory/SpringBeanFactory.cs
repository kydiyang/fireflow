using System;
using System.Collections.Generic;
using System.Text;
using Spring.Context;
using Spring.Context.Support;
namespace FireWorkflow.Net.Engine.Beanfactory
{
    /// <summary>用Spring 的IOC容器作为Fire Workflow 的BeanFactory</summary>
    public class SpringBeanFactory : IBeanFactory
    {
        IApplicationContext springBeanFactory = new XmlApplicationContext("FireflowContext.xml");

        public Object getBean(String beanName)
        {
            return springBeanFactory.GetObject(beanName);
        }

        public void setBeanFactory(IApplicationContext arg0)// throws BeansException 
        {
            springBeanFactory = arg0;
        }
    }
}
