package com.std.common.utils.netty.helper.server;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.handler.codec.Delimiters;

/**
 * 处理器
 * @author liuwei3
 *
 */
public class ServerHelperMessageProcessor implements Runnable {
	private Logger logger = LoggerFactory.getLogger(ServerHelperMessageProcessor.class);
	private String message;
	private Channel channel;

	public ServerHelperMessageProcessor(String message, Channel channel) {
		this.message = message;
		this.channel = channel;
	}

	@Override
	public void run() {
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public static String buildLineDelimiterWriteMessage(String message) {
		StringBuffer sb = new StringBuffer("");
		if(StringUtils.isNotBlank(message)) {
			sb.append(message.replaceAll("\n", "")).append("\r\n");
		}else {
			sb.append("\r\n");
		}
		return sb.toString();
	}
}
