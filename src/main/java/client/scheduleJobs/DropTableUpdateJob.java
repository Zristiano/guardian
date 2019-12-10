package client.scheduleJobs;

import client.Client;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.Constants;
import utils.GdLog;


public class DropTableUpdateJob implements Job {
    @Override
    public void execute(JobExecutionContext context){
//        try {
//            Thread.sleep(100);
//        }catch (InterruptedException e){
//            GdLog.e(""+e);
//        }
        GdLog.i(this+"  DropTableUpdateJob is running");

        Client client = Client.getInstance();
        client.getRateLimiter().syncDropTable(Constants.CLIENT_0, client.getBinder());
        GdLog.i(this+"  DropTableUpdateJob is finished");
    }
}
