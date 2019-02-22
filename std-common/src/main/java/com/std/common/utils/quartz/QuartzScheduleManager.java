package com.std.common.utils.quartz;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * QUARTZ 定时计划管理类
 * @author liuwei3
 *
 */
public class QuartzScheduleManager {
	private static final Logger logger = LoggerFactory.getLogger(QuartzScheduleManager.class);
	
	/**
	 * 调度器工厂
	 */
	private enum QuartzSchedulerFactory {
		STD_FACTORY(new StdSchedulerFactory());
		private SchedulerFactory schedulerFactory;
		private QuartzSchedulerFactory(SchedulerFactory schedulerFactory) {
			this.schedulerFactory = schedulerFactory;
		}
		public SchedulerFactory getSchedulerFactory() {
			return schedulerFactory;
		}
	}
	
	/**
	 * 默认Job组名
	 */
	private static String JOB_GROUP_NAME = GroupName.DEFAULT.getJobGroupName();
	/**
	 * 默认触发器组名
	 */
	private static String TRIGGER_GROUP_NAME = GroupName.DEFAULT.getTriggerGroupName();
	
    /**
     * 获取调度器
     *
     * @return Scheduler
     * @throws SchedulerException Scheduler获取异常
     */
    private static Scheduler getScheduler() throws SchedulerException {
        return QuartzSchedulerFactory.STD_FACTORY.getSchedulerFactory().getScheduler();
    }
    /**
     * 获取CronTrigger
     *
     * @param jobName          任务名
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @return CronTrigger
     */
    private static CronTrigger getCronTrigger(String triggerName, String triggerGroupName, String time) {
        if (StringUtils.isBlank(triggerGroupName)) {
            triggerGroupName = TRIGGER_GROUP_NAME;
        }
        return TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName).withSchedule(CronScheduleBuilder.cronSchedule(time)).build();
    }

    /**
     * 获取JobDetail
     *
     * @param jobName      任务名
     * @param jobGroupName 任务组名（为空使用默认）
     * @param cls          任务类
     * @param jobDataMap   附带参数
     * @return JobDetail
     */
    private static JobDetail getJobDetail(String jobName, String jobGroupName, Class<? extends Job> cls, JobDataMap jobDataMap) {
        if (StringUtils.isBlank(jobGroupName)) {
            jobGroupName = JOB_GROUP_NAME;
        }

        if (jobDataMap != null) {
            return JobBuilder.newJob(cls).withIdentity(jobName, jobGroupName).usingJobData(jobDataMap).build();
        } else {
            return JobBuilder.newJob(cls).withIdentity(jobName, jobGroupName).build();
        }
    }

    /**
     * 设置JobDetail 和 CronTrigger 到 scheduler
     *
     * @param cls              任务嘞
     * @param jobName          任务名
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @param jobDataMap       附带参数
     * @param scheduler        调度器
     * @return 设置成功与否
     * @throws SchedulerException 调度器异常
     */
    private static boolean setJobDetailAndCronTriggerInScheduler(Class<? extends Job> cls, String jobName, String jobGroupName, String triggerGroupName,
                                                                 String time, JobDataMap jobDataMap, Scheduler scheduler) throws SchedulerException {
        if (!isJobKeyExists(scheduler, jobName, jobGroupName)) {
            return false;
        }
        JobDetail jobDetail = getJobDetail(jobName, jobGroupName, cls, jobDataMap);
        CronTrigger trigger = getCronTrigger(jobName, triggerGroupName, time);
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }
        return true;
    }

    /**
     * 从调度器中移除Job
     *
     * @param scheduler  调度器
     * @param triggerKey 触发器key（名，组）
     * @param jobKey     任务key（名，组）
     */
    private static void removeJob(Scheduler scheduler, TriggerKey triggerKey, JobKey jobKey) {
        try {
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            //移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用CronTrigger类型添加任务
     *
     * @param scheduler        调度器
     * @param cls              任务嘞
     * @param jobName          任务名
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @param jobDataMap       附带参数
     * @return 是否添加成功
     */
    private static boolean addJobUseCronTrigger(Scheduler scheduler, Class<? extends Job> cls, String jobName, String jobGroupName,
                                               String triggerGroupName, String time, JobDataMap jobDataMap) {
        try {
            return setJobDetailAndCronTriggerInScheduler(cls, jobName, jobGroupName, triggerGroupName, time, jobDataMap, scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断是否存在JobKey
     *
     * @param scheduler    任务调度器
     * @param jobName      任务名
     * @param jobGroupName 任务组名
     * @return 是否存在JobKey
     */
    private static boolean isJobKeyExists(Scheduler scheduler, String jobName, String jobGroupName) {
        if (StringUtils.isBlank(jobName)) {
            return false;
        }
        if (StringUtils.isBlank(jobGroupName)) {
            jobGroupName = JOB_GROUP_NAME;
        }
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            return jobDetail == null;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 开放方法均使用 getScheduler() 获得的scheduler
    
    /**
     * 添加定时任务
     *
     * @param cls              任务类
     * @param jobName          任务名
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param time             crond格式时间
     * @param jobDataMap       附带参数
     * @return 是否正常添加任务
     */
    public static boolean addJobUseCronTrigger(Class<? extends Job> cls, String jobName, String jobGroupName,
                                              String triggerGroupName, String time, JobDataMap jobDataMap) {
        try {
            if (StringUtils.isBlank(jobName)) {
                return false;
            }
            if (StringUtils.isBlank(triggerGroupName)) {
                triggerGroupName = TRIGGER_GROUP_NAME;
            }
            if (StringUtils.isBlank(jobGroupName)) {
                jobGroupName = JOB_GROUP_NAME;
            }
            Scheduler scheduler = getScheduler();
            return setJobDetailAndCronTriggerInScheduler(cls, jobName, jobGroupName, triggerGroupName, time, jobDataMap, scheduler);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改任务时间
     *
     * @param jobName          任务名
     * @param time             crond格式时间
     * @param jobGroupName     任务组名（为空使用默认）
     * @param triggerGroupName 触发器组名（为空使用默认）
     * @param jobDataMap       附带参数
     * @param useOldJobDataMap 是否使用历史任务附加参数 （如null!=jobDataMap 则 即使设置true 也不生效以新参数传入）
     * @return 是否修改成功
     */
    public static boolean modifyJobTime(String jobName, String time, String jobGroupName,
                                        String triggerGroupName, JobDataMap jobDataMap, boolean useOldJobDataMap) {
        try {
            if (StringUtils.isBlank(jobName)) {
                return false;
            }
            Scheduler scheduler = getScheduler();
            if (StringUtils.isBlank(triggerGroupName)) {
                triggerGroupName = TRIGGER_GROUP_NAME;
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return false;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                if (StringUtils.isBlank(jobGroupName)) {
                    jobGroupName = JOB_GROUP_NAME;
                }
                JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                if(useOldJobDataMap) {
                	JobDataMap oldJobDataMap = jobDetail.getJobDataMap();
                	if(null==jobDataMap&&null!=oldJobDataMap) {
                		jobDataMap = oldJobDataMap;
                	}
                }
                Class<? extends Job> jobClass = jobDetail.getJobClass();
                removeJob(scheduler, triggerKey, jobKey);
                return addJobUseCronTrigger(scheduler, jobClass, jobName, jobGroupName, triggerGroupName, time, jobDataMap);
            }
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 启动所有定时任务
     */
    public static void startJobs() {
        try {
            Scheduler scheduler = getScheduler();
            if(!scheduler.isStarted())
            	scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭所有定时任务
     */
    public static void shutdownJobs() {
        try {
            Scheduler scheduler = getScheduler();
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止一个job任务
     *
     * @param jobName      任务名
     * @param jobGroupName 任务组名（空位默认）
     * @return 是否停止
     */
    public static boolean pauseJob(String jobName, String jobGroupName) {
        try {
            if (StringUtils.isBlank(jobName)) {
                return false;
            }
            if (StringUtils.isBlank(jobGroupName)) {
                jobGroupName = JOB_GROUP_NAME;
            }
            Scheduler scheduler = getScheduler();
            scheduler.interrupt(JobKey.jobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 恢复一个job任务
     *
     * @param jobName      任务名
     * @param jobGroupName 任务组名（空位默认）
     * @return 是否恢复
     */
    public static boolean resumeJob(String jobName, String jobGroupName) {
        try {
            if (StringUtils.isBlank(jobName)) {
                return false;
            }
            if (StringUtils.isBlank(jobGroupName)) {
                jobGroupName = JOB_GROUP_NAME;
            }
            Scheduler scheduler = getScheduler();
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 移除job
     * @param jobName
     * @param jobGroupName
     * @param triggerGroupName
     * @return
     */
    public static boolean removeJob(String jobName, String jobGroupName, String triggerGroupName) {
        try {
            if (StringUtils.isBlank(jobName)) {
                return false;
            }
            if (StringUtils.isBlank(triggerGroupName)) {
                triggerGroupName = TRIGGER_GROUP_NAME;
            }
            if (StringUtils.isBlank(jobGroupName)) {
                jobGroupName = JOB_GROUP_NAME;
            }
            Scheduler scheduler = getScheduler();
            removeJob(scheduler, TriggerKey.triggerKey(jobName, triggerGroupName), JobKey.jobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加定时任务
     *
     * @param cls  任务类
     * @param job 任务类属性
     * @return 是否添加成功
     */
    public static boolean addJob(BaseJob job) {
        if (job == null) {
            return false;
        }
        String jobName = job.getJobName();
        if (StringUtils.isBlank(jobName)) {
            return false;
        }
        String triggerGroupName = job.getTriggerGroupName();
        if (StringUtils.isBlank(triggerGroupName)) {
            triggerGroupName = TRIGGER_GROUP_NAME;
        }
        String jobGroupName = job.getJobGroupName();
        if (StringUtils.isBlank(jobGroupName)) {
            jobGroupName = JOB_GROUP_NAME;
        }
        try {
        	return addJobUseCronTrigger(job.getClass(), jobName, job.getJobGroupName(), job.getTriggerGroupName(), job.getCronTime(), job.getJobDataMap());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 移除任务
     * @param job
     * @return
     */
    public static boolean removeJob(BaseJob job) {
        if (job == null) {
            return false;
        }
        String jobName = job.getJobName();
        if (StringUtils.isBlank(jobName)) {
            return false;
        }
        String triggerGroupName = job.getTriggerGroupName();
        if (StringUtils.isBlank(triggerGroupName)) {
            triggerGroupName = TRIGGER_GROUP_NAME;
        }
        String jobGroupName = job.getJobGroupName();
        if (StringUtils.isBlank(jobGroupName)) {
            jobGroupName = JOB_GROUP_NAME;
        }
        try {
            return removeJob(jobName, job.getJobGroupName(), job.getTriggerGroupName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 修改任务
     *
     * @param cls  任务类
     * @param job 任务类属性
     * @return 是否修改成功
     */
    public static boolean modifyJob(BaseJob job) {
        if (job == null) {
            return false;
        }
        if (!removeJob(job)) {
        	return false;
        }
        return addJob(job);
    }
    
    /**
     * Returns the number of jobs executed since the Scheduler started.. 
     * @return
     */
    public static long getNumberOfJobsExecuted() {
    	long count = 0;
    	try {
			Scheduler scheduler = getScheduler();
			SchedulerMetaData metaData = scheduler.getMetaData();
			count = metaData.getNumberOfJobsExecuted();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    	
    	return count;
    }
    
    /**
     * Return a list size of JobExecutionContext objects that represent all currently executing Jobs in this Scheduler instance. 
     * @return
     */
    public static long getCurrentlyExecutingJobsSize() {
    	long count = 0;
    	try {
			Scheduler scheduler = getScheduler();
			count = scheduler.getCurrentlyExecutingJobs().size();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    	return count;
    }
    
    /**
     * return wait trigger job size
     * @return
     */
    @SuppressWarnings("unchecked")
	public static long getWaitTriggerJobSize(){
    	long count = 0;
        try {
            Scheduler scheduler = getScheduler();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    if(CollectionUtils.isNotEmpty(triggers)) {
                        Date nextFireTime = triggers.get(0).getNextFireTime();
                        if(null!=nextFireTime) {
                            logger.info("[jobName] : " + jobName + " [groupName] : " + jobGroup + " [nextFireTime] : " + dateFormat.format(nextFireTime));
                            count ++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    
    /**
     * 将yyyy-MM-dd HH:mm:ss转CronTime
     * @return
     */
    public static String formatToCronTimeString(String dateTime) {
    	String cronTime = null;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
			Date date = dateFormat.parse(dateTime);
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(date);
			StringBuffer sb = new StringBuffer();
			sb.append(calendar.get(Calendar.SECOND)).append(" ")
			  .append(calendar.get(Calendar.MINUTE)).append(" ")
			  .append(calendar.get(Calendar.HOUR_OF_DAY)).append(" ")
			  .append(calendar.get(Calendar.DATE)).append(" ")
			  .append(calendar.get(Calendar.MONTH)+1).append(" ")
			  .append("? ")
			  .append(calendar.get(Calendar.YEAR)).append(" ");
			cronTime = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return cronTime;
    }
    /**
     * 将日期转CronTime
     * @return
     */
    public static String formatToCronTimeString(Date date) {
    	String cronTime = null;
    	try {
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(date);
			StringBuffer sb = new StringBuffer();
			sb.append(calendar.get(Calendar.SECOND)).append(" ")
			  .append(calendar.get(Calendar.MINUTE)).append(" ")
			  .append(calendar.get(Calendar.HOUR_OF_DAY)).append(" ")
			  .append(calendar.get(Calendar.DATE)).append(" ")
			  .append(calendar.get(Calendar.MONTH)+1).append(" ")
			  .append("? ")
			  .append(calendar.get(Calendar.YEAR)).append(" ");
			cronTime = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return cronTime;
    }
}
