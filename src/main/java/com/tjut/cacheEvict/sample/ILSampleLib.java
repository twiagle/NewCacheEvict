package com.tjut.cacheEvict.sample;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.Feature;
import com.tjut.cacheEvict.feature.FeatureLib;
import com.tjut.cacheEvict.learning.IncrementalLearn;

import java.util.List;

/**
 * @author tb
 * @date 7/3/20-9:40 PM
 * listen for subscribed obj by IncrmnLearn
 */
public class ILSampleLib extends AbstractSampleLib {
    private static ILSampleLib ilSampleLib = new ILSampleLib();

    public static ILSampleLib getInstance() {
        return ilSampleLib;
    }

    public static void addSubscribedObj(int[] subscribedObj) {
        for (int objID : subscribedObj) {
            Feature feature = FeatureLib.getInstance().getFeature(objID);
            labeledFeatureLib.put(objID, null);
        }
    }

    public static void generateSamples(Request req) {
        int id = req.getObjID();
        FeatureLib featureLib = FeatureLib.getInstance();
        //expire window
        List<Integer> expiredObject = FeatureLib.getInstance().getExpiredObject(req);
        if(expiredObject != null && expiredObject.size()>0){
            for (Integer objID : expiredObject) {
                Feature feature = FeatureLib.getInstance().getFeature(objID);
                if(labeledFeatureLib.containsKey(objID)){
                    TrainingSample ts = new TrainingSample(featureLib.getFeature(objID), objID,  req.getReqTimeStamp(), 1);
                    labeledFeatureLib.put(objID, ts);//expired obj
                }
            }
        }

        //add subscribed
        int objID = req.getObjID();
        if(labeledFeatureLib.containsKey(objID)){
        }else{//generate feature for each obj once
            Feature feature = FeatureLib.getInstance().getFeature(req.getObjID());
            TrainingSample ts = new TrainingSample(featureLib.getFeature(objID), objID,  req.getReqTimeStamp(), 1);
            labeledFeatureLib.put(req.getObjID(), ts);//mark as generated
        }

        /*publish to IncrementalLearn, and start to accumulate samples for next round*/
        if(labeledFeatureLib.size() >= Config.getInstance().getTrainingInterval()){
            IncrementalLearn.selfUpdate();
            ILSampleLib.clear();
        }
    }
}
