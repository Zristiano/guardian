package client;

import model.IBinder;
import model.SketchProperty;
import model.User;

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
        int cnt = reqCount.incrementAndGet();
        sketch.request(req.getUserId());
        req.setTime();
        req.setDropTableUpdateTs(dropTableUpdateTs);
        if (cnt>property.getOverallQPS()){
            req.setResult(false);
            return false;
        }
        boolean isBlock = sketch.isBlock(req.getUserId());
        req.setResult(!isBlock);
        return !isBlock;
    }

    public void syncDropTable(IBinder binder){
        sketch.syncDropTable(binder);
        dropTableUpdateTs = System.currentTimeMillis();
        reqCount = new AtomicInteger(0);
    }
}
