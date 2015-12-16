package com.examples;

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

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by ka40215 on 12/10/15.
 */
public class QuartzWs {
    public static void main(String[] args) throws MalformedURLException, SchedulerException {
        QuartzWs quartzWs2 = new QuartzWs();
        quartzWs2.testWs();
    }

    private void testQuartz() throws SchedulerException, InterruptedException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

//        JobDetail jobDetail =  newJob(HelloQuartz.class).withIdentity("job1", "group1").build();
        JobDetail jobDetail =  newJob(QuartzJob.class).build();

//        Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startAt(DateBuilder.futureDate(2000, DateBuilder.IntervalUnit.MILLISECOND)).build();
        Trigger trigger = newTrigger().startAt(DateBuilder.futureDate(2000, DateBuilder.IntervalUnit.MILLISECOND)).build();

        scheduler.scheduleJob(jobDetail, trigger);

        scheduler.start();

        Thread.sleep( 3000);

        scheduler.shutdown();
    }
    private void testWs() throws MalformedURLException, SchedulerException {
        Endpoint.publish("http://localhost:9999/ws/quartz", new QuartzImpl());
        URL url = new URL("http://localhost:9999/ws/quartz?wsdl");

        //1st argument service URI, refer to wsdl document above
        //2nd argument is service name, refer to wsdl document above
        QName qname = new QName("http://examples.com/", "QuartzImplService");
        Service service = Service.create(url, qname);
        QuartzService quartzService = service.getPort(QuartzService.class);

        quartzService.addJob("job1", 1);
        quartzService.addJob("job2", 5);
        quartzService.addJob("job3", 3);

    }

    public static class QuartzJob implements Job {

        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("Executing job: "+jobExecutionContext.getJobDetail().getKey().getName()
                    +" "+ jobExecutionContext.getFireTime()+" "+jobExecutionContext.getFireInstanceId());
        }
    }
    @WebService(endpointInterface = "com.examples.QuartzWs$QuartzService")
    public static class QuartzImpl implements QuartzService{
        private static Scheduler scheduler = null;

        static {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            try {
                scheduler = schedulerFactory.getScheduler();
                scheduler.start();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        public void addJob(String jobName, int interval) throws SchedulerException {
            System.out.println("Adding a job to scheduler: " + jobName);
            JobDetail jobDetail =  newJob(QuartzJob.class).withIdentity(jobName).build();
            Trigger trigger = newTrigger().startAt(DateBuilder.futureDate(interval, DateBuilder.IntervalUnit.SECOND)).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }
    @WebService
    @SOAPBinding(style = SOAPBinding.Style.RPC)
    public interface QuartzService {

        @WebMethod
        void addJob(String jobName, int interval) throws SchedulerException;

    }
}


