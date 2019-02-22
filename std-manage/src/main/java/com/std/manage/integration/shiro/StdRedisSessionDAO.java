package com.std.manage.integration.shiro;

import java.io.Serializable;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;
import com.std.common.utils.StdCommonRedis;
import com.std.manage.consts.ApplicationConsts;

public class StdRedisSessionDAO extends EnterpriseCacheSessionDAO {
	private static final Logger logger = LoggerFactory.getLogger(StdRedisSessionDAO.class);
	private StdCommonRedis stdCommonRedis = StdCommonRedis.getSingletonInstance();
	private String defaultExp = "1800";//如没有配置 则单位秒 30分钟

	// 创建session，保存到数据库
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        stdCommonRedis.setEx(sessionId.toString().getBytes(), Integer.parseInt(getDefaultExp()), SerializationUtils.serialize(session));
        return sessionId;
    }

    // 获取session
    @Override
    protected Session doReadSession(Serializable sessionId) {
        // 先从缓存中获取session，如果没有再去数据库中获取
        Session session = super.doReadSession(sessionId); 
        if(session == null){
            byte[] bytes = stdCommonRedis.get(sessionId.toString().getBytes());
            if(bytes != null && bytes.length > 0){
                session = (Session) SerializationUtils.deserialize(bytes);    
            }
        }
        return session;
    }

    // 更新session的最后一次访问时间
    @Override
    protected void doUpdate(Session session) {
        super.doUpdate(session);
        stdCommonRedis.setEx(session.getId().toString().getBytes(), Integer.parseInt(getDefaultExp()), SerializationUtils.serialize(session));
    }

    // 删除session
    @Override
    protected void doDelete(Session session) {
        super.doDelete(session);
        stdCommonRedis.del(session.getId().toString());
    }

	public String getDefaultExp() {
		return defaultExp;
	}

	public void setDefaultExp(String defaultExp) {
		this.defaultExp = defaultExp;
	}

    
}
