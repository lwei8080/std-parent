package com.std.common.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 数据返回
 * @author liuwei3
 *
 */
public class WebUtil {
	private static final Logger logger = LoggerFactory.getLogger(WebUtil.class);
	//定义 FASTJSON null值属性输出且值为null、null集合输出[]、日期格式yyyy-MM-dd HH:mm:ss、null字符串输出空
	private static final SerializerFeature [] FASTJSON_FORMAT = {SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullListAsEmpty,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteNullStringAsEmpty};
	private static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
	private static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";
	private static final String CONTENT_TYPE_PLAIN = "text/plain;charset=utf-8";
	
	/**
	 * 响应head中自定义的key
	 * @author liuwei3
	 *
	 */
    public enum ResponseHeaders {
    	SESSION_STATUS("X-Session-Status");//访问状态标识
    	
		private String value;
		
		private ResponseHeaders(String value){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
    }
    
    /**
     * X-Session-Status可能的值
     * @author liuwei3
     *
     */
    public enum XSessionStatus{
    	REQUIRED_LOGIN("403"),//要求登录
    	REQUIRED_PERMISSION("401"),//要求授权
    	ERROR("500");//服务错误
    	
		private XSessionStatus(String value){
			this.value = value;
		}
		
		private String value;
		
		public String getValue() {
			return value;
		}
    }
	
	/**
	 * 判断请求是否ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		boolean isAjaxRequest = false;
		if (request.getHeader("x-requested-with") != null && request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
			isAjaxRequest = true;
		}
		return isAjaxRequest;
	}
	
	/**
	 * out
	 * @param response
	 * @param text
	 */
	public static void print(HttpServletResponse response, String text, String contentType) {
		try {
			logger.debug("print->{}",text);
			if(StringUtils.isNotEmpty(contentType)){
				response.setContentType(contentType);
			}
			PrintWriter out = response.getWriter();
			out.print(text);
			out.close();
			out = null;
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	/**
	 * out json
	 * @param response
	 * @param data
	 */
	public static void printJson(HttpServletResponse response, Map<String, Object> data){
		try {
			Map<String, Object> m = new HashMap<String, Object>();
			if(MapUtils.isNotEmpty(data)){
				m.putAll(data);
			}
			String text = JSONObject.toJSONString(m, FASTJSON_FORMAT);
			print(response, text, CONTENT_TYPE_JSON);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public static SerializerFeature[] getFastjsonFormat() {
		return FASTJSON_FORMAT;
	}

	public static String getContentTypeJson() {
		return CONTENT_TYPE_JSON;
	}

	public static String getContentTypeHtml() {
		return CONTENT_TYPE_HTML;
	}

	public static String getContentTypePlain() {
		return CONTENT_TYPE_PLAIN;
	}
	
}
