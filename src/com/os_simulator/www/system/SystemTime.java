package com.os_simulator.www.system;

/**

 * 系统时间片线程
 */
public class SystemTime extends Thread{
    OSTimeCore time;

    public SystemTime(OSTimeCore time) {
        this.time = time;
    }


    public void run() {
        while (true){
            synchronized (time){
                long cTime = System.currentTimeMillis();
                try {
                    time.setTime();//时间片加一
//                    System.out.println("系统时间:"+time.getTime());
                    time.wait(1000 - System.currentTimeMillis() + cTime);
                    time.notify();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取当前系统时间
     * @return
     */
    public int getSystemTime(){
        return time.getTime();
    }
}
