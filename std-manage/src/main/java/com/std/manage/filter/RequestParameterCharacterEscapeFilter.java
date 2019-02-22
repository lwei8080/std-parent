package com.std.manage.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.util.HtmlUtils;

/**
 * 字符转义
 * @author liuwei3
 *
 */
@Order(2)
@WebFilter(filterName = "requestParameterCharacterEscapeFilter", urlPatterns = {"/manage/*"})
public class RequestParameterCharacterEscapeFilter implements Filter {
	public final static Logger logger = LoggerFactory.getLogger(RequestParameterCharacterEscapeFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		logger.debug("============进入请求参数字符转义流程=============");
		logger.debug("============URL[{}]=============",((HttpServletRequest) request).getRequestURI());
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(((HttpServletRequest) request).getServletContext());  
        //判断 request 是否有文件上传,即多部分请求  
        if(multipartResolver.isMultipart((HttpServletRequest) request)){  
            // do something
        }else {
    		request = new HttpRequestAdapter((HttpServletRequest) request);
        }
        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 对请求重包装，特殊字符过滤
	 * @author liuwei3
	 */
	private static class HttpRequestAdapter extends HttpServletRequestWrapper {

		public HttpRequestAdapter(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getParameter(String name) {
			if(StringUtils.isEmpty(name)||StringUtils.isEmpty(super.getParameter(name)))
				return "";
			return StringEscapeUtils.escapeSql(HtmlUtils.htmlEscape(super.getParameter(name)));
		}

		@Override
		public String[] getParameterValues(String name) {
			String[] parameterValues = null;
			parameterValues = super.getParameterValues(name);
			if (null != parameterValues) {
				for (int i = 0; i < parameterValues.length; i++) {
					parameterValues[i] = StringEscapeUtils.escapeSql(HtmlUtils.htmlEscape(parameterValues[i]));
				}
			}
			return parameterValues;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			Map<String, String[]> parameterMap = super.getParameterMap();
			Map<String, String[]> parameterMapWrapper = new ParameterMap<String,String[]>();
			// 遍历
			for (Iterator<Map.Entry<String, String[]>> iter = parameterMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, String[]> element = iter.next();
				// key值
				String key = element.getKey();
				// value,数组形式
				String[] value = (String[]) element.getValue();
				String[] valueWrapper = new String [value.length];
				for (int i = 0; i < value.length; i++) {
					if(StringUtils.isNotEmpty(value[i]))
						valueWrapper[i] = StringEscapeUtils.escapeSql(HtmlUtils.htmlEscape(value[i]));
					else
						valueWrapper[i] = value[i];
				}
				parameterMapWrapper.put(key, valueWrapper);
			}
			((ParameterMap<String,String[]>) parameterMapWrapper).setLocked(true);
			return parameterMapWrapper;
		}
	}
	public static void main(String[] args) {
		System.out.println(StringEscapeUtils.escapeSql(HtmlUtils.htmlEscape("/manage/permission/addOrUpdate	manage:permission:add-update")));
		System.out.println(String.valueOf(Long.MAX_VALUE).length());
	}
}
