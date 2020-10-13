package com.tjut.cacheEvict.cache;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.learning.IncrementalLearn;
import org.apache.maven.wagon.Streams;

import javax.swing.*;
import java.util.*;

/**
 * @author tb
 * @date 7/1/20-1:35 PM
 * cache size should smaller than FeatureLib, if sampled objs cannot find in FeatureLib are out of Belady boundry, just remove
 */
public class Cache{
    private static Cache cache;
    private int sampleNum;
    private long maxCacheSize;
    private long usedCacheSize;//usedCacheSize
    private HashMap<Long, Integer> map;

    private Cache(long maxCacheSize, int sampleNum){
        this.maxCacheSize = maxCacheSize;
        this.sampleNum = sampleNum;
        map = new HashMap<>(1024);
    }

    public static Cache getInstance(){
        if(cache == null){
            Config config = Config.getInstance();
            cache = new Cache(config.getMaxCacheSize(), config.getSampleNum());
        }
        return cache;
    }

    //randomly get 64 keys from keySet
    public long[] getSampledKeys(){
        Random generator = new Random();
        long[] keys = map.keySet().stream().mapToLong(Long::longValue).toArray();
        long[] sampledKeys = new long[cache.sampleNum];
        Set<Integer> set = new HashSet<>(cache.sampleNum);
        while (set.size() < cache.sampleNum) { // 0, 1, 2  共3次
            set.add(generator.nextInt(keys.length));
        }
        int i = 0;
        for (int random : set){
            sampledKeys[i++] = keys[random];
        }
        return sampledKeys;
    }

    public boolean contains(long key){
        return map.containsKey(key);
    }

    public int getObjectSize(int key){
        return map.get(key);
    }

    public void remove(long key){
        Integer size = map.remove(key);
        if (size != null) {
            usedCacheSize -= size;
        }
    }

    public void remove(List<Long> keys){
        for (long key : keys) {
            remove(key);
        }
    }

    public void put(Request req) {
        usedCacheSize += req.getSize();
        while (usedCacheSize > maxCacheSize) {
            List<Long> evictedObjs = IncrementalLearn.evict(req, getSampledKeys());
            remove(evictedObjs);
        }
        map.put(req.getObjID(), req.getSize());
    }
}
