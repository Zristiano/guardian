package server;

import model.BinderImp;
import model.HelloMessage;
import model.IBinder;
import utils.Constants;
import utils.GdLog;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private void start(){
        try {
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            BinderImp binder = new BinderImp();
            Registry registry = LocateRegistry.createRegistry(Constants.RMI_PORT);
            registry.bind(Constants.SERVICE, binder);
        } catch (Exception e) {
            GdLog.e(e+"");
        }
    }

    private void testRMIServer(){
        try {
            System.setProperty("java.rmi.server.hostname","127.0.0.1");
            HelloMessage helloMessage = new HelloMessage();
            Registry registry = LocateRegistry.createRegistry(Constants.RMI_PORT);
            registry.bind(Constants.SERVICE, helloMessage);
        } catch (Exception e) {
            GdLog.e(e+"");
        }
    }
}
