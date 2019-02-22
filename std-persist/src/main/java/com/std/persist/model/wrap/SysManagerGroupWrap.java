package com.std.persist.model.wrap;

import com.std.persist.model.SysGroup;
import com.std.persist.model.SysManager;
import com.std.persist.model.SysManagerGroup;

public class SysManagerGroupWrap extends SysManagerGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SysManager manager;
	
	private SysGroup group;

	public SysManager getManager() {
		return manager;
	}

	public void setManager(SysManager manager) {
		this.manager = manager;
	}

	public SysGroup getGroup() {
		return group;
	}

	public void setGroup(SysGroup group) {
		this.group = group;
	}
	
	

}
