package com.os_simulator.www.system;

/**
 * Created by geange on 16-11-17.
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
