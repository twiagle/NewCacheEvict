package com.tjut.cacheEvict.cache;

import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.learning.IncrementalLearn;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author tb
 * @date 7/1/20-1:35 PM
 * cache size should smaller than FeatureLib, if sampled objs cannot find in FeatureLib are out of Belady boundry, just remove
 */
public class Cache{
    //singleton
    private static Cache cache;
    //id, size
    private int sampleNum;
    private long maxCacheSize;
    private long usedCacheSize;//usedCacheSize
    //overall single cache map
    private HashMap<Integer, Integer> map;

    private Cache(TmpLRUCache<Integer,Integer> tmpLRUCache){
//        this.map = new HashMap<>(map);// LinkedHashMap -> hashMap, invoke frequent resize()

        map = new HashMap<>(tmpLRUCache.getCacheCapacity(), 0.75f);
        for (Map.Entry<Integer, Integer> e : tmpLRUCache.entrySet()) {
            int key = e.getKey();
            int value = e.getValue();
            map.put(key,value);
        }

        cache.maxCacheSize = Config.getInstance().getMaxCacheSize();
        cache.sampleNum = Config.getInstance().getSampleNum();
        cache.usedCacheSize = 0;
    }

    public static Cache ConfigInstance(TmpLRUCache<Integer,Integer> tmpLRUCache){
        if(cache == null){
            cache = new Cache(tmpLRUCache);
        }
        return cache;
    }

    public static Cache getInstance(){
        if(cache == null){
            throw new IllegalArgumentException("Must Init first");
        }
        return cache;
    }

    //randomly get 64 keys from keySet
    public int[] getSampledKeys(){
        Random generator = new Random();
        Integer[] keySet = (Integer[])map.keySet().toArray();
        int[] sampledKeys = new int[cache.sampleNum];
        for (int i = 0; i < 64; i++) {
            sampledKeys[i] = keySet[generator.nextInt(keySet.length)];
        }
        return sampledKeys;
    }
    public boolean contains(Integer key){
        return map.containsKey(key);
    }
    public int getObjectSize(Integer key){
        return map.get(key);
    }
    public void remove(Integer key){
        map.remove(key);
    }
    public void remove(Integer[] keys){
        for (Integer key : keys) {
            remove(key);
        }
    }

    public void put(Integer key, Integer size){
        usedCacheSize += size;
        while(usedCacheSize > maxCacheSize){
            int evictedSize = IncrementalLearn.evict(getSampledKeys());
            usedCacheSize -= evictedSize;
        }
        map.put(key, size);
    }
}
