package com.tjut.cacheEvict;

import com.tjut.cacheEvict.cache.Cache;
import com.tjut.cacheEvict.cache.TmpLRUCache;
import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.FeatureLib;
import com.tjut.cacheEvict.sample.ILSampleLib;
import com.tjut.cacheEvict.sample.PreStudySampleLib;
import com.tjut.cacheEvict.learning.IncrementalLearn;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author tb
 * @date 6/29/20-3:44 PM
 */
//cd jar's directory : java -cp  CacheEvict-1.0-SNAPSHOT.jar com.tjut.cacheEvict.Simulation ../test.properties
public class Simulation {
    static long timeStamp = 0;
    static Config config;//不同进程使用不同config
    static Cache cache;//真正用于统计的lru
    static TmpLRUCache<Integer,Integer> tmpLRU;
    public static void main(String[] args) {
        init(args[0]);//指明配置文件路径，这样多个配置可以并行

        PreStudySampleLib preStudySampleLib = new PreStudySampleLib();
        try (BufferedReader reader = new BufferedReader(new FileReader(config.getTrainDataFile()))) {
            String line;
            //warming up features
            while(timeStamp < config.getWarmingUpFeaturesNum()
            && (line = reader.readLine()) != null){
                timeStamp++;
                /*sparse request*/
                final Request req = parseRequest(line);
                /*updateFeature*/
                FeatureLib.getInstance().updateFeatureLib(req);
            }

            //generate first classifier,拿到interval个训练样本
            while(preStudySampleLib.getLabeledFeatureLib().size() < config.getTrainingInterval()
            && (line = reader.readLine()) != null){
                timeStamp++;
                /*sparse request*/
                final Request req = parseRequest(line);
                //对过期或者再次访问对象，根据旧特征，拿到其真实标签就可以作为样本
                preStudySampleLib.generateSamples(req);
                /*updateFeature*/
                FeatureLib.getInstance().updateFeatureLib(req);
                /* put in cache*/
//                tmpLRU.put(req.getObjID(), req.getSize());// k - v ,here size simulate v
            }
            IncrementalLearn.generateFirstBaseClassifier(preStudySampleLib);
            preStudySampleLib = null; // help GC

            //start prediction and model self-update
            cache = Cache.ConfigInstance(tmpLRU);
            tmpLRU.clear();
            int totalReq = 0;
            int hit = 0;

            //cache
            while ((line = reader.readLine()) != null) {
                timeStamp++;
                totalReq++;
                /*sparse request*/
                final Request req = parseRequest(line);
                /*AOP listening*/
                ILSampleLib.generateSamples(req);
                /*updateFeature*/
                FeatureLib.getInstance().updateFeatureLib(req);
                /* put in cache*/
                if (cache.contains(req.getObjID())) {
                    hit++;
                }else{
                    cache.put(req.getObjID(), req.getSize());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void init(String propertyFile){
        config = Config.ConfigInstance(propertyFile);// this should be first
//        tmpLRU = new TmpLRUCache<>(1024,Config.getInstance().getMaxCacheSize());
    }

    static Request parseRequest(String line) {
        Request req = new Request();
        String[] trace = line.split(" "); // ts id size type
        req.setReqTimeStamp(timeStamp);   //这个可以用trace[0] 看哪个效果好就行
        req.setObjID(Integer.parseInt(trace[1]));
        req.setSize(Integer.parseInt(trace[2]));
        if(trace.length == 4){
            req.setType(Integer.parseInt(trace[3]));
        }else{ //有的数据集没有这个
            req.setType(0);
        }
        return req;
    }
}
