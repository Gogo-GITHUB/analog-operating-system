package com.os_simulator.www.system;

/**
 * Created by geange on 16-11-20.
 */
public class SimulationThread {

    //自增判定线程的ID
    private static int staticID = 1;
    //传入的OSFile
    private OSFile osFile;
    //阻塞原因
    private String blockReson = "";
    //进程ID
    private int threadID;
    //内部的X的指(默认为0)
    private int threadX = 0;
    //设置关闭标志
    private boolean closeFlag = false;


    /**
     * 系统新建的线程（不用分配ID, 由staticID自增确定ID号）
     * @param osFile
     */
    public SimulationThread(OSFile osFile) {
        this.osFile = osFile;
        this.threadID = staticID++;
    }

    /**
     * 通过ID赋值来启动
     * @param osFile
     * @param threadID
     */
    public SimulationThread(OSFile osFile, int threadID){
        this.osFile = osFile;
        this.threadID = threadID;
    }


    /**
     * 该构造方法通过文件路径找到对应的可执行文件
     * @param fileSystem
     * @param path
     */
    public SimulationThread(FileSystem fileSystem, String path){
        OSFile osFile = getOsFileByPath(fileSystem, path);
        this.osFile = osFile;
        this.threadID = staticID++;
    }


    /**
     * 获取进程ID
     * @return
     */
    public int getThreadID(){
        return threadID;
    }


    /**
     * 获取进程阻塞原因
     * @return
     */
    public String getBlockReson(){
        return blockReson;
    }


    public int getCursor(){
        return osFile.getCursor();
    }


    /**
     * 设置osfile将游标移动到下一行
     */
    private void setNextLine(){
        osFile.setCursor();
    }





    /**
     * 获取当前游标指向的那一条指令
     * (提示：应该检测线程是否已经执行完毕后再调用获取指令的方法)
     * @return
     */
    public String getCursorCommand(){
        String command = osFile.getIndexCommand(osFile.getCursor());
        //游标自增
        setNextLine();
        return command;
    }


    public void setCursorBack(){
        osFile.setCursorBack();
    }



    /**
     * 判断文件是否到达最后
     * @return
     */
    public boolean isEnd(){
        return osFile.isEndofFile();
    }


    /**
     * 设置阻塞原因
     * @param reson
     */
    public void setBlockReson(String reson){
        blockReson = reson;
    }

    /**
     * 设置X的值
     * @return
     */
    public int getThreadX() {
        return threadX;
    }

    /**
     * 返回X的值
     * @param threadX
     */
    public void setThreadX(int threadX) {
        this.threadX = threadX;
    }


    /**
     * 获取关闭标志(默认为false,不关闭)
     * @return
     */
    public boolean getCloseFlag() {
        return closeFlag;
    }

    /**
     * 设置关闭标志
     * @param closeFlag
     */
    public void setCloseFlag(boolean closeFlag) {
        this.closeFlag = closeFlag;
    }

    /**
     * 通过路径获取OSFile文件
     * @param filePath
     * @return
     */
    private OSFile getOsFileByPath(FileSystem fileSystem, String filePath){
        OSFile osFile = new OSFile();
        byte[] fileContent = fileSystem.readFile(filePath, 10000);
        fileSystem.closeFile(filePath);
        //当文件内容不为空
        if (fileContent.length != 0){
            for (byte num : fileContent){
                osFile.addOneLine(OSFile.byteToString(num));
            }
        }
        return osFile;
    }
}
