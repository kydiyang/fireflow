using FireWorkflow.Net.Engine.Condition;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;

namespace FireWorkFow.Net.Tests
{
    
    
    /// <summary>
    ///This is a test class for ConditionResolverTest and is intended
    ///to contain all ConditionResolverTest Unit Tests
    ///</summary>
    [TestClass()]
    public class ConditionResolverTest
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
        ///A test for resolveBooleanExpression
        ///</summary>
        //[TestMethod()]
        public void resolveBooleanExpressionTest()
        {
            ConditionResolver target = new ConditionResolver(); // TODO: Initialize to an appropriate value
            Dictionary<string, object> vars = new Dictionary<string,object>(); // TODO: Initialize to an appropriate value
            vars.Add("a", 10);
            vars.Add("b", 11);
            string elExpression = "a<b"; // TODO: Initialize to an appropriate value
            bool expected = false; // TODO: Initialize to an appropriate value
            bool actual;
            actual = target.resolveBooleanExpression(vars, elExpression);
            actual = target.resolveBooleanExpression(vars, "a>b");
            Assert.AreEqual(expected, actual);
        }
    }
}

