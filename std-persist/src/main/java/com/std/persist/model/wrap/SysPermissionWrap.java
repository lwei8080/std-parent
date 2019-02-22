package com.std.persist.model.wrap;

import com.std.persist.model.SysPermission;

public class SysPermissionWrap extends SysPermission {

	/**
	 * 
	 */
	private static final long serialVersionUID = 820832998326488363L;

	private String parentName;

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	
}
