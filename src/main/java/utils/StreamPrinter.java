package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class StreamPrinter<T> {

    public interface Print<T> {
        String compose(T ele);
    }

    private Object[] queue ;

    private int size;

    private Print<T> print ;

    private String filePath;

    private FileWriter fWriter;

    private Consumer consumer;

    private ReentrantLock lock ;

    private Condition empty, full;

    private long in, out;


    public StreamPrinter(int size, String filePath, Print<T> print){
        this.size = size;
        this.print = print;
        this.filePath = filePath;
        in = 0;
        out = 0;

        queue = new Object[size];
        consumer = new Consumer();

        lock = new ReentrantLock();
        empty = lock.newCondition();
        full = lock.newCondition();
        consumer = new Consumer();
        consumer.start();
        initLogFile(filePath);
    }

    private void initLogFile(String filePath){
        try {
            File logFile = new File(filePath);
            if (logFile.exists()){
                logFile.delete();
            }
            logFile.createNewFile();
            fWriter = new FileWriter(filePath);
        }catch (IOException e){
            GdLog.e(""+e);
        }
    }

    private int pos(long index){
        return (int)(index%size);
    }

    public void commit(T element){
        lock.lock();
        try {
            // the queue is full of elements which is waiting for being printed
            while (in >= out+size){
                full.await();
            }
            queue[pos(in++)] = element;
            // The queue has some elements to be printed
            if (in > out){
                empty.signal();
            }
        }catch (Exception e){
            GdLog.e(""+e);
        }finally {
            lock.unlock();
        }
    }

    private class Consumer extends Thread{
        @Override
        public void run() {
            while(true){
                lock.lock();
                try {
                    // There is no elements in the queue
                    while (out >= in){
                        empty.await();
                    }
                    String msg = print.compose((T)queue[pos(out++)]);
                    fWriter.write(msg+"\n");
                    if (in < (out+size)){
                        full.signal();
                    }
                }catch (Exception e){
                    GdLog.e(""+e);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public void close(){
        try {
            fWriter.flush();
            fWriter.close();
        } catch (IOException e) {
            GdLog.e(""+e);
        }
    }

}
