package client.scheduleJobs;

import client.Client;
import client.RequestGenerator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class UserQpsChangeJob2 implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RequestGenerator requestGenerator = Client.getInstance().getRequestGenerator();
        int[][] freqs = {{1500,500},{8000, 2000}};
        for (int[] freq : freqs){
            requestGenerator.setUserFrequency(freq[0],freq[1]);
            System.out.println("user"+freq[0]+":"+requestGenerator.getUser(freq[0]).getID());
        }
        try {
            Thread.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int[] freq : freqs){
            requestGenerator.setUserFrequency(freq[0],1);
        }
    }
}
