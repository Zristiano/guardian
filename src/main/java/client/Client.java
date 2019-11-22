package client;

import model.IBinder;
import model.IMessage;
import model.SketchProperty;
import utils.Constants;
import utils.GdLog;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    private void start(){
        try {
            IBinder binder = (IBinder) Naming.lookup(Constants.SERVER_URL);
            SketchProperty property = binder.getSKetchProperty();
            CountMinSketch countMinSketch = new CountMinSketch(property.getOverallQPS(),property.getSingleUserQPS(),property.getDiffLimit(),property.getErrorDropRate());
        } catch (RemoteException e) {
            GdLog.e(e+"");
        } catch (NotBoundException e) {
            GdLog.e(e+"");
        } catch (MalformedURLException e) {
            GdLog.e(e+"");
        }
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
