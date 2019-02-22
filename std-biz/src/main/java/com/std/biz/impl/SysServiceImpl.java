package com.std.biz.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.std.biz.ISysService;
import com.std.common.consts.CommonConsts;
import com.std.common.consts.CommonConsts.ValidState;
import com.std.persist.model.SysGroup;
import com.std.persist.model.SysGroupExample;
import com.std.persist.model.SysGroupExample.Criteria;
import com.std.persist.model.SysManager;
import com.std.persist.model.SysManagerExample;
import com.std.persist.model.SysManagerGroup;
import com.std.persist.model.SysManagerGroupExample;
import com.std.persist.model.SysManagerRole;
import com.std.persist.model.SysManagerRoleExample;
import com.std.persist.model.SysPermission;
import com.std.persist.model.SysPermissionExample;
import com.std.persist.model.SysRole;
import com.std.persist.model.SysRoleExample;
import com.std.persist.model.SysRolePermission;
import com.std.persist.model.SysRolePermissionExample;
import com.std.persist.model.wrap.SysManagerGroupWrap;
import com.std.persist.model.wrap.SysManagerRoleWrap;
import com.std.persist.model.wrap.SysPermissionWrap;
import com.std.persist.model.wrap.SysRolePermissionWrap;
import com.std.persist.mysql.dao.wrap.ISysGroupDaoWrap;
import com.std.persist.mysql.dao.wrap.ISysManagerDaoWrap;
import com.std.persist.mysql.dao.wrap.ISysManagerGroupDaoWrap;
import com.std.persist.mysql.dao.wrap.ISysManagerRoleDaoWrap;
import com.std.persist.mysql.dao.wrap.ISysPermissionDaoWrap;
import com.std.persist.mysql.dao.wrap.ISysRoleDaoWrap;
import com.std.persist.mysql.dao.wrap.ISysRolePermissionDaoWrap;
import com.std.persist.mysql.plugins.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("sysService")
public class SysServiceImpl implements ISysService {
	public final static Logger logger = LoggerFactory.getLogger(SysServiceImpl.class);
	@Autowired
	private ISysManagerDaoWrap sysManagerDaoWrap;
	@Autowired
	private ISysManagerRoleDaoWrap sysManagerRoleDaoWrap;
	@Autowired
	private ISysPermissionDaoWrap sysPermissionDaoWrap;
	@Autowired
	private ISysRoleDaoWrap sysRoleDaoWrap;
	@Autowired
	private ISysRolePermissionDaoWrap sysRolePermissionDaoWrap;
	@Autowired
	private ISysGroupDaoWrap sysGroupDaoWrap;
	@Autowired
	private ISysManagerGroupDaoWrap sysManagerGroupDaoWrap;
    @Autowired
    private IdentityService identityService;
	
	public SysManager findSysManagerByAccount(String account) {
		SysManagerExample example = new SysManagerExample();
		example.createCriteria()
			   .andAccountEqualTo(account);
		List<SysManager> list = sysManagerDaoWrap.selectByExample(example);
		if(CollectionUtils.isNotEmpty(list)&&list.size()==1)
			return list.get(0);
		return null;
	}
	
	public Set<SysRole> getRolesByAccount(String account) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", "1");
		params.put("account", account);
		return sysRoleDaoWrap.selectByAccount(params);
	}
	
	public Set<SysRole> getAllRolesByAccount(String account) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		return sysRoleDaoWrap.selectByAccount(params);
	}
	
	public List<SysRole> getAllRoles() {
		SysRoleExample example = new SysRoleExample();
		example.setOrderByClause("n_id asc");
		List<SysRole> list = sysRoleDaoWrap.selectByExample(example);
		return list;
	}
	
	public Set<SysPermission> getValidPermissionsByRoles(Set<SysRole> roles){
		if(CollectionUtils.isEmpty(roles))
			return null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", "1");
		params.put("roles", roles);
		return sysPermissionDaoWrap.selectByRoles(params);
	}
	
	public Set<SysPermission> getAllPermissionsByRole(Long roleId){
		Set<SysRole> roles = new HashSet<SysRole>();
		SysRole role = new SysRole();
		role.setId(roleId);
		roles.add(role);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roles", roles);
		return sysPermissionDaoWrap.selectByRoles(params);
	}
	
	public List<SysPermission> getAllValidPermissions(){
		SysPermissionExample example = new SysPermissionExample();
		example.createCriteria()
			   .andStateEqualTo(Integer.valueOf(CommonConsts.ValidState.VALID.getValue()));
		return sysPermissionDaoWrap.selectByExample(example);
	}
	
	public List<SysPermission> getAllPermisions(){
		SysPermissionExample example = new SysPermissionExample();
		example.setOrderByClause("n_order_num asc,n_id asc");
		return sysPermissionDaoWrap.selectByExample(example);
	}

	@Override
	public List<SysManager> pageQuerySysManager(Map<String, Object> queryParams, PageInfo pageInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageInfo", pageInfo);
		if(MapUtils.isNotEmpty(queryParams))
			params.putAll(queryParams);
		List<SysManager> managers = sysManagerDaoWrap.getSysManagerByPage(params);
		return managers;
	}

	@Override
	public int addOrUpdateSysManager(SysManager manager) {
		int affectedCount = 0;
		if(null!=manager) {
			if(null!=manager.getId()) {
				affectedCount = sysManagerDaoWrap.updateByPrimaryKeySelective(manager);
			}else {
				affectedCount = sysManagerDaoWrap.insertSelective(manager);
			}
		}
		if(affectedCount > 0)
			syncFlowableUser(sysManagerDaoWrap.selectByPrimaryKey(manager.getId()));
		return affectedCount;
	}

	@Override
	public Date getSysDate() {
		return sysManagerDaoWrap.getSysDate();
	}

	@Override
	public List<SysRole> pageQuerySysRole(Map<String, Object> queryParams, PageInfo pageInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageInfo", pageInfo);
		if(MapUtils.isNotEmpty(queryParams))
			params.putAll(queryParams);
		List<SysRole> roles = sysRoleDaoWrap.getSysRoleByPage(params);
		return roles;
	}

	@Override
	public int addOrUpdateSysRole(SysRole role) {
		int affectedCount = 0;
		if(null!=role) {
			if(null!=role.getId()) {
				affectedCount = sysRoleDaoWrap.updateByPrimaryKeySelective(role);
			}else {
				affectedCount = sysRoleDaoWrap.insertSelective(role);
			}
		}
		return affectedCount;
	}

	@Override
	public SysRole findSysRoleByName(String name) {
		SysRoleExample example = new SysRoleExample();
		example.createCriteria()
			   .andNameEqualTo(name);
		List<SysRole> list = sysRoleDaoWrap.selectByExample(example);
		if(CollectionUtils.isNotEmpty(list)&&list.size()==1)
			return list.get(0);
		return null;
	}

	@Override
	public List<SysPermissionWrap> pageQuerySysPermission(Map<String, Object> queryParams, PageInfo pageInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageInfo", pageInfo);
		if(MapUtils.isNotEmpty(queryParams))
			params.putAll(queryParams);
		List<SysPermissionWrap> permissions = sysPermissionDaoWrap.getSysPermissionByPage(params);
		return permissions;
	}

	@Override
	public int addOrUpdateSysPermission(SysPermission permission) {
		int affectedCount = 0;
		if(null!=permission) {
			if(null!=permission.getId()) {
				affectedCount = sysPermissionDaoWrap.updateByPrimaryKeySelective(permission);
			}else {
				affectedCount = sysPermissionDaoWrap.insertSelective(permission);
			}
		}
		return affectedCount;
	}

	@Override
	public List<SysPermission> getPermissionAllPossibleParents() {
		SysPermissionExample example = new SysPermissionExample();
		example.createCriteria()
			   .andStateEqualTo(Integer.valueOf(CommonConsts.ValidState.VALID.getValue()))
			   .andTypeLessThanOrEqualTo(Integer.valueOf(CommonConsts.PermissionType.MENU.getValue()));
		example.setOrderByClause("n_type asc, n_order_num asc, n_id asc");
		return sysPermissionDaoWrap.selectByExample(example);
	}

	@Override
	public List<SysRolePermissionWrap> pageQueryRolePermission(Map<String, Object> queryParams, PageInfo pageInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageInfo", pageInfo);
		if(MapUtils.isNotEmpty(queryParams))
			params.putAll(queryParams);
		List<SysRolePermissionWrap> rolePermissions = sysRolePermissionDaoWrap.getSysRolePermissionByPage(params);
		return rolePermissions;
	}

	@Override
	public int addOrUpdateSysRolePermission(Long roleId, List<SysRolePermission> rolePermissions) {
		int affectedCount = 0;
		SysRolePermissionExample example = new SysRolePermissionExample();
		example.createCriteria()
		       .andRoleIdEqualTo(roleId);
		affectedCount += sysRolePermissionDaoWrap.deleteByExample(example);
		if(CollectionUtils.isNotEmpty(rolePermissions))
			affectedCount += sysRolePermissionDaoWrap.insertBatch(rolePermissions);
		return affectedCount;
	}

	@Override
	public List<SysManagerRoleWrap> pageQueryManagerRole(Map<String, Object> queryParams, PageInfo pageInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageInfo", pageInfo);
		if(MapUtils.isNotEmpty(queryParams))
			params.putAll(queryParams);
		List<SysManagerRoleWrap> managerRoleList = sysManagerRoleDaoWrap.getSysManagerRoleByPage(params);
		return managerRoleList;
	}

	@Override
	public int addOrUpdateSysManagerRole(Long managerId, List<SysManagerRole> managerRoles) {
		int affectedCount = 0;
		SysManagerRoleExample example = new SysManagerRoleExample();
		example.createCriteria()
		       .andManagerIdEqualTo(managerId);
		affectedCount += sysManagerRoleDaoWrap.deleteByExample(example);
		if(CollectionUtils.isNotEmpty(managerRoles))
			affectedCount += sysManagerRoleDaoWrap.insertBatch(managerRoles);
		return affectedCount;
	}

	@Override
	public List<SysManager> getAllSysManager() {
		SysManagerExample example = new SysManagerExample();
		example.setOrderByClause("s_account asc,d_create_date,n_id desc");
		return sysManagerDaoWrap.selectByExample(example);
	}

	@Override
	public int addOrUpdateSysGroup(SysGroup group) {
		int affectedCount = 0;
		if(null!=group) {
			if(null!=group.getId()) {
				affectedCount = sysGroupDaoWrap.updateByPrimaryKeySelective(group);
			}else {
				affectedCount = sysGroupDaoWrap.insertSelective(group);
			}
		}
		if(affectedCount > 0)
			syncFlowableGroup(sysGroupDaoWrap.selectByPrimaryKey(group.getId()));
		return affectedCount;
	}

	@Override
	public List<SysGroup> getAllGroups(Map<String, Object> queryParams) {
		SysGroupExample example = new SysGroupExample();
		Criteria criteria = example.createCriteria();
		if(null!=queryParams.get("name"))
			criteria.andNameLike("%"+(String)queryParams.get("name")+"%");
		if(null!=queryParams.get("state"))
			criteria.andStateEqualTo(Integer.valueOf((String)queryParams.get("state")));
		example.setOrderByClause("n_state desc,n_order_num asc,n_id asc");
		return sysGroupDaoWrap.selectByExample(example);
	}

	@Override
	public SysGroup findSysGroupByCode(String code) {
		SysGroupExample example = new SysGroupExample();
		example.createCriteria()
			   .andCodeEqualTo(code);
		List<SysGroup> list = sysGroupDaoWrap.selectByExample(example);
		if(CollectionUtils.isNotEmpty(list)&&list.size()==1)
			return list.get(0);
		return null;
	}

	@Override
	public List<SysManagerGroupWrap> pageQueryManagerGroup(Map<String, Object> queryParams, PageInfo pageInfo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pageInfo", pageInfo);
		if(MapUtils.isNotEmpty(queryParams))
			params.putAll(queryParams);
		List<SysManagerGroupWrap> managerGroupList = sysManagerGroupDaoWrap.getSysManagerGroupByPage(params);
		return managerGroupList;
	}

	@Override
	public int addOrUpdateSysManagerGroup(SysManagerGroup managerGroup) {
		int affectedCount = 0;
		if(null!=managerGroup) {
			if(null!=managerGroup.getId()) {
				affectedCount = sysManagerGroupDaoWrap.updateByPrimaryKeySelective(managerGroup);
			}else {
				affectedCount = sysManagerGroupDaoWrap.insertSelective(managerGroup);
			}
		}
		if(affectedCount > 0)
			syncFlowableMembership(String.valueOf(managerGroup.getState()), String.valueOf(managerGroup.getManagerId()), String.valueOf(managerGroup.getGroupId()));;
		return affectedCount;
	}

	@Override
	public SysManagerGroup findSysManagerGroupByManagerIdAndGroupId(Long managerId, Long groupId) {
		SysManagerGroupExample example = new SysManagerGroupExample();
		example.createCriteria()
			   .andManagerIdEqualTo(managerId)
			   .andGroupIdEqualTo(groupId);
		List<SysManagerGroup> list = sysManagerGroupDaoWrap.selectByExample(example);
		if(CollectionUtils.isNotEmpty(list)&&list.size()==1)
			return list.get(0);
		return null;
	}

	@Override
	public List<SysManagerGroupWrap> getAllValidManagerGroup() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", "1");
		params.put("orderByClause", "mg.n_group_id asc,mg.s_master desc,m.s_name asc,mg.n_id asc");
		List<SysManagerGroupWrap> managerGroupList = sysManagerGroupDaoWrap.getSysManagerGroupByPage(params);
		return managerGroupList;
	}
	
    /**
     * 用户新增修改时同步flowable用户表act_id_user
     * @param manager
     */
    private void syncFlowableUser(SysManager manager) {
		String userId = String.valueOf(manager.getId());
		identityService.deleteUser(userId);
		User flowableUser = identityService.newUser(userId);
		flowableUser.setEmail(manager.getMobile());
		flowableUser.setFirstName(manager.getName());
		flowableUser.setLastName(manager.getName());
		flowableUser.setDisplayName(manager.getAccount());
		flowableUser.setPassword("000000");
		identityService.saveUser(flowableUser);
    }
    
    /**
     * 部门新增修改时同步flowable组织表act_id_group
     * @param group
     */
    private void syncFlowableGroup(SysGroup group) {
		String groupId = String.valueOf(group.getId());
		identityService.deleteGroup(groupId);
		Group flowableGroup = identityService.newGroup(groupId);
		flowableGroup.setName(group.getName());
		flowableGroup.setType(group.getParentId().toString());
		identityService.saveGroup(flowableGroup);
    }
    
    /**
     * 绑定部门、人员关系
     * @param managerGroupState
     * @param managerId
     * @param groupId
     */
    private void syncFlowableMembership(String managerGroupState,String managerId,String groupId) {
    	if(ValidState.VALID.getValue().equals(managerGroupState)) {
    		// 删除绑定关系
    		identityService.deleteMembership(managerId, groupId);
    		// 重新绑定关系
    		identityService.createMembership(managerId, groupId);
    	}else {
    		// 删除绑定关系
    		identityService.deleteMembership(managerId, groupId);
    	}
    }
}
