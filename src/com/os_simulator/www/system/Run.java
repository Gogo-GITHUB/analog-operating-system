package com.os_simulator.www.system;

import com.os_simulator.www.hardware.CPU;
import com.os_simulator.www.system.software.Compiler;

import java.util.List;

/**
 * Created by geange on 16-11-20.
 *
 * 测试ExcuteTask和SystemTime
 */
public class Run {
    public static void main(String[] args){
//        OSTimeCore osTimeCore = new OSTimeCore();
//        SystemTime systemTime = new SystemTime(osTimeCore);

//        CPU cpu = new CPU();

        SystemCore systemCore = new SystemCore();




//        ExcuteTask excuteTask = new ExcuteTask(osTimeCore, new Compiler(), cpu);
//        systemTime.start();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> apps = systemCore.getSamplePaths();
                for (String path : apps){
                    systemCore.startApplication(path);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                System.out.println(systemCore.startApplication("/usr/000.e"));
//                System.out.println(systemCore.getDiskDir());
//                while (true){
//                    if (systemCore.closeApplication(5)){
//                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>线程5被关闭");
//                        break;
//                    }
//                }
                for (MemoryBlock m : systemCore.getMemDetail())
                    System.err.println("log--" + m.getSize() + "  " + m.getThreadID());
                System.out.println(systemCore.executeShellCommand("create /usr/0a0.t"));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(systemCore.executeShellCommand("delete /usr/0a0.t"));
                System.out.println(systemCore.getDiskDir());
            }
        });
        thread.start();

        Thread stopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true){
                    if (systemCore.closeApplication(5)){
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>线程5被关闭");
                        break;
                    }
                }


            }
        });
//        excuteTask.start();
    }
}
