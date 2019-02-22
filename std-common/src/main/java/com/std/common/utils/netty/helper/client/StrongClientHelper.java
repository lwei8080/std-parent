package com.std.common.utils.netty.helper.client;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.netty.SocketAddressConvertor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 用于连接server的客户端实现 自动重连
 * @author liuwei3
 *
 */
public class StrongClientHelper extends AbstractClientHelper {
	private final static Logger logger = LoggerFactory.getLogger(StrongClientHelper.class);
	private Class<? extends ClientHelperMessageProcessor> clz;
	
	/**
	 * @param addressString
	 *   addressString 格式 例 127.0.0.1:2018
	 * @param handler
	 */
	public StrongClientHelper(String addressString,Class<? extends ClientHelperMessageProcessor> clz) {
		super();
		setAddress(SocketAddressConvertor.convert(addressString));
		List<Class<? extends ChannelHandler>> handlerClasses = new ArrayList<Class<? extends ChannelHandler>>();
		handlerClasses.add(StrongClientHelperHandler.class);
		setHandlerClasses(handlerClasses);
		this.clz = clz;
	}
	
	/**
	 * 建立连接，重复调用只维持最后一次成功建立的连接
	 */
	public void open() {
		try {
			EventLoopGroup group = new NioEventLoopGroup();
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .handler(new LoggingHandler(LogLevel.INFO))
			 .option(ChannelOption.TCP_NODELAY, true)
			 .option(ChannelOption.SO_KEEPALIVE, true)
			 .handler(new StrongClientHelperChannelInitializer(clz))
			 .remoteAddress(getAddress());
			// 连接服务端
			ChannelFuture future = b.connect(getAddress());
			future.addListener(new ConnectNotifyListener(getAddress(), group, this));
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * NetworkClient实例通过此方法关闭，如需接收消息，判断消息回复后再调用；否则直接关闭将收不到回复消息。
	 */
	public void close() {
		try {
			// 1.注销自定义handler
			Channel channel = getChannel();
			for(Class<? extends ChannelHandler> clz : getHandlerClasses()) {
				channel.pipeline().remove(clz);
			}
			channel.attr(AbstractClientHelper.KEY_CLIENT_ORIGIN).set(null);
			// 2.断开连接
			channel.close();
			// 3.清理对象
			EventLoopGroup group = channel.eventLoop().parent();
			group.shutdownGracefully();
			setChannel(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
