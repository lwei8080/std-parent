package com.std.common.utils.netty.helper.client;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.netty.NetworkTaskPool;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;

/**
 * 为其他工程提供长连接服务
 * 抽象network客户端
 * @author liuwei3
 *
 */
public abstract class AbstractClientHelper {
	public final static AttributeKey<AbstractClientHelper> KEY_CLIENT_ORIGIN = AttributeKey.valueOf("ORIGIN");
	private final static Logger logger = LoggerFactory.getLogger(AbstractClientHelper.class);
	private SocketAddress address;
	private static final long waitMillis = 3000;
	private Channel channel;
	private List<Class<? extends ChannelHandler>> handlerClasses;
	
	public AbstractClientHelper() {
		
	}
	
	/**
	 * 建立连接
	 */
	public abstract void open();
	
	/**
	 * Client实例通过此方法关闭
	 */
	public abstract void close();
	
	/**
	 * 写数据
	 * @param msg
	 * @return
	 */
	public void channelWriteAndFlush(final String msg,boolean sync) {
		try {
			if(StringUtils.isNotEmpty(msg)) {
				// channel 为null 则延迟50毫秒重新获取任然获取不到则返回写失败
				Channel clientChannel = getChannel();
				if(null==clientChannel||!clientChannel.isActive()) {
					TimeUnit.MILLISECONDS.sleep(50l);
					clientChannel = getChannel();
				}
				if(null!=clientChannel&&clientChannel.isActive()) {
					ChannelFuture future = null;
					if(sync)
						future = clientChannel.writeAndFlush(msg).sync();
					else
						future = clientChannel.writeAndFlush(msg);
					future.addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							logger.info("Write [{}] Message-> {{}}",future.isSuccess(), msg);
						}
					});
				} else {
					logger.error("channel inactive, write fail -> {{}}", msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("channel write fail",e);
		}
	}
	
	/**
	 * 客户端通讯建立且channel处于active=true状态，返回true
	 * @return
	 */
	public boolean isConnected() {
		boolean isConnected = true;
		Channel clientChannel = getChannel();
		if(null==clientChannel || !clientChannel.isActive())
			isConnected = false;
		return isConnected;
	}
	
	/**
	 * 客户端connect通知监听
	 * @author liuwei3
	 *
	 */
	class ConnectNotifyListener implements ChannelFutureListener{
		private SocketAddress address;
		private EventLoopGroup group;
		private AbstractClientHelper originClientHelper;// 起源对象
		
		public ConnectNotifyListener(SocketAddress address,EventLoopGroup group,AbstractClientHelper originClientHelper) {
			this.address = address;
			this.group = group;
			this.originClientHelper = originClientHelper;
		}
		
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if(future.isSuccess()) {
				Channel c = future.channel();
				logger.info("连接[{}]成功.",c.remoteAddress());
				try {
					// 如当前client对象已经建立过连接，则断开之前连接，并释放之前eventloop 【同一对象（非重连机制）重复调用openNetworkClient的场景】
					Channel historyChannel = originClientHelper.getChannel();
					if(null!=historyChannel) {
						EventLoopGroup historyGroup = historyChannel.eventLoop().parent();
						historyChannel.attr(AbstractClientHelper.KEY_CLIENT_ORIGIN).set(null);
						if(!historyGroup.isShutdown()) {
							for(Class<? extends ChannelHandler> clz : originClientHelper.getHandlerClasses()) {
								channel.pipeline().remove(clz);
							}
							historyChannel.close();
							historyGroup.shutdownGracefully();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 绑定channel的所属networkclient
				originClientHelper.setChannel(c);
				c.attr(AbstractClientHelper.KEY_CLIENT_ORIGIN).set(originClientHelper);
			} else {
				logger.info("连接[{}]失败...",address);
				NetworkTaskPool.POOL_CLIENT_TASK.submit(new ClientHelperReconnect(group,originClientHelper));
			}
		}
		
	}
	
	public static long getWaitMillis() {
		return waitMillis;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public SocketAddress getAddress() {
		return address;
	}

	public void setAddress(SocketAddress address) {
		this.address = address;
	}

	public List<Class<? extends ChannelHandler>> getHandlerClasses() {
		return handlerClasses;
	}

	public void setHandlerClasses(List<Class<? extends ChannelHandler>> handlerClasses) {
		this.handlerClasses = handlerClasses;
	}

}
