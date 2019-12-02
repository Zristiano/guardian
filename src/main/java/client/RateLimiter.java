package client;

import model.IBinder;
import model.SketchProperty;
import model.User;

public class RateLimiter {

    private CountMinSketch sketch;

    private SketchProperty property ;

    private long dropTableUpdateTs;

    public RateLimiter(SketchProperty sketchProperty){
        property = sketchProperty;
        sketch = new CountMinSketch(property.getOverallQPS(),property.getSingleUserQPS(),property.getDiffLimit(),property.getErrorDropRate());
    }

    public boolean request(Request req){
        sketch.request(req.getUserId());
        req.setTime();
        req.setDropTableUpdateTs(dropTableUpdateTs);
        boolean isBlock = sketch.isBlock(req.getUserId());
        req.setResult(!isBlock);
        return !isBlock;
    }

    public void syncDropTable(IBinder binder){
        sketch.syncDropTable(binder);
        dropTableUpdateTs = System.currentTimeMillis();
    }
}
