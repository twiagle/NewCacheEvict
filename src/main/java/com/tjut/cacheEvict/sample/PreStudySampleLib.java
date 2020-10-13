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
public class PreStudySampleLib implements AbstractSampleLib{
    List<TrainingSample> labeledFeatureLib;

    public PreStudySampleLib(){
        labeledFeatureLib = new ArrayList<>(Config.getInstance().getTrainingInterval());
    }

    public void generateSamples(Request req){
        long id = req.getObjID();
        boolean flag = false;
        FeatureLib featureLib = FeatureLib.getInstance();
        //expire window
        List<Long> expiredObject = featureLib.getExpiredObject(req);
        if(expiredObject != null && expiredObject.size() > 0){
            for (long objID : expiredObject) {
                if (objID == id) {
                    flag = true;
                }
                TrainingSample ts = new TrainingSample(featureLib.getFeature(objID), objID, req.getReqTimeStamp(), OUT_OF_BB);
                labeledFeatureLib.add(ts);//expired obj
            }
        }
        //不能在此干预FeatureLib的更迭，自己标识处理
        if (!flag) {
            //预训练时，每个对象只要在BB之内在第二次及以后出现就把特征和标签输出作为样本,也就是featurelib中有就要做
            Feature f = featureLib.getFeature(id);
            if(f != null){
                TrainingSample ts = new TrainingSample(f,id,req.getReqTimeStamp(), WITH_IN_BB);
                labeledFeatureLib.add(ts);//mark as generated
            }
        }
    }

    public List<TrainingSample> getSamples() {
        return labeledFeatureLib;
    }
}
