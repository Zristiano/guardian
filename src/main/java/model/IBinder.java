package model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBinder extends Remote {

    SketchProperty getSketchProperty() throws RemoteException;

    int[][] assembleUserRequest(String client, int[][] sketch) throws RemoteException;
}
