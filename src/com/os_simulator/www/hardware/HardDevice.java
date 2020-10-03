package com.os_simulator.www.hardware;

/**
 * Created by Demkors Infinity (iDemkors) in 2016/9/7
 * iDemkors允许您自由参考并引用此源代码之内容。如有疑问，请咨询iDemkors或大神。
 * iDemkors promises you can have a view of the source code and/or use it freely.
 * If having any question, contact iDemkors or God.
 */
//【硬件设备】抽象类，默认无任何模拟功能，声明硬件ID、状态及获取方法。必须被除CPU之外的硬件类继承。
class HardDevice {
    /*
    status -- 硬件状态字节
    暂定【0】正常 【1】数据错误 【2】地址错误 【127】设备被占用 【100以上】各硬件特定
     */
    byte status;
    //硬件ID
    byte ID;
    //硬件在系统中的分配名称
    String name;
    HardDevice(){
        //do nothing
    }

    /**
     * 获取此设备的当前状态
     * @return 状态字节
     */
    public byte getStatus(){return status;}

    /**
     * 获取此设备的编号
     * @return 此设备的ID（字节）
     */
    public byte getID(){return ID;}

    /**
     * 获取此设备在系统中分配的名称
     * @return 此设备分配的名称
     */
    public String getName(){return name;}
}
