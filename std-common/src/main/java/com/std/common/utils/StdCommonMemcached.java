package com.std.common.utils;

import java.net.InetSocketAddress;
import java.util.Map;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * memcache 操作工具类
 * @author liuwei3
 *
 */
public class StdCommonMemcached extends ConfigurableConstants {
	private static final Logger logger = LoggerFactory.getLogger(StdCommonMemcached.class);
    static {
    	logger.info("init std-common-memcached.properties");
        init("std-common-memcached.properties");
    }
    private static final String ADDRESS_MASTER = getValue("xmemcached.address.master");
    private static final String ADDRESS_MASTER_WEIGHT = getValue("xmemcached.address.master.weight");
    private static final String CONNECTION_POOL_SIZE = getValue("xmemcached.connection.pool.size");
    private static final String HEAL_SESSION_INTERVAL = getValue("xmemcached.heal.session.interval");
    private static final String SERIALIZING_TRANSCODER_COMPRESSION_THRESHOLD = getValue("xmemcached.serializing.transcoder.compression.threshold");
    private static final String FAILURE_MODE = getValue("xmemcached.failure.mode");
    private static final String NAME = getValue("xmemcached.name");
    private static final String CONNECTION_TIMEOUT = getValue("xmemcached.connection.timeout");
    private static final String OBJECT_EXPIRE_TIME = getValue("xmemcached.object.expire.time");
	private final XMemcachedClientBuilder builder;
	private MemcachedClient memcachedClient;
	/**
	 * 默认保存时间
	 */
	private static int defaultExp = Integer.valueOf(OBJECT_EXPIRE_TIME);
	/**
	 * 默认超时时间
	 */
	private static long defaultTimeout = Long.valueOf(CONNECTION_TIMEOUT);

	private StdCommonMemcached() {
		logger.info("init {} instance" ,this.getClass().getSimpleName());
		//server's weights 
		int [] weights = {Integer.valueOf(ADDRESS_MASTER_WEIGHT)};
		builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(ADDRESS_MASTER),weights);
		//nio connection pool size
		builder.setConnectionPoolSize(Integer.valueOf(CONNECTION_POOL_SIZE));
		//Use binary protocol,default is TextCommandFactory 
		builder.setCommandFactory(new BinaryCommandFactory());
		//Distributed strategy 
		builder.setSessionLocator(new KetamaMemcachedSessionLocator());
		//Serializing transcoder 
		SerializingTranscoder serializingTranscoder = new SerializingTranscoder();
		serializingTranscoder.setCompressionThreshold(Integer.valueOf(SERIALIZING_TRANSCODER_COMPRESSION_THRESHOLD));
		builder.setTranscoder(serializingTranscoder);
		//Failure mode 
		builder.setFailureMode(Boolean.valueOf(FAILURE_MODE));
		builder.setHealSessionInterval(Long.valueOf(HEAL_SESSION_INTERVAL));
		builder.setName(NAME);
		initMemcachedClient();
	}

	private void initMemcachedClient() {
		try {
			memcachedClient = builder.build();
			Map<InetSocketAddress, Map<String, String>> stats = memcachedClient
					.getStats();
			logger.info(">>>{} 初始化成功 {} ...<<<",this.getClass().getSimpleName(),stats);
		} catch (Exception e) {
			logger.info(">>>{} 初始化失败...\\n{}",this.getClass().getSimpleName(),ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	/**
	 * 获取单实例
	 * @author liuwei3
	 *
	 */
	private enum Singleton {
		INSTANCE;
		private StdCommonMemcached stdMemcached;
		private Singleton() {
			this.stdMemcached = new StdCommonMemcached();
		}
		public StdCommonMemcached getStdMemcached() {
			return stdMemcached;
		}
	}

	public boolean add(String key, Object value, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addExe(key, defaultExp, value, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean add(String key, int exp, Object value, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addExe(key, exp, value, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean add(String key, Object value, long timeout, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addExe(key, defaultExp, value, timeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean add(String key, int exp, Object value, long timeOut, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addExe(key, exp, value, timeOut);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean update(String key, Object value, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = updateExe(key, defaultExp, value, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean update(String key, int exp, Object value, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = updateExe(key, exp, value, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean update(String key, Object value, long timeout, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = updateExe(key, defaultExp, value, timeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean update(String key, int exp, Object value, long timeOut, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = updateExe(key, exp, value, timeOut);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}
	
	public boolean addOrUpdate(String key, Object value, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addOrUpdateExe(key, defaultExp, value, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean addOrUpdate(String key, int exp, Object value, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addOrUpdateExe(key, exp, value, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean addOrUpdate(String key, Object value, long timeout, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addOrUpdateExe(key, defaultExp, value, timeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean addOrUpdate(String key, int exp, Object value, long timeOut, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = addOrUpdateExe(key, exp, value, timeOut);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public Object get(String key, boolean isExThrowAllowed) throws Exception {
		Object obj = null;
		try {
			obj = getExe(key, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return obj;
	}

	public Object get(String key, long timeOut, boolean isExThrowAllowed) throws Exception {
		Object obj = null;
		try {
			obj = getExe(key, timeOut);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return obj;
	}

	public Object getRefresh(String key, boolean isExThrowAllowed) throws Exception {
		Object obj = null;
		try {
			obj = getAndRefreshExe(key, defaultExp, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return obj;
	}

	public Object getRefresh(String key, int exp, boolean isExThrowAllowed) throws Exception {
		Object obj = null;
		try {
			obj = getAndRefreshExe(key, exp, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return obj;
	}

	public Object getRefresh(String key, int exp, long timeOut, boolean isExThrowAllowed) throws Exception {
		Object obj = null;
		try {
			obj = getAndRefreshExe(key, exp, timeOut);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return obj;
	}

	public boolean delete(String key, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = deleteExe(key, defaultTimeout);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	public boolean delete(String key, long timeOut, boolean isExThrowAllowed) throws Exception {
		boolean flag = false;
		try {
			flag = deleteExe(key, timeOut);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			if (isExThrowAllowed) {
				throw e;
			}
		}
		return flag;
	}

	/**
	 * 执行添加，如key已经存在则更新
	 * @param key
	 * @param exp
	 * @param value
	 * @param timeOut
	 * @return
	 */
	private boolean addOrUpdateExe(String key, int exp, Object value, long timeOut) throws Exception {
		boolean flag = memcachedClient.set(key, exp, value, timeOut);
		return flag;
	}
	
	/**
	 * 执行增加
	 * 
	 * @param key
	 * @param exp
	 * @param value
	 * @param timeOut
	 * @return
	 */
	private boolean addExe(String key, int exp, Object value, long timeOut) throws Exception{
		boolean flag = memcachedClient.add(key, exp, value, timeOut);
		return flag;	
	}

	/**
	 * 刷新缓存
	 * 
	 * @param key
	 * @param exp
	 * @param timeOut
	 * @return
	 */
	private Object getAndRefreshExe(String key, int exp, long timeOut) throws Exception{
		Object obj = memcachedClient.getAndTouch(key, exp, timeOut);
		return obj;
	}

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @param timeOut
	 * @return
	 */
	private Object getExe(String key, long timeOut) throws Exception {
		Object obj = memcachedClient.get(key, timeOut);
		return obj;
	}

	/**
	 * 更新
	 * 
	 * @param key
	 * @param exp
	 * @param value
	 * @param timeOut
	 * @return
	 */
	private boolean updateExe(String key, int exp, Object value, long timeOut) throws Exception {
		boolean flag = memcachedClient.replace(key, exp, value, timeOut);
		return flag;
	}

	/**
	 * 删除
	 * 
	 * @param key
	 * @param timeOut
	 * @return
	 */
	private boolean deleteExe(String key, long timeOut) throws Exception {
		boolean flag = memcachedClient.delete(key, timeOut);
		return flag;
	}
	
	public static StdCommonMemcached getSingletonInstance() {
		return Singleton.INSTANCE.getStdMemcached();
	}
}
