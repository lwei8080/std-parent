package com.std.common.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.util.SafeEncoder;

/**
 * redis 操作工具类
 * 		opt key\string\set\list\hash\sortset
 * @author liuwei3
 *
 */
public class StdCommonRedis extends ConfigurableConstants {
	private static final Logger logger = LoggerFactory.getLogger(StdCommonRedis.class);
	
    static {
    	logger.info("init std-common-redis.properties");
        init("std-common-redis.properties");
    }
    
    /**
     * 控制一个pool可分配多少个jedis实例
     * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
     */
    private static final String REDIS_POOL_MAX_ACTIVE = getValue("redis.pool.maxActive");
    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例
     */
    private static final String REDIS_POOL_MAX_IDLE = getValue("redis.pool.maxIdle");
    /**
     * 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
     */
    private static final String REDIS_POOL_MAX_WAIT = getValue("redis.pool.maxWait");
    /**
     * 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
     */
    private static final String REDIS_POOL_TEST_ON_BORROW = getValue("redis.pool.testOnBorrow");
    /**
     * 在return给pool时，是否提前进行validate操作
     */
    private static final String REDIS_POOL_TEST_ON_RETURN = getValue("redis.pool.testOnReturn");
    /**
     * redis服务主机域名或ip
     */
    private static final String REDIS_HOST = getValue("redis.host");
    /**
     * redis服务端口号
     */
    private static final String REDIS_PORT = getValue("redis.port");
    /**
     * 访问redis服务密码
     */
    private static final String REDIS_PASSWORD = getValue("redis.password");
    
    private static final String CONNECTION_TIMEOUT = getValue("redis.connection.timeout");
    private static final String OBJECT_EXPIRE_TIME = getValue("redis.object.expire.time");
    
	/**
	 * 默认保存时间
	 */
	private static int defaultExp = Integer.valueOf(OBJECT_EXPIRE_TIME);
	/**
	 * 默认超时时间
	 */
	private static int defaultTimeout = Integer.valueOf(CONNECTION_TIMEOUT);
	
	/**
	 * 返回成功OK标识
	 */
	private static String RET_OK = "OK";
	
	/**
	 * 返回成功1标识
	 */
	private static Long RET_SUCCESS_1 = 1l;
    
	public static String getRET_OK() {
		return RET_OK;
	}

	public static Long getRET_SUCCESS_1() {
		return RET_SUCCESS_1;
	}

	public static int getDefaultTimeout() {
		return defaultTimeout;
	}

	/**
	 * 单实例 JedisPool
	 * @author liuwei3
	 *
	 */
	private enum SingletonJedisPool {
		INSTANCE;
		private JedisPool jedisPool;
		private SingletonJedisPool () {
			initJedisPool();
		}
		private void initJedisPool(){
			if(jedisPool==null){
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxTotal(Integer.valueOf(REDIS_POOL_MAX_ACTIVE).intValue());
				config.setMaxIdle(Integer.valueOf(REDIS_POOL_MAX_IDLE).intValue());
				config.setMaxWaitMillis(Long.valueOf(REDIS_POOL_MAX_WAIT).longValue());
				config.setTestOnBorrow(Boolean.valueOf(REDIS_POOL_TEST_ON_BORROW));
				config.setTestOnReturn(Boolean.valueOf(REDIS_POOL_TEST_ON_RETURN));
				// 非集群redis
				if(StringUtils.isNotEmpty(REDIS_PASSWORD)){
					jedisPool = new JedisPool(config, REDIS_HOST,Integer.valueOf(REDIS_PORT), defaultTimeout,REDIS_PASSWORD);
				}else{
					jedisPool = new JedisPool(config, REDIS_HOST,Integer.valueOf(REDIS_PORT), defaultTimeout);
				}
//				// 集群redis
//				List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
//				JedisShardInfo jedisShardInfo = new JedisShardInfo(REDIS_HOST,Integer.valueOf(REDIS_PORT), defaultTimeout, "master");
//				if(StringUtils.isNotEmpty(REDIS_PASSWORD))
//					jedisShardInfo.setPassword(REDIS_PASSWORD);
//				shards.add(jedisShardInfo);
//				ShardedJedisPool shardedJedisPool = new ShardedJedisPool(config, shards);
			}
		}
		public JedisPool getJedisPool() {
			return jedisPool;
		}
	}
    
    private StdCommonRedis() {
    	
    }
    
	/**
	 * 获取单实例
	 * @author liuwei3
	 *
	 */
	private enum Singleton {
		INSTANCE;
		private StdCommonRedis stdCommonRedis;
		private Singleton() {
			this.stdCommonRedis = new StdCommonRedis();
		}
		public StdCommonRedis getStdCommonRedis() {
			return stdCommonRedis;
		}
	}
	
	public static StdCommonRedis getSingletonInstance() {
		return Singleton.INSTANCE.getStdCommonRedis();
	}

    /**
     * 从jedis连接池中获取获取jedis对象 
     * @return
     */
    public Jedis getJedis() {
        return SingletonJedisPool.INSTANCE.getJedisPool().getResource();
    }
 
    /**
     * 回收jedis
     * @param jedis
     */
    public void releaseJedis(Jedis jedis) {
    	if(null!=jedis)
    		jedis.close();
    }

    //-----------------------------------------------Key--------------------------------------------------/
    /**
     * 清空所有key
     */
    public String flushAll() {
    	String result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.flushAll();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 更改key,仅当新key不存在时才执行
     * @param String oldkey
     * @param String newkey
     * @return 状态码
     * */
    public Long renamenx(String oldkey, String newkey) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.renamenx(oldkey, newkey);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 更改key 
     * @param String oldkey
     * @param String newkey
     * @return 状态码
     * */
    public String rename(byte[] oldkey, byte[] newkey) {
    	String result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.rename(oldkey, newkey);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 更改key
     * @param String oldkey
     * @param String newkey
     * @return 状态码
     * */
    public String rename(String oldkey, String newkey) {
        return rename(SafeEncoder.encode(oldkey), SafeEncoder.encode(newkey));
    }
     
    /**
     * 设置key的过期时间，以秒为单位
     * @param String  key
     * @param 时间 seconds,秒为单位
     * @return 影响的记录数
     * */
    public Long expire(String key, int seconds) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.expire(key, seconds);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 设置默认过期时间
     * @param key
     */
    public Long expire(String key) {
        return expire(key, defaultExp);
    }
    
    /**
     * 设置key的过期时间,它是距历元（即格林威治标准时间 1970 年 1 月 1 日的 00:00:00，格里高利历）的偏移量。
     * @param String  key
     * @param 时间 timestamp,秒为单位
     * @return 影响的记录数
     * */
    public Long expireAt(String key, long timestamp) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.expireAt(key, timestamp);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 查询key的过期时间
     * @param String key
     * @return 以秒为单位的时间表示
     * */
    public Long ttl(String key) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.ttl(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 取消对key过期时间的设置 
     * @param key
     * @return 影响的记录数
     * */
    public Long persist(String key) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.persist(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 删除keys对应的记录,可以是多个key 
     * @param String ... keys
     * @return 删除的记录数
     * */
    public Long del(String... keys) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.del(keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 删除keys对应的记录,可以是多个key 
     * @param String ... keys
     * @return 删除的记录数
     * */
    public Long del(byte[]... keys) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.del(keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 判断key是否存在
     *
     * @param String
     *            key
     * @return boolean
     * */
    public boolean exists(String key) {
    	boolean result = false;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.exists(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 对List,Set,SortSet进行排序,如果集合数据较大应避免使用这个方法 
     * @param String  key
     * @return List<String> 集合的全部记录
     * **/
    public List<String> sort(String key) {
    	List<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sort(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 对List,Set,SortSet进行排序或limit 
     * @param String key
     * @param SortingParams parame 定义排序类型或limit的起止位置.
     * @return List<String> 全部或部分记录
     * **/
    public List<String> sort(String key, SortingParams parame) {
    	List<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sort(key, parame);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 返回指定key存储的类型 
     * @param String key
     * @return String string|list|set|zset|hash
     * **/
    public String type(String key) {
    	String result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.type(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 查找所有匹配给定的模式的键 
     * @param String key的表达式,*表示多个，？表示一个
     * */
    public Set<String> keys(String pattern) {
    	Set<String> result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.keys(pattern);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    //-----------------------------------------------Key--------------------------------------------------/
    
    //-----------------------------------------------String--------------------------------------------------/
    
    /**
     * 根据key获取记录
     * @param String  key
     * @return 值
     * */
    public String get(String key) {
    	String result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.get(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 根据key获取记录
     * @param byte[] key
     * @return 值
     * */
    public byte[] get(byte[] key) {
    	byte[] result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.get(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加有过期时间的记录
     *
     * @param String  key
     * @param int seconds 过期时间，以秒为单位
     * @param String value
     * @return String 操作状态
     * */
    public String setEx(String key, int seconds, String value) {
    	String result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.setex(key, seconds, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加有过期时间的记录
     *
     * @param String key
     * @param int seconds 过期时间，以秒为单位
     * @param String  value
     * @return String 操作状态
     * */
    public String setEx(byte[] key, int seconds, byte[] value) {
    	String result = null;
    	Jedis jedis = null;
        try {
			jedis = getJedis(); 
			result = jedis.setex(key, seconds, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加一条记录，仅当给定的key不存在时才插入
     * @param String key
     * @param String value
     * @return long 状态码，1插入成功且key不存在，0未插入，key存在
     * */
    public Long setnx(String key, String value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.setnx(key, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 添加一条记录，仅当给定的key不存在时才插入, 并设置过期时间 单位 秒
     * 
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String setnxEx(String key, int seconds, byte[] value) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			/**
			 * EX seconds − 设置指定的到期时间(以秒为单位)。
		     * PX milliseconds - 设置指定的到期时间(以毫秒为单位)。
		     * NX - 仅在键不存在时设置键。
		     * XX - 只有在键已存在时才设置。
			 */
			result = jedis.set(SafeEncoder.encode(key), value, SafeEncoder.encode("NX"), SafeEncoder.encode("EX"), seconds);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 删除key对应的记录,仅当key对应的值与value一致时成功
     * @param key
     * @param value
     * @return 1 ： 成功 ； 0 ： 未成功
     */
    public Long del(String key, byte[] value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			// lua脚本 通过eval执行原子操作
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			jedis = getJedis();
			result = (Long)jedis.eval(SafeEncoder.encode(script), Collections.singletonList(SafeEncoder.encode(key)), Collections.singletonList(value));
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加记录,如果记录已存在将覆盖原有的value
     * @param String key
     * @param String value
     * @return 状态码
     * */
    public String set(String key, String value) {
        return set(SafeEncoder.encode(key), SafeEncoder.encode(value));
    }

    /**
     * 添加记录,如果记录已存在将覆盖原有的value
     * @param String  key
     * @param String value
     * @return 状态码
     * */
    public String set(String key, byte[] value) {
        return set(SafeEncoder.encode(key), value);
    }

    /**
     * 添加记录,如果记录已存在将覆盖原有的value
     * @param byte[] key
     * @param byte[] value
     * @return 状态码
     * */
    public String set(byte[] key, byte[] value) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.set(key, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 从指定位置开始插入数据，插入的数据会覆盖指定位置以后的数据<br/>
     * 例:String str1="123456789";<br/>
     * 对str1操作后setRange(key,4,0000)，str1="123400009";
     * @param String  key
     * @param long offset
     * @param String  value
     * @return long value的长度
     * */
    public Long setRange(String key, long offset, String value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.setrange(key, offset, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 在指定的key中追加value
     * @param String  key
     * @param String value
     * @return long 追加后value的长度
     * **/
    public Long append(String key, String value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.append(key, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 将key对应的value减去指定的值，只有value可以转为数字时该方法才可用
     *
     * @param String
     *            key
     * @param long number 要减去的值
     * @return long 减指定值后的值
     * */
    public Long decrBy(String key, long number) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.decrBy(key, number);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * <b>可以作为获取唯一id的方法</b><br/>
     * 将key对应的value加上指定的值，只有value可以转为数字时该方法才可用
     * @param String  key
     * @param long number 要减去的值
     * @return long 相加后的值
     * */
    public Long incrBy(String key, long number) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.incrBy(key, number);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 对指定key对应的value进行截取
     * @param String   key
     * @param long startOffset 开始位置(包含)
     * @param long endOffset 结束位置(包含)
     * @return String 截取的值
     * */
    public String getrange(String key, long startOffset, long endOffset) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.getrange(key, startOffset, endOffset);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取并设置指定key对应的value<br/>
     * 如果key存在返回之前的value,否则返回null
     * @param String  key
     * @param String value
     * @return String 原始value或null
     * */
    public String getSet(String key, String value) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.getSet(key, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 批量获取记录,如果指定的key不存在返回List的对应位置将是null
     * @param String keys
     * @return List<String> 值得集合
     * */
    public List<String> mget(String... keys) {
    	List<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.mget(keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 批量存储记录
     * @param String keysvalues 例:keysvalues="key1","value1","key2","value2";
     * @return String 状态码
     * */
    public String mset(String... keysvalues) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.mset(keysvalues);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取key对应的值的长度
     * @param String key
     * @return value值得长度
     * */
    public long strlen(String key) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.strlen(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    //-----------------------------------------------String--------------------------------------------------/
    
    //-----------------------------------------------Set--------------------------------------------------/
    /**
     * 向Set添加一条记录，如果member已存在返回0,否则返回1 
     * @param String key
     * @param String  member
     * @return 操作码,0或1
     * */
    public Long sadd(String key, String member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sadd(key, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    public Long sadd(byte[] key, byte[] member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sadd(key, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取给定key中元素个数 
     * @param String key
     * @return 元素个数
     * */
    public Long scard(String key) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.scard(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 返回从第一组和所有的给定集合之间的差异的成员 
     * @param String ... keys
     * @return 差异的成员集合
     * */
    public Set<String> sdiff(String... keys) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sdiff(keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 这个命令等于sdiff,但返回的不是结果集,而是将结果集存储在新的集合中，如果目标已存在，则覆盖。 
     * @param String newkey 新结果集的key
     * @param String ... keys 比较的集合
     * @return 新集合中的记录数
     * **/
    public Long sdiffstore(String newkey, String... keys) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sdiffstore(newkey, keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 返回给定集合交集的成员,如果其中一个集合为不存在或为空，则返回空Set 
     * @param String ... keys
     * @return 交集成员的集合
     * **/
    public Set<String> sinter(String... keys) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sinter(keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 这个命令等于sinter,但返回的不是结果集,而是将结果集存储在新的集合中，如果目标已存在，则覆盖。 
     * @param String newkey 新结果集的key
     * @param String ... keys 比较的集合
     * @return 新集合中的记录数
     * **/
    public Long sinterstore(String newkey, String... keys) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sinterstore(newkey, keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 确定一个给定的值是否存在 
     * @param String key
     * @param String  member 要判断的值
     * @return 存在返回1，不存在返回0
     * **/
    public boolean sismember(String key, String member) {
    	boolean result = false;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sismember(key, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 返回集合中的所有成员
     *
     * @param String
     *            key
     * @return 成员集合
     * */
    public Set<String> smembers(String key) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.smembers(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    public Set<byte[]> smembers(byte[] key) {
    	Set<byte[]> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.smembers(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 将成员从源集合移出放入目标集合 <br/>
     * 如果源集合不存在或不包哈指定成员，不进行任何操作，返回0<br/>
     * 否则该成员从源集合上删除，并添加到目标集合，如果目标集合中成员已存在，则只在源集合进行删除 
     * @param String srckey 源集合
     * @param String dstkey 目标集合
     * @param String member 源集合中的成员
     * @return 状态码，1成功，0失败
     * */
    public Long smove(String srckey, String dstkey, String member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.smove(srckey, dstkey, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 从集合中随机删除成员 
     * @param String key
     * @return 被删除的成员
     * */
    public String spop(String key) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.spop(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 从集合中删除指定成员 
     * @param String key
     * @param String member 要删除的成员
     * @return 状态码，成功返回1，成员不存在返回0
     * */
    public Long srem(String key, String member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.srem(key, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 合并多个集合并返回合并后的结果，合并后的结果集合并不保存<br/> 
     * @param String ... keys
     * @return 合并后的结果集合
     * @see sunionstore
     * */
    public Set<String> sunion(String... keys) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sunion(keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 合并多个集合并将合并后的结果集保存在指定的新集合中，如果新集合已经存在则覆盖 
     * @param String  newkey 新集合的key
     * @param String ... keys 要合并的集合
     * **/
    public Long sunionstore(String newkey, String... keys) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.sunionstore(newkey, keys);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    //-----------------------------------------------Set--------------------------------------------------/
    
    //-----------------------------------------------List--------------------------------------------------/
    /**
     * List长度
     * @param String key
     * @return 长度
     * */
    public Long llen(String key) {
        return llen(SafeEncoder.encode(key));
    }

    /**
     * List长度
     * @param byte[] key
     * @return 长度
     * */
    public Long llen(byte[] key) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.llen(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 覆盖操作,将覆盖List中指定位置的值
     * @param byte[] key
     * @param int index 位置
     * @param byte[] value 值
     * @return 状态码
     * */
    public String lset(byte[] key, int index, byte[] value) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lset(key, index, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 覆盖操作,将覆盖List中指定位置的值
     * @param key
     * @param int index 位置
     * @param String  value 值
     * @return 状态码
     * */
    public String lset(String key, int index, String value) {
        return lset(SafeEncoder.encode(key), index, SafeEncoder.encode(value));
    }

    /**
     * 在value的相对位置插入记录
     * @param key
     * @param LIST_POSITION   前面插入或后面插入
     * @param String pivot 相对位置的内容
     * @param String value 插入的内容
     * @return 记录总数
     * */
    public Long linsert(String key, LIST_POSITION where, String pivot, String value) {
        return linsert(SafeEncoder.encode(key), where, SafeEncoder.encode(pivot), SafeEncoder.encode(value));
    }

    /**
     * 在指定位置插入记录
     * @param String key
     * @param LIST_POSITION 前面插入或后面插入
     * @param byte[] pivot 相对位置的内容
     * @param byte[] value 插入的内容
     * @return 记录总数
     * */
    public Long linsert(byte[] key, LIST_POSITION where, byte[] pivot, byte[] value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.linsert(key, where, pivot, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取List中指定位置的值
     * @param String  key
     * @param int index 位置
     * @return 值
     * **/
    public String lindex(String key, int index) {
        return SafeEncoder.encode(lindex(SafeEncoder.encode(key), index));
    }

    /**
     * 获取List中指定位置的值
     * @param byte[] key
     * @param int index 位置
     * @return 值
     * **/
    public byte[] lindex(byte[] key, int index) {
    	byte[] result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lindex(key, index);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 将List中的第一条记录移出List
     * @param String key
     * @return 移出的记录
     * */
    public String lpop(String key) {
    	return SafeEncoder.encode(lpop(SafeEncoder.encode(key)));
    }

    /**
     * 将List中的第一条记录移出List
     * @param byte[] key
     * @return 移出的记录
     * */
    public byte[] lpop(byte[] key) {
    	byte[] result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lpop(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 将List中最后第一条记录移出List 
     * @param string key
     * @return 移出的记录
     * */
    public String rpop(String key) {
    	return SafeEncoder.encode(rpop(SafeEncoder.encode(key)));
    }
    
    /**
     * 将List中最后第一条记录移出List 
     * @param byte[] key
     * @return 移出的记录
     * */
    public byte[] rpop(byte[] key) {
    	byte[] result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.rpop(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 向List头部加记录
     * @param String key
     * @param String value
     * @return 记录总数
     * */
    public Long lpush(String key, String value) {
        return lpush(SafeEncoder.encode(key), SafeEncoder.encode(value));
    }
    
    /**
     * 向List头部加记录
     * @param byte[] key
     * @param byte[] value
     * @return 记录总数
     * */
    public Long lpush(byte[] key, byte[] value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lpush(key, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 向List尾部追加记录
     * @param String  key
     * @param String  value
     * @return 记录总数
     * */
    public Long rpush(String key, String value) {
    	return rpush(SafeEncoder.encode(key), SafeEncoder.encode(value));
    }

    /**
     * 向List尾部追加记录
     * @param String key
     * @param String value
     * @return 记录总数
     * */
    public Long rpush(byte[] key, byte[] value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.rpush(key, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }



    /**
     * 获取指定范围的记录，可以做为分页使用
     * @param String key
     * @param long start
     * @param long end
     * @return List
     * */
    public List<String> lrange(String key, long start, long end) {
    	List<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lrange(key, start, end);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取指定范围的记录，可以做为分页使用
     * @param byte[] key
     * @param int start
     * @param int end 如果为负数，则尾部开始计算
     * @return List
     * */
    public List<byte[]> lrange(byte[] key, int start, int end) {
    	List<byte[]> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lrange(key, start, end);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 删除List中c条记录，被删除的记录值为value
     * @param byte[] key
     * @param int c 要删除的数量，如果为负数则从List的尾部检查并删除符合的记录
     * @param byte[] value 要匹配的值
     * @return 删除后的List中的记录数
     * */
    public Long lrem(byte[] key, int c, byte[] value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.lrem(key, c, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 删除List中c条记录，被删除的记录值为value
     * @param String key
     * @param int c 要删除的数量，如果为负数则从List的尾部检查并删除符合的记录
     * @param String value 要匹配的值
     * @return 删除后的List中的记录数
     * */
    public long lrem(String key, int c, String value) {
        return lrem(SafeEncoder.encode(key), c, SafeEncoder.encode(value));
    }

    /**
     * 删除，只保留start与end之间的记录
     * @param byte[] key
     * @param int start 记录的开始位置(0表示第一条记录)
     * @param int end 记录的结束位置（如果为-1则表示最后一个，-2，-3以此类推）
     * @return 执行状态码
     * */
    public String ltrim(byte[] key, int start, int end) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.ltrim(key, start, end);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 算是删除吧，只保留start与end之间的记录
     * @param String key
     * @param int start 记录的开始位置(0表示第一条记录)
     * @param int end 记录的结束位置（如果为-1则表示最后一个，-2，-3以此类推）
     * @return 执行状态码
     * */
    public String ltrim(String key, int start, int end) {
        return ltrim(SafeEncoder.encode(key), start, end);
    }
    
    //-----------------------------------------------List--------------------------------------------------/
    
    //-----------------------------------------------Hash--------------------------------------------------/
    /**
     * 从hash中删除指定的存储
     * @param String key
     * @param String  fieid 存储的名字
     * @return 状态码，1成功，0失败
     * */
    public Long hdel(String key, String fieid) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hdel(key, fieid);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * hash中指定的存储是否存在
     * @param String key
     * @param String  fieid 存储的名字
     * @return 1存在，0不存在
     * */
    public boolean hexists(String key, String fieid) {
    	boolean result = false;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hexists(key, fieid);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 返回hash中指定存储位置的值 
     * @param String key
     * @param String fieid 存储的名字
     * @return 存储对应的值
     * */
    public String hget(String key, String fieid) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hget(key, fieid);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    public byte[] hget(byte[] key, byte[] fieid) {
    	byte[] result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hget(key, fieid);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 以Map的形式返回hash中的存储和值
     * @param String    key
     * @return Map<Strinig,String>
     * */
    public Map<String, String> hgetAll(String key) {
    	Map<String, String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hgetAll(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    public Map<String, Object> hscan(String key,String match,String cursor,Integer count) {
    	Map<String, Object> result = new HashMap<String, Object>();
        Jedis jedis = null;
		try {
			jedis = getJedis();
			ScanParams sp = new ScanParams();
			if(null!=count)
				sp.count(count);
			else
				sp.count(Integer.MAX_VALUE);
			sp.match("*"+match+"*");
			ScanResult<Entry<String, String>> scanResult = jedis.hscan(key, cursor, sp);
			result.put("data", scanResult.getResult());
			result.put("cursor", scanResult.getStringCursor());
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }
    
    /**
     * 以Map的形式返回hash中的存储和值
     * @param String    key
     * @return Map<Strinig,String>
     * */
    public Map<byte[], byte[]> hgetAll(byte[] key) {
    	Map<byte[], byte[]> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hgetAll(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加一个对应关系
     * @param String  key
     * @param String fieid
     * @param String value
     * @return 状态码 1成功，0失败，fieid已存在将更新，也返回0
     * **/
    public Long hset(String key, String fieid, String value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hset(key, fieid, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    public Long hset(String key, String fieid, byte[] value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hset(key.getBytes(), fieid.getBytes(), value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加对应关系，只有在fieid不存在时才执行
     * @param String key
     * @param String fieid
     * @param String value
     * @return 状态码 1成功，0失败fieid已存
     * **/
    public Long hsetnx(String key, String fieid, String value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hsetnx(key, fieid, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取hash中value的集合
     * @param String key
     * @return List<String>
     * */
    public List<String> hvals(String key) {
    	List<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hvals(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 在指定的存储位置加上指定的数字，存储位置的值必须可转为数字类型 
     * @param String  key
     * @param String  fieid 存储位置
     * @param String  Long value 要增加的值,可以是负数
     * @return 增加指定数字后，存储位置的值
     * */
    public Long hincrBy(String key, String fieid, Long value) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hincrBy(key, fieid, value);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 返回指定hash中的所有存储名字,类似Map中的keySet方法 
     * @param String key
     * @return Set<String> 存储名称的集合
     * */
    public Set<String> hkeys(String key) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hkeys(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取hash中存储的个数，类似Map中size方法 
     * @param String key
     * @return Long 存储的个数
     * */
    public Long hlen(String key) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hlen(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 根据多个fieids，获取对应的value，返回List,如果指定的fieids不存在,List对应位置为null 
     * @param String key
     * @param String ... fieids 存储位置
     * @return List<String>
     * */
    public List<String> hmget(String key, String... fieids) {
    	List<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hmget(key, fieids);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    public List<byte[]> hmget(byte[] key, byte[]... fieids) {
    	List<byte[]> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hmget(key, fieids);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加对应关系，如果对应关系已存在，则覆盖 
     * @param String key
     * @param Map <String,String> 对应关系
     * @return 状态，成功返回OK
     * */
    public String hmset(String key, Map<String, String> map) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hmset(key, map);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 添加对应关系，如果对应关系已存在，则覆盖 
     * @param String key
     * @param Map <String,String> 对应关系
     * @return 状态，成功返回OK
     * */
    public String hmset(byte[] key, Map<byte[], byte[]> map) {
    	String result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.hmset(key, map);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    //-----------------------------------------------Hash--------------------------------------------------/
    
    //-----------------------------------------------Sortset--------------------------------------------------/
    /**
     * 向集合中增加一条记录,如果这个值已存在，这个值对应的权重将被置为新的权重 
     * @param String key
     * @param double score 权重
     * @param String member 要加入的值，
     * @return 状态码 1成功，0已存在member的值
     * */
    public Long zadd(String key, double score, String member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zadd(key, score, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    public Long zadd(String key, Map<String,Double> scoreMembers) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zadd(key, scoreMembers);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取集合中元素的数量 
     * @param String  key
     * @return 如果返回0则集合不存在
     * */
    public Long zcard(String key) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zcard(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 获取指定权重区间内集合的数量 
     * @param String key
     * @param double min 最小排序位置
     * @param double max 最大排序位置
     * */
    public Long zcount(String key, double min, double max) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zcount(key, min, max);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
        return result;
    }

    /**
     * 权重增加给定值，如果给定的member已存在 
     * @param String  key
     * @param double score 要增的权重
     * @param String member 要插入的值
     * @return 增后的权重
     * */
    public Double zincrby(String key, double score, String member) {
    	Double result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zincrby(key, score, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 返回指定位置的集合元素,0为第一个元素，-1为最后一个元素 
     * @param String key
     * @param int start 开始位置(包含)
     * @param int end 结束位置(包含)
     * @return Set<String>
     * */
    public Set<String> zrange(String key, int start, int end) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zrange(key, start, end);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 返回指定权重区间的元素集合 
     * @param String key
     * @param double min 上限权重
     * @param double max 下限权重
     * @return Set<String>
     * */
    public Set<String> zrangeByScore(String key, double min, double max) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zrangeByScore(key, min, max);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 获取指定值在集合中的位置，集合排序从低到高 
     * @see zrevrank
     * @param String key
     * @param String member
     * @return Long 位置
     * */
    public Long zrank(String key, String member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zrank(key, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 获取指定值在集合中的位置，集合排序从高到低 
     * @see zrank
     * @param String key
     * @param String member
     * @return Long 位置
     * */
    public Long zrevrank(String key, String member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zrevrank(key, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 从集合中删除成员
     *
     * @param String key
     * @param String member
     * @return 返回1成功
     * */
    public Long zrem(String key, String member) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zrem(key, member);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 删除给定位置区间的元素 
     * @param String key
     * @param int start 开始区间，从0开始(包含)
     * @param int end 结束区间,-1为最后一个元素(包含)
     * @return 删除的数量
     * */
    public Long zremrangeByRank(String key, int start, int end) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zremrangeByRank(key, start, end);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 删除给定权重区间的元素 
     * @param String key
     * @param double min 下限权重(包含)
     * @param double max 上限权重(包含)
     * @return 删除的数量
     * */
    public Long zremrangeByScore(String key, double min, double max) {
    	Long result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zremrangeByScore(key, min, max);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 获取给定区间的元素，原始按照权重由高到低排序 
     * @param String  key
     * @param int start
     * @param int end
     * @return Set<String>
     * */
    public Set<String> zrevrange(String key, int start, int end) {
    	Set<String> result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return result;
    }

    /**
     * 获取给定值在集合中的权重
     * @param String  key
     * @param memeber
     * @return double 权重
     * */
    public Double zscore(String key, String memebr) {
    	Double result = null;
        Jedis jedis = null;
		try {
			jedis = getJedis();
			result = jedis.zscore(key, memebr);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		} finally {
			releaseJedis(jedis);
		}
		return null==result?0:result;
    }
    //-----------------------------------------------Sortset--------------------------------------------------/
}
