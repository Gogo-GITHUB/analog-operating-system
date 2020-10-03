package com.os_simulator.www.system.software;


import com.os_simulator.www.system.OSFile;
import com.os_simulator.www.system.OSFile;

/**
 *
 * Created by geange on 16-9-12.
 */
public class Compiler {

    //寄存器
    String resgister = "AX";
    //操作码
    String[] operation = {"00", "01", "10", "11"};
    //目标
    String[] aim = {"00", "01", "10"};


    //返回null说明编译该行代码出错
    private String dealOneLine(final String oneCommand){



        //去除多余的空格
        String ownCommand = oneCommand.replace(" ", "");

        //操作语句类型
        CalType calType;

        if (ownCommand.contains("=")){
            calType = CalType.ASSIGNMENT;
        }else if (ownCommand.contains("++")){
            calType = CalType.ADD;
        }else if (ownCommand.contains("--")){
            calType = CalType.DEL;
        }else if (ownCommand.contains("!")){
            calType = CalType.USEHARD;
        }else if (ownCommand.equals("end")){
            calType = CalType.FINISH;
        }else {
            calType = CalType.NONE;
        }

        switch (calType){
            case ASSIGNMENT:
                return comFromAssignment(ownCommand);
            case ADD:
                return comFromAdd(ownCommand);
            case DEL:
                return comFromDel(ownCommand);
            case USEHARD:
                return comFromUseHardware(ownCommand);
            case FINISH:
                return "11000000";
            case NONE:
                return null;
        }

        return null;
    }

    //获取赋值语句机器码
    private String comFromAssignment(String commands){
        String machineCode = "";
        String[] tmp = commands.split("=");
        if (tmp.length != 2){
            //编译错误
            return null;
        }else {
            if (!tmp[0].equals(resgister)){
                //编译错误
                return null;
            }else {
                machineCode += ( operation[0] + aim[0] );
            }
            int intTmp = Integer.parseInt(tmp[tmp.length - 1]);
            if (intTmp > 15){
                return null;
            }else {
                String numTmp = Integer.toBinaryString(intTmp).toString();
                //不够四位的补齐四位
                for (int i = 0; i < 4 - numTmp.length(); i++){
                    machineCode += "0";
                }
                machineCode += numTmp;
                return machineCode;
            }
        }
    }

    //获取加法命令的机器码
    private String comFromAdd(String commands){
        String checkresgister = commands.replace("++", "");
        if (checkresgister.equals(resgister)){
            return operation[1] + aim[0] + "0001";
        }else {
            return null;
        }

    }

    //获取减法命令的机器码
    private String comFromDel(String commands){
        String checkresgister = commands.replace("--", "");
        if (checkresgister.equals(resgister)){
            return operation[1] + aim[0] + "1111";
        }else {
            return null;
        }
    }

    //获取调用设备命令的机器码
    private String comFromUseHardware(String commands){
        String machineCode = "";
        char[] command = commands.toCharArray();
        if (command.length != 3){
            return null;
        }else {
            machineCode += operation[2];
            switch (command[1]){
                case 'A':
                    machineCode += aim[0];
                    break;
                case 'B':
                    machineCode += aim[1];
                    break;
                case 'C':
                    machineCode += aim[2];
                    break;
                default:
                    return null;
            }
            int num = Integer.parseInt(""+command[2]);
            String numTmp = Integer.toBinaryString(num).toString();

            //不够四位的补齐四位
            for (int i = 0; i < 4 - numTmp.length(); i++){
                machineCode += "0";
            }
            machineCode += numTmp;

            return machineCode;
        }
    }

    /**
     * 把一个File类型编译成机器码, 再存入File类型中
     * @param source
     * @return
     */
    public OSFile compileToFile(OSFile source){

        //结尾为"end"才可以通过编译
        if (!source.getIndexCommand(source.getLineNum() - 1).equals("end")){
            return null;
        }

        OSFile executeOSFile = new OSFile();

        int sourceLine = source.getLineNum();

        if (sourceLine != 0){
            for (int i = 0; i < sourceLine; i++){
                if (source.getIndexCommand(i).equals("")){
                    continue;
                }
                String tmp = dealOneLine(source.getIndexCommand(i));
                if (tmp != null){
                    executeOSFile.addOneLine(tmp);
                }else {
                    return null;
                }
            }
        }else {
            return null;
        }
        return executeOSFile;
    }


    /**
     * 将字符串的机器指令转成byte数据，返回一个byte
     * @param command
     * @return
     */
    public static byte stringToBinary(String command){
        System.out.println("StringToBinary: "+command);

        char[] machineCode = command.toCharArray();
        byte sum = 0;
        for (char num : machineCode){
            sum = (byte) (sum * 2 + Byte.parseByte(num+""));
        }
        return sum;
    }

    public static void main(String[] args){
        OSFile OSFile = new OSFile();
        OSFile.addOneLine("AX=9");
        OSFile.addOneLine("AX++");
        OSFile.addOneLine("!A2");
        OSFile.addOneLine("AX--");
        OSFile.addOneLine("end");

        OSFile a = new Compiler().compileToFile(OSFile);

        for (int i = 0; i < a.getLineNum(); i++){
            System.out.println(a.getIndexCommand(i));
        }
    }
}

enum CalType{
    //分别代表赋值运算, 终止运算, 自加运算, 自减运算和其他
    ASSIGNMENT, FINISH, ADD, DEL, USEHARD, NONE
}
