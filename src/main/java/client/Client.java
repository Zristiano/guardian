package client;

import model.IBinder;
import model.SketchProperty;
import model.User;
import org.quartz.impl.StdSchedulerFactory;
import utils.Constants;
import utils.GdLog;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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

    private RequestLogger requestLogger;

    private IBinder binder;

    private RequestGenerator requestGenerator;

    private void start(){
        try {
            binder = (IBinder) Naming.lookup(Constants.SERVER_URL);
            SketchProperty property = binder.getSketchProperty();
            rateLimiter = new RateLimiter(property);
            requestLogger = RequestLogger.getInstance();
            requestLogger.initMode(RequestLogger.MODE_BUFFERED);
            requestGenerator = new RequestGenerator(80);

            /** Quartz **/
            try {
                Scheduler scheduler = new StdSchedulerFactory().getScheduler();
                scheduler.start();
                Thread.sleep(55000);
                scheduler.shutdown();
                requestLogger.close();
            }catch (Exception e){
                GdLog.e(""+e);
            }

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            GdLog.e(e+"");
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
        for(int i=0; i<10000; i++){
            for (int j=0; j<userNum; j++){
                Request request = new Request(users[j].getID());
                if (rateLimiter.request(request)){
                    passCount[j] ++ ;
                } else {
                    blockCount[j] ++;
                }
                requestLogger.log(request);
            }
//            try {
//                Thread.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        long endTime = System.currentTimeMillis();
        for(int i=0; i<userNum; i++){
            GdLog.i("duration:%d  passCount:%d  blockCount:%d" ,endTime-startTime, passCount[i], blockCount[i]);
        }
        requestLogger.close();
        GdLog.i("close requestLogger");
    }

    private void runUserRequest1(){
        requestGenerator = new RequestGenerator(80);
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
        requestLogger.close();
    }

    public IBinder getBinder(){
        return binder;
    }

    public RateLimiter getRateLimiter(){
        return rateLimiter;
    }

    public RequestLogger getRequestLogger(){
        return requestLogger;
    }

    public RequestGenerator getRequestGenerator(){
        return requestGenerator;
    }

    public static void main(String[] args) {
        Client.getInstance().start();
//        Client.getInstance().runUserRequest();
//        Client.getInstance().produceUser(500000);
    }

    private void produceUser(int num){
        try {
            FileWriter writer = new FileWriter(Constants.USERS_INFO_PATH);
            for (int i=0; i<num; i++){
                User user = new User("user"+i);
                writer.write(user.toString()+"\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
