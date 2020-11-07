package com.os_simulator.www.system;

import com.os_simulator.www.hardware.CPU;
import com.os_simulator.www.system.software.Compiler;
import com.os_simulator.www.system.software.Shell;

import java.util.List;

/**
 * 操作系统的内核

 */
public class SystemCore {
    //系统时间
    private OSTimeCore osTimeCore = new OSTimeCore();

    private SystemTime systemTime;

    private ExcuteTask excuteTask;//进程管理
    private FileSystem fileSystem;//文件管理
    private CPU cpu;//cpu管理
    private Shell shell;

    public SystemCore(){

        cpu = new CPU();
        fileSystem = new FileSystem(cpu);
        systemTime = new SystemTime(osTimeCore);
        excuteTask = new ExcuteTask(osTimeCore, new Compiler(), cpu);
        shell = new Shell(fileSystem, excuteTask);

        systemTime.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        excuteTask.start();
    }





    /**
     * 检查设备返回值并确定设备是否被占用
     * @param number
     * @return
     */
    private boolean isUsed(Byte number){
        if (number != null){
            if (number == 0){
                return false;
            }
        }
        return true;
    }

    //==========================================================
    //==========================================================
    //=========================getter===========================
    //==========================================================
    //==========================================================
    public OSTimeCore getOsTimeCore() {
        return osTimeCore;
    }

//    public SystemTime getSystemTime() {
//        return systemTime;
//    }

    public ExcuteTask getExcuteTask() {
        return excuteTask;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public CPU getCpu() {
        return cpu;
    }

    public Shell getShell() {
        return shell;
    }

    //==========================================================
    /**
     * 下面是通过SystemCore对象获取相应的对象
     *
     */
    //==========================================================

    /**
     * ==================================================================
     * ==================================================================   获取时间
     * ==================================================================
     */

    /**
     * 获取当前系统时间
     * @return
     */
    public int getSystemTime(){
        return systemTime.getSystemTime();
    }

    /**
     * 获取当前剩余的时间片
     * @return
     */
    public int getRemainTimeBlock(){
        return excuteTask.getRemainTimeBlock();
    }

    /**
     * 获取当前的执行的指令
     * @return
     */
    public String getExecuteCommand(){
        return excuteTask.getExecuteCommand();
    }

    /**
     * ==================================================================
     * ==================================================================
     * ==================================================================
     */


    /**
     * ==================================================================
     * ==================================================================    获取进程队列相应的方法
     * ==================================================================
     */


    /**
     * 获取当前正在运行的线程的ID
     * @return
     */
    public int getRunningID(){
        return excuteTask.getRunningID();
    }

    /**
     * 获取就绪队列的IDs
     * @return
     */
    public List<Integer> getReadyID(){
        return excuteTask.getReadyID();
    }

    /**
     * 获取阻塞队列的IDs
     * @return
     */
    public List<Integer> getWaitID(){
        return excuteTask.getWaitID();
    }

    /**
     * 返回等待设备的进程
     * |-------------------------------------|
     * |设备名称规范                         |
     * |    A                                |
     * |    B                                |
     * |    C                                |
     * |example :getWaitDevicesList("A")     |
     * |-------------------------------------|
     *
     * @param devicesName
     * @return
     */
    public List<Integer> getWaitDevicesList(String devicesName){
        return excuteTask.getWaitDevicesList(devicesName);
    }


    /**
     * ==================================================================
     * ==================================================================     启动关闭程序,进程相关
     * ==================================================================
     */

    /**
     * 获取所有演示可执行文件的路径
     * @return 记录路径的List
     */
    public List<String> getSamplePaths(){
        return fileSystem.getSamplePaths();
    }

    /**
     * 通过文件目录启动程序,返回值是进程的ID
     * @param filePath
     * @return
     */
    public int startApplication(String filePath){
        return excuteTask.startApplication(filePath, fileSystem);
    }

    /**
     * 关闭应用
     *
     * 通过进程的ID关闭线程,返回true则进程被标记关闭,返回false表示当前线程已经关闭,无法继续执行关闭(关闭窗口即可)
     * (原理:进程会被标记关闭,不是立刻关闭,如果正好是当前时间片执行的线程,立刻关闭,如果不是,则等到该进程的时间片再关闭)
     * @param threadID
     * @return
     */
    public boolean closeApplication(int threadID){
        return excuteTask.closeApplication(threadID);
    }

    /**
     * 判断当前线程是否仍在运行---------------------[未实现]
     * @param threadID
     * @return
     */
    public boolean isRunning(int threadID){
        return true;
    }


    /**
     * 获取进程的X的值,
     * (当返回值为-1的时候则线程不在就绪队列或者阻塞队列中, 可能还没有启动或者已经停止了)
     * @param threadID
     * @return
     */
    public int getX(int threadID){
        return excuteTask.getX(threadID);
    }



    /**
     * ==================================================================
     * ==================================================================   用户命令接口(终端的方法)
     * ==================================================================
     */

    /**
     * String command为指令, 返回值为系统执行后的返回值
     * @param command
     * @return
     */
    public String executeShellCommand(String command){
        return shell.executeCommand(command);
    }




    /**
     * ==================================================================
     * ==================================================================   获取硬件信息
     * ==================================================================
     */


    /**
     * 获取设备被占用的状态,不占用返回false,占用返回true
     * ------------------------------------
     * String devicesName为设备名称
     *=====================================|
     *分别为:                               |
     *      A1,A2                          |
     *      B1,B2,B3                       |
     *      C1,C2,C3                       |
     *=====================================|
     * @param devicesName
     */
    public boolean getDevicesStatus(String devicesName){
        switch (devicesName){
            case "A1":
                return isUsed(cpu.getStatus("PeripheralA:0"));
            case "A2":
                return isUsed(cpu.getStatus("PeripheralA:1"));
            case "A3":
                return isUsed(cpu.getStatus("PeripheralA:2"));
            case "B1":
                return isUsed(cpu.getStatus("PeripheralB:0"));
            case "B2":
                return isUsed(cpu.getStatus("PeripheralB:1"));
            case "C1":
                return isUsed(cpu.getStatus("PeripheralC:0"));
            case "C2":
                return isUsed(cpu.getStatus("PeripheralC:1"));
            case "C3":
                return isUsed(cpu.getStatus("PeripheralC:2"));
            default:
                return true;
        }
    }

    /**
     * 获取内存最大容量
     * @return
     */
    public int getMemoryCapacity(){
        return cpu.getMemoryCapacity();
    }

    /**
     * 获取当前内存被使用的情况
     * @return
     */
    public int getMemoryUsed(){
        int sum = 0;
        List<MemoryBlock> mBlocks = excuteTask.getMemoryController();
        for (MemoryBlock mBlock : mBlocks){
            sum += mBlock.getSize();
        }
        return sum;
    }

    /**
     * 获取内存的细节
     * @return
     */
    public List<MemoryBlock> getMemDetail(){
        return excuteTask.getMemDetail();
    }

    /**
     * 获取硬盘最大容量
     * @return
     */
    public int getDiskCapacity(){
        return cpu.getDiskCapacity();
    }

    /**
     * 获取当前硬盘被使用的情况 [未实现相应的功能, 仅返回100作为模拟]
     * @return
     */
    public int getDiskUsed(){
        return 100;
    }

    /**
     * 返回磁盘的使用情况, 256个块的占用情况, 使用就是true, 不使用就是false
     * @return
     */
    public List<Boolean> getDiskBlocks(){
        List<Boolean> diskBlocks = fileSystem.getFAT();
        if (diskBlocks.size() != 256){
            for (int i = 0; i < 256 - diskBlocks.size(); i++){
                diskBlocks.add(true);
            }
        }
        return diskBlocks;
    }


    /**
     * 返回系统的全局目录
     * @return
     */
    public String getDiskDir(){
        String dir = fileSystem.getJsonDirTree();
        if (dir != null){
            return dir;
        }
        return "";
    }

    /**
     * ==================================================================
     * ==================================================================
     * ==================================================================
     */

    public static void main(String[] args){

    }
}
