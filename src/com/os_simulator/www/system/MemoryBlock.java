package com.os_simulator.www.system;

/**
 * Created by geange on 16-11-27.
 */
public class MemoryBlock {
    private int size;
    private int threadID;

    public MemoryBlock(int size, int threadID) {
        this.size = size;
        this.threadID = threadID;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getThreadID() {
        return threadID;
    }

    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }
}
