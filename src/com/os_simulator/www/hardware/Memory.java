package com.os_simulator.www.hardware;

/**

 */
class Memory extends HardDevice{
    private byte[] storage;//内存中的信息
    private int maxCapacity;//最大容量
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
        }//越界
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
