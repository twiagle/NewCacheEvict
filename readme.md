## bug fix
- 0919-1545 优化为LFB获取特征方式，第一次access一个对象时不应该生成cycleQueue中访问间隔，
因为没有上次时间戳，导致只能减去零值生成无意义的特征间隔，增大了学习的误差，只记录第一次访问时间，这样也不用new出CycleQueue对象，由于75%只来一次所以很大的空间节省。
添加Feature类构造函数，第一次访问只记录时间戳，第二次访问才会新建记录特征的CycleQueue
- 0919-1632 fix 测试环境test.properties作为路径参数相对路径是工程根目录，不要打包到jar中，生产环景配置全部放在一起
- 0919-1712 fix 滑动窗口OOM，迭代器无限初始化导致死循环OOM
- 0919-1938 fix 构建第一个基分类器，结束条件不是读入interval条记录，因为可能出现重复对象导致训练样本不足interval个导致崩溃，而是生成interval个
- 0920-1624 fix 构建第一个基分类器时，过期窗口没起作用，必须先调用getExpiredObject()
- 0920-1747 fix 特征向量拷贝失败, 发现- 0920-1545没有彻底修复，删除第一次新建Feature时feature.updateFeature(request);
- 0920-1825 优化 0920-1545 后引入，特征向量为空时，arrayCopy空指针异常，全部加判断
## 测试观察
```
trainDataFile = ./data/data50.tr
outputFolder = ./out/
trainingInterval = 5
beladyBoundry = 25
maxCacheSize = 100000
sampleNum = 5
featureNum = 7
warmingUpFeaturesNum = 50
```
## bug fix 10.4
* 2249 fix PreStudySampleLib因该是根据featureLib中有没有记录来生成新样本，而不是自己，自己肯定没有第一个
* 2305 fix 循环队列提取特征少了最后一个 改为<=