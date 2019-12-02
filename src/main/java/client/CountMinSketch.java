package client;

import com.google.common.hash.Hashing;
import model.IBinder;
import model.LRUCache;
import model.User;
import utils.Constants;
import utils.GdLog;

import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.Arrays;
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

    private LRUCache<String, int[]> hashPosCache;

    public CountMinSketch(int overallQPS, int singleUserQPS, int diffLimit, double errorDropRate){
        epsilon = (double) diffLimit/overallQPS ;
        this.overallQPS = overallQPS;
        this.singleUserQPS = singleUserQPS;
        this.errorDropRate = errorDropRate;
        this.diffLimit = diffLimit;
        init();
    }

    private void init(){

        random = new Random();
        hashPosCache = new LRUCache<>(Constants.HASH_CACHE_SIZE);

        hashCount = (int)Math.ceil(Math.log((1/ errorDropRate)));
//        hashCount = 100;
        hashSize = (int)Math.ceil(Math.log((1/ errorDropRate)) * Math.E/epsilon);
        sketch = new int[hashCount][hashSize];
        dropTable = new int[hashCount][hashSize];

        salts = new String[hashCount];
        // init salt
        for (int i=0; i<hashCount; i++){
            salts[i] = generateSalt(i);
            GdLog.i("salt[%d]=%s",i,salts[i]);
        }
        // init the monitors for sketch table
        monitor = new Object[hashCount][hashSize];
        for(int i=0; i<hashCount; i++){
            for (int j=0; j<hashSize; j++){
                monitor[i][j] = new Object();
            }
        }

        GdLog.i("overallQps:%d, singleQps:%d, diffLimit:%d, errorRate:%f,  hashCount:%d, hashSize:%d", overallQPS, singleUserQPS, diffLimit, errorDropRate, hashCount, hashSize);
    }

    private String generateSalt(int n){
        byte[] saltBytes = new byte[random.nextInt(50)];
        random.nextBytes(saltBytes);
        return new String(saltBytes, Charset.forName("UTF-8"));
    }

    private int hash(String key, int i){
        int[] hashPos = hashPosCache.get(key);
        if (hashPos == null){
            hashPos = new int[hashCount];
            Arrays.fill(hashPos, -1);
            hashPosCache.put(key,hashPos);
        }
        if (hashPos[i]<0) {
            int hashCode = Hashing.sha256().hashString(key+salts[i], Charset.forName("UTF-8")).hashCode() ;
            hashPos[i] = (int)(Math.abs((long)hashCode) % hashSize);
        }
        return hashPos[i];
    }


    /**
     * update the hash table when a user request comes in
     * @param userId user
     */
    public void request(String userId){
        if(userId==null) return ;
        for(int i=0; i<hashCount; i++){
            int j = hash(userId, i);
            synchronized (monitor[i][j]){
                sketch[i][j] ++ ;
            }
//            GdLog.i("hash sketch[%d][%d]:%d",i, j, sketch[i][j]);
        }
    }

    public boolean isBlock(String usrId){
        if(usrId==null) return true;
        int lastQPS = Integer.MAX_VALUE;
        for(int i=0; i<hashCount; i++){
            int j = hash(usrId, i);
            lastQPS = Math.min(lastQPS, dropTable[i][j]);
        }

        if (lastQPS<singleUserQPS) return false;
        int expectedDropCount = lastQPS - singleUserQPS;
//        GdLog.i("lastQPS:%d, expectedDropCount:%d", lastQPS, expectedDropCount);
        int rd = random.nextInt(lastQPS);
        return rd<expectedDropCount;
    }

    public void syncDropTable(IBinder binder){
        try {
            // TODO: 2019/11/23 这里有很多异步问题需要解决
            long startTime = System.currentTimeMillis();
            dropTable = binder.assembleUserRequest(Constants.CLIENT_1, sketch);
            long duration = System.currentTimeMillis()-startTime;
//            GdLog.i("sync duration : "+duration);
            sketch = new int[hashCount][hashSize];
        } catch (RemoteException e) {
            GdLog.e(e+"");
        }
    }
}
