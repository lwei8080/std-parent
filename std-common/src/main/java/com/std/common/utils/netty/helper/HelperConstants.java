package com.std.common.utils.netty.helper;

public class HelperConstants {
	
	private static String heartbeatMessage = "{T=1}\r\n";
	
    public enum IdleTime {
    	SERVER_READ(40000),//服务端 读
		CLIENT_WRITE(30000);//客户端 写
    	
		private long value;
		
		private IdleTime(long value){
			this.value = value;
		}
		
		public long getValue() {
			return value;
		}
    }

	public static String getHeartbeatMessage() {
		return heartbeatMessage;
	}
    
    
}
