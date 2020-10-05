package com.tjut.cacheEvict.cache;

import com.tjut.cacheEvict.config.Config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author tb
 * @date 7/5/20-4:04 PM
 */
public class TmpLRUCache<K,V> extends LinkedHashMap<K,V> {
    public int getCacheCapacity() {
        return cacheCapacity;
    }

    //定义缓存的容量
    private int cacheCapacity;
    //带参数的构造器
    public TmpLRUCache(int objectNum, int cacheCapacity){
        //调用LinkedHashMap的构造器，传入以下参数
        super(objectNum,0.75f,true);
        this.cacheCapacity = cacheCapacity;
    }

    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest){
        return cacheCapacity > Config.getInstance().getMaxCacheSize();
    }

    @Override
    public V put(K key, V value) {
        cacheCapacity += (int)value;
        return super.put(key, value);
    }
}
