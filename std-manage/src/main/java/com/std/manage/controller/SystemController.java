package com.std.manage.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.std.biz.ISysService;
import com.std.common.consts.CommonConsts.LengthLimit;
import com.std.common.consts.CommonConsts.PermissionType;
import com.std.common.consts.CommonConsts.ProcessResult;
import com.std.common.consts.CommonConsts.ReferenceMessage;
import com.std.common.consts.CommonConsts.ValidState;
import com.std.common.utils.MD5;
import com.std.common.utils.RegexUtil;
import com.std.manage.consts.ApplicationConsts;
import com.std.manage.integration.shiro.StdRedisSessionDAO;
import com.std.manage.integration.shiro.StdShiroRealm;
import com.std.persist.model.SysGroup;
import com.std.persist.model.SysManager;
import com.std.persist.model.SysManagerGroup;
import com.std.persist.model.SysManagerRole;
import com.std.persist.model.SysPermission;
import com.std.persist.model.SysRole;
import com.std.persist.model.SysRolePermission;
import com.std.persist.model.wrap.SysManagerGroupWrap;
import com.std.persist.model.wrap.SysManagerRoleWrap;
import com.std.persist.model.wrap.SysPermissionWrap;
import com.std.persist.model.wrap.SysRolePermissionWrap;
import com.std.persist.mysql.plugins.PageInfo;

@Controller
@RequestMapping(value="/manage")
public class SystemController extends BaseController {
	public final static Logger logger = LoggerFactory.getLogger(SystemController.class);
    @Autowired
    private ISysService sysService;
	@Autowired
	@Qualifier("stdShiroRealm")
	private StdShiroRealm stdShiroRealm;
	@Autowired
	@Qualifier("sessionManager")
	private DefaultWebSessionManager sessionManager;
    
	/**
	 * 登录后默认页
	 * @return
	 */
	@RequestMapping(value="/index",method=RequestMethod.GET)
	@RequiresPermissions({"manage:index"})
	public String  index(HttpServletRequest request, HttpServletResponse response) {
		logger.info("timeout::::::::::::{}",SecurityUtils.getSubject().getSession().getTimeout());
		return "ftl/index";
	}
	
	/**
	 * 管理后台账号管理页面
	 * @return
	 */
	@RequestMapping(value="/manager",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager:page"})
	public String manager(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入后台账号管理");
		return "ftl/manager";
	}
	
	/**
	 * 所有账号查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/manager/all",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager:all"})
	@ResponseBody
	public Map<String, Object> allManagers(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("所有账号查询");
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<SysManager> data = sysService.getAllSysManager();
		retMap.put("data", data);
		return retMap;
	}
	
	/**
	 * 账号分页查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/manager/pageQuery",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager:page-query"})
	@ResponseBody
	public Map<String, Object> managerPageQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("后台账号分页查询"+getDefaultPageSize());
		Map<String, Object> retMap = new HashMap<String, Object>();
		String pageNo = request.getParameter("page_no");
		String account = request.getParameter("account");
		String mobile = request.getParameter("mobile");
		String state = request.getParameter("state");
		String name = request.getParameter("name");
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if(!RegexUtil.posInteger(pageNo))
			pageNo = String.valueOf(getDefaultPageNo());
		if(StringUtils.isNotEmpty(name))
			queryParams.put("name", name);
		if(StringUtils.isNotEmpty(account))
			queryParams.put("account", account);
		if(StringUtils.isNotEmpty(mobile))
			queryParams.put("mobile", mobile);
		if(StringUtils.isNotEmpty(state))
			queryParams.put("state", state);
		queryParams.put("orderByClause", "n_id desc");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(Integer.parseInt(pageNo));
		pageInfo.setPageSize(getDefaultPageSize());
		List<SysManager> data = sysService.pageQuerySysManager(queryParams, pageInfo);
		retMap.put("queryParams", queryParams);
		retMap.put("pageInfo", pageInfo);
		retMap.put("pageData", data);
		return retMap;
	}
	
	/**
	 * 账号新增、修改
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/manager/addOrUpdate",method=RequestMethod.POST)
	@RequiresPermissions({"manage:manager:add-update"})
	@ResponseBody
	public Map<String, Object> managerAddOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("后台账号新增或修改");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		retMap.put("message", ReferenceMessage.SAVE_FAIL.getMessage());
		String id = request.getParameter("id");
		String account = request.getParameter("account");
		String pwd = request.getParameter("pwd");
		String mobile = request.getParameter("mobile");
		String state = request.getParameter("state");
		String name = request.getParameter("name");
		// 参数校验
		if(StringUtils.isBlank(account)||StringUtils.length(account)<LengthLimit.ACCOUNT.getMinLength()||StringUtils.length(account)>LengthLimit.ACCOUNT.getMaxLength())
			retMap.put("message", ReferenceMessage.ACCOUNT_VERIFY_ERROR.getMessage());
		else if((StringUtils.isBlank(id)&&StringUtils.isBlank(pwd))||(StringUtils.isNotBlank(pwd)&&(StringUtils.length(pwd)<LengthLimit.PWD.getMinLength()||StringUtils.length(pwd)>LengthLimit.PWD.getMaxLength())))
			retMap.put("message", ReferenceMessage.PWD_VERIFY_ERROR.getMessage());
		else if(!RegexUtil.mobileMacth(mobile))
			retMap.put("message", ReferenceMessage.MOBILE_VERIFY_ERROR.getMessage());
		else if(!ValidState.isExistValue(state))
			retMap.put("message", ReferenceMessage.STATE_VERIFY_ERROR.getMessage());
		else if(StringUtils.length(name)>LengthLimit.NAME.getMaxLength())
			retMap.put("message", ReferenceMessage.NAME_VERIFY_ERROR.getMessage());
		else {
			// 检测账号名是否存在
			SysManager checkManager = sysService.findSysManagerByAccount(account);
			if(null!=checkManager&&(!String.valueOf(checkManager.getId()).equals(id)||StringUtils.isBlank(id)))
				retMap.put("message", ReferenceMessage.ACCOUNT_EXISTS.getMessage());
			else {
				SysManager manager = new SysManager();
				BeanUtils.populate(manager, request.getParameterMap());
				if(StringUtils.isBlank(id)) {
					// 新增
					manager.setId(null);
					manager.setPwd(MD5.MD5Encode(pwd));
					manager.setCreateDate(sysService.getSysDate());
					manager.setUpdateDate(null);
					int affectedCount = sysService.addOrUpdateSysManager(manager);
					if(affectedCount > 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}else {
					// 修改
					manager.setCreateDate(null);
					if(StringUtils.isNotBlank(pwd))
						manager.setPwd(MD5.MD5Encode(pwd));
					else
						manager.setPwd(null);
					manager.setUpdateDate(sysService.getSysDate());
					int affectedCount = sysService.addOrUpdateSysManager(manager);
					if(affectedCount >= 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						if(affectedCount == 0)
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS_BUT_NO_CHANGE.getMessage());
						else
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}
			}
		}
		return retMap;
	}
	
	/**
	 * 角色管理页面
	 * @return
	 */
	@RequestMapping(value="/role",method=RequestMethod.GET)
	@RequiresPermissions({"manage:role:page"})
	public String role(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入角色管理");
		return "ftl/role";
	}
	
	/**
	 * 账号拥有角色查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/role/byAccount",method=RequestMethod.GET)
	@RequiresPermissions({"manage:role:by-account"})
	@ResponseBody
	public Map<String, Object> allRolesByAccount(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("账号拥有角色查询");
		String account = request.getParameter("account");
		Map<String, Object> retMap = new HashMap<String, Object>();
		Set<SysRole> data = sysService.getAllRolesByAccount(account);
		retMap.put("data", data);
		return retMap;
	}
	
	/**
	 * 角色分页查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/role/pageQuery",method=RequestMethod.GET)
	@RequiresPermissions({"manage:role:page-query"})
	@ResponseBody
	public Map<String, Object> rolePageQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("角色分页查询"+getDefaultPageSize());
		Map<String, Object> retMap = new HashMap<String, Object>();
		String pageNo = request.getParameter("page_no");
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if(!RegexUtil.posInteger(pageNo))
			pageNo = String.valueOf(getDefaultPageNo());
		if(StringUtils.isNotEmpty(name))
			queryParams.put("name", name);
		if(StringUtils.isNotEmpty(description))
			queryParams.put("description", description);
		queryParams.put("orderByClause", "n_id desc");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(Integer.parseInt(pageNo));
		pageInfo.setPageSize(getDefaultPageSize());
		List<SysRole> data = sysService.pageQuerySysRole(queryParams, pageInfo);
		retMap.put("queryParams", queryParams);
		retMap.put("pageInfo", pageInfo);
		retMap.put("pageData", data);
		return retMap;
	}
	
	/**
	 * 角色新增、修改[禁止]
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/role/addOrUpdate",method=RequestMethod.POST)
	@RequiresPermissions({"manage:role:add-update"})
	@ResponseBody
	public Map<String, Object> roleAddOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("角色新增或修改");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		retMap.put("message", ReferenceMessage.SAVE_FAIL.getMessage());
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		
		// 参数校验
		if(StringUtils.isNotBlank(id))
			retMap.put("message", ReferenceMessage.UPDATE_NOT_ALLOWED.getMessage());
		else if(StringUtils.isBlank(name)||StringUtils.length(name)<LengthLimit.ROLE_NAME.getMinLength()||StringUtils.length(name)>LengthLimit.ROLE_NAME.getMaxLength())
			retMap.put("message", ReferenceMessage.ROLE_NAME_VERIFY_ERROR.getMessage());
		else if(StringUtils.length(description)<LengthLimit.ROLE_DESCRIPTION.getMinLength()||StringUtils.length(description)>LengthLimit.ROLE_DESCRIPTION.getMaxLength())
			retMap.put("message", ReferenceMessage.ROLE_DESCRIPTION_VERIFY_ERROR.getMessage());
		else {
			// 检测角色名是否存在
			SysRole checkRole = sysService.findSysRoleByName(name);
			if(null!=checkRole&&(!String.valueOf(checkRole.getId()).equals(id)||StringUtils.isBlank(id)))
				retMap.put("message", ReferenceMessage.ROLE_EXISTS.getMessage());
			else {
				SysRole role = new SysRole();
				BeanUtils.populate(role, request.getParameterMap());
				if(StringUtils.isBlank(id)) {
					// 新增
					role.setId(null);
					role.setCreateDate(sysService.getSysDate());
					int affectedCount = sysService.addOrUpdateSysRole(role);
					if(affectedCount > 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}else {
					// 修改
					int affectedCount = sysService.addOrUpdateSysRole(role);
					if(affectedCount >= 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						if(affectedCount == 0)
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS_BUT_NO_CHANGE.getMessage());
						else
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}
			}
		}
		return retMap;
	}
	
	/**
	 * 权限管理页面
	 * @return
	 */
	@RequestMapping(value="/permission",method=RequestMethod.GET)
	@RequiresPermissions({"manage:permission:page"})
	public String permission(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入权限管理");
		return "ftl/permission";
	}
	
	/**
	 * 权限分页查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/permission/pageQuery",method=RequestMethod.GET)
	@RequiresPermissions({"manage:permission:page-query"})
	@ResponseBody
	public Map<String, Object> permissionPageQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("权限分页查询"+getDefaultPageSize());
		Map<String, Object> retMap = new HashMap<String, Object>();
		String pageNo = request.getParameter("page_no");
		String url = request.getParameter("url");
		String permissions = request.getParameter("permissions");
		String type = request.getParameter("type");
		String state = request.getParameter("state");
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if(!RegexUtil.posInteger(pageNo))
			pageNo = String.valueOf(getDefaultPageNo());
		if(StringUtils.isNotEmpty(url))
			queryParams.put("url", url);
		if(StringUtils.isNotEmpty(permissions))
			queryParams.put("permissions", permissions);
		if(StringUtils.isNotEmpty(type))
			queryParams.put("type", type);
		if(StringUtils.isNotEmpty(state))
			queryParams.put("state", state);
		queryParams.put("orderByClause", "p1.n_id desc");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(Integer.parseInt(pageNo));
		pageInfo.setPageSize(getDefaultPageSize());
		List<SysPermissionWrap> data = sysService.pageQuerySysPermission(queryParams, pageInfo);
		retMap.put("queryParams", queryParams);
		retMap.put("pageInfo", pageInfo);
		retMap.put("pageData", data);
		return retMap;
	}
	
	/**
	 * 权限上级节点查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/permission/parents",method=RequestMethod.GET)
	@RequiresPermissions({"manage:permission:parents"})
	@ResponseBody
	public Map<String, Object> permissionParents(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("权限上级节点查询");
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<SysPermission> data = sysService.getPermissionAllPossibleParents();
		retMap.put("data", data);
		return retMap;
	}
	
	/**
	 * 权限新增、修改
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/permission/addOrUpdate",method=RequestMethod.POST)
	@RequiresPermissions({"manage:permission:add-update"})
	@ResponseBody
	public Map<String, Object> permissionAddOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("权限新增或修改");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		retMap.put("message", ReferenceMessage.SAVE_FAIL.getMessage());
		String id = request.getParameter("id");
		String parentId = request.getParameter("parentId");//数字
		String name = request.getParameter("name");//不可为空，[1,12]
		String tag = request.getParameter("tag");//可为空，[0,20]
		String url = request.getParameter("url");//可为空，[0,500]
		String permissions = request.getParameter("permissions");//可为空，[0,250]
		String classIcon = request.getParameter("classIcon");//可为空，[0,50]
		String orderNum = request.getParameter("orderNum");//可为空，数字
		String type = request.getParameter("type");//枚举值
		String state = request.getParameter("state");//枚举值
		// 参数校验
		if(!RegexUtil.isDigit(parentId))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(StringUtils.isBlank(name)||StringUtils.length(name)<LengthLimit.PERMISSION_MODULE_NAME.getMinLength()||StringUtils.length(name)>LengthLimit.PERMISSION_MODULE_NAME.getMaxLength())
			retMap.put("message", ReferenceMessage.MODULE_NAME_VERIFY_ERROR.getMessage());
		else if(StringUtils.isNotEmpty(tag)&&(StringUtils.length(tag)<LengthLimit.PERMISSION_MODULE_TAG.getMinLength()||StringUtils.length(tag)>LengthLimit.PERMISSION_MODULE_TAG.getMaxLength()))
			retMap.put("message", ReferenceMessage.MODULE_TAG_VERIFY_ERROR.getMessage());
		else if(StringUtils.isNotEmpty(url)&&(StringUtils.length(url)<LengthLimit.PERMISSION_URL.getMinLength()||StringUtils.length(url)>LengthLimit.PERMISSION_URL.getMaxLength()))
			retMap.put("message", ReferenceMessage.URL_VERIFY_ERROR.getMessage());
		else if(StringUtils.isNotEmpty(permissions)&&(StringUtils.length(permissions)<LengthLimit.PERMISSIONS.getMinLength()||StringUtils.length(permissions)>LengthLimit.PERMISSIONS.getMaxLength()))
			retMap.put("message", ReferenceMessage.PERMISSION_VERIFY_ERROR.getMessage());
		else if(StringUtils.isNotEmpty(classIcon)&&(StringUtils.length(classIcon)<LengthLimit.PERMISSION_CLASS_ICON.getMinLength()||StringUtils.length(classIcon)>LengthLimit.PERMISSION_CLASS_ICON.getMaxLength()))
			retMap.put("message", ReferenceMessage.CLASS_ICON_VERIFY_ERROR.getMessage());
		else if(StringUtils.isNotEmpty(orderNum)&&!RegexUtil.isDigit(orderNum))
			retMap.put("message", ReferenceMessage.ORDER_NUM_VERIFY_ERROR.getMessage());
		else if(!PermissionType.isExistValue(type))
			retMap.put("message", ReferenceMessage.TYPE_VERIFY_ERROR.getMessage());
		else if(!ValidState.isExistValue(state))
			retMap.put("message", ReferenceMessage.STATE_VERIFY_ERROR.getMessage());
		else {
			SysPermission permission = new SysPermission();
			BeanUtils.populate(permission, request.getParameterMap());
			if(StringUtils.isBlank(id)) {
				// 新增
				permission.setId(null);
				permission.setCreateDate(sysService.getSysDate());
				permission.setUpdateDate(null);
				int affectedCount = sysService.addOrUpdateSysPermission(permission);
				if(affectedCount > 0) {
					retMap.put("result", ProcessResult.SUCCESS.getValue());
					retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					clearAuthCache();
				}
			}else {
				// 修改
				permission.setCreateDate(null);
				permission.setUpdateDate(sysService.getSysDate());
				int affectedCount = sysService.addOrUpdateSysPermission(permission);
				if(affectedCount >= 0) {
					retMap.put("result", ProcessResult.SUCCESS.getValue());
					if(affectedCount == 0)
						retMap.put("message", ReferenceMessage.SAVE_SUCCESS_BUT_NO_CHANGE.getMessage());
					else
						retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					clearAuthCache();
				}
			}
		}
		return retMap;
	}
	
	/**
	 * 清理权限缓存
	 */
	private void clearAuthCache() {
		stdShiroRealm.getAuthenticationCache().clear();
		stdShiroRealm.getAuthorizationCache().clear();
	}
	
	/**
	 * 所有角色查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/role/all",method=RequestMethod.GET)
	@RequiresPermissions({"manage:role:all"})
	@ResponseBody
	public Map<String, Object> allRoles(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("所有角色查询");
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<SysRole> data = sysService.getAllRoles();
		retMap.put("data", data);
		return retMap;
	}
	
	/**
	 * 所有功能权限查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/permission/all",method=RequestMethod.GET)
	@RequiresPermissions({"manage:permission:all"})
	@ResponseBody
	public Map<String, Object> allPermisions(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("所有功能权限查询");
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<SysPermission> data = sysService.getAllPermisions();
		retMap.put("data", data);
		return retMap;
	}
	
	/**
	 * 角色所有功能权限查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/permission/byRole",method=RequestMethod.GET)
	@RequiresPermissions({"manage:permission:by-role"})
	@ResponseBody
	public Map<String, Object> allPermisionsByRole(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("角色所有功能权限查询");
		String roleId = request.getParameter("roleId");
		Map<String, Object> retMap = new HashMap<String, Object>();
		Set<SysPermission> data = sysService.getAllPermissionsByRole(Long.valueOf(roleId));
		retMap.put("data", data);
		return retMap;
	}
	
	/**
	 * 角色权限页面
	 * @return
	 */
	@RequestMapping(value="/role-permission",method=RequestMethod.GET)
	@RequiresPermissions({"manage:role-permission:page"})
	public String rolePermission(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入角色权限管理");
		return "ftl/role-permission";
	}
	
	/**
	 * 角色权限分页查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/role-permission/pageQuery",method=RequestMethod.GET)
	@RequiresPermissions({"manage:role-permission:page-query"})
	@ResponseBody
	public Map<String, Object> rolePermissionPageQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("角色权限分页查询"+getDefaultPageSize());
		Map<String, Object> retMap = new HashMap<String, Object>();
		String pageNo = request.getParameter("page_no");
		String roleName = request.getParameter("roleName");
		String permissionName = request.getParameter("permissionName");
		String url = request.getParameter("url");
		String permissions = request.getParameter("permissions");
		String state = request.getParameter("state");
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if(!RegexUtil.posInteger(pageNo))
			pageNo = String.valueOf(getDefaultPageNo());
		if(StringUtils.isNotEmpty(roleName))
			queryParams.put("roleName", roleName);
		if(StringUtils.isNotEmpty(permissionName))
			queryParams.put("permissionName", permissionName);
		if(StringUtils.isNotEmpty(url))
			queryParams.put("url", url);
		if(StringUtils.isNotEmpty(permissions))
			queryParams.put("permissions", permissions);
		if(StringUtils.isNotEmpty(state))
			queryParams.put("state", state);
		queryParams.put("orderByClause", "r.n_id desc,rp.n_id asc");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(Integer.parseInt(pageNo));
		pageInfo.setPageSize(getDefaultPageSize());
		List<SysRolePermissionWrap> data = sysService.pageQueryRolePermission(queryParams, pageInfo);
		retMap.put("queryParams", queryParams);
		retMap.put("pageInfo", pageInfo);
		retMap.put("pageData", data);
		return retMap;
	}
	
	/**
	 * 角色权限设置
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/role-permission/addOrUpdate",method=RequestMethod.POST)
	@RequiresPermissions({"manage:role-permission:add-update"})
	@ResponseBody
	public Map<String, Object> rolePermissionAddOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("角色权限设置");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		retMap.put("message", ReferenceMessage.SAVE_FAIL.getMessage());
		String roleId = request.getParameter("roleId");
		String permissionIdsString = request.getParameter("permissionIds");
		String [] permissionIdsArray = StringUtils.split(permissionIdsString, "|");
		List<SysRolePermission> rolePermissions = new ArrayList<SysRolePermission>();
		boolean isCheck = true;
		try {
			for(String permissionId : permissionIdsArray) {
				SysRolePermission rolePermission = new SysRolePermission();
				rolePermission.setRoleId(Long.valueOf(roleId));
				rolePermission.setPermissionId(Long.valueOf(permissionId));
				rolePermissions.add(rolePermission);
			}
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
			isCheck = false;
		}
		if(isCheck) {
			int affectedCount = sysService.addOrUpdateSysRolePermission(Long.valueOf(roleId), rolePermissions);
			if(affectedCount > 0) {
				retMap.put("result", ProcessResult.SUCCESS.getValue());
				retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
				clearAuthCache();
			}
		}
		return retMap;
	}
	
	/**
	 * 账号角色页面
	 * @return
	 */
	@RequestMapping(value="/manager-role",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager-role:page"})
	public String managerRole(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入账号角色管理");
		return "ftl/manager-role";
	}
	
	/**
	 * 账号角色分页查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/manager-role/pageQuery",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager-role:page-query"})
	@ResponseBody
	public Map<String, Object> managerRolePageQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("账号角色分页查询"+getDefaultPageSize());
		Map<String, Object> retMap = new HashMap<String, Object>();
		String pageNo = request.getParameter("page_no");
		String account = request.getParameter("account");
		String roleName = request.getParameter("roleName");
		String managerState = request.getParameter("managerState");

		Map<String, Object> queryParams = new HashMap<String, Object>();
		if(!RegexUtil.posInteger(pageNo))
			pageNo = String.valueOf(getDefaultPageNo());
		if(StringUtils.isNotEmpty(roleName))
			queryParams.put("roleName", roleName);
		if(StringUtils.isNotEmpty(account))
			queryParams.put("account", account);
		if(StringUtils.isNotEmpty(managerState))
			queryParams.put("managerState", managerState);
		
		queryParams.put("orderByClause", "m.s_account asc, m.d_create_date desc, m.n_id desc");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(Integer.parseInt(pageNo));
		pageInfo.setPageSize(getDefaultPageSize());
		List<SysManagerRoleWrap> data = sysService.pageQueryManagerRole(queryParams, pageInfo);
		retMap.put("queryParams", queryParams);
		retMap.put("pageInfo", pageInfo);
		retMap.put("pageData", data);
		return retMap;
	}
	
	/**
	 * 账号角色设置
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/manager-role/addOrUpdate",method=RequestMethod.POST)
	@RequiresPermissions({"manage:manager-role:add-update"})
	@ResponseBody
	public Map<String, Object> managerRoleAddOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("账号角色设置");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		retMap.put("message", ReferenceMessage.SAVE_FAIL.getMessage());
		String managerId = request.getParameter("managerId");
		String roleIdsString = request.getParameter("roleIds");
		String [] roleIdsArray = StringUtils.split(roleIdsString, "|");
		List<SysManagerRole> managerRoles = new ArrayList<SysManagerRole>();
		boolean isCheck = true;
		try {
			for(String roleId : roleIdsArray) {
				SysManagerRole managerRole = new SysManagerRole();
				managerRole.setManagerId(Long.valueOf(managerId));
				managerRole.setRoleId(Long.valueOf(roleId));
				managerRoles.add(managerRole);
			}
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
			isCheck = false;
		}
		if(isCheck) {
			int affectedCount = sysService.addOrUpdateSysManagerRole(Long.valueOf(managerId), managerRoles);
			if(affectedCount > 0) {
				retMap.put("result", ProcessResult.SUCCESS.getValue());
				retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
				clearAuthCache();
			}
		}
		return retMap;
	}
	
	/**
	 * 部门新增、修改
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/group/addOrUpdate",method=RequestMethod.POST)
	@RequiresPermissions({"manage:group:add-update"})
	@ResponseBody
	public Map<String, Object> groupAddOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("部门新增或修改");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		retMap.put("message", ReferenceMessage.SAVE_FAIL.getMessage());
		
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		String parentId = request.getParameter("parentId");
		String orderNum = request.getParameter("orderNum");
		String state = request.getParameter("state");
		
		parentId = StringUtils.isEmpty(parentId)?"0":parentId;
		orderNum = StringUtils.isEmpty(orderNum)?"0":orderNum;
		
		// 参数校验
		if(StringUtils.isNotEmpty(id)&&!RegexUtil.isDigit(id))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(!RegexUtil.isDigit(parentId))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(StringUtils.isBlank(name)||StringUtils.length(name)<LengthLimit.GROUP_NAME.getMinLength()||StringUtils.length(name)>LengthLimit.GROUP_NAME.getMaxLength())
			retMap.put("message", ReferenceMessage.GROUP_NAME_ERROR.getMessage());
		else if(StringUtils.isBlank(code)||StringUtils.length(code)<LengthLimit.GROUP_CODE.getMinLength()||StringUtils.length(code)>LengthLimit.GROUP_CODE.getMaxLength())
			retMap.put("message", ReferenceMessage.GROUP_CODE_ERROR.getMessage());
		else if(!RegexUtil.isDigit(orderNum))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(StringUtils.isNotBlank(state)&&!ValidState.isExistValue(state))
			retMap.put("message", ReferenceMessage.STATE_VERIFY_ERROR.getMessage());
		else {
			// 检测组织编码是否存在
			SysGroup checkGroup = sysService.findSysGroupByCode(code);
			if(null!=checkGroup&&(!String.valueOf(checkGroup.getId()).equals(id)||StringUtils.isBlank(id)))
				retMap.put("message", ReferenceMessage.GROUP_CODE_EXISTS.getMessage());
			else {
				SysGroup group = new SysGroup();
				BeanUtils.populate(group, request.getParameterMap());
				if(StringUtils.isBlank(id)) {
					// 新增
					group.setId(null);
					int affectedCount = sysService.addOrUpdateSysGroup(group);
					if(affectedCount > 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}else {
					// 修改
					int affectedCount = sysService.addOrUpdateSysGroup(group);
					if(affectedCount >= 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						if(affectedCount == 0)
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS_BUT_NO_CHANGE.getMessage());
						else
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}
			}
		}
		return retMap;
	}
	
	/**
	 * 查询所有部门（部门树，不分页）
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/group/all",method=RequestMethod.GET)
	@RequiresPermissions({"manage:group:all"})
	@ResponseBody
	public Map<String, Object> allGroups(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("所有部门查询");
		String name = request.getParameter("name");
		String state = request.getParameter("state");
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(name))
			queryParams.put("name", name);
		if(StringUtils.isNotEmpty(state))
			queryParams.put("state", state);
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<SysGroup> data = sysService.getAllGroups(queryParams);
		retMap.put("data", data);
		return retMap;
	}
	
	/**
	 * 部门管理页面
	 * @return
	 */
	@RequestMapping(value="/group",method=RequestMethod.GET)
	@RequiresPermissions({"manage:group:page"})
	public String group(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入组织管理");
		return "ftl/group";
	}
	
	/**
	 * 人员部门管理页面
	 * @return
	 */
	@RequestMapping(value="/manager-group",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager-group:page"})
	public String managerGroup(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入人员部门管理页面");
		return "ftl/manager-group";
	}
	
	/**
	 * 人员部门关系新增修改
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/manager-group/addOrUpdate",method=RequestMethod.POST)
	@RequiresPermissions({"manage:manager-group:add-update"})
	@ResponseBody
	public Map<String, Object> managerGroupAddOrUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("人员部门关系新增修改");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("result", ProcessResult.FAIL.getValue());
		retMap.put("message", ReferenceMessage.SAVE_FAIL.getMessage());
		String id = request.getParameter("id");
		String managerId = request.getParameter("managerId");
		String groupId = request.getParameter("groupId");
		String show = request.getParameter("show");
		String master = request.getParameter("master");
		String state = request.getParameter("state");
		
		// 参数校验
		if(StringUtils.isNotEmpty(id)&&!RegexUtil.isDigit(id))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(!RegexUtil.isDigit(managerId))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(!RegexUtil.isDigit(groupId))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(!ValidState.isExistValue(show))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(!ValidState.isExistValue(master))
			retMap.put("message", ReferenceMessage.PARAM_ERROR.getMessage());
		else if(!ValidState.isExistValue(state))
			retMap.put("message", ReferenceMessage.STATE_VERIFY_ERROR.getMessage());
		else {
			// 检测关系是否存在
			SysManagerGroup checkManagerGroup = sysService.findSysManagerGroupByManagerIdAndGroupId(Long.valueOf(managerId),Long.valueOf(groupId));
			if(null!=checkManagerGroup&&(!String.valueOf(checkManagerGroup.getId()).equals(id)||StringUtils.isBlank(id)))
				retMap.put("message", ReferenceMessage.ACCOUNT_GROUP_EXISTS.getMessage());
			else {
				SysManagerGroup managerGroup = new SysManagerGroup();
				BeanUtils.populate(managerGroup, request.getParameterMap());
				if(StringUtils.isBlank(id)) {
					// 新增
					managerGroup.setId(null);
					managerGroup.setCreateDate(sysService.getSysDate());
					int affectedCount = sysService.addOrUpdateSysManagerGroup(managerGroup);
					if(affectedCount > 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}else {
					// 修改
					int affectedCount = sysService.addOrUpdateSysManagerGroup(managerGroup);
					if(affectedCount >= 0) {
						retMap.put("result", ProcessResult.SUCCESS.getValue());
						if(affectedCount == 0)
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS_BUT_NO_CHANGE.getMessage());
						else
							retMap.put("message", ReferenceMessage.SAVE_SUCCESS.getMessage());
					}
				}
			}
		}
		
		return retMap;
	}
	
	/**
	 * 人员部门分页查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/manager-group/pageQuery",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager-group:page-query"})
	@ResponseBody
	public Map<String, Object> managerGroupPageQuery(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("人员部门分页查询"+getDefaultPageSize());
		Map<String, Object> retMap = new HashMap<String, Object>();
		String pageNo = request.getParameter("page_no");
		String account = request.getParameter("account");
		String name = request.getParameter("name");
		String groupName = request.getParameter("groupName");
		String state = request.getParameter("state");
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		if(!RegexUtil.posInteger(pageNo))
			pageNo = String.valueOf(getDefaultPageNo());
		if(StringUtils.isNotEmpty(name))
			queryParams.put("name", name);
		if(StringUtils.isNotEmpty(account))
			queryParams.put("account", account);
		if(StringUtils.isNotEmpty(groupName))
			queryParams.put("groupName", groupName);
		if(StringUtils.isNotEmpty(state))
			queryParams.put("state", state);
		queryParams.put("orderByClause", "n_id desc");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setPageNo(Integer.parseInt(pageNo));
		pageInfo.setPageSize(getDefaultPageSize());
		List<SysManagerGroupWrap> data = sysService.pageQueryManagerGroup(queryParams, pageInfo);
		retMap.put("queryParams", queryParams);
		retMap.put("pageInfo", pageInfo);
		retMap.put("pageData", data);
		return retMap;
	}
	
	/**
	 * 查询所有人员部门有效关联
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/manager-group/validQuery",method=RequestMethod.GET)
	@RequiresPermissions({"manage:manager-group:valid-query"})
	@ResponseBody
	public Map<String, Object> allValidManagerGroups(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("查询所有人员部门有效关联");
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<SysManagerGroupWrap> data = sysService.getAllValidManagerGroup();
		retMap.put("data", data);
		return retMap;
	}
	
	// 查询人员信息总览
}
