package model;

import utils.GdLog;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Random;

public class SketchProperty implements Cloneable, Serializable {

    public static final long serialVersionUID = 201911220212L;

    /**
     * Max QPS supported by the system
     */
    private int overallQPS;

    /**
     * Max QPS of single user
     */
    private int singleUserQPS;

    /**
     * εQ
     */
    private int diffLimit;

    /**
     * probability of user event dropping when the count of corresponding user event is less than singleUserQPS - diffLimit
     */
    private double errorDropRate;

    private int hashCount;

    private String[] salts;

    public SketchProperty(int overallQPS, int singleUserQPS, int diffLimit, double errorDropRate){
        this.overallQPS = overallQPS;
        this.singleUserQPS = singleUserQPS;
        this.errorDropRate = errorDropRate;
        this.diffLimit = diffLimit;
        initSalt();
    }

    private void initSalt(){
        hashCount = (int)Math.ceil(Math.log((1/ errorDropRate)));
        salts = new String[hashCount];
        // init salt
        for (int i=0; i<hashCount; i++){
            salts[i] = generateSalt();
            GdLog.i("salt[%d]=%s",i,salts[i]);
        }
    }

    private String generateSalt(){
        Random random = new Random();
        byte[] saltBytes = new byte[random.nextInt(50)];
        random.nextBytes(saltBytes);
        return new String(saltBytes, Charset.forName("UTF-8"));
    }

    public int getOverallQPS() {
        return overallQPS;
    }

    public String[] getSalts(){
        return salts;
    }

    public int getSingleUserQPS() {
        return singleUserQPS;
    }

    public int getDiffLimit() {
        return diffLimit;
    }

    public double getErrorDropRate() {
        return errorDropRate;
    }

}
