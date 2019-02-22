package com.std.manage.integration.shiro;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.std.biz.ISysService;
import com.std.manage.consts.ApplicationConsts;
import com.std.persist.model.SysPermission;

public class StdFilterChainDefinitionMapBuilder {
	@Autowired
	private ISysService sysService;

    public Map<String, String> buildFilterChainDefinitionMap(){
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/resources/**", "anon");
        filterChainDefinitionMap.put("/manage/doLogin", "anon");
        filterChainDefinitionMap.put("/manage/login", "anon");
        filterChainDefinitionMap.put("/previlige/no", "anon");
        filterChainDefinitionMap.put("/error", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/captcha", "anon");
        filterChainDefinitionMap.put("/druid/**", ApplicationConsts.druidAuth);
        List<SysPermission> list = sysService.getAllValidPermissions();
        for (SysPermission resource : list) {
        	if(StringUtils.isNotEmpty(resource.getUrl())&&StringUtils.isNotEmpty(resource.getPermissions()))
        		filterChainDefinitionMap.put(resource.getUrl(), "perms[\"" + resource.getPermissions() + "\"]");
        }
        filterChainDefinitionMap.put("/**", "perms");
        return filterChainDefinitionMap;
    }
}
