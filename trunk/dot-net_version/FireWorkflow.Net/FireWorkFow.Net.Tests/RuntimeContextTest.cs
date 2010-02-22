using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using FireWorkflow.Net.Model;
using FireWorkflow.Net.Model.Net;
using FireWorkflow.Net.Model.Resource;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using FireWorkflow.Net.Engine.Beanfactory;
using Spring.Context;
using Spring.Context.Support;

namespace FireWorkFow.Net.Tests
{
    
    
    /// <summary>
    ///This is a test class for RuntimeContextTest and is intended
    ///to contain all RuntimeContextTest Unit Tests
    ///</summary>
    [TestClass()]
    public class RuntimeContextTest
    {


        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        /// <summary>
        ///A test for RuntimeContext Constructor
        ///</summary>
        [TestMethod()]
        public void RuntimeContextConstructorTest()
        {
            RuntimeContext actual;
            actual = RuntimeContextFactory.getRuntimeContext();

            SpringBeanFactory mySpringBeanFactory = new SpringBeanFactory();
            mySpringBeanFactory.setBeanFactory(new XmlApplicationContext(@"E:\Visual Studio 2010\Projects\FireWorkflow.Net\FireWorkflow.Net\FireflowContext.xml"));
            RuntimeContext rtCtx;
            rtCtx = (RuntimeContext)mySpringBeanFactory.GetBean("runtimeContext");
            IWorkflowSession workflowSession = rtCtx.getWorkflowSession();
        }
    }
}
