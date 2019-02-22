package com.std.common.utils.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;

public abstract class BaseJob implements Job {
	
	private String jobName;
	private String jobGroupName;
	private String triggerGroupName;
	private String cronTime;
	private JobDataMap jobDataMap;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroupName() {
		return jobGroupName;
	}
	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}
	public String getTriggerGroupName() {
		return triggerGroupName;
	}
	public void setTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
	}
	public String getCronTime() {
		return cronTime;
	}
	public void setCronTime(String cronTime) {
		this.cronTime = cronTime;
	}
	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}
	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}
	
}
