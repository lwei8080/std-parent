package com.std.common.utils.netty.helper;

/**
 * 支持处理的消息类别
 * @author liuwei3
 *
 */
public enum HelperProcessorMessageType {
	TEST("TEST") // 测试类型
	;
	private String value;
	private HelperProcessorMessageType(String value) {
		this.value = value;
	}
	public String value() {
		return value;
	}
    public static boolean contains(String value) {
    	boolean contains = false;
        for (HelperProcessorMessageType mt : HelperProcessorMessageType.values()) {
            if (mt.value.equals(value)) {
                contains = true;
                break;
            }
        }
        return contains;
    }
}
