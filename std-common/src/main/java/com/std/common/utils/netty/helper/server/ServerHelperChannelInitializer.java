package com.std.common.utils.netty.helper.server;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.netty.helper.HelperConstants;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerHelperChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final static Logger logger = LoggerFactory.getLogger(ServerHelperChannelInitializer.class);
	private Class<? extends ServerHelperMessageProcessor> clz;
	public ServerHelperChannelInitializer(Class<? extends ServerHelperMessageProcessor> clz) {
		super();
		this.clz = clz;
	}

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
		// 心跳
		pipeline.addLast("IdleStateHandler",new IdleStateHandler(HelperConstants.IdleTime.SERVER_READ.getValue(), 0, 0, TimeUnit.MILLISECONDS));
		
		pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(65536, Delimiters.lineDelimiter()));
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());
		pipeline.addLast(ServerHelperHandler.class.getSimpleName(), new ServerHelperHandler(clz));
		
		// 心跳逻辑Handler
		pipeline.addLast(HeartbeatServerHandler.class.getSimpleName(), new HeartbeatServerHandler());
	}

}
