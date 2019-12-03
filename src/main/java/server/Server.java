package server;

import model.BinderImp;
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

}
