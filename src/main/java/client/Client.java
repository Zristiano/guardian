package client;

import model.IBinder;
import model.IMessage;
import model.SketchProperty;
import model.User;
import utils.Constants;
import utils.GdLog;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

public class Client {

    private static Client INSTANCE;

    public static Client getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    private RateLimiter rateLimiter;

    private void start(){
        try {
            IBinder binder = (IBinder) Naming.lookup(Constants.SERVER_URL);
            SketchProperty property = binder.getSketchProperty();
            rateLimiter = new RateLimiter(property);
            UpdateJob updateJob = new UpdateJob(binder);
            new Timer().scheduleAtFixedRate(updateJob,1000,1000);
        } catch (RemoteException e) {
            GdLog.e(e+"");
        } catch (NotBoundException e) {
            GdLog.e(e+"");
        } catch (MalformedURLException e) {
            GdLog.e(e+"");
        }
    }

    public static class UpdateJob extends TimerTask{
        private IBinder binder;

        UpdateJob(IBinder binder){
            this.binder = binder;
        }

        public void run() {
            Client.getInstance().rateLimiter.syncDropTable(binder);
        }
    }

    private void runUserRequest(){
        User user = new User("user1");
        for(int i=0; i<300; i++){
            GdLog.i("request: " + rateLimiter.request(user));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Client.getInstance().start();
        Client.getInstance().runUserRequest();
    }


    private void testRMIClient(){
        try {
            IMessage message = (IMessage) Naming.lookup(Constants.SERVER_URL);
            GdLog.i(message.greeting());
        } catch (Exception e) {
            GdLog.e(e+"");
        }
    }
}
