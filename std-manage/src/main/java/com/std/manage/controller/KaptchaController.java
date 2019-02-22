package com.std.manage.controller;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.CookieGenerator;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.std.common.consts.CommonConsts.ProcessResult;
import com.std.common.utils.CookieUtil;
import com.std.common.utils.StdCommonMemcached;
import com.std.manage.consts.ApplicationConsts;

/**
 * kaptcha 验证码
 * @author liuwei3
 *
 */
@Controller
public class KaptchaController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(KaptchaController.class);
	@Autowired
	private Producer captchaProducer;
	
	/**
	 * 验证码
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping(value="/captcha")
	public void getKaptchaImage(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
        try {
			response.setDateHeader("Expires", 0);  
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");  
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");  
			response.setHeader("Pragma", "no-cache");  
			response.setContentType("image/jpeg");  
			
			String hisKidVal = CookieUtil.getCookieVal(request, ApplicationConsts.kaptchaManagerSessionKey);
			if(StringUtils.isNotEmpty(hisKidVal))
				StdCommonMemcached.getSingletonInstance().delete(ApplicationConsts.kaptchaManagerSessionKey+hisKidVal,false);
			 
			String capText = captchaProducer.createText();  
			//session 存放
			String kidVal = UUID.randomUUID().toString().replaceAll("-", "");
			//memcache 60 秒内验证有效
			StdCommonMemcached.getSingletonInstance().addOrUpdate(ApplicationConsts.kaptchaManagerSessionKey+kidVal, 60, capText, false);
			CookieUtil.addCookie(new CookieGenerator(), request, response, ApplicationConsts.kaptchaManagerSessionKey, kidVal, 0 , ApplicationConsts.domainManager);
			logger.debug("new captcha: " + capText );
			 
			BufferedImage bi = captchaProducer.createImage(capText);  
			ServletOutputStream out = response.getOutputStream();  
			ImageIO.write(bi, "jpg", out);  
			try {  
			    out.flush();  
			} finally {  
			    out.close();  
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} 
	}
	
	public static boolean verifyKaptcha(HttpServletRequest request,HttpServletResponse response,HttpSession session){
		boolean result = false;
		try {
			String inputText = request.getParameter("inputCaptcha");
			if(StringUtils.isNotEmpty(inputText)){
				String kidVal = CookieUtil.getCookieVal(request, ApplicationConsts.kaptchaManagerSessionKey);
				String capText = (String)StdCommonMemcached.getSingletonInstance().get(ApplicationConsts.kaptchaManagerSessionKey+kidVal,false);
				logger.debug("session captcha val : "+capText+";input captcha val : "+inputText);
				if(null!=capText&&capText.equalsIgnoreCase(inputText)){
					result = true;
					CookieUtil.clearCookie(new CookieGenerator(), request, response, ApplicationConsts.kaptchaManagerSessionKey, ApplicationConsts.domainManager);
				}
				if(null!=capText) {
					// 服务端使失效
					if(StringUtils.isNotEmpty(kidVal))
						StdCommonMemcached.getSingletonInstance().delete(ApplicationConsts.kaptchaManagerSessionKey+kidVal,false);
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	
}
