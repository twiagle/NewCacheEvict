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
    private int[] trainingSample;

    //SampleLib !contains, and put an unlabeled training sample into lib, the feature is done but waiting for label
    //invoked when reused or expired
    public TrainingSample(Feature feature, int objID, long curTimeStamp, int label){
        int len = feature.getTotalFeatureNum() + 2;
        trainingSample = new int[len]; // 比常规存储特征多一个最后一次访问间隔，多一个标签
        trainingSample[len - 2] = (int) (curTimeStamp - feature.getLastTimeStamp());// 一定有这个体征
        System.arraycopy(feature.getFeatures(),0,trainingSample,0,feature.getTotalFeatureNum());
        // 1 超过BB会被驱逐， 0 BB之内
        trainingSample[len - 1] = label;
    }

    public int[] getTrainingSample() {
        return trainingSample;
    }
}
