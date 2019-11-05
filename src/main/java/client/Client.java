package client;

import model.IMessage;
import utils.Constants;
import utils.GdLog;

import java.rmi.Naming;

public class Client {
    public static void main(String[] args) {
        try {
            IMessage message = (IMessage) Naming.lookup(Constants.SERVER_URL);
            GdLog.i(message.greeting());
        } catch (Exception e) {
            GdLog.e(e+"");
        }
    }
}
