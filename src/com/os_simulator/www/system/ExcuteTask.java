package com.os_simulator.www.system;

import com.os_simulator.www.hardware.CPU;
import com.os_simulator.www.system.software.Compiler;

import java.util.*;

/**
 * Created by geange on 16-11-17.
 * 执行线程，执行可执行文件并进行调度
 */
public class ExcuteTask extends Thread{
    private CPU cpu;
    private Compiler compiler;

    //系统剩余时间片
    private int remainTimeBlock = 0;

    //正在运行的指令
    private String executeCommand = "";

    //内存控制器
    private MemoryController memoryController = new MemoryController();

    //系统自建的空闲线程
    private OSFile freeosFile = new OSFile();

    //系统时间
    private OSTimeCore time;

    //就绪队列
    private Queue<SimulationThread> ReadyThreads = new LinkedList<>();

    //阻塞队列
    private LinkedList<SimulationThread> BlockThreads = new LinkedList<>();

    //设备状态表
    private Map<String, Byte> devicesStatusMap = new HashMap<>();


    public ExcuteTask(OSTimeCore time, Compiler compiler, CPU cpu) {
        this.time = time;
        this.compiler = compiler;
        this.cpu = cpu;

        //提交设备状态表
        devicesStatusMap.put("PeripheralA", (byte) 0);
        devicesStatusMap.put("PeripheralB", (byte) 0);
        devicesStatusMap.put("PeripheralC", (byte) 0);

    }

    //运行次数
    private int ctimes = 0;

    @Override
    public void run() {
        while (true){
            synchronized (time){
                long cTime = System.currentTimeMillis();
                try {
                    time.wait();
                    //执行可执行文件
                    System.out.println("Run方法里的time："+ctimes +"  remainTimeBlock="+ (6 - ctimes));
                    //设定剩余时间片
                    remainTimeBlock =   6 - ctimes;
                    //当就绪队列中线程数量不为空
                    if (ReadyThreads.size() != 0){
                        SimulationThread thread = ReadyThreads.peek();
                        //当线程ID不为0
                        if (thread.getThreadID() != 0){
                            execute(ReadyThreads.peek(), ctimes++);//将就绪队列队头的第一个元素运行
                            if (ctimes > 6)
                                ctimes = 0;
                        }
                    }else {
                        //当进程数量为0,剩余时间片置空
                        remainTimeBlock = 0;
                        //将执行的指令置空
                        executeCommand = "";
                        //更新设备
                        if (cpu != null)
                            flashDevices();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 如果内存在系统不存在,申请内存使用,如果存在,获取申请的内存的块所在的对象
     * @param thread
     */
    private void applyMemory(SimulationThread thread){
        MemoryBlock mBlock = new MemoryBlock(100, thread.getThreadID());
        System.err.println(">>>>>153.向申请内存块<<<<<");
        memoryController.addNewBlock(mBlock);
    }

    /**
     * 线程关闭后释放内存
     * @param thread
     */
    private void freeMemory(SimulationThread thread){
        MemoryBlock memoryBlock = null;
        for (MemoryBlock m : memoryController.getMList()){//遍历内存块，找出线程占用的内存
            if (m.getThreadID() == thread.getThreadID()){
                System.out.println(">>>>>释放内存: 线程ID"+thread.getThreadID()+"<<<<<");
                memoryBlock = m;
                break;
            }
        }
        memoryController.getMList().remove(memoryBlock);
    }

    /**
     * 传入需要执行的SimulationThread，执行6个时间片
     * @param thread
     * @param times
     */
    private void execute(SimulationThread thread, int times){
        if (thread != null){
            MemoryBlock mBlock = null;
//            applyMemory(mBlock, thread);
            if (times < 6){
                //当线程还没有执行完，打印数据；
                // 如果执行完了，抛掉执行完成的线程，则重置计数器，让下一个线程继续执行
                if (!thread.isEnd()){
                    //当进程的关闭标志位为false时继续执行
                    if (!thread.getCloseFlag()){
                        //进行string跟byte的转换
                        String strCommand = thread.getCursorCommand();
                        //编译,获取指令的byte格式
                        byte command = compiler.stringToBinary(strCommand);
                        //更新设备
                        if (cpu != null)
                            flashDevices();
                        //cpu执行当前指令，检查CPU返回状态
                        char[] ASM = cpu.execute(command);
                        //设置当前线程的x
                        thread.setThreadX(cpu.getX());
                        //写入当前执行的指令
                        executeCommand = String.valueOf(ASM);
                        System.out.println(String.valueOf(ASM));
                        //调用设备的指令

                        //输出打印
                        System.err.println("线程"+thread.getThreadID() + "   " +"执行到第"+thread.getCursor());

                        System.out.println(strCommand);
                        if (thread.isEnd()){
                            System.out.println("================在6个时间片之前结束================");
                            freeMemory(ReadyThreads.poll());

                            ctimes = 0;
                        }

                        if (ASM[0] == '!'){//没有获取到设备，被阻塞了
                            System.err.println(">>>>>>>>>>>>>>>>>>>>设备"+ASM[1]);
                            if (checkDevicesStatus(thread, ASM[1])){
                                System.err.println("-------------------------->>>>>>>>"+ "线程" +thread.getThreadID() +"加入阻塞队列<<<<<<<<--------------------------");
                            }
                            //如果不发生阻塞，则继续运行，并将运行设备的ID写入到设备使用列表;


                        }else {

                        }
                    }else {
                        //当进程的关闭标志为true(即请求关闭的状态)
                        //就绪进程的队列抛出该进程(即停止执行)
                        freeMemory(ReadyThreads.poll());
//                        ReadyThreads.poll();
                    }
                }
            }else {
                //当一个线程执行了六个时间片后把它放到就绪线程最后
                if (!thread.isEnd()){
                    ReadyThreads.add(thread);
                    memoryController.rmOldBlock(mBlock);
                }
                ReadyThreads.poll();
            }
        }else {
            System.out.println("当前没有运行的线程");
        }
    }


    /**
     * 刷新硬件及检查设备状态
     */
    private void flashDevices(){
        //刷新硬件
        cpu.refreshPeripheral("PeripheralA");
        cpu.refreshPeripheral("PeripheralB");
        cpu.refreshPeripheral("PeripheralC");
        //读取硬件，刷新状态
        byte devicesA = cpu.getPeripheralStatus("PeripheralA");
        devicesStatusMap.replace("PeripheralA", devicesA);

        byte devicesB = cpu.getPeripheralStatus("PeripheralB");
        devicesStatusMap.replace("PeripheralB", devicesB);

        byte devicesC = cpu.getPeripheralStatus("PeripheralC");
        devicesStatusMap.replace("PeripheralC", devicesC);


        if (devicesA == -1){//唤醒阻塞队列的进程
            awakeSleepThread("PeripheralA");
        }
        if (devicesB == -1){
            awakeSleepThread("PeripheralB");
        }
        if (devicesC == -1){
            awakeSleepThread("PeripheralC");
        }
    }

    private int[] devicesUSE = new int[8];


    /**
     * 唤醒阻塞队列的线程
     */
    private static List<SimulationThread> threads = new ArrayList<>();
    private void awakeSleepThread(final String awakeReason){
        for (SimulationThread t : BlockThreads){//找出对应阻塞原因的线程
            if (t.getBlockReson().equals(awakeReason)){
                System.err.println("||===========================>> "+t.getThreadID()+"离开阻塞线程 <<===========================||");
                threads.add(t);
            }
        }
        for (SimulationThread t: threads){//将他们移到就绪队列
            ReadyThreads.add(t);
            BlockThreads.remove(t);
        }
        //清空队列
        threads.clear();
    }


    /**
     * 当SimulationThread调用设备失败，转入阻塞队列，返回true
     * @param thread
     * @param devices
     * @return
     */
    private boolean checkDevicesStatus(SimulationThread thread, char devices){
        byte devicesName = devicesStatusMap.get("Peripheral"+devices);
        if (devicesName == 127){
            //设置游标返回上一条
            thread.setCursorBack();
            //设置其阻塞原因
            thread.setBlockReson("Peripheral"+devices);
            //放入阻塞队列
            BlockThreads.add(ReadyThreads.poll());
            //错误打印
//            System.err.println("-------------------------->>>>>>>>加入阻塞队列<<<<<<<<--------------------------");
            return true;
        }
        return false;
    }


    //=====================================================================================
    //=====================================================================================
    //===================================供外界调用的方法====================================
    //=====================================================================================
    //=====================================================================================

    /**
     * 给就绪队列添加线程
     * @param t
     */
    public void addThread(SimulationThread t){
        applyMemory(t);
        ReadyThreads.add(t);
//        memoryController.addNewBlock(new MemoryBlock(100, t.getThreadID()));
    }

    /**
     * 获取当前正在运行的线程的ID
     * @return
     */
    public int getRunningID(){
        if (ReadyThreads.size() != 0)
            return ReadyThreads.peek().getThreadID();
        else
            return 0;
    }

    /**
     * 获取就绪队列的IDs
     * @return
     */
    public List<Integer> getReadyID(){
        List<Integer> readyList = new ArrayList<>();
        Iterator iterator = ReadyThreads.iterator();
        //跳过运行那个线程
        if (iterator.hasNext())
            iterator.next();
        while (iterator.hasNext()){
            SimulationThread t = (SimulationThread) iterator.next();
            readyList.add(t.getThreadID());
        }
        return readyList;
    }

    /**
     * 获取阻塞队列的IDs
     * @return
     */
    public List<Integer> getWaitID(){
        List<Integer> waitingList = new ArrayList<>();
        Iterator iterator = BlockThreads.iterator();
        while (iterator.hasNext()){
            SimulationThread t = (SimulationThread) iterator.next();
            waitingList.add(t.getThreadID());
        }
        return waitingList;
    }

    /**
     * 获取内存控制单元的链表
     * @return
     */
    public List<MemoryBlock> getMemoryController(){
        return memoryController.getMList();
    }


    /**
     * 获取当前剩余的时间片
     * @return
     */
    public int getRemainTimeBlock(){
        return remainTimeBlock;
    }

    /**
     * 获取当前的执行的指令
     * @return
     */
    public String getExecuteCommand(){
        return executeCommand;
    }

    /**
     * ============================================================================================================================
     * ============================================================================================================================
     * ============================================================================================================================
     */
    /**
     * 返回相应设备的等待进程
     * @param devicesName
     * @return
     */
    public List<Integer> getWaitDevicesList(String devicesName){
        return getList(devicesName);
    }
    /**
     * 1.检查每个进程的阻塞原因是否等同输入的reason====================>>>(隶属于getWaitDevicesList)
     * @param reason
     * @return
     */
    private boolean equalThreadBlockReason(SimulationThread thread, String reason){
        return thread.getBlockReson().equals("Peripheral"+reason);
    }
    /**
     * 2.遍历阻塞队列,返回相应的List====================>>>(隶属于getWaitDevicesList)
     * @param devicesName
     * @return
     */
    private List<Integer> getList(String devicesName){
        List<Integer> list = new ArrayList<>();
        for (SimulationThread thread : BlockThreads){
            if (equalThreadBlockReason(thread, devicesName)){
                list.add(thread.getThreadID());
            }
        }
        return list;
    }
    /**
     * ============================================================================================================================
     * ============================================================================================================================
     * ============================================================================================================================
     */

    /**
     * 添加需要运行的进程
     * @param path
     * @param fileSystem
     * @return
     */
    public int startApplication(String path, FileSystem fileSystem){
        SimulationThread thread = new SimulationThread(fileSystem, path);
        addThread(thread);
        return thread.getThreadID();
    }

    /**
     * 关闭线程
     * @param threadID
     * @return
     */
    public boolean closeApplication(int threadID){
        //标志位,>0表示找到
        int flag = 0;
        for (SimulationThread t : ReadyThreads){//遍历查找就绪队列
            if (t.getThreadID() == threadID){
                t.setCloseFlag(true);
                flag++;
            }
        }
        if (flag == 0){
            for (SimulationThread t : BlockThreads){//遍历查找阻塞队列
                if (t.getThreadID() == threadID){
                    t.setCloseFlag(true);
                    flag++;
                }
            }
        }
        //全部遍历后没有找到该进程(说明该进程已经关闭)
        if (flag == 0){
            return false;
        }
        return true;
    }

    /**
     * 获取进程的X对应的值
     * @param threadID
     * @return
     */
    public int getX(int threadID){
        int flag = -1000;
        int threadX = flag;
        for (SimulationThread t : ReadyThreads){
            if (t.getThreadID() == threadID){
                threadX = t.getThreadX();
            }
        }
        if (threadX == flag){
            for (SimulationThread t : BlockThreads){
                if (t.getThreadID() == threadID){
                    threadX = t.getThreadX();
                }
            }
        }
        return threadX;
    }

    /**
     * 返回内存详细情况
     * @return
     */
    public List<MemoryBlock> getMemDetail(){
        System.out.println("log--"+"memoryController.getMList().size()--"+memoryController.getMList().size());
        return memoryController.getMList();
    }


}
