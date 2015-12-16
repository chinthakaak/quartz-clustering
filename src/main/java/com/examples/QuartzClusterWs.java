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
import java.sql.Timestamp;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by ka40215 on 12/11/15.
 */
public class QuartzClusterWs {
    public static void main(String[] args) throws MalformedURLException, SchedulerException {
        QuartzClusterWs quartzClusterWs = new QuartzClusterWs();
        quartzClusterWs.testWs();
    }
    private void testWs() throws MalformedURLException, SchedulerException {
        Endpoint.publish("http://0.0.0.0:9999/ws/quartz", new QuartzClusterImpl());
//        URL url = new URL("http://localhost:9999/ws/quartz?wsdl");
//
//        //1st argument service URI, refer to wsdl document above
//        //2nd argument is service name, refer to wsdl document above
//        QName qname = new QName("http://examples.com/", "QuartzClusterImplService");
//        Service service = Service.create(url, qname);
//        QuartzClusterService quartzService = service.getPort(QuartzClusterService.class);
//
//        quartzService.addJob("job1", 1);
//        quartzService.addJob("job2", 5);
//        quartzService.addJob("job3", 3);

    }
    public static class QuartzClusterJob implements Job {

        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            System.out.println("Executing job: "+jobExecutionContext.getJobDetail().getKey().getName()
                    +" "+ jobExecutionContext.getFireTime()+" "+jobExecutionContext.getFireInstanceId());        }
    }

    @WebService(endpointInterface = "com.examples.QuartzClusterWs$QuartzClusterService")
    public static class QuartzClusterImpl implements QuartzClusterService{
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
            System.out.println("Adding a job to scheduler: " + jobName +" "+ new Timestamp(System.currentTimeMillis()));
            JobDetail jobDetail =  newJob(QuartzClusterJob.class).withIdentity(jobName).requestRecovery().build();
            Trigger trigger = newTrigger().startAt(DateBuilder.futureDate(interval, DateBuilder.IntervalUnit.SECOND)).build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    @WebService
    @SOAPBinding(style = SOAPBinding.Style.RPC)
    public interface QuartzClusterService {

        @WebMethod
        void addJob(String jobName, int interval) throws SchedulerException;

    }
}
