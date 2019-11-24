package server;

import model.SketchProperty;

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

    private ConcurrentHashMap<String, int[][]> sketchMap;

    private RateController(){
        init();
    }

    private void init(){
        // TODO: 2019/11/22 it supposed to parse the parameters from a meta-data file (e.g. xml, yaml)
        sketchProperty = new SketchProperty(1000,50,10, 0.001);
        sketchMap = new ConcurrentHashMap<String, int[][]>();
    }

    public SketchProperty getSketchProperty() {
        return sketchProperty;
    }

    public int[][] assembleSketch(String client, int[][] sketch){
        int[][] sum = new int[sketch.length][sketch[0].length];
        sketchMap.put(client, sketch);
        ArrayList<int[][]> sketchList = new ArrayList<int[][]>(sketchMap.values());
        for (int[][] currentSketch : sketchList) {
            for (int i = 0; i < sketch.length; i++) {
                for (int j = 0; j < sketch[0].length; j++) {
                    sum[i][j] += currentSketch[i][j];
                }
            }
        }
        return sum;
    }
}
