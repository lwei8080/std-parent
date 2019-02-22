package com.std.common.utils.netty.helper;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.std.common.utils.WebUtil;

/**
 * 消息字符串对应的实体结构
 * @author liuwei3
 *
 */
public class HelperProcessorMessage implements Serializable {
	private static final long serialVersionUID = -9110769506354187622L;

	// 消息类别
	private String type;
	
	// 消息数据
	private Map<String, Object> data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "HelperProcessorMessage [type=" + type + ", data=" + data + "]";
	}
	
	public static String objectToJsonString(HelperProcessorMessage helperProcessorMessage) {
		String jsonString = JSONObject.toJSONString(helperProcessorMessage, WebUtil.getFastjsonFormat());
		return jsonString;
	}
	
	public static HelperProcessorMessage jsonStringToObject(String jsonString) {
		HelperProcessorMessage helperProcessorMessage = JSONObject.parseObject(jsonString, HelperProcessorMessage.class);
		return helperProcessorMessage;
	}
}
