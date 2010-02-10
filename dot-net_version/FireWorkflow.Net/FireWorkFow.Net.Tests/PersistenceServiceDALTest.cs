using FireWorkflow.Net.Persistence.OracleDAL;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;

namespace FireWorkFow.Net.Tests
{
    
    
    /// <summary>
    ///This is a test class for PersistenceServiceDALTest and is intended
    ///to contain all PersistenceServiceDALTest Unit Tests
    ///</summary>
    [TestClass()]
    public class PersistenceServiceDALTest
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
        ///A test for saveOrUpdateProcessInstance
        ///</summary>
        [TestMethod()]
        public void saveOrUpdateProcessInstanceTest()
        {
            PersistenceServiceDAL target = new PersistenceServiceDAL(); // TODO: Initialize to an appropriate value
            ProcessInstance processInstance = new ProcessInstance(); // TODO: Initialize to an appropriate value
            processInstance.setCreatedTime(DateTime.Now);
            processInstance.setDisplayName("test");
            processInstance.setStartedTime(DateTime.Now);
            processInstance.setExpiredTime(DateTime.Now);
            processInstance.setEndTime(DateTime.Now);
            processInstance.setProcessId(Guid.NewGuid().ToString().Replace("-", ""));
            processInstance.setName("namet");
            processInstance.setCreatorId("0033");
            //processInstance.setParentProcessInstanceId("asdfasdf");
            //processInstance.setParentTaskInstanceId("asdf");
            bool expected = true; // TODO: Initialize to an appropriate value
            bool actual;
            actual = target.saveOrUpdateProcessInstance(processInstance);

            object ddd = target.findProcessInstancesByProcessId("3051d71da776423bb525e00c5de60775");
            //processInstance1.setName(DateTime.Now.ToString());

            //actual = target.saveOrUpdateProcessInstance(processInstance1);

            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

    }
}
