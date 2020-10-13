package com.tjut.cacheEvict.learning;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.FeatureLib;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.sample.AbstractSampleLib;
import com.tjut.cacheEvict.sample.ILSampleLib;
import com.tjut.cacheEvict.sample.TrainingSample;
import com.tjut.cacheEvict.sample.Utils;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import moa.classifiers.meta.LearnNSE;
import moa.streams.CachedInstancesStream;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tb
 * @date 7/1/20-9:53 PM
 */
public class IncrementalLearn {
    static IncrementalLearn incrementalLearn = new IncrementalLearn();
    static LearnNSE learnNSE;

    private IncrementalLearn() {
        learnNSE = new LearnNSE();
        learnNSE.resetLearningImpl();
    }

    public static List<Long> evict(Request req, long[] evictCandidates){
        ILSampleLib.addSubscribedObjs(req, evictCandidates);
        return getPredictExpireObjs(req, evictCandidates);
    }

    private static List<Long> getPredictExpireObjs(Request req, long[] evictCandidates) {
        List<Long> expireObjs = new LinkedList<>();
        FeatureLib featureLib = FeatureLib.getInstance();
        for (long objID : evictCandidates) {
            TrainingSample trainingSample = new TrainingSample(featureLib.getFeature(objID), objID, req.getReqTimeStamp(), AbstractSampleLib.OUT_OF_BB);
            double[] prediction = learnNSE.getVotesForInstance(Utils.generateInstance(trainingSample));
            int t = moa.core.Utils.maxIndex(prediction);
            // same as arff {0, 1}
            if (moa.core.Utils.maxIndex(prediction) == AbstractSampleLib.OUT_OF_BB) {
                expireObjs.add(objID);
            }
        }
        return expireObjs;
    }

    public static void generateFirstBaseClassifier(Instance[] instances){
        selfUpdate(instances);
    }

    public static void selfUpdate(Instance[] instances){
        int i = 0;
        for (Instance instance : instances) {
            if (i < Config.getInstance().getTrainingInterval()) {
                learnNSE.trainOnInstanceImpl(instance);
                i++;
            }
        }
    }
}
