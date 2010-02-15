using FireWorkflow.Net.Persistence.OracleDAL;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using FireWorkflow.Net.Engine;
using FireWorkflow.Net.Engine.Impl;
using System.Data.OracleClient;
using FireWorkflow.Net.Engine.Definition;

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
        //[TestMethod()]
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

            ProcessInstance processInstance1 = (ProcessInstance)target.findProcessInstanceById("3377237c170342adad0f1654747b609a");
            processInstance1.setName(DateTime.Now.ToString());

            actual = target.saveOrUpdateProcessInstance(processInstance1);

            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for findAliveProcessInstanceById
        ///</summary>
        //[TestMethod()]
        public void findAliveProcessInstanceByIdTest()
        {
            PersistenceServiceDAL target = new PersistenceServiceDAL(); // TODO: Initialize to an appropriate value
            string id = "3377237c170342adad0f1654747b609a"; // TODO: Initialize to an appropriate value
            IProcessInstance expected = null; // TODO: Initialize to an appropriate value
            IProcessInstance actual;
            actual = target.findAliveProcessInstanceById(id);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for findProcessInstanceById
        ///</summary>
        //[TestMethod()]
        public void findProcessInstanceByIdTest()
        {
            PersistenceServiceDAL target = new PersistenceServiceDAL(); // TODO: Initialize to an appropriate value
            string id = "3377237c170342adad0f1654747b609a"; // TODO: Initialize to an appropriate value
            IProcessInstance expected = null; // TODO: Initialize to an appropriate value
            IProcessInstance actual;
            actual = target.findProcessInstanceById(id);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for saveOrUpdateWorkflowDefinition
        ///</summary>
        //[TestMethod()]
        public void saveOrUpdateWorkflowDefinitionTest()
        {
            PersistenceServiceDAL target = new PersistenceServiceDAL(); // TODO: Initialize to an appropriate value
            WorkflowDefinition workflowDef = new WorkflowDefinition(); // TODO: Initialize to an appropriate value
            workflowDef.setDefinitionType("asdf");
            workflowDef.setProcessId("ssss");
            workflowDef.setName("asdfasdf");
            workflowDef.setState(true);
            workflowDef.setProcessContent("asdfasdf");
            bool expected = false; // TODO: Initialize to an appropriate value
            bool actual;
            actual = target.saveOrUpdateWorkflowDefinition(workflowDef);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for findWorkflowDefinitionById
        ///</summary>
        //[TestMethod()]
        public void findWorkflowDefinitionByIdTest()
        {
            PersistenceServiceDAL target = new PersistenceServiceDAL(); // TODO: Initialize to an appropriate value
            string id = "d766dab5222d449998827d75bf59723b"; // TODO: Initialize to an appropriate value
            WorkflowDefinition expected = null; // TODO: Initialize to an appropriate value
            WorkflowDefinition actual;
            actual = target.findWorkflowDefinitionById(id);
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }
    }
}
