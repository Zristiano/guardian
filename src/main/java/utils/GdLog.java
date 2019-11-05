package utils;

public class GdLog {

    public static void i(String msg){
        StackTraceElement[] statckEles = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement ele = statckEles[3];
        System.out.println(ele.getClassName()+"."+ele.getMethodName()+"()_"+ele.getLineNumber()+": "+msg);
    }

    public static void i(String format, Object... obj){
        String msg = String.format(format,obj);
        StackTraceElement[] statckEles = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement ele = statckEles[3];
        System.out.println(ele.getClassName()+"."+ele.getMethodName()+"()_"+ele.getLineNumber()+": "+msg);
    }

    public static void e(String msg){
        StackTraceElement[] statckEles = Thread.getAllStackTraces().get(Thread.currentThread());
        StackTraceElement ele = statckEles[3];
        System.err.println(ele.getClassName()+"."+ele.getMethodName()+"()_"+ele.getLineNumber()+": "+msg);
    }
}
