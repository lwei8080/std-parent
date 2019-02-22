package com.std.biz;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDelegate implements JavaDelegate {
	private final static Logger logger = LoggerFactory.getLogger(TestDelegate.class);

	public TestDelegate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(DelegateExecution execution) {
		// TODO Auto-generated method stub
		logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		logger.debug(":::::::::::::::{}",execution.getVariables());
		logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}

}
