package com.std.common.utils.netty.helper.client;

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

public class StrongClientHelperChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final static Logger logger = LoggerFactory.getLogger(StrongClientHelperChannelInitializer.class);
	private Class<? extends ClientHelperMessageProcessor> clz;

	public StrongClientHelperChannelInitializer(Class<? extends ClientHelperMessageProcessor> clz) {
		super();
		this.clz = clz;
	}

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {
		ChannelPipeline pipeline = sc.pipeline();
		// 心跳
		pipeline.addLast("IdleStateHandler",new IdleStateHandler(0, HelperConstants.IdleTime.CLIENT_WRITE.getValue(), 0, TimeUnit.MILLISECONDS));
		
		pipeline.addLast("frameDecoder", new DelimiterBasedFrameDecoder(65536, Delimiters.lineDelimiter()));
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());
		// 客户端业务处理handler
		pipeline.addLast(StrongClientHelperHandler.class.getSimpleName(), new StrongClientHelperHandler(clz));
		
		// 心跳逻辑Handler
		pipeline.addLast(HeartbeatClientHandler.class.getSimpleName(),new HeartbeatClientHandler());
	}

}
