package com.tjut.cacheEvict.sample;

import com.tjut.cacheEvict.config.Config;
import com.yahoo.labs.samoa.instances.*;
import scala.Int;

import java.util.*;

public class Utils {
   public static Instance[] generateInstances(List<TrainingSample> samples) {
      Instance[] instances = new Instance[Config.getInstance().getTrainingInterval()];
      int i = 0;
      for (TrainingSample sample : samples) {
         if (i < Config.getInstance().getTrainingInterval()) { // protect not overflow
            instances[i++] = wrapper(sample);
         }
      }
      return instances;
   }

   public static Instance[] generateInstances(Map<Long, TrainingSample> samples) {
      List<TrainingSample> trainingSamples = new ArrayList<>(samples.values());
      return generateInstances(trainingSamples);
   }

   public static Instance generateInstance(TrainingSample sample) {
      return wrapper(sample);
   }

   static Instance wrapper(TrainingSample sample) {
      InstancesHeader header = Config.getInstance().getInstancesHeader();
      double[] s = Arrays.stream(sample.getTrainingSample()).mapToDouble(Int::int2double).toArray();
      InstanceData data = new DenseInstanceData(s); // 1-7,type,size,lastgap,label
      return new InstanceImpl(data, header);
   }
}
