package com.tjut.cacheEvict.feature;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;

import java.util.Arrays;

/**
 * @author tb
 * @date 7/1/20-2:00 PM
 *
 * @date 7/2/30-4:38 PM
 * Feature stores
 */
public class Feature {
    private int objID;
    private int size;
    private int type;
    private CycQueue cycQueue;

    private long lastTimeStamp;
    private int deltaNum;
    private int totalFeatureNum;

    public Feature(Request req) { // 第一次访问同一个对象时不初始化特征向量
        lastTimeStamp = req.getReqTimeStamp();
        objID = req.getObjID();
        size = req.getSize();
        type = req.getType();
        deltaNum = Config.getInstance().getFeatureNum(); // 初始化循环队列时就决定，以后可以逐渐增大，需要确定LearnNse代码是否支持空特征，或把不足的在学习之前手动添加无穷
        totalFeatureNum = deltaNum + 2;//size + type 用于确定特征数组下标
    }

    public void updateFeature(Request req){
        if(cycQueue == null){ // 第二次访问同一个对象时调用
            cycQueue = new CycQueue(deltaNum);
        }
        // 窗口期内第2次及以后访问同一个对象时，记录delta时间差
        cycQueue.enQueue((int) (req.getReqTimeStamp() - lastTimeStamp));
        lastTimeStamp = req.getReqTimeStamp();
    }

    public int[] getFeatures(){
        int[] features = new int[totalFeatureNum];// delta size type
        if (cycQueue == null) {
            Arrays.fill(features, Integer.MAX_VALUE);// initial MAX_VALUE for those interval are not available
        } else {
            System.arraycopy(cycQueue.getDeltas(),0, features, 0, deltaNum);
        }
        features[totalFeatureNum-2] = size;
        features[totalFeatureNum-1] = type;
        return features;
    }

    public int getTotalFeatureNum() {
        return totalFeatureNum;
    }

    public int getObjID() {
        return objID;
    }

    public int getSize() {
        return size;
    }

    public int getType() {
        return type;
    }

    public CycQueue getCycQueue() {
        return cycQueue;
    }

    public long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public int getDeltaNum() {
        return deltaNum;
    }
}
