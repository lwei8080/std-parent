package com.std.common.utils.netty.helper.client;

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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 重连 client 业务处理器
 * @author liuwei3
 *
 */
public class StrongClientHelperHandler extends SimpleChannelInboundHandler<String> {
	private final static Logger logger = LoggerFactory.getLogger(StrongClientHelperHandler.class);
	private Class<? extends ClientHelperMessageProcessor> clz;
	
	public StrongClientHelperHandler(Class<? extends ClientHelperMessageProcessor> clz) {
		this.clz = clz;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		Map<String, String> socketMap = SocketAddressConvertor.getAddress(channel.remoteAddress());
		logger.info("connect from : {}" , socketMap);
	}
	
	/**
	 * 客户端连接服务器，返回的消息
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
		if(StringUtils.trim(HelperConstants.getHeartbeatMessage()).equals(StringUtils.trim(message))) {
			ctx.fireChannelRead(message);
		}else {
			Channel channel = ctx.channel();
			logger.info("receive message from : {}, message : {{}}", SocketAddressConvertor.getAddress(channel.remoteAddress()), message);
			// 业务处理
			if(StringUtils.isNotBlank(message)) {
				// 处理消息
				try {
					NetworkTaskPool.POOL_CLIENT_TASK.submit(clz.getConstructor(String.class,Channel.class).newInstance(message,channel));
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("exception:{}", ExceptionUtils.getFullStackTrace(cause));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		EventLoopGroup group = channel.eventLoop().parent();
		logger.info("连接[{}]断开...", channel.remoteAddress());
		// 重连
		NetworkTaskPool.POOL_CLIENT_TASK.submit(new ClientHelperReconnect(group,channel.attr(AbstractClientHelper.KEY_CLIENT_ORIGIN).get()));
	}
}
