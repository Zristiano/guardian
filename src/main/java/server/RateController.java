package server;

import model.SketchProperty;
import utils.GdLog;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class RateController {

    private static class InstanceHolder{
        private static RateController SINGLETON = new RateController();
    }

    public static RateController getInstance(){
        return InstanceHolder.SINGLETON;
    }

    private SketchProperty sketchProperty;

    private volatile ConcurrentHashMap<String, ClientSketch> sketchMap;

    private RateController(){
        init();
    }

    private void init(){
        // TODO: 2019/11/22 it supposed to parse the parameters from a meta-data file (e.g. xml, yaml)
        sketchProperty = new SketchProperty(100000,50,10, 0.1);
        sketchMap = new ConcurrentHashMap<>();
    }

    public SketchProperty getSketchProperty() {
        return sketchProperty;
    }

    public int[][] assembleSketch(String client, int[][] sketch){
        GdLog.i(Thread.currentThread().toString() + "   "+ client+"   "+Thread.currentThread().getId());
        int[][] sum = new int[sketch.length][sketch[0].length];
        ClientSketch clientSketch = new ClientSketch(client, System.currentTimeMillis(), sketch);
        sketchMap.put(client, clientSketch);
        for(String c : sketchMap.keySet()){
            ClientSketch currentClientSketch = sketchMap.get(c);
            if (System.currentTimeMillis() - currentClientSketch.ts>1500) continue;
            int[][] currentSketch = currentClientSketch.sketch;
            for (int i = 0; i < sketch.length; i++) {
                for (int j = 0; j < sketch[0].length; j++) {
                    sum[i][j] += currentSketch[i][j];
                }
            }
        }
        return sum;
    }

    private class ClientSketch{
        private int[][] sketch;
        private long ts;
        private String client;
        ClientSketch(String client, long ts, int[][] sketch){
            this.client = client;
            this.ts = ts;
            this.sketch = sketch;
        }
    }


}
