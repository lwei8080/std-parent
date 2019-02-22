package com.std.common.utils.netty.helper.client;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoopGroup;

public class ClientHelperReconnect implements Runnable {
	private Logger logger = LoggerFactory.getLogger(ClientHelperReconnect.class);
	private AbstractClientHelper originClientHelper; // 起源对象
	private EventLoopGroup group;
	
	public ClientHelperReconnect(EventLoopGroup group, AbstractClientHelper originClientHelper) {
		this.group = group;
		this.originClientHelper = originClientHelper;
	}
	
	@Override
	public void run() {
		try {
			// 关闭调用者EventLoopGroup
			group.shutdownGracefully();
			long waitMillis = AbstractClientHelper.getWaitMillis();
			logger.info("等待{}毫秒", waitMillis);
			TimeUnit.MILLISECONDS.sleep(waitMillis);
			// 重建连接
			logger.info("开始重连...");
			originClientHelper.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
