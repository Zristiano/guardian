package client.scheduleJobs;

import client.Client;
import client.RequestGenerator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class UserQpsChangeJob1 implements Job {

    @Override
    public void execute(JobExecutionContext context)  {

        RequestGenerator requestGenerator = Client.getInstance().getRequestGenerator();
        int[][] freqs = {{1000,1000},{2000, 2000},{3000, 1500}};
        for (int[] freq : freqs){
            requestGenerator.setUserFrequency(freq[0],freq[1]);
            System.out.println("user"+freq[0]+":"+requestGenerator.getUser(freq[0]).getID());
        }
        try {
            Thread.sleep(7);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int[] freq : freqs){
            requestGenerator.setUserFrequency(freq[0],1);
        }
    }
}
