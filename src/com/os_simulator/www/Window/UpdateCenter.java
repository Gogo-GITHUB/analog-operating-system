package com.os_simulator.www.Window;



/**

 */

//更新模块，更新窗口状态
public class UpdateCenter implements Runnable{

    private Controller controller;//获取controller
    private long time;//一帧时间

    public UpdateCenter(Controller controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        while (true){
            String status = "===========UpdateCenter===========\n";
            long start = System.currentTimeMillis();//一帧开始
            if (time < 1000) {
                try {
                    Thread.sleep(1000 - time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
                //窗口遍历，调用每个窗口的update方法
            controller.getLock().lock();
            for (Win win : controller.getWinHashMap().values()) {
                try {
                    if (win.isUpdate) {
                        win.update();
                        status += (win.getName() + " update success\n");//记录
                    }
                } catch (Exception e) {
                    status += (" ERROR-> " + win.getName() + " update fail\n");//记录
                } finally {
                    continue;
                }
            }
            controller.getLock().unlock();
            status += "==================================\n";
            System.out.println(status);//输出状态信息
            time = System.currentTimeMillis() - start;//一帧结束，获取一帧时长
        }
    }

}

