package com.std.manage.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.std.common.utils.WebUtil;

@Component("exceptionResolver")
public class GlobalExceptionResolver implements HandlerExceptionResolver {
	public final static Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		logger.info("Exception resolve:");
        if(WebUtil.isAjaxRequest(request)) {
        	String sessionStatus = WebUtil.XSessionStatus.ERROR.getValue();
        	if (ex instanceof AuthorizationException || ex instanceof AuthenticationException) {
        		sessionStatus = WebUtil.XSessionStatus.REQUIRED_PERMISSION.getValue();
        	}
        	response.setHeader(WebUtil.ResponseHeaders.SESSION_STATUS.getValue(), sessionStatus);
        	WebUtil.print(response, sessionStatus, WebUtil.getContentTypeHtml());
        	return new ModelAndView("");
        }else {
    		if (ex instanceof AuthorizationException || ex instanceof AuthenticationException) {
    			ModelAndView mv = new ModelAndView("/previlige/no");
    			return mv;
    		}
    		return new ModelAndView("redirect:/error");
        }
	}

	
	
}
