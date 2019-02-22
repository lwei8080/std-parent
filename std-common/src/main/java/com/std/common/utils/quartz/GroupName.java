package com.std.common.utils.quartz;

public enum GroupName {
	DEFAULT("DEFAULT_JOB_GROUP","DEFAULT_TRIGGER_GROUP");
	private String jobGroupName;
	private String triggerGroupName;
	private GroupName(String jobGroupName,String triggerGroupName){
		this.jobGroupName = jobGroupName;
		this.triggerGroupName = triggerGroupName;
	}
	public String getJobGroupName() {
		return jobGroupName;
	}
	public String getTriggerGroupName() {
		return triggerGroupName;
	}
}
