package com.std.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.CookieGenerator;

public class CookieUtil {
	
	public static void addCookie(CookieGenerator generator,
			HttpServletRequest request, HttpServletResponse response,
			String name, String value, int maxAge, String domain) {
		if (request.getContextPath().equals("")) {
			generator.setCookiePath("/");
		} else {
			generator.setCookiePath(request.getContextPath());
		}
		generator.setCookieDomain(domain);
		if (maxAge != 0) {
			generator.setCookieMaxAge(maxAge);
		}
		generator.setCookieName(name);
		generator.addCookie(response, value);
	}

	public static String getCookieVal(HttpServletRequest request, String name) {
		Cookie cookie = getCookieForName(request, name);
		if (null != cookie) {
			return cookie.getValue();
		}
		return null;
	}

	public static void updateCookie(HttpServletResponse response,
			Cookie cookie, int maxAge, String domain) {
		if (null != cookie) {
			if (maxAge != 0) {
				cookie.setMaxAge(maxAge);
			}
			cookie.setPath("/");
			cookie.setDomain(domain);
			response.addCookie(cookie);
		}
	}

	public static void clearCookie(CookieGenerator generator,
			HttpServletRequest request, HttpServletResponse response,
			String name, String domain) {
		generator.setCookieName(name);
		if (request.getContextPath().equals("")) {
			generator.setCookiePath("/");
		} else {
			generator.setCookiePath(request.getContextPath());
		}
		generator.setCookieDomain(domain);
		generator.setCookieMaxAge(0);
		generator.addCookie(response, null);
	}

	private static Cookie getCookieForName(HttpServletRequest request,
			String name) {
		// 跨域请求时可以将值放在请求参数中传递
		String getParamterForCookie = request.getParameter(name);
		if (StringUtils.isNotEmpty(getParamterForCookie)) {
			return new Cookie(name, getParamterForCookie);
		}
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie coo : cookies) {
				if (coo.getName().equals(name)) {
					return coo;
				}
			}
		}
		return null;
	}
}
