package com.tjut.cacheEvict;

import com.tjut.cacheEvict.cache.Cache;
import com.tjut.cacheEvict.cache.TmpLRUCache;
import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.FeatureLib;
import com.tjut.cacheEvict.sample.ILSampleLib;
import com.tjut.cacheEvict.sample.PreStudySampleLib;
import com.tjut.cacheEvict.learning.IncrementalLearn;
import com.tjut.cacheEvict.sample.Utils;
import com.yahoo.labs.samoa.instances.*;
import moa.streams.ArffFileStream;

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
    public static void main(String[] args) {
        init(args[0]);//指明配置文件路径，这样多个配置可以并行
        try (BufferedReader reader = new BufferedReader(new FileReader(config.getTrainDataFile()))) {
            String line;
            //warming up features
            while(timeStamp < config.getWarmingUpFeaturesNum() && (line = reader.readLine()) != null){
                timeStamp++;
                final Request req = parseRequest(line);
                FeatureLib.getInstance().updateFeatureLib(req, null);
            }
            //generate first classifier,拿到interval个训练样本
            PreStudySampleLib preStudySampleLib = new PreStudySampleLib();
            while(preStudySampleLib.getSamples().size() < config.getTrainingInterval() && (line = reader.readLine()) != null){
                timeStamp++;
                final Request req = parseRequest(line);
                //对过期或者再次访问对象，根据旧特征，拿到其真实标签就可以作为样本
                preStudySampleLib.generateSamples(req);
                FeatureLib.getInstance().updateFeatureLib(req, null);
            }
            Instance[] instances = Utils.generateInstances(preStudySampleLib.getSamples());
            preStudySampleLib = null; // help GC
            IncrementalLearn.generateFirstBaseClassifier(instances);

            //start prediction and model self-update
            cache = Cache.getInstance();
            int totalReq = 0;
            int hit = 0;
            while ((line = reader.readLine()) != null) {
                timeStamp++;
                totalReq++;
                final Request req = parseRequest(line);
                ILSampleLib.generateSamples(req);
                FeatureLib.getInstance().updateFeatureLib(req, cache);
                if (cache.contains(req.getObjID())) {
                    hit++;
                }else{
                    cache.put(req);
                }
                if (timeStamp % 100000 == 0) {
                    System.out.println("timeStamp" + timeStamp);
                }
            }
            System.out.printf("hit ratio is %f", (float)hit / totalReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void init(String propertyFile){
        config = Config.ConfigInstance(propertyFile);// this should be first
    }

    static Request parseRequest(String line) {
        Request req = new Request();
        String[] trace = line.split(" "); // ts id size type
        req.setReqTimeStamp(timeStamp);   //这个可以用trace[0] 看哪个效果好就行
        req.setObjID(Long.parseLong(trace[1]));
        req.setSize(Integer.parseInt(trace[2]));
//        if(trace.length == 4){
//            req.setType(Integer.parseInt(trace[3]));
//        }else{ //有的数据集没有这个
//            req.setType(0);
//        }
        return req;
    }
}
