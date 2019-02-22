package com.std.common.utils.netty.helper.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.netty.helper.HelperConstants;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {
	private final static Logger logger = LoggerFactory.getLogger(HeartbeatClientHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                logger.info("HeartBeat[{}]发送心跳包",ctx.channel().localAddress());
                ctx.channel().writeAndFlush(HelperConstants.getHeartbeatMessage());
            }
        }else{
        	super.userEventTriggered(ctx, evt);
        }
//        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("HeartBeat[{}], Server Resp->{}",ctx.channel().localAddress(),msg);
        ReferenceCountUtil.release(msg);
//        ctx.fireChannelRead(msg);
    }

}
