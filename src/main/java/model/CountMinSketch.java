package model;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class CountMinSketch {

    public static String BASE_SALT = "Roger1Ding2Zristiano3";

    public static int SALT_INTERVAL = 1;

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
    private double dropRate;

    /**
     * ε
     */
    private double epsilon;

    private int hashCount;

    private int hashSize;

    private int[][] sketch;

    private String[] salts;

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
        salts = new String[hashCount];
        for (int i=0; i<hashCount; i++){
            salts[i] = getSalt(i);
        }
    }

    private String getSalt(int n){
        char[] salt = BASE_SALT.toCharArray();
        int interval = n*SALT_INTERVAL;
        for(int i=0; i<salt.length; i++){
            salt[i] = (char)((salt[i]+interval) %256);
        }
        return String.valueOf(salt);
    }

    private int hash(String key, int i){
        int hashCode = Hashing.sha256().hashString(key+salts[i], Charset.forName("UTF-8")).hashCode() ;
        return (int)(((long)hashCode) % hashSize);
    }

    /**
     * update the hash table when a user request comes in
     * @param usr user
     */
    public void request(User usr){
        if(usr==null) return ;
        for(int i=0; i<hashCount; i++){
            int hash = hash(usr.getID(), i);
            sketch[i][hash] ++ ;
        }
    }
}
