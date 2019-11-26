package client;

import model.IBinder;
import model.IMessage;
import model.SketchProperty;
import model.User;
import utils.Constants;
import utils.GdLog;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import org.quartz.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Client {

    private static Client INSTANCE;

    public static Client getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    private RateLimiter rateLimiter;

    private void start(){
        try {
            IBinder binder = (IBinder) Naming.lookup(Constants.SERVER_URL);
            SketchProperty property = binder.getSketchProperty();
            rateLimiter = new RateLimiter(property);
            
            // UpdateJob updateJob = new UpdateJob(binder);
            // new Timer().scheduleAtFixedRate(updateJob,1000,1000);
            
            /** Quartz **/
            try {
                SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
                Scheduler sched = schedFact.getScheduler();
                sched.start();

                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("binder", binder);

                JobDetail job = newJob(DropTableUpdateJob.class)
                        .withIdentity("dropTable", "group-1")
                        .setJobData(jobDataMap)
                        .build();

                Trigger trigger = newTrigger()
                        .withIdentity("dropTableTrigger", "group-1")
                        .startNow()
                        .withSchedule(simpleSchedule().withIntervalInMilliseconds(1000).repeatForever())
                        .build();

                sched.scheduleJob(job, trigger);
            }catch (SchedulerException e){
                GdLog.e(""+e);
            }

            
            
        } catch (RemoteException e) {
            GdLog.e(e+"");
        } catch (NotBoundException e) {
            GdLog.e(e+"");
        } catch (MalformedURLException e) {
            GdLog.e(e+"");
        }
    }

    public static class DropTableUpdateJob implements Job {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
            IBinder binder = (IBinder) dataMap.get("binder");
            Client.getInstance().rateLimiter.syncDropTable(binder);
        }
    }
    
    public static class UpdateJob extends TimerTask{
        private IBinder binder;

        UpdateJob(IBinder binder){
            this.binder = binder;
        }

        public void run() {
            Client.getInstance().rateLimiter.syncDropTable(binder);
        }
    }

    private void runUserRequest(){
        int userNum = 300;
        User[] users = new User[userNum];
        for (int i=0; i<userNum; i++){
            users[i] = new User("user"+i);
        }
        int[] passCount = new int[userNum];
        int[] blockCount = new int[userNum];
        long startTime = System.currentTimeMillis();
        for(int i=0; i<60000; i++){
            for (int j=0; j<userNum; j++){
                if (rateLimiter.request(users[j])){
                    passCount[j] ++ ;
                }else {
                    blockCount[j] ++;
                }
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        for(int i=0; i<userNum; i++){
            GdLog.i("duration:%d  passCount:%d  blockCount:%d" ,endTime-startTime, passCount[i], blockCount[i]);
        }
    }


    public static void main(String[] args) {

        Client.getInstance().start();
        Client.getInstance().runUserRequest();
    }


    private void testRMIClient(){
        try {
            IMessage message = (IMessage) Naming.lookup(Constants.SERVER_URL);
            GdLog.i(message.greeting());
        } catch (Exception e) {
            GdLog.e(e+"");
        }
    }
}
