package org.fireflow.engine.test.support;

import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.hibernate.FireWorkflowHelperDao;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.support.TransactionTemplate;

// hibernate
@ContextConfiguration(locations = { "classpath:/config/applicationContext-hibernate.xml",
									"classpath:/config/FireflowContext-hibernate.xml", 
									"classpath:/config/AllTheProcessHandlers.xml" })
// jdbc
// @ContextConfiguration(locations = {"classpath:/config/applicationContext-jdbc.xml",
// 									"classpath:/config/FireflowContext-jdbc.xml",
// 									"classpath:/config/AllTheProcessHandlers.xml" })
// JPA
// @ContextConfiguration(locations = {"classpath:/config/applicationContext-jpa.xml",
// 									"classpath:/config/FireflowContext-jpa.xml",
// 									"classpath:/config/AllTheProcessHandlers.xml" })
public class FireFlowAbstractTests extends AbstractJUnit4SpringContextTests
{
	private static final Logger log = LoggerFactory.getLogger(FireFlowAbstractTests.class);

	@Autowired
	protected RuntimeContext runtimeContext = null;

	@Autowired
	protected TransactionTemplate transactionTemplate = null;

	@Autowired
	protected FireWorkflowHelperDao fireWorkflowHelperDao;

	@Before
	public void prepareTestData()
	{
		log.debug("方法执行前调用");
	}

	@After
	public void destroyTestData()
	{
		// 每个测试方法执行完成后调用
		log.debug("方法执行后调用");
	}
}
