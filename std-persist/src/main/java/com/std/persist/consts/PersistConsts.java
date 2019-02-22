package com.std.persist.consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.ConfigurableConstants;

public class PersistConsts extends ConfigurableConstants {
	private final static Logger logger = LoggerFactory.getLogger(PersistConsts.class);
    static {
    	logger.info("load std persist.properties");
        init("persist.properties");
    }
}
