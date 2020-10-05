package com.tjut.cacheEvict.config;

/**
 * @author tb
 * @date 7/2/20-7:30 PM
 */
public class Request {
    long reqTimeStamp;
    int objID;
    int size;
    int type;

    public int getObjID() {
        return objID;
    }

    public void setObjID(int objID) {
        this.objID = objID;
    }

    public long getReqTimeStamp() {
        return reqTimeStamp;
    }

    public void setReqTimeStamp(long reqTimeStamp) {
        this.reqTimeStamp = reqTimeStamp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
