package server;

import model.SketchProperty;

public class RateController {

    private static class InstanceHolder{
        private static RateController SINGLETON = new RateController();
    }

    public static RateController getInstance(){
        return InstanceHolder.SINGLETON;
    }

    private SketchProperty sketchProperty;

    private RateController(){
        init();
    }

    private void init(){
        // TODO: 2019/11/22 it supposed to parse the parameters from a meta-data file (e.g. xml, yaml)
        sketchProperty = new SketchProperty(1000,50,10, 0.001);
    }

    public SketchProperty getSketchProperty() {
        return sketchProperty;
    }
}
