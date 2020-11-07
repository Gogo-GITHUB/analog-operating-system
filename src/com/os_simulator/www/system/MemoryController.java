package com.os_simulator.www.system;

import java.util.LinkedList;
import java.util.List;

/**
 * 内存空间的管理类
 */
public class MemoryController {

    private List<MemoryBlock> memoryBlocks = new LinkedList<>();
    /**
     * 维护一个内存块队列
     * @param m
     *
     */
    public void addNewBlock(MemoryBlock m){
        memoryBlocks.add(m);
    }

    public void rmOldBlock(MemoryBlock m){
        memoryBlocks.remove(m);
    }

    public List<MemoryBlock> getMList(){
        return memoryBlocks;
    }
}
