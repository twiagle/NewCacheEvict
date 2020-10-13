package com.tjut.cacheEvict;

import com.tjut.cacheEvict.cache.Cache;
import com.tjut.cacheEvict.cache.TmpLRUCache;
import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import com.tjut.cacheEvict.feature.FeatureLib;
import com.tjut.cacheEvict.learning.IncrementalLearn;
import com.tjut.cacheEvict.sample.ILSampleLib;
import com.tjut.cacheEvict.sample.PreStudySampleLib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tb
 * @date 6/29/20-3:44 PM
 */
//cd jar's directory : java -cp  CacheEvict-1.0-SNAPSHOT.jar com.tjut.cacheEvict.Simulation ../test.properties
public class Test {

    public static void main(String[] args) {
        List<Integer> expiredObj = new ArrayList<>();
        for (Integer integer : expiredObj) {
            System.out.println(integer);
        }
    }

}
