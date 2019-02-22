package com.std.common.utils.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Ip 地址转换工具
 * @author yanhg
 *
 */
public class SocketAddressConvertor {

	/**
	 * 将IP:port格式的字符串转换为{@link SocketAddress}
	 * 
	 * @param address
	 * @return
	 * @throws ProtocolParamValidationException
	 */
	public static SocketAddress convert(String address) throws ProtocolParamValidationException {
		if (StringUtils.isBlank(address)) {
			throw new ProtocolParamValidationException("address must be formed as ip:port");
		}
		String[] split = StringUtils.split(address, ":");
		if (split.length != 2) {
			throw new ProtocolParamValidationException("address must be formed as ip:port");
		}
		return new InetSocketAddress(split[0], NumberUtils.toInt(split[1]));
	}
	/**
	 * 根据SocketAddress转换成map 包含ip、port
	 * @param socketAddress
	 * @return Map<String,String>
	 */
	public static Map<String,String> getAddress(SocketAddress socketAddress)
	{
		Map<String,String> addressMap=new HashMap<String,String>();
		if(socketAddress!=null)
		{
			String[] strs=socketAddress.toString().split("\\:");
			addressMap.put("ip", strs[0].substring(1));
			addressMap.put("port", strs[1]);
		}
		
		return addressMap;
				
				
		
	}
}
