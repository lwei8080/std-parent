package com.std.manage.integration.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.CookieUtil;
import com.std.common.utils.WebUtil;
import com.std.manage.consts.ApplicationConsts;
/**
 * 
 * @author liuwei3
 *
 */
public class StdPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {
	private static final Logger logger = LoggerFactory.getLogger(StdPermissionsAuthorizationFilter.class);
	
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		logger.info("Denied uri->{}",((HttpServletRequest)request).getRequestURI());
		Subject subject = getSubject(request, response);
        if(WebUtil.isAjaxRequest((HttpServletRequest)request)) {
        	String sessionStatus = WebUtil.XSessionStatus.REQUIRED_LOGIN.getValue();
        	if (subject.getPrincipal() != null) {
        		sessionStatus = WebUtil.XSessionStatus.REQUIRED_PERMISSION.getValue();
        	}
        	((HttpServletResponse)response).setHeader(WebUtil.ResponseHeaders.SESSION_STATUS.getValue(), sessionStatus);
        	WebUtil.print((HttpServletResponse)response, sessionStatus, WebUtil.getContentTypeHtml());
        }else {
            // If the subject isn't identified, redirect to login URL
            if (subject.getPrincipal() == null) {
                saveRequestAndRedirectToLogin(request, response);
            } else {
                // If subject is known but not authorized, redirect to the unauthorized URL if there is one
                // If no unauthorized URL is specified, just return an unauthorized HTTP status code
                String unauthorizedUrl = getUnauthorizedUrl();
                String unloginUrl = getLoginUrl();
                String amspidAttr = (String)request.getAttribute(ApplicationConsts.amspid);
                //SHIRO-142 - ensure that redirect _or_ error code occurs - both cannot happen due to response commit:
                if (StringUtils.hasText(unauthorizedUrl)) {
                	if(StringUtils.hasLength(amspidAttr))
                		WebUtils.issueRedirect(request, response, unloginUrl);
                	else
                		WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } else {
                    WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
        return false;
	}

	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws IOException {
		logger.debug("#####################################url->{}",((HttpServletRequest) request).getRequestURI());
		boolean isAccessAllowed = super.isAccessAllowed(request, response, mappedValue);
		request.removeAttribute(ApplicationConsts.amspid);
		try {
			Subject subject = SecurityUtils.getSubject();
			if(null!=subject&&subject.isAuthenticated()) {
				String amspidVal = (String)subject.getSession().getAttribute(ApplicationConsts.amspid);
				String hisAmspidVal = CookieUtil.getCookieVal((HttpServletRequest)request, ApplicationConsts.amspid);
				if(!StringUtils.hasLength(amspidVal)||!StringUtils.hasLength(hisAmspidVal)||!amspidVal.equals(hisAmspidVal)) {
					isAccessAllowed = false;
					request.setAttribute(ApplicationConsts.amspid, ApplicationConsts.amspid);
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return isAccessAllowed;
	}

}
