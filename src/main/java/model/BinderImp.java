package model;

import server.RateController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BinderImp extends UnicastRemoteObject implements IBinder{

    public BinderImp() throws RemoteException {
        super();
    }

    public SketchProperty getSKetchProperty() throws RemoteException{
        SketchProperty property =  RateController.getInstance().getSketchProperty();
        return property;
    }
}
