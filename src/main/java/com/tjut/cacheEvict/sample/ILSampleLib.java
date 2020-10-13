package com.tjut.cacheEvict.sample;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.Feature;
import com.tjut.cacheEvict.feature.FeatureLib;
import com.tjut.cacheEvict.learning.IncrementalLearn;

import java.util.HashMap;
import java.util.List;

/**
 * @author tb
 * @date 7/3/20-9:40 PM
 * listen for subscribed obj by IncrmnLearn
 */
public class ILSampleLib implements AbstractSampleLib {
    private static HashMap<Long, TrainingSample> labeledFeatureLib;
    private static HashMap<Long, TrainingSample> subscribedFeatureLib;
    private static ILSampleLib ilSampleLib = new ILSampleLib();

    private ILSampleLib() {
        int sampleNum = Config.getInstance().getSampleNum();
        labeledFeatureLib = new HashMap<>(sampleNum);
        subscribedFeatureLib = new HashMap<>(sampleNum);
    }

    public static void clear(){
        labeledFeatureLib.clear();
    }

    //保存起来当前的特征，不需要保留预测值，因为LearnNSE更新模型的时候会一样的特征再算一遍得到相同的结果
    public static void addSubscribedObjs(Request req, long[] subscribedObj) {
        for (long objID : subscribedObj) {
            FeatureLib featureLib = FeatureLib.getInstance();
            TrainingSample trainingSample = new TrainingSample(featureLib.getFeature(objID), objID, req.getReqTimeStamp(), AbstractSampleLib.OUT_OF_BB);
            subscribedFeatureLib.put(objID, trainingSample);
        }
    }

    public static void generateSamples(Request req) {
        long id = req.getObjID();
        FeatureLib featureLib = FeatureLib.getInstance();
        List<Long> expiredObject = featureLib.getExpiredObject(req);
        TrainingSample ts;
        // add expired
        for (Long objID : expiredObject) {
            if (null != (ts = subscribedFeatureLib.get(objID))) {
                ts.setLabel(OUT_OF_BB);
                labeledFeatureLib.put(objID, ts);
                subscribedFeatureLib.remove(objID);
            }
        }
        // add subscribed,过期的就remove掉,所以不会再进来
        if (null != (ts = subscribedFeatureLib.get(id))) {
            ts.setLabel(WITH_IN_BB);
            labeledFeatureLib.put(id, ts);
            subscribedFeatureLib.remove(id);
        }
        // IL update
        if (labeledFeatureLib.size() >= Config.getInstance().getTrainingInterval()) {
            IncrementalLearn.selfUpdate(Utils.generateInstances(labeledFeatureLib));
            clear();
        }
    }
}
