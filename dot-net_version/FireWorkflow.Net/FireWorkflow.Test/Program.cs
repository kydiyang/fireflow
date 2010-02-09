using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Model.Resource;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Beanfactory;

namespace FireWorkflow.Test
{
    class Program
    {
        static void Main(string[] args)
        {
            SpringBeanFactory mySpringBeanFactory = new SpringBeanFactory();
            RuntimeContext rtCtx = (RuntimeContext)mySpringBeanFactory.getBean("runtimeContext");
            IWorkflowSession workflowSession = rtCtx.getWorkflowSession();
        }
    }
}
