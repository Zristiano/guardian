package model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMessage extends Remote {
    public String greeting() throws RemoteException;
}
