package com.std.persist.model.wrap;

import com.std.persist.model.SysManager;
import com.std.persist.model.SysManagerRole;
import com.std.persist.model.SysRole;

public class SysManagerRoleWrap extends SysManagerRole {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2976070848044254620L;
	
	private SysManager manager;
	
	private SysRole role;

	public SysManager getManager() {
		return manager;
	}

	public void setManager(SysManager manager) {
		this.manager = manager;
	}

	public SysRole getRole() {
		return role;
	}

	public void setRole(SysRole role) {
		this.role = role;
	}

}
