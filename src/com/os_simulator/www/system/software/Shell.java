package com.os_simulator.www.system.software;

import com.os_simulator.www.system.ExcuteTask;
import com.os_simulator.www.system.FileSystem;
import com.os_simulator.www.system.OSFile;

/**
 * Created by geange on 16-11-24.
 * 用户命令接口，用来执行用户输入的指令并返回相应的数据
 *
 *
 */
public class Shell {

    private String helpList = "\n=================================\n"+
            "1.create : create /usr/tes.e  [创建一个带空字符的普通文件]\n"+
            "2.delete : delete /usr/tes.e  [删除文件]\n"+
            "3.cat : cat /usr/tes.e  [查看文件的内容]\n"+
            "4.copy : copy /usr/tes.e /drv/tes.e  [复制文件到其他的目录]\n"+
            "5.mv : mv /usr/tes.e /drv/tes.e  [移动文件到其他的目录]\n"+
            "6.echo : echo \"ABC\" > /usr/tes.e  [重定向输入文本到文件中,双引号包含输入内容]\n"+
            "7.mkdir : mkdir /usr/bin  [创建一个目录]\n"+
            "8.rmdir : rmdir /usr/bin  [删除一个目录]\n"+
            "9.exe : exe /usr/000/e  [执行一个可执行的文件]\n"+
            "10.time : time  [查看当前系统的时间片]\n"+
            "11.clear : clear  [清屏]\n"+
            "12.poweroff : poweroff  [关机]\n"+
            "13.system : system  [系统开发者]\n"+
            "14.compile : compile /usr/abc.s [在当前目录下生成编译文件,编译前必须删除以前的编译文件]\n"+
            "15.close : close /usr/005.e  [手动关闭文件]\n"+
            "=================================\n";
    //shell内置编译器
    private Compiler compiler = new Compiler();

    private FileSystem fileSystem;

    private ExcuteTask excuteTask;

    public Shell(FileSystem fileSystem, ExcuteTask excuteTask){
        this.fileSystem = fileSystem;
        this.excuteTask = excuteTask;
    }

    /**
     * 主执行的方法，调用的入口
     * @param command
     * @return
     */
    public String executeCommand(String command){
        String sourceCommand = command;
        command = command.replaceAll(" +", " ");
        String[] commands = command.split(" ");
        switch (commands[0]){
            case "create":
                return create(commands[1]);
            case "delete":
                return delete(commands[1]);
            case "cat":
                return type(commands[1]);
            case "copy":
                if (commands.length != 3){
                    return "指令错误";
                }
                return copy(commands[1], commands[2]);
            case "mv":
                if (commands.length != 3){
                    return "指令错误";
                }
                return xcopy(commands[1], commands[2]);
            case "echo":
                String[] tmps=dealEchoCommand(sourceCommand);
//                if (commands.length == 4 && commands[2].equals(">")){
//                    return echo(commands[3], commands[1]);
//                }
                if (tmps == null){
                    return "指令错误";
                }
                if (tmps.length != 2){
                    return "指令错误";
                }else {
                    return echo(tmps[1], tmps[0]);
                }
            case "mkdir":
                return mkdir(commands[1]);
            case "rmdir":
                return rmdir(commands[1]);
            case "exe":
                if (commands.length != 2){
                    return "指令错误";
                }
                return executeFIle(commands[1]);
            //返回当前系统时间片
            case "time":
                return "";
            //清空当前窗口
            case "clear":
                return "";
            case "poweroff":
                System.exit(0);
                return "";
            case "help":
                return helpList;
            case "compile":
                return compile(sourceCommand);
            case "close":
                return closeFile(commands[1]);

        }
        return "指令错误";
    }

    /**
     * 创建文件,输入的path是目录
     * @param path
     * @return
     */
    private String create(String path){
        if (createFile(path)){
            //向文件写入空字符串
            fileSystem.writeFile(path, "");
            fileSystem.closeFile(path);
            return path+":文件创建成功";
        }else {
            return "创建失败："+fileSystem.getStatus();
        }
    }

    /**
     * 删除文件,输入的path是目录
     * @param path
     * @return
     */
    private String delete(String path){
        if (fileSystem.deleteFile(path)){
            return path+":文件已经删除";
        }else {
            return "删除失败："+fileSystem.getStatus();
        }
    }

    private String closeFile(String path){
        fileSystem.closeFile(path);
        return "尝试关闭文件,"+fileSystem.getStatus();
    }

    /**
     * 显示文件,输入的path是目录
     * @param path
     * @return
     */
    private String type(String path){
        String[] result = fileSystem.readFiletoString(path, 1000);
        //读取完成关闭文件
        fileSystem.closeFile(path);
        if (result.length == 1){
            return result[0];
        }else {
            return "读取内容/目录项失败";
        }
    }

    /**
     * 向文件写入内容
     * @param path
     * @param content
     * @return
     */
    private String echo(String path, String content){
        if (fileSystem.writeFile(path, content)){
            fileSystem.closeFile(path);
            return "文件写入完成";
        }else {
            fileSystem.closeFile(path);
            return "文件写入失败："+fileSystem.getStatus();
        }
    }

    /**
     * 拷贝文件,输入的sourcePath是源目录,aimPath是目标目录
     * @param sourcePath
     * @param aimPath
     * @return
     */
    private String copy(String sourcePath, String aimPath){
        //读取原文件
        String[] result = fileSystem.readFiletoString(sourcePath, 1000);
        //读取后关闭文件
        fileSystem.closeFile(sourcePath);
        //读取文件是否成功
        if (result.length == 1){
            //创建文件是否成功
            if (createFile(aimPath)){
                //写入目标文件
                if (fileSystem.writeFile(aimPath, result[0])){
                    fileSystem.closeFile(aimPath);
                    return "复制文件成功";
                }
                fileSystem.closeFile(aimPath);
            }
        }
        return "复制文件失败："+fileSystem.getStatus();
    }

    /**
     * 移动文件到某一目录下
     * @param sourcePath
     * @param aimPath
     * @return
     */
    private String xcopy(String sourcePath, String aimPath){
        //读取原文件
        String[] result = fileSystem.readFiletoString(sourcePath, 1000);
        //读取后关闭文件
        fileSystem.closeFile(sourcePath);
        //读取文件是否成功
        if (result.length == 1){
            //创建文件是否成功
            if (createFile(aimPath)){
                //写入目标文件
                if (fileSystem.writeFile(aimPath, result[0])){
                    fileSystem.closeFile(aimPath);
                    //移动后删除源文件
                    String deleteStatus = delete(sourcePath);
                    return "移动文件成功（删除情况："+deleteStatus+"）";
                }
                fileSystem.closeFile(aimPath);
            }
        }
        return "移动文件失败："+fileSystem.getStatus();
    }

    /**
     * 建立目录, 输入的path是目录
     * @param path
     * @return
     */
    private String mkdir(String path){
        if (createDir(path)){
            return path+":目录建立成功";
        }else {
            return "目录建立失败";
        }
    }

    /**
     * 删除空目录, 非空不能删除,输入的path是目录
     * @param path
     * @return
     */
    private String rmdir(String path){
        if (fileSystem.deleteEmptyDir(path)){
            return path+":目录已经删除";
        }else {
            return path+":目录不为空";
        }
    }

    /**
     * 在命令行下实现进程的创建
     * @param path
     * @return
     */
    private String executeFIle(String path){
        int threadID = excuteTask.startApplication(path, fileSystem);
        return "创建新进程，ID为："+threadID;
    }

    /**
     * 输入为执行的命令
     * @param command
     * @return
     */
    private String compile(String command){
        command = command.replaceAll(" +", " ");
        String[] coms = command.split(" ");
        if (coms.length == 2 || coms.length == 3){
            if (coms.length == 2){
                String path = coms[1].replace(".s", ".e");
                String[] content = fileSystem.readFiletoString(coms[1], 1000);
                fileSystem.closeFile(coms[1]);
                System.err.println("log--273");
                if (content.length != 0){
                    System.err.println("log--275");
                    String COMMAND = content[0];
                    byte[] nums = compileToByte(COMMAND);
                    if (nums != null){
                        System.err.println("log--279");
                        createFile(path);
                        fileSystem.writeFile(path, nums);
                        fileSystem.closeFile(path);
                        fileSystem.closeFile(path);
                        return path+"编译文件已经生成";
                    }else {
                        return "编译出错";
                    }
                }
            }
            return "log-296--指令出错";

        }else {
            System.err.println("log--286");
            return "指令错误";
        }

    }

    private byte[] compileToByte(String sources){
        String[] source = sources.split(" ");
        OSFile sourceOSFile = new OSFile();
        if (!source[source.length-1].equals("end")){
            System.err.println("log--300");
            //返回指令错误
            return null;
        }else {

            for (int i=0; i<source.length; i++){
                sourceOSFile.addOneLine(source[i]);
            }
            sourceOSFile = compiler.compileToFile(sourceOSFile);
            if(sourceOSFile == null){
                return null;
            }
            int length = sourceOSFile.getLineNum();
            byte[] nums = new byte[length];
            for (int i=0; i<length; i++){
                byte num = compiler.stringToBinary(sourceOSFile.getIndexCommand(i));
                nums[i] = num;
            }
            return nums;
        }

    }


    /**
     * 新建一个普通文件
     * @param filePath
     * @return
     */
    private boolean createFile(String filePath){
        boolean flag = fileSystem.createFile(filePath, (byte) 4);
        System.out.println(fileSystem.getStatus());
        fileSystem.closeFile(filePath);
        return flag;
    }

    /**
     * 创建一个目录
     * @param filePath
     * @return
     */
    private boolean createDir(String filePath){
        boolean flag = fileSystem.createFile(filePath, (byte) 8);
        System.out.println(fileSystem.getStatus());
        fileSystem.closeFile(filePath);
        return flag;
    }

    /**
     *
     * @param command
     * @return
     */
    private String[] dealEchoCommand(String command){
        String commandLocal = "";
        String[] result;
        String content = "";
        String contentWithoutSpace = "";
        String[] saveContent = command.split("\"");
        if (saveContent.length != 3){
            //语句解析错误
            System.err.println("292");
            return null;
        }else {
            content+=saveContent[1];
//            //去除content中的空格，再将saveContent字符串数组合并成
//            contentWithoutSpace += content.replaceAll(" ", "");
            //当第一个字符串不符合规格
            if (!saveContent[0].replaceAll(" +", " ").equals("echo ")){
                System.err.println("300");
                return null;
            }
            String tmp = saveContent[2].replaceAll(" +", " ");
            if (!tmp.startsWith(" > ")){
                System.err.println("305");
                return null;
            }
            //删除第一个空格
            tmp = tmp.replaceFirst(" ", "");
            //去除末尾最后一个空格
            if (tmp.endsWith(" ")){
                char[] t = tmp.toCharArray();
                tmp="";
                for (int i=0; i<t.length-1; i++){
                    tmp+=t[i];
                }
            }
            String[] tmps = tmp.split(" ");
            if (tmps.length != 2){
                System.err.println("log--320");
                System.err.println("log--321"+tmp);
                return null;
            }

            System.err.println("log--"+content + "   "+tmps[1]);

            return new String[]{content, tmps[1]};


        }
        //清除语句中多个连续的空格
//        String commandTmp = command.replaceAll(" +", " ");
//        String[] isEcho = commandTmp.split(" ");
//        if (!isEcho[0].equals("echo")){
//            return null;
//        }else {
//
////            if ()
//        }
//        return new String[]{"", ""};
//        return null;
    }



}
