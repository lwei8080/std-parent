package com.std.manage.consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.std.common.utils.ConfigurableConstants;

public class ApplicationConsts extends ConfigurableConstants {
	private final static Logger logger = LoggerFactory.getLogger(ApplicationConsts.class);
    static {
    	logger.info("load std application.properties");
        init("application.properties");
    }
	public static final String ckey = getValue("academic.cookie.remember.ckey");
	
	public static final String druidAuth = getValue("academic.manage.auth.druid.config");
	
	public static final String shiroEhcacheLocation = getValue("academic.shiro.location.ehcache.xml");

    public static final String passwordRetryCacheName = getValue("academic.shiro.password.retry.cache.name");

    public static final String retryCountMax = getValue("academic.shiro.password.retry.count.max");
    
	public static final String authenticationCache = getValue("academic.shiro.authentication.cache.name");
	
	public static final String authorizationCache = getValue("academic.shiro.authorization.cache.name");
	
	public static final String domainManager = getValue("academic.domain.manager");
	
	public static final String kaptchaManagerSessionKey = getValue("academic.manager.kaptcha.key");
	
	public static final String amsid = getValue("academic.manage.shiro.sessionid.cookie.key");
	public static final String amspid = getValue("academic.manage.shiro.sessionid.cookie.protect.key");
}
