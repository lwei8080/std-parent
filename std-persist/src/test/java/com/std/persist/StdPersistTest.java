package com.std.persist;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.std.persist.model.SysManager;
import com.std.persist.mysql.dao.wrap.ISysManagerDaoWrap;
import com.std.persist.mysql.plugins.PageInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext-persist.xml"})
public class StdPersistTest {
	private final static Logger logger = LoggerFactory.getLogger(StdPersistTest.class);
	@Autowired
	private ISysManagerDaoWrap sysManagerDaoWrap;

	@Test
	public void test() {
		Map<String, Object> params = new HashMap<String, Object>();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(1);
		pageInfo.setPageSize(10);
		params.put("pageInfo", pageInfo);
		List<SysManager> managers = sysManagerDaoWrap.getSysManagerByPage(params);
		for(SysManager m : managers) {
			logger.info("{}", ReflectionToStringBuilder.toString(m));
		}
	}

}
