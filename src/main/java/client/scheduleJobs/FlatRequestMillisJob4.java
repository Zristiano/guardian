package client.scheduleJobs;

import client.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.GdLog;

public class FlatRequestMillisJob4 implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        GdLog.i(this+"  sending request at a steady pace");
        lowQPS();
        GdLog.i(this+"  finish sending request");
    }

    private void lowQPS(){
        Client client = Client.getInstance();
        RequestGenerator requestGenerator = client.getRequestGenerator();
        RequestLogger requestLogger = client.getRequestLogger();
        RateLimiter rateLimiter = client.getRateLimiter();
        for (int i=0; i<16000; i++){
            Request request = new Request(requestGenerator.getRandomUser(80).getID());
            rateLimiter.request(request);
            requestLogger.log(request);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                GdLog.e(""+e);
            }
        }
        requestLogger.flush();
    }

    private void highQPS(){
        Client client = Client.getInstance();
        RequestGenerator requestGenerator = client.getRequestGenerator();
        RequestLogger requestLogger = client.getRequestLogger();
        RateLimiter rateLimiter = client.getRateLimiter();
        for (int i=0; i<15000; i++){
            for (int j=0; j<10; j++){
                Request request = new Request(requestGenerator.getRandomUser(10000).getID());
                rateLimiter.request(request);
                requestLogger.log(request);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                GdLog.e(""+e);
            }
        }
        requestLogger.flush();
    }
}
