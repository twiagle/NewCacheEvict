package com.tjut.cacheEvict.sample;

import java.util.*;

/**
 * @author tb
 * @date 7/3/20-1:00 PM
 */
public abstract class AbstractSampleLib {
    static HashMap<Integer,TrainingSample> labeledFeatureLib = new HashMap<>();
//    abstract void generateSamples(Request req);

    public HashMap<Integer, TrainingSample> getLabeledFeatureLib() {
        return labeledFeatureLib;
    }
    public static void clear(){
        labeledFeatureLib.clear();
    }
}
