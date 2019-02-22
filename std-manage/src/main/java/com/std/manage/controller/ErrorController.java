package com.std.manage.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * 错误页面
 * @author liuwei3
 *
 */
@Controller
public class ErrorController extends BaseController{
	public final static Logger logger = LoggerFactory.getLogger(ErrorController.class);

	public String getErrorPath() {
		return "error";
	}

	/**
	 * 异常或错误页面
	 * @return
	 */
	@RequestMapping(value= {"/error"})
	public String  errorPage(HttpServletRequest request, HttpServletResponse response) {
		return getErrorPath();
	}
}
