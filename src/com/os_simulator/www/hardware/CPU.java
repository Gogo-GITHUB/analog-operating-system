package com.os_simulator.www.hardware;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * 【中央处理器】控制一切硬件的行为，硬件层对接模拟系统
 * 【硬件名称格式】硬盘 Disk:0
 *   内存 Memory:0
 *
*/
public class
CPU {
    private Memory memory;
    private Disk disk;
    private Map<String,Peripheral> peripherals;
    private Map<String,HardDevice> devices;
    private byte x;
    private int interval;
    public CPU(){
        x=0;
        interval=10;
        memory = new Memory("Memory:0");
        disk = new Disk("Disk:0");
        peripherals = new HashMap<>();
        devices = new HashMap<>();
        initializePeripherals();
        devices.put("Memory:0",memory);
        devices.put("Disk:0",disk);
        for (String i : peripherals.keySet()){
            devices.put(i,peripherals.get(i));
        }
    }

    /**
     * 自内存的address地址读取一字节内容
     * 【注意】须检查内存状态，确认是否成功读取（0为正常）
     * @param address
     * @return 该地址的内容
     */
    public byte readMemory(int address){
        return memory.read(address);
    }

    /**
     * 将一字节value内容写入内存的address地址
     * 【注意】须检查内存状态，确认是否写入（0为正常）
     * @param value
     * @param address
     */
    public void writeMemory(byte value,int address){
        memory.write(value,address);
    }

    /**
     * 自硬盘的第address块读取一块64字节内容
     * 【注意】须检查硬盘状态，确认是否成功读取（0为正常）
     * @param address
     * @return 该地址的内容
     */
    public byte[] readDisk(int address){
        return disk.read(address);
    }

    /**
     * 将一块64字节的内容写入硬盘的第address块
     * 【注意】须检查硬盘状态，确认是否写入（0为正常）
     * @param data
     * @param address
     */
    public void writeDisk(byte[] data,int address){
        disk.write(data,address);
    }

    /**
     * 生成CPU实例时，初始化所需外设
     */
    private void initializePeripherals(){
        for (int i=0;i<3;i++){
            PeripheralA A = new PeripheralA("PeripheralA:"+i);
            peripherals.put("PeripheralA:"+i,A);
        }
        for (int i=0;i<2;i++){
            PeripheralB B = new PeripheralB("PeripheralB:"+i);
            peripherals.put("PeripheralB:"+i,B);
        }
        for (int i=0;i<3;i++){
            PeripheralC C = new PeripheralC("PeripheralC:"+i);
            peripherals.put("PeripheralC:"+i,C);
        }
    }

    /**
     * 取得寄存器AX的值
     * @return AX的值
     */
    public byte getX(){
        return x;
    }

    /**
     * 获取指定识别名称所对应之硬件的状态
     * 【注意】使用精确的、带入口编号的名称（如PeripheralA:0）
     * @param deviceName
     * @return 该硬件的状态
     */
    public Byte getStatus(String deviceName){
        Byte status;
        for (String i: devices.keySet()){
            if (deviceName.equals(i)){
                HardDevice device = devices.get(i);
                status = device.getStatus();
                return status;
            }
        }
        return null;
    }

    /**
     * 获取指定名称的外设之状态
     * 【注意】名称仅指定外设类型（如PeripheralA）
     * 【返回值】若至少一个设备发生中断，返回-1（同时解除其中断状态）；
     *   若无中断且至少一个设备空闲，返回0；若全部占用，返回127
     * @param deviceName
     * @return 视上述情况返回
     */
    public Byte getPeripheralStatus(String deviceName){
        Byte status=127;
        boolean found = false;
        for (String i: peripherals.keySet()){
            if (i.contains(deviceName)){
                found = true;
                Peripheral peripheral = peripherals.get(i);
                byte new_status = peripheral.getStatus();
                if (new_status==-1){
                    status=-1;
                    peripheral.reset();//中断解除
                    return status;
                }
                if (new_status==0){
                    status=0;
                    return status;
                }
            }
        }
        if (found) return status;
        else return null;
    }

    /**
     * 获取内存最大容量
     * @return 内存最大容量
     */
    public int getMemoryCapacity(){
        return memory.getMaxCapacity();
    }

    /**
     * 获取硬盘最大容量
     * @return 硬盘容量
     */
    public int getDiskCapacity(){
        return disk.getMaxCapacity();
    }

    public int getDiskSectorCount(){
        return disk.getSectorCount();
    }
    /**
     * 申请占用设备time个计时周期（现仅由CPU的execute()定期调用）
     * 使用不带编号的名称（如【PeripheralA】）
     * 【注意】新进程占用时，指令执行前后，由系统检查是否已被既有进程占用：
     *   使用getStatus，系统先准备当前状态表，执行后根据回传的汇编命令检查状态表，
     *   若所有入口均为“被占用”，则认为既有进程已占用
     * @param peripheralName
     * @param time
     */
    private void allocatePeripheral(String peripheralName, int time){
        for (String i: peripherals.keySet()){
            if (i.contains(peripheralName)){
                Peripheral peripheral = peripherals.get(i);
                if (peripheral.getStatus()!=127) {
                    peripheral.allocate(time);
                    break;
                }
            }
        }
    }

    /**
     * 更新设备状态，若之前挂中断状态，取消之；
     * 若被占用，扣除clock个计时周期，此时若计时结束，产生中断状态
     * 使用不带编号的名称（如【PeripheralA】）
     * 【注意】更新后由系统另行检查是否发生中断（使用getStatus）；每CPU周期开始时执行
     * @param peripheralName
     */
    public void refreshPeripheral(String peripheralName){
        for (String i: peripherals.keySet()){
            if (i.contains(peripheralName)){
                Peripheral peripheral = peripherals.get(i);
                peripheral.refresh(interval);
            }
        }
    }

    /**
     * 重置一指定外设的状态（如有必要）
     * 【注意】使用精确的、带入口编号的名称（如PeripheralA:0）
     * @param peripheralName
     */
    public void resetPeripheral(String peripheralName){
        for (String i: peripherals.keySet()){
            if (peripheralName.equals(i)){
                Peripheral peripheral = peripherals.get(i);
                peripheral.reset();
            }
        }
    }

    /**
     * 执行自定义可执行文件的一条一字节指令；返回该指令的汇编语言形式（3字节char）
     * @param command
     * @return 3字节汇编命令（如【!A8】）
     */
    public char[] execute(byte command){
        char[] assembly = new char[3];
        if ((command & 0B11110000) == 0){
            //0000xxxx 【X=?】
            x = (byte)(command & 0B00001111);
            assembly[0]='x';
            assembly[1]='=';
            assembly[2]=(char)((x>9)? 'A'-10+x : x+48);
        }
        else if ((command & 0B11110000) == 0B01000000){
            assembly[0]='x';
            if (command%16 == 15){
                //01001111 【X++】
                x--;
                assembly[1] = assembly[2] = '-';
            }
            else {
                //01000001 【X--】
                x++;
                assembly[1] = assembly[2] = '+';
            }
        }
        else if ((command & 0B11000000) == 0B10000000){
            /*
              10xxyyyy 【!xy】
              其中xx为设备类型（A=00 B=01 C=10）
              yyyy为计数时间（十六进制0~9,A~F）
             */
            char type='A';
            switch (command & 0B00110000){
                case 0B00010000:
                    type+=1;
                    break;
                case 0B00100000:
                    type+=2;
                    break;
                default:
                    break;
            }
            assembly[0]='!';
            assembly[1]=type;
            int time = command & 0B00001111;
            assembly[2]=(char)((time>9)?'A'-10+time : time+48);

            String peripheralName = "Peripheral"+type;
            allocatePeripheral(peripheralName,time*interval);
        }
        else if ((command & 0B11000000) == 0B11000000){
            assembly[0]='e';
            assembly[1]='n';
            assembly[2]='d';
        }
        else {
            Arrays.fill(assembly,'?');
        }
        return assembly;
    }
}
