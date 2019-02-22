package com.std.common.utils.netty.helper.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
/**
 * 为其他工程提供长连接服务
 * 服务端
 * @author liuwei3
 *
 */
public class ServerHelper{
	private final static Logger logger = LoggerFactory.getLogger(ServerHelper.class);
	private Class<? extends ServerHelperMessageProcessor> clz;
	
	public ServerHelper(Class<? extends ServerHelperMessageProcessor> clz) {
		this.clz = clz;
	}

	public void start(String port) throws IOException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			InetSocketAddress address = new InetSocketAddress(Integer.parseInt(port));
			
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					 .channel(NioServerSocketChannel.class)
					 .handler(new LoggingHandler(LogLevel.INFO))
					 .localAddress(address)
					 .option(ChannelOption.SO_BACKLOG, 1024)
					 .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					 .childOption(ChannelOption.TCP_NODELAY, true)
					 .childOption(ChannelOption.SO_KEEPALIVE, true)
					 .childOption(ChannelOption.SO_REUSEADDR, true)
					 .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
					 .childOption(ChannelOption.SO_RCVBUF, 65536)
					 .childHandler(new ServerHelperChannelInitializer(clz));

			// 服务器绑定端口监听
			ChannelFuture f = bootstrap.bind().sync();
			
			logger.info("Server start listen at {}",address);
			
			// 服务器关闭监听
			f.channel().closeFuture().sync();
			
		} catch (Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			logger.info("Server work stoped.");
		}
	}
}
