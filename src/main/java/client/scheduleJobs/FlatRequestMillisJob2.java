package client.scheduleJobs;

import client.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.GdLog;

public class FlatRequestMillisJob2 implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        GdLog.i(this+"  sending request at a steady pace");
        Client client = Client.getInstance();
        RequestGenerator requestGenerator = client.getRequestGenerator();
        RequestLogger requestLogger = client.getRequestLogger();
        RateLimiter rateLimiter = client.getRateLimiter();
        for (int i=0; i<7000; i++){
            Request request = new Request(requestGenerator.getRandomUser(80).getID());
            rateLimiter.request(request);
            requestLogger.log(request);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                GdLog.e(""+e);
            }
        }
        requestLogger.flush();

        GdLog.i(this+"  finish sending request");
    }
}
