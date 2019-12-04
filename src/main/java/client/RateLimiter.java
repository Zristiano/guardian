package client;

import model.IBinder;
import model.SketchProperty;
import model.User;
import utils.GdLog;

import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {

    private CountMinSketch sketch;

    private SketchProperty property ;

    private long dropTableUpdateTs;

    private AtomicInteger reqCount;

    public RateLimiter(SketchProperty sketchProperty){
        property = sketchProperty;
        sketch = new CountMinSketch(property.getOverallQPS(),
                property.getSingleUserQPS(),
                property.getDiffLimit(),
                property.getErrorDropRate(),
                property.getSalts());
        reqCount = new AtomicInteger(0);
    }

    public boolean request(Request req){
        sketch.request(req.getUserId());
        req.setTime();
        req.setDropTableUpdateTs(dropTableUpdateTs);
//        if (reqCount.intValue()>property.getOverallQPS()){
//            req.setResult(false);
//            return false;
//        }
        boolean isBlock = sketch.isBlock(req.getUserId());
        req.setResult(!isBlock);
        if (!isBlock){
            reqCount.incrementAndGet();
        }
        return !isBlock;
    }

    public void syncDropTable(String client, IBinder binder){
        sketch.syncDropTable(client, binder);
        dropTableUpdateTs = System.currentTimeMillis();
        reqCount = new AtomicInteger(0);
    }
}
