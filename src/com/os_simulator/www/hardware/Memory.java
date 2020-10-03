package com.os_simulator.www.hardware;

/**
 * Created by Demkors Infinity (iDemkors) in 2016/9/6
 * iDemkors允许您自由参考并引用此源代码之内容。如有疑问，请咨询iDemkors或大神。
 * iDemkors promises you can have a view of the source code and/or use it freely.
 *   If having any question, contact iDemkors or God.
 */
class Memory extends HardDevice{
    private byte[] storage;
    private int maxCapacity;
    public Memory(String name){
        this.name = name;
        //内存容量暂定为2KB
        maxCapacity = 2048;
        storage = new byte[maxCapacity];
        status = 0;
    }

    /**
     * 从内存的address地址读一字节
     * @param address
     * @return 该地址的内容
     */
    public byte read(int address){
        if (address<0||address>=2048){
            status=2; return -1;
        }
        status=0;
        return storage[address];
    }

    /**
     * 将一字节data写入内存的Address地址
     * @param data
     * @param address
     */
    public void write(byte data,int address){
        if (address<0||address>=2048){
            status=2; return;
        }
        status=0;
        storage[address] = data;
    }

    /**
     * 获取内存最大容量
     * @return 最大容量（字节）
     */
    public int getMaxCapacity(){
        return maxCapacity;
    }
}
