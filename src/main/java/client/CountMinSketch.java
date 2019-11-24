package client;

import com.google.common.hash.Hashing;
import model.IBinder;
import model.User;
import utils.Constants;
import utils.GdLog;

import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Random;

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
     * εQ
     */
    private int diffLimit;

    /**
     * probability of user event dropping when the count of corresponding user event is less than singleUserQPS - diffLimit
     */
    private double errorDropRate;

    /**
     * ε
     */
    private double epsilon;

    private int hashCount;

    private int hashSize;

    private int[][] sketch;

    private int[][] dropTable;

    private Object[][] monitor;

    private String[] salts;

    private Random random;

    public CountMinSketch(int overallQPS, int singleUserQPS, int diffLimit, double errorDropRate){
        epsilon = (double) diffLimit/singleUserQPS ;
        this.overallQPS = overallQPS;
        this.singleUserQPS = singleUserQPS;
        this.errorDropRate = errorDropRate;
        this.diffLimit = diffLimit;
        init();
    }

    private void init(){

        random = new Random();

        hashCount = (int)Math.ceil(Math.log((1/ errorDropRate)));
        hashSize = (int)Math.ceil(Math.log((1/ errorDropRate)) * Math.E/epsilon);
        sketch = new int[hashCount][hashSize];
        dropTable = new int[hashCount][hashSize];

        salts = new String[hashCount];
        // init salt
        for (int i=0; i<hashCount; i++){
            salts[i] = getSalt(i);
        }
        // init the monitors for sketch table
        monitor = new Object[hashCount][hashSize];
        for(int i=0; i<hashCount; i++){
            for (int j=0; j<hashSize; j++){
                monitor[i][j] = new Object();
            }
        }

        GdLog.i("overallQps:%d, singleQps:%d, diffLimit:%d, errorRate:%f", overallQPS, singleUserQPS, diffLimit, errorDropRate);
    }

    private String getSalt(int n){
        char[] salt = Constants.BASE_SALT.toCharArray();
        int interval = n*Constants.SALT_INTERVAL;
        for(int i=0; i<salt.length; i++){
            salt[i] = (char)((salt[i]+interval) % 256);
        }
        return String.valueOf(salt);
    }

    private int hash(String key, int i){
        int hashCode = Hashing.sha256().hashString(key+salts[i], Charset.forName("UTF-8")).hashCode() ;
        return (int)(Math.abs((long)hashCode) % hashSize);
    }


    /**
     * update the hash table when a user request comes in
     * @param usr user
     */
    public void request(User usr){
        if(usr==null) return ;
        for(int i=0; i<hashCount; i++){
            int j = hash(usr.getID(), i);
            synchronized (monitor[i][j]){
                sketch[i][j] ++ ;
            }
        }
    }

    public boolean isBlock(User usr){
        if(usr==null) return true;
        int lastQPS = Integer.MAX_VALUE;
        for(int i=0; i<hashCount; i++){
            int j = hash(usr.getID(), i);
            lastQPS = Math.min(lastQPS, dropTable[i][j]);
        }

        if (lastQPS<singleUserQPS) return true;
        int expectedDropCount = lastQPS - singleUserQPS;
        int rd = random.nextInt(lastQPS);
        return rd<expectedDropCount;
    }

    public void syncDropTable(IBinder binder){
        try {
            // TODO: 2019/11/23 这里有很多异步问题需要解决
            dropTable = binder.assembleUserRequest(Constants.CLIENT_1, sketch);
        } catch (RemoteException e) {
            GdLog.e(e+"");
        }
    }
}
