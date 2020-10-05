package com.tjut.cacheEvict.sample;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.FeatureLib;
import com.tjut.cacheEvict.feature.Feature;
import java.util.*;

/**
 * @author tb
 * @date 7/3/20-1:07 PM
 * listen for all objects
 * only put each obj once into this lab to avoid popular bias
 */
public class PreStudySampleLib extends AbstractSampleLib {
    public PreStudySampleLib(){}

    public void generateSamples(Request req){
        int id = req.getObjID();
        FeatureLib featureLib = FeatureLib.getInstance();
        //expire window
        List<Integer> expiredObject = featureLib.getExpiredObject(req);
        if(expiredObject != null && expiredObject.size() > 0){
            for (Integer objID : expiredObject) {
                TrainingSample ts = new TrainingSample(featureLib.getFeature(objID), objID, req.getReqTimeStamp(), 0);
                labeledFeatureLib.put(objID, ts);//expired obj
            }
        }
        //预训练时，每个对象只要在BB之内在第二次及以后出现就把特征和标签输出作为样本,也就是featurelib中有就要做
        Feature f = featureLib.getFeature(id);
        if(f != null){
            TrainingSample ts = new TrainingSample(f,id,req.getReqTimeStamp(),1);
            labeledFeatureLib.put(id, ts);//mark as generated
        }
    }
}
