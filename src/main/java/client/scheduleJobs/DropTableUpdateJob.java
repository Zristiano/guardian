package client.scheduleJobs;

import client.Client;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.Constants;
import utils.GdLog;


public class DropTableUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext context){
<<<<<<< HEAD
//        try {
//            Thread.sleep(100);
//        }catch (InterruptedException e){
//            GdLog.e(""+e);
//        }
        GdLog.i(this+"  DropTableUpdateJob is running");
=======
        try {
            Thread.sleep(100);
        }catch (InterruptedException e){
            GdLog.e(""+e);
        }
//        GdLog.i("DropTableUpdateJob is running");
>>>>>>> 561627e937ae9bc224a080d08c47cfac3f031bd1
        Client client = Client.getInstance();
        client.getRateLimiter().syncDropTable(Constants.CLIENT_0, client.getBinder());
        GdLog.i(this+"  DropTableUpdateJob is finished");
    }
}
