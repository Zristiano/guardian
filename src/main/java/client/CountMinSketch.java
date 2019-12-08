package client;

import com.google.common.hash.Hashing;
import model.IBinder;
import model.LRUCache;
import model.SketchProperty;
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

    public CountMinSketch(SketchProperty sketchProperty){
        this.overallQPS = sketchProperty.getOverallQPS();
        this.singleUserQPS = sketchProperty.getSingleUserQPS();
        this.errorDropRate = sketchProperty.getErrorDropRate();
        this.diffLimit = sketchProperty.getDiffLimit();

        this.hashCount = sketchProperty.getHashCount();
        this.hashSize = sketchProperty.getHashSize();
        this.salts = Arrays.copyOf(sketchProperty.getSalts(),sketchProperty.getSalts().length);
        init();
    }

    private void init(){

        random = new Random();
        hashPosCache = new LRUCache<>(Constants.HASH_CACHE_SIZE);

        sketch = new int[hashCount][hashSize];
        dropTable = new int[hashCount][hashSize];

        // init the monitors for sketch table
        monitor = new Object[hashCount][hashSize];
        for(int i=0; i<hashCount; i++){
            for (int j=0; j<hashSize; j++){
                monitor[i][j] = new Object();
            }
        }

        GdLog.i("overallQps:%d, singleQps:%d, diffLimit:%d, errorRate:%f,  hashCount:%d, hashSize:%d", overallQPS, singleUserQPS, diffLimit, errorDropRate, hashCount, hashSize);
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

    public void syncDropTable(String client, IBinder binder){
        try {
            long startTime = System.currentTimeMillis();
            dropTable = binder.assembleUserRequest(client, sketch);
            long duration = System.currentTimeMillis()-startTime;
//            GdLog.i("client:%s, sync duration: %d", client, duration);
            sketch = new int[hashCount][hashSize];
        } catch (RemoteException e) {
            GdLog.e(e+"");
        }
    }
}
