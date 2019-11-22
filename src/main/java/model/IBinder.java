package model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBinder extends Remote {

    public SketchProperty getSKetchProperty() throws RemoteException;
}
