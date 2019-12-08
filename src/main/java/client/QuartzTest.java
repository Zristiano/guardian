package client;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import utils.GdLog;

public class QuartzTest implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        GdLog.i("execute in QuartzTest");
    }
}
