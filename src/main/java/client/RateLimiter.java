package client;

import model.IBinder;
import model.SketchProperty;
import model.User;

public class RateLimiter {

    private CountMinSketch sketch;

    private SketchProperty property ;

    public RateLimiter(SketchProperty sketchProperty){
        property = sketchProperty;
        sketch = new CountMinSketch(property.getOverallQPS(),property.getSingleUserQPS(),property.getDiffLimit(),property.getErrorDropRate());
    }

    public boolean request(User user){
        sketch.request(user);
        boolean isBlock = sketch.isBlock(user);
        return isBlock;
    }

    public void syncDropTable(IBinder binder){
        sketch.syncDropTable(binder);
    }
}
