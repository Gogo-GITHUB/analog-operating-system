package com.os_simulator.www.system;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by geange on 16-11-21.
 */
public class MemoryController {

    private List<MemoryBlock> memoryBlocks = new LinkedList<>();

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
