package com.quartz.qsample.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * @author songyu.li@yeepay.com
 * @createDatetime 2016年9月20日 上午11:38:41
 */

@Service
public class PickNewsJobService implements Job {

	private static Scheduler scheduler;
	
	@Autowired
    public void setScheduler(SchedulerFactoryBean quartzScheduler123) {
        this.scheduler = quartzScheduler123.getScheduler();
    }
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("在" + sdf.format(new Date()) + "更新日志");
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.err.println("[" + Thread.currentThread().getName() + "] FireTime: "
		    + df.format(arg0.getFireTime()) + " ScheduledFireTime: "
		    + df.format(arg0.getScheduledFireTime()) + " JobRunTime(): "
		    + arg0.getJobRunTime());

	}
	
	 /**
     *根据数据库中的记录 恢复异常中断的任务
     */
    public static void resumeJob() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        // ①获取调度器中所有的触发器组
        List<String> triggerGroups = scheduler.getTriggerGroupNames();
        // ②重新恢复在tgroup1组中，名为trigger1触发器的运行
        for (int i = 0; i < triggerGroups.size(); i++) {
            List<String> triggers = scheduler.getTriggerGroupNames();
            for (int j = 0; j < triggers.size(); j++) {
                Trigger tg = scheduler.getTrigger(new TriggerKey(triggers
                        .get(j), triggerGroups.get(i)));
                // ②-1:根据名称判断
                if (tg instanceof SimpleTrigger
                        && tg.getDescription().equals("jgroup1.DEFAULT")) {//由于我们之前测试没有设置触发器所在组，所以默认为DEFAULT
                    // ②-1:恢复运行
                    scheduler.resumeJob(new JobKey(triggers.get(j),
                            triggerGroups.get(i)));
                }
            }
        }
        scheduler.start();

    }
    
    public static void simpleTriggerQrtz() throws SchedulerException{
    	JobDetail jobDetail = JobBuilder.newJob(PickNewsJobService.class)
                .withIdentity("job1", "jgroup1").build();
        SimpleTrigger simpleTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity("trigger1")
                .withSchedule(
                        SimpleScheduleBuilder
                                .repeatSecondlyForTotalCount(10, 2))
                                .startNow()
                .build();
        
        try{
        	//创建scheduler
           /* SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.scheduleJob(jobDetail, simpleTrigger);
            scheduler.start();*/
        	
        	scheduler.scheduleJob(jobDetail, simpleTrigger);
        	scheduler.start();  
        }catch ( ObjectAlreadyExistsException e) {
        	resumeJob();
        }
    }
    
    public static void cronTriggerQrtz() throws SchedulerException{
    	JobDetail jobDetail = JobBuilder.newJob(PickNewsJobService.class)
                .withIdentity("job4", "jgroup2").build();
    	 CronTrigger cronTrigger = TriggerBuilder
                 .newTrigger()
                 .withIdentity("trigger4")
                 .withSchedule(
                		 CronScheduleBuilder.cronSchedule("0 02 * * * ?"))
                		 .startNow()
                		 .build();
         try{
         	
         	scheduler.scheduleJob(jobDetail, cronTrigger);
         	scheduler.start();  
         }catch ( ObjectAlreadyExistsException e) {
         	resumeJob();
         }
    }
    
    public static void upDateTrigger() throws SchedulerException{
    	// retrieve the trigger
    	Trigger oldTrigger = scheduler.getTrigger(new TriggerKey("trigger2", "DEFAULT"));

    	// obtain a builder that would produce the trigger
    	TriggerBuilder tb = oldTrigger.getTriggerBuilder();

    	// update the schedule associated with the builder, and build the new trigger
    	// (other builder methods could be called, to change the trigger in any desired way)
    	Trigger newTrigger = tb.withSchedule(CronScheduleBuilder.cronSchedule("0 25 * * * ?"))
    	    .build();

    	scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
    }
    
    public static void timerTriggerQrtz() throws SchedulerException, ParseException{
    	
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = new Date(df.parse("2016-09-22 11:08:00").getTime());
    	
    	JobDetail jobDetail = JobBuilder.newJob(PickNewsJobService.class)
                .withIdentity("job5", "jgroup5").build();
        SimpleTrigger simpleTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity("trigger5", "trigger5Group")
                .withSchedule(
                        SimpleScheduleBuilder.repeatSecondlyForTotalCount(1))
                                .startAt(date)
                .build();
        try{
        	
        	scheduler.scheduleJob(jobDetail, simpleTrigger);
        	scheduler.start(); 
        	
        }catch ( ObjectAlreadyExistsException e) {
        	resumeJob();
        }
    	
    }
}
