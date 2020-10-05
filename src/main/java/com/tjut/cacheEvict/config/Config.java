package com.tjut.cacheEvict.config;

import java.io.*;
import java.util.Properties;

/**
 * @author tb
 * @date 6/29/20-3:57 PM
 */
public class Config {
    private Properties properties;
    private String trainDataFile;
    private String outputFolder;
    private int trainingInterval;
    private int beladyBoundry;
    private int sampleNum;
    private int maxCacheSize;
    private int featureNum;
    private int warmingUpFeaturesNum;
    private static Config config;

    private Config(){};
    //get args from main(args[0])
    public static Config ConfigInstance(String configFile){
        if(configFile == null || "".equals(configFile)) {
            throw new IllegalArgumentException("config does not contain the key ");
        }

        if (config == null) {
            config = new Config();
            config.properties = new Properties();
            try(BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(configFile))){
                config.properties.load(fileInputStream);
                config.trainDataFile = config.properties.getProperty("trainDataFile");//数据集
                config.outputFolder = config.properties.getProperty("outputFolder");//输出试验统计数据
                config.trainingInterval = Integer.parseInt(config.properties.getProperty("trainingInterval"));//重训练上样本数阈值
                config.maxCacheSize = Integer.parseInt(config.properties.getProperty("maxCacheSize"));//变量
                config.beladyBoundry = Integer.parseInt(config.properties.getProperty("beladyBoundry"));//定值和cache对应
                config.sampleNum = Integer.parseInt(config.properties.getProperty("sampleNum"));//驱逐时采样预测数量
                config.featureNum = Integer.parseInt(config.properties.getProperty("featureNum"));//gap特征数量不含size和type，代码里写死，两个都要
                String t = config.properties.getProperty("warmingUpFeaturesNum");
                config.warmingUpFeaturesNum = Integer.parseInt(t);//gap特征数量不含size和type，代码里写死，两个都要
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;

    }
    public static Config getInstance(){
        if(config == null){
            throw new IllegalArgumentException("Must Init first");
        }
        return config;
    }

    public String getTrainDataFile() {
        return trainDataFile;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public int getTrainingInterval() {
        return trainingInterval;
    }

    public int getBeladyBoundry() {
        return beladyBoundry;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public int getSampleNum() {
        return sampleNum;
    }
    public int getFeatureNum() {
        return featureNum;
    }

    public long getWarmingUpFeaturesNum() {
        return warmingUpFeaturesNum;
    }
}
