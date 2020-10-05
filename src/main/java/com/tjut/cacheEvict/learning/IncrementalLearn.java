package com.tjut.cacheEvict.learning;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.sample.AbstractSampleLib;
import com.tjut.cacheEvict.sample.ILSampleLib;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import moa.classifiers.meta.LearnNSE;
import moa.streams.CachedInstancesStream;

/**
 * @author tb
 * @date 7/1/20-9:53 PM
 */
public class IncrementalLearn {

    LearnNSE learnNSE;
    static IncrementalLearn incrementalLearn = new IncrementalLearn();
    static Instances instances;
    static CachedInstancesStream  cachedInstancesStream;

    public static IncrementalLearn getInstance() {
        return incrementalLearn;
    }

    private IncrementalLearn() {
        learnNSE = new LearnNSE();
        learnNSE.resetLearningImpl();
        Attribute[] attribute;
//        =  new Attribute();
//        instances = new Instances("ILCache", attribute, Config.getInstance().getTrainingInterval());
//        instances.setClassIndex(Config.getInstance().getFeatureNum());//index start from 0, so last is label
//        instances.setAttributes();
//        instances. = new ArrayList<Instance>();
//        this.computeAttributesIndices();

//        cachedInstancesStream = new CachedInstancesStream(instances);
    }

    public static int evict(int[] evictCandidates){
        int evictedSize = 0;
        int[] evictedObj = getEvictedObjsByCandidates(evictCandidates);
        ILSampleLib.addSubscribedObj(evictedObj);//subscribe evicted obj
        return evictedSize;
    }

    private static int[] getEvictedObjsByCandidates(int[] evictCandidates) {
        int[] evictedObj = null;

//        getVotesForInstance(Instance inst)
        return evictedObj;
    }

    public static void generateFirstBaseClassifier(AbstractSampleLib sampleLib){

    }
    public static void selfUpdate(){
//        trainOnInstanceImpl(Instance inst)
    }
}
