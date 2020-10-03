package com.os_simulator.www.system;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by geange on 16-11-16.
 */
public class OSFile {
    private List<String> commands = new LinkedList<>();

    /**
     * 空文件
     * 1.为空,返回true; 2.不为空,返回false
     */
    public boolean isEnptyFile(){
        if (commands.size() == 0){
            return true;
        }
        return false;
    }

    /**
     * 文件游标（标志执行文件的执行位置）
     */
    private int cursor = 0;
    //获取游标的位置
    public int getCursor(){
        return cursor;
    }
    //游标自增
    public void setCursor(){
        this.cursor++;
    }
    //游标自减
    public void setCursorBack(){
        this.cursor--;
    }

    public String getIndexCommand(int lineNum){
        System.err.println("===================>>> lineNum :"+ lineNum);
        return commands.get(lineNum);
    }

    /**
     * 返回文件末端的位置（文件内容的最后一行）
     * @return
     */
    public int getEndLine(){
        return commands.size()-1;
    }

    public int getLineNum(){
        return commands.size();
    }

    /**
     * 判断是否到文件尾部
     * @return
     */
    public boolean isEndofFile(){
        if (getCursor() > getEndLine()){
            return true;
        }
        return false;
    }

    /**
     * 添加一行命令
     * @param command
     */
    public void addOneLine(String command){
        commands.add(command);
    }

    /**
     * 将byte转化为字符串
     * @param num
     * @return
     */
    public static String byteToString(byte num){
        String command = "";
        byte temp=1;
        for (int i = 0; i<8; i++){
            char tmp = ((temp & num)==0)? '0': '1';
            command = tmp + command;
            temp = (byte)(temp<<1);
        }
        return command;
    }

    public static void main(String[] args){
        System.out.println(byteToString((byte) 9));

    }



}
