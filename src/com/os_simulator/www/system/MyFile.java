package com.os_simulator.www.system;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Demkors Infinity (iDemkors) in 2016/11/2
 * iDemkors允许您自由参考并引用此源代码之内容。如有疑问，请咨询iDemkors或大神。
 * iDemkors promises you can have a view of the source code and/or use it freely.
 * If having any question, contact iDemkors or God.
 *
 * 【文件】模拟系统内的文件
 */
class MyFile {
    private byte[] data;
    private byte[] name;
    private byte extName;
    private byte attribute;
    private byte startingSector;
    private short length;
    private boolean hasError;
    private List<MyFile> subFiles;
    MyFile(){
        this.name = new byte[3];
        Arrays.fill(this.name,(byte)0);
        length = 0;
        hasError = false;
        subFiles = null;
    }
    void initialize(byte[] attributes){
        if (attributes.length==8){
            name[0]=attributes[0];
            name[1]=attributes[1];
            name[2]=attributes[2];
            extName=attributes[3];
            attribute = attributes[4];
            startingSector = attributes[5];
            length = (short)(attributes[6]+attributes[7]*256);
        }
        else hasError=true;
    }
    void initialize(byte[] attributes,byte[] data){
        if (attributes.length==8){
            name[0]=attributes[0];
            name[1]=attributes[1];
            name[2]=attributes[2];
            extName=attributes[3];
            attribute = attributes[4];
            startingSector = attributes[5];
            length = (short)(attributes[6]+attributes[7]*256);
        }
        else hasError=true;
        if (!hasError){
            this.data = new byte[this.length];
            this.data = Arrays.copyOf(data,this.length);
        }
    }
    public void setName(String new_name){
        boolean split = false;
        char[] name = new_name.toCharArray();
        int length = new_name.length();
        if (length>=1){
            if (name[0]>255){
                this.name[0]=(byte)(name[0]%256);
                this.name[1]=(byte)(name[0]/256);
                split = true;
            }
            else this.name[0]=(byte)name[0];
        }
        if (length>=2 && split){
            this.name[2]=(byte)(name[1]%256);
        }
        if (length>=2 && (!split)){
            this.name[1]=(byte)name[1];
        }
        if (length>=3 && (!split)){
            this.name[2]=(byte)name[2];
        }
    }
    public void setData(String new_data){
        char[] data = new_data.toCharArray();
        int length = new_data.length();
        this.data = new byte[length];
        for (int i=0;i<length;i++){
            this.data[i] = (byte)data[i];
        }
        this.length = (short)length;
    }
    public void countLength(){
        if (isNormalFile()||isReadOnlyFile()||isSystemFile()){
            length = (short)data.length;
        }
        else length=0;
    }
    public void setData(byte[] new_data){
        this.data = new_data;
    }
    public void setExtName(char extName){
        this.extName = (byte)extName;
    }
    public boolean setAttribute(byte attribute){
        boolean status = false;
        if (isDirectory()) return false;
        if (attribute==1){
            setAsReadOnlyFile();
            status=true;
        }
        if (attribute==2){
            setAsSystemFile();
            status=true;
        }
        if (attribute==4){
            setAsNormalFile();
            status=true;
        }
        return status;
    }
    public byte[] getData(){
        return (isDirectory())?data:Arrays.copyOf(data,length);
    }
    public byte[] getName(){
        return name;
    }
    public byte getExtName(){
        return extName;
    }
    public short getLength(){
        return length;
    }
    public byte getStartingSector(){
        return startingSector;
    }
    public boolean getErrorStatus(){
        return hasError;
    }
    public byte getAttribute(){
        return attribute;
    }
    public byte[] getAttributes(){
        byte[] result = new byte[8];
        int i;
        for (i=0;i<name.length;i++) result[i]=name[i];
        for (;i<3;i++) result[i]=0;
        result[3]=extName;
        result[4]=attribute;
        result[5]=startingSector;
        result[6]=(byte)(length%256);
        result[7]=(byte)(length/256);
        return result;
    }
    public boolean isDirectory(){
        return (attribute==8);
    }
    public boolean isReadOnlyFile(){
        return (attribute==1);
    }
    public boolean isNormalFile(){
        return (attribute==4);
    }
    public boolean isSystemFile(){
        return (attribute==2);
    }
    public void setAsDirectory(){
        attribute=8;
    }
    public void setAsNormalFile(){
        attribute=4;
    }
    public void setAsSystemFile(){
        attribute=2;
    }
    public void setAsReadOnlyFile(){
        attribute=1;
    }
    public List<MyFile> getSubFiles(){
        return subFiles;
    }
    public void setSubFiles(List<MyFile> subFiles){
        this.subFiles = subFiles;
    }

}
