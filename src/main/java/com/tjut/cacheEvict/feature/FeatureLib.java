package com.tjut.cacheEvict.feature;

import com.tjut.cacheEvict.cache.Cache;
import com.tjut.cacheEvict.config.Config;
import com.tjut.cacheEvict.config.Request;
import java.util.*;

/**
 * @author tb
 * @date 7/3/20-1:36 PM
 * maintain all the feature during the window
 * only maintain feature, ignore label
 */
public class FeatureLib {
    //maintain ID -> Feature
    private final LinkedHashMap<Long, Feature> lib;
    private static FeatureLib featureLib = new FeatureLib();

    private FeatureLib(){
        lib = new LinkedHashMap<>(2<<15, 0.75f,true);
    }

    public static FeatureLib getInstance(){
        return featureLib;
    }

    //must be invoked for each request, after SampleLib filter
    public void updateFeatureLib(Request request, Cache cache) {
        long objID = request.getObjID();
        if (lib.containsKey(objID)) {
            lib.get(objID).updateFeature(request);
        }else {
            Feature feature = new Feature(request);
            lib.put(objID, feature);
        }
        for (Long expireObjID: getExpiredObject(request)) {
            lib.remove(expireObjID);
            if (cache != null) {
                cache.remove(expireObjID);
            }
        }
    }

    public List<Long> getExpiredObject(Request request) {
        long expire = request.getReqTimeStamp() - Config.getInstance().getBeladyBoundry();
        List<Long> expiredObj = new ArrayList<>();
        Iterator<Map.Entry<Long, Feature>> iterator = lib.entrySet().iterator();
        while(true){
            if(iterator.hasNext()){
                Map.Entry<Long, Feature> entry = iterator.next();
                Long expireObjID  = entry.getKey();
                Feature feature = entry.getValue();
                if(feature.getLastTimeStamp() <= expire){
                    expiredObj.add(expireObjID);
                }else{
                    break;
                }
            }
        }
        return expiredObj;
    }
//    public <K, V> Map.Entry<K, V> getHeadByReflection(LinkedHashMap<K, V> map)
//            throws NoSuchFieldException, IllegalAccessException {
//        Field head = map.getClass().getDeclaredField("head");
//        head.setAccessible(true);
//        return (Map.Entry<K, V>) head.get(map);
//    }
//    public <K, V> Map.Entry<K, V> getHead(LinkedHashMap<K, V> map) {
//        return map.entrySet().iterator().next();
//    }
    public Feature getFeature(long objID){
        return lib.get(objID);
    }
}
