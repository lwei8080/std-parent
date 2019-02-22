package com.std.persist.model.wrap;

import com.std.persist.model.SysPermission;
import com.std.persist.model.SysRole;
import com.std.persist.model.SysRolePermission;

public class SysRolePermissionWrap extends SysRolePermission {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7378853755022909974L;
	
	private SysRole role;
	
	private SysPermission permission;

	public SysRole getRole() {
		return role;
	}

	public void setRole(SysRole role) {
		this.role = role;
	}

	public SysPermission getPermission() {
		return permission;
	}

	public void setPermission(SysPermission permission) {
		this.permission = permission;
	}
	
	

}
