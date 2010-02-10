using FireWorkflow.Net.Model.Io;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.IO;
using FireWorkflow.Net.Model;

namespace FireWorkFow.Net.Tests
{
    
    
    /// <summary>
    ///This is a test class for Dom4JFPDLParserTest and is intended
    ///to contain all Dom4JFPDLParserTest Unit Tests
    ///</summary>
    [TestClass()]
    public class Dom4JFPDLParserTest
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
        ///A test for parse
        ///</summary>
        [TestMethod()]
        public void parseTest()
        {
            Dom4JFPDLParser target = new Dom4JFPDLParser(); // TODO: Initialize to an appropriate value
            Stream srin = null; // TODO: Initialize to an appropriate value
            WorkflowProcess expected = null; // TODO: Initialize to an appropriate value
            WorkflowProcess actual;
            actual = target.parse(srin);
            Assert.AreEqual(expected, actual);
            //Assert.Inconclusive("Verify the correctness of this test method.");
        }
    }
}
