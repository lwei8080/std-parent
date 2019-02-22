package com.std.manage.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class StdInitializingBean implements InitializingBean {
	public final static Logger logger = LoggerFactory.getLogger(StdInitializingBean.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	}

}
