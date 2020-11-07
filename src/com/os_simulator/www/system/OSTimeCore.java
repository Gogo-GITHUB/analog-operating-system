package com.os_simulator.www.system;

/**
 * 系统时间保存
 */
public class OSTimeCore {
    private volatile int time = 0;

    public int getTime() {
        return time;
    }

    public void setTime() {
        this.time++;
    }
}
