package com.std.common.utils.netty.helper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.netty.helper.HelperConstants;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
	private final static Logger log = LoggerFactory.getLogger(HeartbeatServerHandler.class);
	private static int MAX_TIMEOUT_READ_COUNT = 3;

	private ThreadLocal<Integer> timeoutReadCount = new ThreadLocal<Integer>(){
		@Override
		protected Integer initialValue() {
			return 0;
		}
		
	};

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				timeoutReadCount.set(timeoutReadCount.get().intValue()+1);
				String channelKey = ctx.channel().remoteAddress().toString();
				log.info("HeartBeat[{}]-->[{}]次[{}]毫秒服务端没有收到心跳包",channelKey,timeoutReadCount.get().intValue(),HelperConstants.IdleTime.SERVER_READ.getValue());
				if (timeoutReadCount.get().intValue() >= MAX_TIMEOUT_READ_COUNT) {
					log.info("HeartBeat[{}]-->关闭,Reason：连续[{}]次没有收到心跳包",channelKey,timeoutReadCount.get().intValue());
					ctx.channel().close();
				}
			}
		} else{
			super.userEventTriggered(ctx, evt);
		}
//		ctx.fireUserEventTriggered(evt);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object objMsg) throws Exception {
		Channel incoming = ctx.channel();
		String channelKey = ctx.channel().remoteAddress().toString();
		String msg = (String)objMsg;
		log.info("HeartBeat:[{}];msg:{}",channelKey,msg);
		timeoutReadCount.set(0);
		incoming.writeAndFlush(HelperConstants.getHeartbeatMessage());
//		ctx.fireChannelRead(msg);
	}

}
