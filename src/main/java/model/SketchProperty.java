package model;

import java.io.Serializable;

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

    public SketchProperty(int overallQPS, int singleUserQPS, int diffLimit, double errorDropRate){
        this.overallQPS = overallQPS;
        this.singleUserQPS = singleUserQPS;
        this.errorDropRate = errorDropRate;
        this.diffLimit = diffLimit;
    }

    public int getOverallQPS() {
        return overallQPS;
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
