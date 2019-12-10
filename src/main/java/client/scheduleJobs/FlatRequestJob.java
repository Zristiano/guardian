package client.scheduleJobs;

import client.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.GdLog;

public class FlatRequestJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
<<<<<<< HEAD
        GdLog.i(this+"  sending request at a steady pace");
=======
        GdLog.i("sending request at a steady pace");
>>>>>>> 561627e937ae9bc224a080d08c47cfac3f031bd1
        Client client = Client.getInstance();
        RequestGenerator requestGenerator = client.getRequestGenerator();
        RequestLogger requestLogger = client.getRequestLogger();
        RateLimiter rateLimiter = client.getRateLimiter();
<<<<<<< HEAD
        for (int i=0; i<300000; i++){
=======
        for (int i=0; i<3000000; i++){
>>>>>>> 561627e937ae9bc224a080d08c47cfac3f031bd1
            Request request = new Request(requestGenerator.getRandomUser(10).getID());
            rateLimiter.request(request);
            requestLogger.log(request);
        }
        requestLogger.flush();

<<<<<<< HEAD
        GdLog.i(this+"  finish sending request");
=======
        GdLog.i("finish sending request");
>>>>>>> 561627e937ae9bc224a080d08c47cfac3f031bd1
    }
}
