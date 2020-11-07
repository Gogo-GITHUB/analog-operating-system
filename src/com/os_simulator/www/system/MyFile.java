package com.os_simulator.www.system;
import java.util.Arrays;
import java.util.List;

/**
 * 【文件】模拟系统内的文件
 */
class MyFile {
    /**

     目录名或文件名：3 个字节；
     扩展名： 1 个字节（可执行文件扩展名为 e，目录没有扩展名） ；
     目录属性、文件属性：1 字节；
     起始盘号：1 字节；
     文件长度：2 字节
     */
    private byte[] data;
    private byte[] name;
    private byte extName;//扩展名
    private byte attribute;//属性
    private byte startingSector;//起始扇区
    private short length;//长度
    private boolean hasError;//状态标注
    private List<MyFile> subFiles;//子文件夹
    /**
     *初始化文件
     *
     */

    MyFile(){
        this.name = new byte[3];
        Arrays.fill(this.name,(byte)0);
        length = 0;
        hasError = false;
        subFiles = null;
    }
    /**
     *利用attributes初始化各个属性
     * @param  attributes
     */

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

    /**
     *利用attributes和写入的data初始化各个属性
     * @param  attributes
     * @param  data
     */
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
        if (!hasError){//没有出错则将数据写入
            this.data = new byte[this.length];
            this.data = Arrays.copyOf(data,this.length);
        }
    }


    /**
     *利用new_name重新设置名字
     * @param  new_name
     */
    public void setName(String new_name){
        boolean split = false;
        char[] name = new_name.toCharArray();
        int length = new_name.length();
        if (length>=1){
            if (name[0]>255){//分割成两个字节
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
    /**
     *利用字符串更改文件的数据
     * @param  new_data
     */

    public void setData(String new_data){
        char[] data = new_data.toCharArray();
        int length = new_data.length();
        this.data = new byte[length];
        for (int i=0;i<length;i++){//逐个字符写入
            this.data[i] = (byte)data[i];
        }
        this.length = (short)length;
    }


    /**
     *读取文件的长度
     */

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

    /**
     *更改文件的属性
     * 第 0 位表示文件为只读文件，第 1 位表示文件为系统文件，第 2 位表示文件为
     * 可读、可写的普通文件，第 3 位表示该登记项不是文件的登记项
     * @param  attribute
     */
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

    /**
     *返回文件的全部属性
     * @return 文件全部属性
     */
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
