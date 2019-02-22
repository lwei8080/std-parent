package com.std.biz;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public interface ISysService {
	Date getSysDate();
	SysManager findSysManagerByAccount(String account);
	Set<SysRole> getRolesByAccount(String account);
	Set<SysRole> getAllRolesByAccount(String account);
	List<SysRole> getAllRoles();
	Set<SysPermission> getValidPermissionsByRoles(Set<SysRole> roles);
	Set<SysPermission> getAllPermissionsByRole(Long roleId);
	List<SysPermission> getAllValidPermissions();
	List<SysPermission> getAllPermisions();
	List<SysManager> pageQuerySysManager(Map<String, Object> queryParams,PageInfo pageInfo);
	List<SysManager> getAllSysManager();
	int addOrUpdateSysManager(SysManager manager);
	List<SysRole> pageQuerySysRole(Map<String, Object> queryParams,PageInfo pageInfo);
	int addOrUpdateSysRole(SysRole role);
	SysRole findSysRoleByName(String name);
	List<SysPermissionWrap> pageQuerySysPermission(Map<String, Object> queryParams,PageInfo pageInfo);
	int addOrUpdateSysPermission(SysPermission permission);
	List<SysPermission> getPermissionAllPossibleParents();
	List<SysRolePermissionWrap> pageQueryRolePermission(Map<String, Object> queryParams,PageInfo pageInfo);
	int addOrUpdateSysRolePermission(Long roleId,List<SysRolePermission> rolePermissions);
	List<SysManagerRoleWrap> pageQueryManagerRole(Map<String, Object> queryParams,PageInfo pageInfo);
	int addOrUpdateSysManagerRole(Long managerId,List<SysManagerRole> managerRoles);
	int addOrUpdateSysGroup(SysGroup group);
	List<SysGroup> getAllGroups(Map<String, Object> queryParams);
	SysGroup findSysGroupByCode(String code);
	List<SysManagerGroupWrap> pageQueryManagerGroup(Map<String, Object> queryParams,PageInfo pageInfo);
	int addOrUpdateSysManagerGroup(SysManagerGroup managerGroup);
	SysManagerGroup findSysManagerGroupByManagerIdAndGroupId(Long managerId,Long groupId);
	List<SysManagerGroupWrap> getAllValidManagerGroup();
}
