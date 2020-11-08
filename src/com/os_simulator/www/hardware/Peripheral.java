package com.os_simulator.www.hardware;

/**

 */
class Peripheral extends HardDevice{
    //被占用倒计时（计时周期暂定为0.1秒；由CPU决定，CPU周期必须为计时周期的整数倍）
    int interval;
    public Peripheral(String name){
        this.name = name;
        reset();
    }

    /**
     * 申请占用设备time个计时周期
     * @param time
     */
    public void allocate(int time){
        if (status!=127) {
            status = 127;
            this.interval = time;
        }
    }

    /**
     * 更新设备状态，若之前挂中断状态，取消之；若被占用，扣除clock个计时周期（每CPU周期开始时执行）
     * @param clock
     */
    public void refresh(int clock){
        if (status==-1)
            status=0;
        if (interval>0) {
            interval -= clock;
            if (interval<=0) {
                status=-1;
                interval=0;
            }
        }
    }

    /**
     * 重置设备的计时器和状态
     */
    public void reset(){
        interval=0;
        status=0;
    }
}
