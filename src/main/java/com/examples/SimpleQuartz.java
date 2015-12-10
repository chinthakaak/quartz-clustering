package com.examples;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ka40215 on 12/10/15.
 */
public class SimpleQuartz {
    private Logger logger = LoggerFactory.getLogger(SimpleQuartz.class);

    public void testQuartz() throws SchedulerException, InterruptedException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

//        JobDetail jobDetail =  newJob(HelloQuartz.class).withIdentity("job1", "group1").build();
        JobDetail jobDetail =  newJob(HelloQuartz.class).build();

//        Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startAt(DateBuilder.futureDate(2000, DateBuilder.IntervalUnit.MILLISECOND)).build();
        Trigger trigger = newTrigger().startAt(DateBuilder.futureDate(2000, DateBuilder.IntervalUnit.MILLISECOND)).build();

        scheduler.scheduleJob(jobDetail, trigger);

        scheduler.start();

        Thread.sleep(3000);

        scheduler.shutdown();
    }

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        SimpleQuartz simpleQuartz = new SimpleQuartz();
        simpleQuartz.testQuartz();
    }

    public static class HelloQuartz implements Job{
        private Logger logger = LoggerFactory.getLogger(HelloQuartz.class);

        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            logger.info("Hello Quartz");
        }
    }
}


