package com.os_simulator.www.system;

/**
 * 内存块类，具体被分配的线程id的属性和大小
 */
public class MemoryBlock {
    private int size;
    private int threadID;

    public MemoryBlock(int size, int threadID) {
        this.size = size;
        this.threadID = threadID;
    }

    public int getSize() {
        return 100;
//        return size;
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
