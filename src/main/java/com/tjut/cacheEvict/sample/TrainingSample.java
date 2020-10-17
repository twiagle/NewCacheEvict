package com.tjut.cacheEvict.sample;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.Feature;
import com.tjut.cacheEvict.feature.FeatureLib;

import java.util.Arrays;

/**
 * @author tb
 * @date 7/3/20-1:15 PM
 */
public class TrainingSample {
    long objID;
    private int[] trainingSample;

    public TrainingSample(Feature feature, long objID, long curTimeStamp, int label){
        this.objID = objID;
        int len = feature.getTotalFeatureNum() + 1;
        trainingSample = new int[len]; // 送入学习的时间点距离最后一次访问的间隔
        System.arraycopy(feature.getFeatures(),0,trainingSample,0,feature.getTotalFeatureNum());
//        trainingSample[len - 2] = (int) (curTimeStamp - feature.getLastTimeStamp());
        trainingSample[len - 1] = label;
    }

    public void setLabel(int label) {
        trainingSample[trainingSample.length - 1] = label;
    }

    public int[] getTrainingSample() {
        return trainingSample;
    }
}
