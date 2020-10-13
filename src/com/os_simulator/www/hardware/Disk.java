package com.os_simulator.www.hardware;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Demkors Infinity (iDemkors) in 2016/9/7
 * iDemkors允许您自由参考并引用此源代码之内容。如有疑问，请咨询iDemkors或大神。
 * iDemkors promises you can have a view of the source code and/or use it freely.
 * If having any question, contact iDemkors or God.
 *
 * 【硬盘声明】硬盘为最底层硬件，需“分区表”协助记录，设想为“分区表”继承此硬盘类/“分区表”构造时传参硬盘
 * 【特定状态】【101】未从实际硬盘读数据 【102】未保存至实际硬盘 【105】在实际硬盘初始化失败
 */
class Disk extends HardDevice{
    private byte[] storage;
    private String filename = "disk.dat";
    private int maxCapacity;
    private int sectorSize;
    private int sectorCount;
    Disk(String name){
        this.name = name;
        maxCapacity = 16384;//最大容量
        sectorSize = 64;//块容量
        sectorCount = 256;//块数
        storage = new byte[maxCapacity];
        boolean hasError = false;
        //自纠错式加载虚拟硬盘数据，如无数据，新建数据文件
        //首先试图加载已有数据
        try {load();}
        catch (Exception e){hasError=true;}
        if (hasError){
            //若未能加载（无该文件），则新建数据文件，此时硬盘为空
            try {initialize();}
            catch (Exception e){
                // 仍然失败，则退出后不会保存数据
            }
        }
    }

    /**
     * 从磁盘读取第address块的64字节
     * @param address
     * @return 该块的内容
     */
    public byte[] read(int address){
        status=0;
        byte[] temp;
        try {
            temp = Arrays.copyOfRange(storage,address*sectorSize,(address+1)*sectorSize);//将address块内容载入
        }
        catch (Exception e){
            status=2;//标记状态
            temp=new byte[sectorSize];
            Arrays.fill(temp, (byte) 0);//返回全为0的块
        }
        return temp;
    }

    /**
     * 将64字节数据data写入磁盘的第address块
     * @param data
     * @param address
     */
    public void write(byte[] data,int address){
        if(data.length!=sectorSize){//写入的数据超过一个块的最大容量
            status=1; return;
        }
        if(address<0||address>=sectorCount){//非法地址
            status=2; return;
        }
        int index = address*sectorSize;
        System.arraycopy(data, 0, storage, index, sectorSize);
        status=0;
        try {
            store();
        }
        catch (IOException e){
            System.err.println("Disk cannot be stored truly");
        }
    }

    /**
     * 从实际硬盘中尝试加载filename指定的虚拟硬盘，并读取数据（构造方法用）
     * @throws IOException
     */
    private void load() throws IOException {
        File diskfile = new File(filename);
        status=101;
        FileInputStream in = new FileInputStream(diskfile);
        int count = in.read(storage);//将disk.dat里的文件读入
        in.close();
        if(count==maxCapacity) status=0;
    }

    /**
     * （若未找到虚拟硬盘文件）建立filename指定的文件，初始为空（构造方法用）
     * @throws IOException
     */
    private void initialize() throws IOException {
        File diskfile = new File(filename);
        status=105;
        diskfile.createNewFile();
        status=0;
    }

    /**
     * 将暂存于内存的硬盘数据存入实际硬盘（退出时调用）
     * @throws IOException
     */
    public void store() throws IOException {
        File diskfile = new File(filename);
        status=102;
        FileOutputStream out = new FileOutputStream(diskfile);
        out.write(storage);//存入disk.dat文件
        out.close();
        status=0;
    }

    /**
     * 获取硬盘最大容量
     * @return 最大容量（字节）
     */
    public int getMaxCapacity(){
        return maxCapacity;
    }
    public int getSectorSize() {
        return sectorSize;
    }
    public int getSectorCount(){
        return sectorCount;
    }
}
