package model;

import utils.GdLog;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloMessage extends UnicastRemoteObject implements IMessage {

    public HelloMessage() throws RemoteException{
        super();
    }

    public String greeting() {
        GdLog.i("hello in HelloMessage");
        return "hello";
    }
}
