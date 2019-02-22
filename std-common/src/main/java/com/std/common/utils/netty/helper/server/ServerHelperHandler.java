package com.std.common.utils.netty.helper.server;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.netty.NetworkTaskPool;
import com.std.common.utils.netty.SocketAddressConvertor;
import com.std.common.utils.netty.helper.HelperConstants;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 业务处理分发器
 * @author liuwei3
 *
 */
public class ServerHelperHandler extends SimpleChannelInboundHandler<String>{
	private final static Logger logger = LoggerFactory.getLogger(ServerHelperHandler.class);
	private Class<? extends ServerHelperMessageProcessor> clz;
	
	public ServerHelperHandler(Class<? extends ServerHelperMessageProcessor> clz) {
		this.clz = clz;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		Map<String, String> socketMap = SocketAddressConvertor.getAddress(channel.remoteAddress());
		logger.info("connect from : {}" , socketMap);
	}

	/**
	 * <p>接收服务器发来的消息</p>
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
		if(StringUtils.trim(HelperConstants.getHeartbeatMessage()).equals(StringUtils.trim(message))) {
			ctx.fireChannelRead(message);
		}else {
			Channel channel = ctx.channel();
			logger.info("receive message from : {}, message : {{}}" , SocketAddressConvertor.getAddress(channel.remoteAddress()),message);
			if(StringUtils.isNotBlank(message)) {
				// 处理消息
				try {
					NetworkTaskPool.POOL_SERVER_TASK.submit(clz.getConstructor(String.class,Channel.class).newInstance(message,channel));
				} catch (Exception e2) {
					logger.error(e2.getMessage(), e2.getCause());
				}
			}
		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		ctx.fireUserEventTriggered(evt);
	}

	/**
	 * channelRead处理完成刷缓冲区
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/**
	 * 记录异常日志
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("exceptionCaught:{}",ExceptionUtils.getFullStackTrace(cause));
	}

	/**
	 * <p>当客户端连接断开时业务处理</p>
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		Map<String, String> socketMap = SocketAddressConvertor.getAddress(channel.remoteAddress());
		logger.info("disconnect from : {}" , socketMap);
	}
	
}
