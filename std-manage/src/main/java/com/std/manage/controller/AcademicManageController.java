package com.std.manage.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.std.common.consts.CommonConsts.ProcessResult;

import freemarker.template.Template;

@Controller
@RequestMapping(value="/manage")
public class AcademicManageController extends BaseController {
	public final static Logger logger = LoggerFactory.getLogger(AcademicManageController.class);
    @Autowired
    @Qualifier("freemarkerConfig")
    private FreeMarkerConfigurer freemarkerConfig;
	@Value("${academic.make.page.dir}")
	private String pageDir;
	@Value("${academic.make.output.encoding}")
	private String outputEncode;
    
	
	/**
	 * 导航数据维护页面
	 * @return
	 */
	@RequestMapping(value="/academic",method=RequestMethod.GET)
	@RequiresPermissions({"manage:academic:page"})
	public String manager(HttpServletRequest request, HttpServletResponse response) {
		logger.info("导航数据维护页面");
		return "ftl/academic";
	}
	
	/**
	 * 根据freemarker模板结合数据库数据动态生成静态html页面
	 * @return
	 */
	@RequestMapping(value="/academic/page-make",method=RequestMethod.GET)
	@ResponseBody
	@RequiresPermissions({"manage:academic:page-make"})
	public Map<String, Object>  makeMainPage() {
		logger.info("根据template-academic.ftl模板结合数据库数据动态生成静态html页面");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		try {
			File htmlFile = new File(pageDir + "/academic.html");
			// 获得模板对象
			Template template = freemarkerConfig.getConfiguration().getTemplate("ftl/template-academic.ftl");
			//先得到文件的上级目录，并创建上级目录，在创建文件
			htmlFile.getParentFile().mkdir();
			htmlFile.createNewFile();
			//创建map数据集
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("title","A-"+RandomUtils.nextLong());
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), outputEncode));
			// 合并输出 创建页面文件
			template.process(map,out);
			retMap.put("result", ProcessResult.SUCCESS.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}
}
