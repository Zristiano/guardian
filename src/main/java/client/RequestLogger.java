package client;

import utils.Constants;
import utils.GdLog;
import utils.StreamPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RequestLogger {

    /**
     * Not thread safe when writing logs
     */
    public static final int MODE_DIRECT = 0;

    /**
     * Thread safe when writing logs
     */
    public static final int MODE_BUFFERED = 1;

    private int mode;

    private RequestLogger impl;

    private static class InstanceHolder{
        private static RequestLogger INSTANCE = new RequestLogger();
    }

    public static RequestLogger getInstance(){
        return InstanceHolder.INSTANCE;
    }

    private RequestLogger(){ }

    public void initMode(int mode) {
        this.mode = mode;
        if (mode==MODE_DIRECT){
            impl = new DirectLogger();
        }else if (mode==MODE_BUFFERED){
            impl = new BufferedLogger();
        }
    }

    public void log(Request request){
        impl.log(request);
    }

    public void close(){
        impl.close();
    }



    private class DirectLogger extends RequestLogger{

        private FileWriter fileWriter;

        DirectLogger(){
            try {
                fileWriter = new FileWriter(Constants.REQUEST_LOG_PATH);
            } catch (IOException e) {
                GdLog.e(""+e);
            }
        }

        @Override
        public void log(Request request) {
            try {
                fileWriter.write(request.toString()+"\n");
            } catch (IOException e) {
                GdLog.e(""+e);
            }
        }

        @Override
        public void close() {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                GdLog.e(""+e);
            }
        }
    }

    private class BufferedLogger extends RequestLogger{

        private StreamPrinter<Request> printer;

        private List<Request> requestList;

        private Timer timer;

        BufferedLogger(){
            printer = new StreamPrinter<>(100, Constants.REQUEST_LOG_PATH, new StreamPrinter.Print<Request>() {
                @Override
                public String compose(Request ele) {
                    return ele.toString();
                }
            });
            requestList = new ArrayList<>();
            timer = new Timer();
            PrintTask printTask = new PrintTask();
            timer.scheduleAtFixedRate(printTask, 1000,1000);
        }

        @Override
        public void log(Request request) {
            synchronized (BufferedLogger.this){
                requestList.add(request);
            }
        }

        @Override
        public void close() {
            try {
                Thread.sleep(2000);
                timer.cancel();
                printer.close();
            } catch (Exception e) {
                GdLog.e(""+e);
            }
        }

        class PrintTask extends TimerTask{
            @Override
            public void run() {
                List<Request> printList = requestList;
                synchronized (RequestLogger.this){
                    if (requestList.isEmpty()) return;
                    requestList = new ArrayList<>();
                }
                for (Request request : printList){
                    printer.commit(request);
                }
            }
        }
    }


}
