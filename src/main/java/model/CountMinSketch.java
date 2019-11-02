package model;

public class CountMinSketch {

    /**
     * Max QPS supported by the system
     */
    private int overallQPS;

    /**
     * Max QPS of single user
     */
    private int singleUserQPS;

    /**
     * εQ,
     */
    private int diffLimit;

    /**
     * probability of user event dropping when the count of corresponding user event is less than singleUserQPS - diffLimit
     */
    private double dropRate;

    /**
     * ε
     */
    private double epsilon;

    private int hashCount;

    private int hashSize;

    private int[][] sketch;

    public CountMinSketch(int overallQPS, int singleUserQPS, int diffLimit, double dropRate){
        epsilon = (double) diffLimit/singleUserQPS ;
        this.overallQPS = overallQPS;
        this.singleUserQPS = singleUserQPS;
        this.dropRate = dropRate;
        this.diffLimit = diffLimit;
        init();
    }

    private void init(){
        hashCount = (int)Math.ceil(Math.log((1/dropRate)));
        hashSize = (int)Math.ceil(Math.log((1/dropRate)) * Math.E/epsilon);
        sketch = new int[hashCount][hashSize];
    }

}
