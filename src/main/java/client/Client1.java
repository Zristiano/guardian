package client;

import model.IBinder;
import model.SketchProperty;
import model.User;
import org.quartz.*;
import utils.Constants;
import utils.GdLog;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.TimerTask;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Client1 {
    private static Client1 INSTANCE;

    public static Client1 getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new Client1();
        }
        return INSTANCE;
    }

    private RateLimiter rateLimiter;

    private RequestLogger requestLogger;

    private void start(){
        try {
            IBinder binder = (IBinder) Naming.lookup(Constants.SERVER_URL);
            SketchProperty property = binder.getSketchProperty();
            rateLimiter = new RateLimiter(property);
            requestLogger = RequestLogger.getInstance();
            requestLogger.initMode(RequestLogger.MODE_BUFFERED);
            // UpdateJob updateJob = new UpdateJob(binder);
            // new Timer().scheduleAtFixedRate(updateJob,1000,1000);

            /** Quartz **/
            try {
                SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
                Scheduler sched = schedFact.getScheduler();
                sched.start();

                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("binder", binder);

                JobDetail job = newJob(Client1.DropTableUpdateJob.class)
                        .withIdentity("dropTable", "group-1")
                        .setJobData(jobDataMap)
                        .build();

                Trigger trigger = newTrigger()
                        .withIdentity("dropTableTrigger", "group-1")
                        .startNow()
                        .withSchedule(simpleSchedule().withIntervalInMilliseconds(1000).repeatForever())
                        .build();

                sched.scheduleJob(job, trigger);
            }catch (Exception e){
                GdLog.e(""+e);
            }

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            GdLog.e(e+"");
        }


    }

    public static class DropTableUpdateJob implements Job {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
            IBinder binder = (IBinder) dataMap.get("binder");
            Client1.getInstance().rateLimiter.syncDropTable(Constants.CLIENT_1, binder);
        }
    }

    public static class UpdateJob extends TimerTask {
        private IBinder binder;

        UpdateJob(IBinder binder){
            this.binder = binder;
        }

        public void run() {
            Client1.getInstance().rateLimiter.syncDropTable(Constants.CLIENT_1, binder);
        }
    }

    private void runUserRequest(){
        int userNum = 200;
        User[] users = new User[userNum];
        for (int i=0; i<userNum; i++){
            users[i] = new User("user"+i);
        }
        int[] passCount = new int[userNum];
        int[] blockCount = new int[userNum];
        long startTime = System.currentTimeMillis();
        GdLog.i("runUsrRequest");
        for(int i=0; i<6000; i++){
            for (int j=0; j<userNum; j++){
                Request request = new Request(users[j].getID());
                if (rateLimiter.request(request)){
                    passCount[j] ++ ;
                }else {
                    blockCount[j] ++;
                }
                requestLogger.log(request);
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
        requestLogger.close();
        GdLog.i("close requestLogger");
    }

    private void runUserRequest1(){
        RequestGenerator requestGenerator = new RequestGenerator(10);
        requestGenerator.setUserFrequency(3, 5);
        for (int i=0; i<10000; i++){
            User user = requestGenerator.getRandomUser(10);
            if (i==4000){
                requestGenerator.setUserFrequency(4,40);
            }
            Request request = new Request(user.getID());
            rateLimiter.request(request);
            requestLogger.log(request);
            GdLog.i(user.toString()+"\n");
        }
    }

    public static void main(String[] args) {
        Client1.getInstance().start();
        Client1.getInstance().runUserRequest();
    }

}
