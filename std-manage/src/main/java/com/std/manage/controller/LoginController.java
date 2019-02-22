package com.std.manage.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.CookieGenerator;

import com.std.common.consts.CommonConsts;
import com.std.common.consts.CommonConsts.ProcessResult;
import com.std.common.utils.CookieUtil;
import com.std.common.utils.MD5;
import com.std.manage.consts.ApplicationConsts;
import com.std.manage.integration.shiro.StdShiroRealm;
import com.std.persist.model.SysManager;
/**
 * 登录
 * @author liuwei3
 *
 */
@Controller
public class LoginController extends BaseController {
	public final static Logger logger = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	@Qualifier("stdShiroRealm")
	private StdShiroRealm stdShiroRealm;
	
	/**
	 * 清理权限缓存
	 */
	private void clearAuthCache() {
		stdShiroRealm.getAuthenticationCache().clear();
		stdShiroRealm.getAuthorizationCache().clear();
	}
	
	/**
	 * 资源重定向
	 * @return
	 */
	@RequestMapping(value= {"", "/", "/index"})
	public String index() {
		return "redirect:/manage/index";
	}
	
	/**
	 * 没有权限页面
	 * @return
	 */
	@RequestMapping(value="/previlige/no",method=RequestMethod.GET)
	public String  noprevilige() {
		return "ftl/previlige";
	}
	
	/**
	 * 登录页面
	 * @return
	 */
	@RequestMapping(value="/manage/login",method=RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		try {
			Subject subject = SecurityUtils.getSubject();
			if(null!=subject&&subject.isAuthenticated()) {
				subject.logout();
				return "redirect:/manage/login";
			}
			
			clearAuthCache();
			CookieUtil.clearCookie(new CookieGenerator(), request, response, ApplicationConsts.amspid, ApplicationConsts.domainManager);
			CookieUtil.clearCookie(new CookieGenerator(), request, response, ApplicationConsts.amsid, ApplicationConsts.domainManager);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return "ftl/login";
	}
	
	/**
	 * 登录
	 * @return
	 */
	@RequestMapping(value="/manage/doLogin",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Map<String, Object> retMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
		SysManager currentManager = (SysManager)subject.getPrincipal();
		String username = request.getParameter("username");
		String realname = "-";
		boolean isCaptchaVerify = KaptchaController.verifyKaptcha(request, response, session);
		if(isCaptchaVerify) {
			String password = request.getParameter("password");
	        // 测试当前的用户是否已经被认证，即是否已经登陆
	        // 调用Subject的isAuthenticated
	        if (!subject.isAuthenticated() || !currentManager.getAccount().equals(username) || !currentManager.getPwd().equals(MD5.MD5Encode(password))) {
	        	
	        	if(subject.isAuthenticated()&&currentManager.getAccount().equals(username))
	        		SecurityUtils.getSubject().logout();
	        	
	        	if(subject.isAuthenticated()&&!currentManager.getAccount().equals(username)) {
	        		// 异常cookie登录
	        		retMap.put("result", ProcessResult.FAIL.getValue());
	    			CookieUtil.clearCookie(new CookieGenerator(), request, response, ApplicationConsts.amspid, ApplicationConsts.domainManager);
	    			CookieUtil.clearCookie(new CookieGenerator(), request, response, ApplicationConsts.amsid, ApplicationConsts.domainManager);
	        	} else {
		            // 把用户名和密码封装为UsernamePasswordToken 对象
		            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		            token.setRememberMe(false);
		            try {
		                // 执行登陆
		                subject.login(token);
		                retMap.put("result", ProcessResult.SUCCESS.getValue());
		                logger.info("{}登录成功.",username);
		            } catch (Exception ae) {
		            	retMap.put("result", ProcessResult.FAIL.getValue());
		            	if(ae instanceof LockedAccountException
		            			|| ae instanceof ExcessiveAttemptsException)
		            		retMap.put("message", ae.getMessage());
		            	logger.info("{}登录失败,Exception:{}",username,ae.getMessage());
		            }
	        	}
	        } else {
	        	//已经登录过
	        	retMap.put("result", ProcessResult.SUCCESS.getValue());
	        	logger.info("{}处于登录状态.",((SysManager) subject.getPrincipal()).getAccount());
	        }
	        if(ProcessResult.SUCCESS.getValue().equals((String)retMap.get("result"))) {
	        	SysManager manager = (SysManager)SecurityUtils.getSubject().getPrincipal();
	        	realname = StringUtils.isEmpty(manager.getName())?realname:manager.getName();
                // 向cookie中注册token二次令牌
                String amspidVal = UUID.randomUUID().toString().replaceAll("-", "");
                CookieUtil.addCookie(new CookieGenerator(), request, response, ApplicationConsts.amspid, amspidVal, 0 , ApplicationConsts.domainManager);
                SecurityUtils.getSubject().getSession().setAttribute(ApplicationConsts.amspid, amspidVal);
                
    	        SecurityUtils.getSubject().getSession().setAttribute("username", username);
    	        SecurityUtils.getSubject().getSession().setAttribute("realname", realname);
    	        //String basePath = request.getScheme() + "://" + request.getServerName() + ":" + String.valueOf(request.getServerPort()) + request.getContextPath();
    	        String basePath = request.getContextPath();
    	        SecurityUtils.getSubject().getSession().setAttribute("basePath", basePath);
	        }
		} else {
			if(null!=subject&&subject.isAuthenticated()&&currentManager.getAccount().equals(username)) {
				subject.logout();
				clearAuthCache();
			}
			retMap.put("result", ProcessResult.FAIL.getValue());
			retMap.put("message", CommonConsts.ReferenceMessage.CAPTCHA_ERROR.getMessage());
		}
		return retMap;
	}
	
	/**
	 * 登出
	 * @return
	 */
	@RequestMapping(value="/manage/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			Subject subject = SecurityUtils.getSubject();
			if(null!=subject&&subject.isAuthenticated())
				subject.logout();
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return "redirect:/manage/login";
	}

}
