package com.tjut.cacheEvict.feature;

import com.tjut.cacheEvict.config.Config;

import java.util.Arrays;

/**
 * @author tb
 * @date 7/22/19-3:19 PM
 *
 * @date 7/2/30-4:38 PM
 * each array element of arr stores interval length between two requests of an identical object
 * rear is latest, head is far
 */
public class CycQueue {//
        private int[] arr;//previous Request sequence
        private int front;//头指针，若队列不为空，指向队头元素
        private int rear; //尾指针，若队列不为空，指向队列尾元素的下一个位置
        private int maxSize;//power of 2 to support & (maxSize -1)
        private int deltaNum;//power of 2 to support & (maxSize -1)
        private int beladyBoundry;

    public CycQueue(int deltaNum) { // e.g. features are delta 1 2 3 4 5 6 7
        this.beladyBoundry = Config.getInstance().getBeladyBoundry();
        this.deltaNum = deltaNum; // 7
        this.maxSize = deltaNum + 1;// so 8
        arr = new int[this.maxSize];// 循环队列最大8 7+black
        Arrays.fill(arr, beladyBoundry);// initial MAX_VALUE for those interval are not available
        front = rear = 0;
    }

    public CycQueue copy(CycQueue c) {
        CycQueue cycQueue = new CycQueue(this.deltaNum);
        cycQueue.arr = Arrays.copyOf(arr, arr.length);
        beladyBoundry = c.beladyBoundry;
        return cycQueue;
    }

    //入队前判满
    public void enQueue(int e) {
        //队列头指针在队尾指针的下一位位置上  说明满了 override it!
        if (((rear+1)&(maxSize-1)) == front) {
            front = (front + 1) & (maxSize-1);;
        }
        arr[rear] = e;
        rear = (rear + 1) & (maxSize-1);
    }

    public CycQueue destroyQueue() {
        rear = front = 0;
        arr = null;
        return this;
    }

    //get delta 1 to delta 7 feature, still can optimize x2 by ArraysCopy head to size
    public int[] getDeltas(){
        int[] deltas = new int[deltaNum];
        for (int i = 1; i <= deltaNum; i++) { // 1-7
            deltas[i-1] = getLastElement(i);
        }
        return deltas;
    }

    //rear store nothing, so lastWhat should >= 1  这里可以优化，首先front完全没用，一直到length次就行，如果是0，就赋给MAX，不知道一开始为啥弄个这么复杂。放屁，怎么能逆向拷贝呢，这就是唯一办法
    private int getLastElement(int lastWhat) {
        assert lastWhat > 0 ;
        if (lastWhat > queueLength()) {
//                return Integer.MAX_VALUE;//default value
            return beladyBoundry;//default value
        }
        return arr[(rear - lastWhat + maxSize) & (maxSize-1)];
    }

    private Integer queueLength() {
        return (rear - front + maxSize) & (maxSize-1); //求环形队列的元素个数 1-7
    }
}
