package org.fireflow.engine.test.support;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

// hibernate
@ContextConfiguration(locations = { "classpath:/config/applicationContext-hibernate.xml",
									"classpath:/config/FireflowContext-hibernate.xml",
									"classpath:/config/AllTheProcessHandlers.xml"
								  })
// jdbc
//@ContextConfiguration(locations = { "classpath:/config/applicationContext-jdbc.xml",
//									"classpath:/config/FireflowContext-jdbc.xml",
//									"classpath:/config/AllTheProcessHandlers.xml"
//								  })

// JPA
//@ContextConfiguration(locations = { "classpath:/config/applicationContext-jpa.xml",
//									"classpath:/config/FireflowContext-jpa.xml",
//									"classpath:/config/AllTheProcessHandlers.xml"
//								  })
public class FireFlowAbstractTests extends AbstractTransactionalJUnit4SpringContextTests
{
	private static final Logger log = LoggerFactory.getLogger(FireFlowAbstractTests.class);

	@Before
	public void prepareTestData()
	{
		log.debug(this.getClass().getName());

		// 如下两条是在需要执行每个方法前时初始化的数据脚本
		// setSqlScriptEncoding("UTF-8");
		// executeSqlScript("classpath:/sql/init.sql", false);
	}

	@After
	public void destroyTestData()
	{
		// log.debug("destroyTestData");
	}
}
