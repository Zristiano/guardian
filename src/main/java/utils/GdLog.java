package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GdLog {

    public static void i(String msg){
        StackTraceElement[] statckEles = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement ele = statckEles[3];
        System.out.println(getTime()+" "+ ele.getClassName()+"."+ele.getMethodName()+"()_"+ele.getLineNumber()+": "+msg);
    }

    public static void i(String format, Object... obj){
        String msg = String.format(format,obj);
        StackTraceElement[] statckEles = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement ele = statckEles[3];
        System.out.println(getTime()+" "+ ele.getClassName()+"."+ele.getMethodName()+"()_"+ele.getLineNumber()+": "+msg);
    }

    public static void e(String msg){
        StackTraceElement[] statckEles = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement ele = statckEles[3];
        System.err.println(getTime()+" "+ ele.getClassName()+"."+ele.getMethodName()+"()_"+ele.getLineNumber()+": "+msg);
    }

    public static String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
}
