package com.os_simulator.www.system;
import com.os_simulator.www.hardware.CPU;
import java.util.*;
import java.util.HashMap;
/**
 * Created by Demkors Infinity (iDemkors) in 2016/10/30
 * iDemkors允许您自由参考并引用此源代码之内容。如有疑问，请咨询iDemkors或大神。
 * iDemkors promises you can have a view of the source code and/or use it freely.
 * If having any question, contact iDemkors or God.
 *
 * 【文件系统】模拟系统底层的文件管理，采用FAT格式
 */
public class FileSystem {
    private CPU cpu;
    private byte[] FAT;
    //记录已打开之文件（文件路径，文件对象）
    private Map<String,MyFile> openedFiles;
    //已打开之文件的打开方式（文件路径，是否为写）
    private Map<String,Boolean> openedStatus;
    //根目录
    private MyFile ROOT_DIR;
    //记录文件系统错误
    private String status;
    private int availableSectors;
    private DirTreeParser parser;
    private FileSamplesGenerator generator;
    //结束标志、错误标志，将保留分区最后2块以使用它们
    public static byte errorCode = -2;
    public static byte endCode = -1;
    public static int sectorSize = 64;
    static byte dirType = 8;
    static byte fileType = 4;
    //---------------构造方法----------------
    public FileSystem(CPU cpu){
        this.cpu = cpu;
        status="OK";
        FAT = new byte[cpu.getDiskSectorCount()];
        if (cpu.getDiskSectorCount()%sectorSize != 0){
            status="[WARNING] File Allocation Table established improperly";
            System.err.println(status);
        }
        openedFiles = new HashMap<>();
        openedStatus = new HashMap<>();
        ROOT_DIR = new MyFile();
        parser = new DirTreeParser(this);
        generator = new FileSamplesGenerator(this);

        initialize();
    }

    //---------------对内的工具方法----------------
    /**
     * 初始化文件系统，包括FAT、所有目录与文件（构建时使用）
     */
    private void initialize(){
        int i;
        availableSectors=0;
        int sectorsForFAT = cpu.getDiskSectorCount()/sectorSize;//可用盘块
        for (i=0;i<sectorsForFAT;i++){
            byte[] temp = cpu.readDisk(i);//读取磁盘，写入fat
            for (int j=0;j<sectorSize;j++){
                FAT[i*sectorSize+j]=temp[j];
                if (temp[j]!=endCode&&temp[j]!=errorCode) availableSectors++;
            }
        }
        if (FAT.length>sectorsForFAT){
            if (FAT[0]!=endCode) availableSectors-=(sectorsForFAT+1);
            for (i=0;i<sectorsForFAT+1;i++){
                FAT[i] = endCode;
            }
        }

        byte[] attributes = new byte[8];
        Arrays.fill(attributes,(byte)0);
        attributes[4] = 0B00001000; //isDirectory
        attributes[5] = (byte)sectorsForFAT; //StartingSector = 4
        ROOT_DIR.initialize(attributes);//设置根目录的属性
        parseFile(ROOT_DIR);//解析根目录

        //预生成3个目录、10个可执行文件
        generator.generate();
    }

    /**
     * 解析指定文件或文件夹（初始化时调用，由根目录开始解析）
     * @param file
     */
    private void parseFile(MyFile file){
        byte[] data = new byte[sectorSize];
        byte hasNext = file.getStartingSector();
        byte emptyRecord[] = new byte[8];
        Arrays.fill(emptyRecord,(byte)0);
        byte sectorCount = 0;
        if (file.isDirectory()){//目录文件
            List<MyFile> subFiles = file.getSubFiles();
            if (subFiles!=null) return;
            subFiles = new LinkedList<>();//获取子目录
            while (hasNext!=endCode
                    &&hasNext!=errorCode){
                byte[] rawData = cpu.readDisk(hasNext);//当前盘块数据
                if (rawData.length==sectorSize){
                    if (sectorCount==0){//第一个盘块
                        data = Arrays.copyOf(rawData,sectorSize);
                    }
                    else {
                        byte[] oldData = data;
                        data = new byte[(sectorCount+1)*sectorSize];
                        System.arraycopy(oldData,0,data,0,oldData.length);
                        System.arraycopy(rawData,0,data,sectorCount*sectorSize,sectorSize);
                    }
                    sectorCount++;

                    for (int i=0;i<8;i++){//读取下层的子文件
                        byte[] rawRecord = Arrays.copyOfRange(rawData,i*8,(i+1)*8);
                        if (Arrays.equals(emptyRecord,rawRecord)) break;//空记录退出
                        MyFile new_file = new MyFile();
                        new_file.initialize(rawRecord);//文件属性设置
                        if (new_file.getErrorStatus()) continue;
                        parseFile(new_file);
                        subFiles.add(new_file);
                    }
                }
                hasNext = FAT[hasNext];//下一盘块
            }
            file.setSubFiles(subFiles);
            file.setData(data);//将从磁盘读取出来的数据，放进去文件对象
        }
        else if (file.isNormalFile() || file.isReadOnlyFile() || file.isSystemFile()){//普通文件
            while (hasNext!=endCode
                    &&hasNext!=errorCode){
                byte[] rawData = cpu.readDisk(hasNext);
                if (rawData.length==sectorSize){
                    if (sectorCount==0){
                        data = Arrays.copyOf(rawData,sectorSize);
                    }
                    else {
                        byte[] oldData = data;
                        data = new byte[(sectorCount+1)*sectorSize];
                        System.arraycopy(oldData,0,data,0,oldData.length);//将原来的盘块放进去data
                        System.arraycopy(rawData,0,data,sectorCount*sectorSize,sectorSize);//添加新读取的
                    }
                    sectorCount++;
                }
                hasNext = FAT[hasNext];
            }
            file.setData(data);
        }
    }

    /**
     * 从FAT寻找并获取可用的一个区块
     * @param requiredSpace
     * @return 若寻得，返回区块号；否则返回endCode
     */
    private byte takeAvailableSector(int requiredSpace){
        int emptySpace = availableSectors*sectorSize;
        if (requiredSpace>emptySpace) {//空间不够
            status = "Available disk space not enough";
            return endCode;
        }
        byte emptySector = endCode;
        for (int i=cpu.getDiskSectorCount()/sectorSize+1;i<cpu.getDiskSectorCount()-2;i++){//查找空块
            if (FAT[i]==0) {
                emptySector=(byte)i;
                availableSectors--;
                break;
            }
        }
        return emptySector;
    }

    /**
     * 将从第firstSectorToFree区块开始的已分配区块释放出去
     * （不处理5~254以外的赋值）
     * @param firstSectorToFree
     */
    private void freeSector(int firstSectorToFree){
        int i;
        int currentSector = firstSectorToFree;
        if (currentSector>cpu.getDiskSectorCount()/sectorSize
                && currentSector<=cpu.getDiskSectorCount()-2){
            for (i = FAT[currentSector];
                 i!=0&&i!=endCode&&i!=errorCode;
                 i=FAT[i]){
                FAT[currentSector]=0;
                currentSector = i;
                availableSectors++;
            }//释放盘块
            FAT[currentSector]=0;
            availableSectors++;
        }
    }

    /**
     * 自文件系统获取指定路径的MyFile对象文件（对象可为文件或文件夹目录
     * @return 若寻得，返回目标；否则返回Null
     */
    MyFile getFile(String path){
        //以“斜杠”分割路径，由根目录开始寻找目标
        System.err.println(path);
        String[] pathSplit = path.split("/");
        if (pathSplit.length>1)
            pathSplit = Arrays.copyOfRange(pathSplit,1,pathSplit.length);//提取目录
        System.err.println(Arrays.toString(pathSplit));
        return seekPath(ROOT_DIR,pathSplit);
    }

    /**
     * 根据打碎的路径名在指定目录中寻找目标或下一级目录（目标可为文件或目录）
     * @param currentDir
     * @param path
     * @return 若寻得，返回目标；否则返回null
     */
    private MyFile seekPath(MyFile currentDir, String[] path){
        List<MyFile> subFiles = currentDir.getSubFiles();
        //目录为空，返回空值
        if (subFiles==null) return null;
        //路径名为空，则认为查询根目录，返回根目录
        if (path.length==0) return ROOT_DIR;
        //默认返回空值（未找到目标）
        MyFile result = null;
        for (MyFile file : subFiles){
            byte[] rawName = file.getName();
            char[] rawNameChars = new char[rawName.length];
            for (int j=0;j<rawName.length;j++) rawNameChars[j]=(char)rawName[j];
            String fileName = new String(rawNameChars);//获取子文件的名字

            if (fileName.isEmpty()) return null;
            if (file.isDirectory() && path.length>1 && fileName.equals(path[0])){
                //目标在子目录中，寻找子目录
                String[] new_path = Arrays.copyOfRange(path,1,path.length);
                result = seekPath(file,new_path);//递归下一级
            }
            if (path.length==1
                    && (file.isDirectory() || file.isNormalFile()
                    || file.isReadOnlyFile() || file.isSystemFile())
                    ){
                if (file.isNormalFile() || file.isReadOnlyFile() || file.isSystemFile()){
                    //目标是文件
                    char extName = (char)file.getExtName();
                    String requiredExtName = path[0].substring(path[0].lastIndexOf('.')+1,path[0].length());//目标后缀
                    String originalExtName = new String(new char[]{extName});//源后缀
                    if (path[0].contains(fileName) && requiredExtName.equals(originalExtName)){
                        //文件名、扩展名完全匹配
                        result = file;
                    }
                }
                else {
                    //目标是目录，且目录名完全匹配
                    if (path[0].equals(fileName)) result = file;
                }
            }
        }
        return result;
    }

    /**
     * 保存修改后的FAT表值
     * 【注意】必须确保FAT准确反映分区的实际分配情况
     */
    private void saveFAT(){
        for (int i=0;i<FAT.length/sectorSize;i++){
            System.err.println("FAT.length------------  "+FAT.length+"\n"+
                    "FAT.length-i*sectorSize--------  "+(FAT.length-i*sectorSize)+"\n"+
                    "(i+1)*sectorSize----------  "+((i+1)*sectorSize)+"\n");
            int max = ((i+1)*sectorSize)>FAT.length? FAT.length-i*sectorSize: (i+1)*sectorSize;
            //是否到达末尾，如果到达末尾不需要把整个块写入

            byte[] data = Arrays.copyOfRange(FAT,i*sectorSize,max);
            cpu.writeDisk(data,i);//写入磁盘文件
        }
    }

    /**
     * 在第startingSector区块开始，保存文件数据
     * @param data
     * @param startingSector
     * @return 保存成功与否
     */
    private boolean saveBytesOfFile(byte[] data,int startingSector){
        int currentSector = startingSector;
        int nextSector = currentSector;
        boolean status = false;
        for (int i=0;i<data.length;i+=sectorSize){
            //首先自数据流裁剪第0~63，第64~127，……个字节作为缓存
            byte[] buffer = Arrays.copyOfRange(data,i,i+sectorSize);
            if (buffer.length<sectorSize){
                //若缓存长度不足64，补足至64（填充0）
                byte[] temp = buffer;
                buffer = new byte[sectorSize];
                System.arraycopy(temp,0,buffer,0,temp.length);
                Arrays.fill(buffer,temp.length,sectorSize,(byte)0);
            }
            if (i+sectorSize>=data.length){
                //保存至最后一个区块，在该区块设置endCode，并释放之前多占用的区块
                freeSector(FAT[currentSector]);
                FAT[currentSector]=endCode;
                status = true;
            }
            else {
                //尚未到最后一个区块
                if (FAT[currentSector]!=endCode&&FAT[currentSector]!=errorCode){
                    //如已经分配下一个区块，切换至下一个
                    nextSector = FAT[currentSector];
                }
                else {
                    //已分配区块用尽，申请新区块
                    nextSector = takeAvailableSector(data.length-(i+sectorSize));
                    if (nextSector==endCode){
                        //无法申请，终止保存过程
                        status=false;
                        break;
                    }
                }
            }
            cpu.writeDisk(buffer,currentSector);//保存进去磁盘文件
            currentSector = nextSector;
        }
        return status;
    }

    /**
     * 保存指定目录的子文件项
     * @param currentDir
     * @return 若目标是根目录，且子文件项>8，则返回false；否则返回saveBytesOfFile()的结果
     */
    private boolean saveRecordOfDir(MyFile currentDir){
        if (currentDir==ROOT_DIR){
            List<MyFile> subFiles = currentDir.getSubFiles();
            if (subFiles.size()>8) {
                status="Root directory cannot hold MORE THAN 8 files/directories";
                return false;
            }
        }
        return saveBytesOfFile(currentDir.getData(),currentDir.getStartingSector());
    }

    //-------------对外的公用方法--------------
    /**
     * 根据指定路径建立一文件或目录
     * （如文件：/usr/inf/kkk.e，目录：/usr/etc）
     * @param filePath
     * @param attribute
     * @return 成功建立与否
     */
    public boolean createFile(String filePath,byte attribute){
        MyFile formerDir = ROOT_DIR;
        String[] pathSplit = filePath.split("/");
        int depth = pathSplit.length;
        //若目标是根目录，返回false
        if (depth==0) return false;
        if (depth>1){
            pathSplit = Arrays.copyOfRange(pathSplit,1,pathSplit.length);//去掉第一层目录
            depth--;
        }
        System.err.println(Arrays.toString(pathSplit));
        StringBuilder builder = new StringBuilder();
        System.err.println("Start create");
        for (String path: pathSplit){
            //逐级寻找目标
            builder.append("/");
            builder.append(path);
            MyFile currentFileOrDir = getFile(builder.toString());//按目录查找文件
            if (currentFileOrDir==null && depth!=1) {
                //未抵达最后一级，且未发现下一级目录，不予建立
                status="Target directory not found-1";
                System.err.println(status);
                return false;
            }
            if (currentFileOrDir==null){
                //【到达最后一级，不存在与目标同名的文件或目录】
                List<MyFile> subFiles = formerDir.getSubFiles();
                if (subFiles==null) {
                    //但最后一级竟然不是目录
                    status="Target directory not found-2";
                    System.err.println(status);
                    return false;
                }
                MyFile new_file = new MyFile();
                int oldCount = subFiles.size();
                byte[] attributes = new byte[8];
                //建立文件需要申请新区块，不能申请者，不予建立
                byte emptySector = takeAvailableSector(sectorSize);//获取磁盘空间
                if (emptySector==endCode) return false;
                attributes[4] = attribute;
                attributes[5] = emptySector;
                new_file.initialize(attributes);//设置文件

                if (new_file.isReadOnlyFile()) {
                    //所建立文件是只读的，不予建立
                    status="Read-only file cannot be established";
                    System.err.println(status);
                    return false;}
                if (new_file.isNormalFile() || new_file.isReadOnlyFile() || new_file.isSystemFile()){
                    //目标是文件

                    String newName = path.substring(0,path.indexOf('.'));
                    String extName = path.substring(path.lastIndexOf('.')+1,path.length());
                    //提取文件名
                    System.err.println(newName + " | " + extName);
                    if (newName.length()>3 || extName.length()!=1) {
                        //不允许文件名带有“.”；文件名不超过3个字符；扩展名1个字符
                        status="Invalid filename-2";
                        System.err.println(status);
                        return false;
                    }
                    char[] temp = extName.toCharArray();
                    new_file.setExtName(temp[0]);
                    new_file.setName(newName);
                }
                else {
                    //目标是目录
                    if (path.length()>3) {
                        status="Invalid filename-3";
                        System.err.println(status);
                        return false;
                    }
                    new_file.setSubFiles(new LinkedList<>());
                    new_file.setName(path);
                    new_file.setData(new byte[sectorSize]);
                    //设置文件夹
                }
                //终于可以保存了？（准备工作）
                subFiles.add(new_file);
                formerDir.setSubFiles(subFiles);

                int newCount = subFiles.size();
                byte[] oldRecord = formerDir.getData();//提取旧文件夹的内容
                byte[] newRecord = new byte[(newCount%8==0)?(newCount/8+1)*64:newCount*8];
                System.arraycopy(oldRecord,0,newRecord,0,oldCount*8);
                System.arraycopy(new_file.getAttributes(),0,newRecord,oldCount*8,8);
                //用新文件的内容对旧文件夹进行更新

                formerDir.setData(newRecord);
                //实际永久性保存操作
                System.err.println("OK - Before permanent saving");
                if (!saveBytesOfFile(new byte[sectorSize],emptySector)) return false;
                if (!saveRecordOfDir(formerDir)) return false;
                System.err.println("Create done");
                //确认文件内容、目录项已先后保存，才改动FAT
                FAT[emptySector]=endCode;
                saveFAT();
                break;
            }
            if (depth==1) {
                //到达最后一级，存在同名文件/目录，不予建立
                status="Target has been already exist";return false;
            }
            //未抵达最后一级，发现下一级，将寻找下一级目录（currentFileOrDir!=null && depth!=1）
            depth--;
            formerDir = currentFileOrDir;
        }
        //在上面过程未跳出，则认为完成建立
        status = "OK";
        return true;
    }

    /**
     * 根据指定路径打开文件（toWrite表示是否为写模式，true=写，false=读）
     * @param filePath
     * @param toWrite
     * @return 若目标不存在，或者目标为只读却试图写，返回false；否则返回true
     */
    public boolean openFile(String filePath,boolean toWrite) {
        MyFile file = getFile(filePath);
        if (file==null) {
            status="Target file not found";return false;
        }
        if (file.isReadOnlyFile()&&toWrite){
            status="Target file is read-only";return false;
        }
        openedFiles.put(filePath,file);//加入文件路径和文件的映射
        openedStatus.put(filePath,toWrite);//加入文件路径状态的映射
        return true;
    }

    /**
     * 从指定路径的文件，读取内容（自动打开文件，因此须加上关闭操作）
     * 【返回值】成功读取，返回byte数组形式的文件内容；
     *   目标不存在、目标处于写模式、目标是目录或属性不明，返回空值
     * @param filePath
     * @param length
     * @return 视上述情况返回
     */
    public byte[] readFile(String filePath,int length){
        MyFile source = null;
        boolean toWrite=true;
        byte[] result;
        for (String i: openedFiles.keySet()){//根据路径获取文件对象
            if (filePath.equals(i)){
                source = openedFiles.get(i);
                toWrite = openedStatus.get(i);
                break;
            }
        }
        if (source==null){
            if (!openFile(filePath,false)) return null;
            source = openedFiles.get(filePath);
            toWrite = false;
        }
        if (toWrite) {
            status = "File has been opened in WRITE mode";
            return null;
        }
        if (source.isNormalFile()||source.isReadOnlyFile()||source.isSystemFile()){
            //目标为文件，返回byte数组形式的文件内容
            result = source.getData();
            return result;
        }
        else return null;  //文件属性为目录或未知
    }

    /**
     * 从指定路径的目录，读取下一级目录项（仅限此一级）
     * 【返回值】返回该目录下各项的路径（空目录返回长度1、包含“null”的数组）；
     *   目标不存在、目标是文件、目标是目录但目录表为null，返回空值
     * @param filePath
     * @return 视上述情况返回
     */
    public String[] readDirectory(String filePath){
        MyFile source = null;
        String[] result;
        for (String i: openedFiles.keySet()){
            if (filePath.equals(i)){
                source = openedFiles.get(i);
                break;
            }
        }
        if (source==null){
            source = getFile(filePath);
            if (source==null) return null;
        }
        if (!source.isDirectory()) {
            status = "Target is not a directory";
            return null;
        }
        closeFile(filePath);
        List<MyFile> subFiles = source.getSubFiles();
        if (subFiles==null) return null;
        if (subFiles.size()==0){//没有子目录
            result = new String[]{"null"};
        }
        else {
            result = new String[subFiles.size()];
            for (int i=0;i<subFiles.size();i++){
                MyFile file = subFiles.get(i);
                byte[] rawName = file.getName();
                char[] fileName = new char[rawName.length];
                for (int j=0;j<rawName.length;j++) fileName[j]=(char)rawName[j];
                //返回的路径应该是：（被查询的目录路径）/（目录内文件名）
                String new_path = filePath + "/" + new String(fileName);
                result[i] = new_path;
            }
        }
        return result;
    }


    /**
     * 从指定路径的文件/目录，读取内容/目录项
     * 【返回值】若目标为文件，返回长度1、仅包含文件内容的String数组；
     *   若目标为目录，返回各目录项的路径（空目录返回长度1、包含“null”的数组）；
     *   目标不存在、目标处于写模式、目标是目录但目录表为null，返回空值
     * @param filePath
     * @param length
     * @return 视上述情况返回
     */
    public String[] readFiletoString(String filePath,int length){
        MyFile source = null;
        boolean toWrite=true;
        String[] result;
        for (String i: openedFiles.keySet()){
            if (filePath.equals(i)){
                source = openedFiles.get(i);
                toWrite = openedStatus.get(i);
                break;
            }
        }
        if (source==null){
            if (!openFile(filePath,false)) return null;
            source = openedFiles.get(filePath);
            toWrite = false;
        }
        if (toWrite) {
            status = "File has been opened in WRITE mode";
            return null;
        }

        if (source.isNormalFile()||source.isReadOnlyFile()||source.isSystemFile()){
            //目标为文件，返回长度1、仅包含文件内容的String数组
            byte[] rawData = source.getData();
            int bytesToRead = (length>source.getLength())? source.getLength(): length;
            char[] chars = new char[bytesToRead];
            for (int i=0;i<bytesToRead;i++){
                chars[i] = (char)rawData[i];
            }
            result = new String[]{new String(chars)};
        }
        else if (source.isDirectory()){
            //目标为目录，返回各目录项的路径（空目录返回长度1、包含“null”的数组）
            List<MyFile> subFiles = source.getSubFiles();
            if (subFiles==null) return null;
            if (subFiles.size()==0){
                result = new String[]{"null"};
            }
            else {
                result = new String[subFiles.size()];
                for (int i=0;i<subFiles.size();i++){
                    MyFile file = subFiles.get(i);
                    byte[] rawName = file.getName();
                    char[] fileName = new char[rawName.length];
                    for (int j=0;j<rawName.length;j++) fileName[j]=(char)rawName[j];
                    //返回的路径应该是：（被查询的目录路径）/（目录内文件名）
                    String new_path = filePath + "/" + new String(fileName);
                    result[i] = new_path;
                }
            }
        }
        else return null;  //文件属性未知？
        return result;
    }

    /**
     * 向指定路径的文件写入新数据，数据为String格式
     * 【注意】新数据将覆盖文件对象中的既有数据！
     * @param filePath
     * @param buffer
     * @return 若目标已以读模式打开，或目标为只读，或写入失败，返回false；写入成功，返回true
     */
    public boolean writeFile(String filePath,String buffer){
        MyFile target = null;
        boolean toWrite = false;
        for (String i: openedFiles.keySet()){
            if (filePath.equals(i)){
                target = openedFiles.get(i);
                toWrite = openedStatus.get(i);
                break;
            }
        }
        if (target==null){
            if (!openFile(filePath,true)) return false;
            target = openedFiles.get(filePath);
            toWrite = true;
        }
        if (!toWrite){
            status = "File has been opened in READ mode";
            return false;
        }
        String dirToBeFound = filePath.substring(0,filePath.lastIndexOf("/"));
        MyFile currentDir = getFile(dirToBeFound);
        List<MyFile> subFiles = currentDir.getSubFiles();//获取父目录和父目录的子目录
        boolean found = false;
        if (target.isNormalFile()||target.isReadOnlyFile()||target.isSystemFile()){
            if (subFiles.contains(target)){
                byte[] records = currentDir.getData();//当前目录的数据
                byte[] recordOfFile = target.getAttributes();//目标文件的属性

                for (int i=0;i<records.length;i+=8){
                    byte[] record = Arrays.copyOfRange(records,i,i+8);
                    System.out.println(Arrays.toString(record));
                    if (Arrays.equals(record,recordOfFile)){
                        byte[] newRecords = new byte[records.length];
                        target.setData(buffer);
                        if (records.length>8){
                            if (i>0)
                                System.arraycopy(records,0,newRecords,0,i);
                            System.arraycopy(target.getAttributes(),0,newRecords,i,8);//复制文件属性
                            i+=8;
                            if (i<records.length)
                                System.arraycopy(records,i,newRecords,i,records.length-i);//复制源文件的内容
                        }
                        currentDir.setData(newRecords);//重新设置数据
                        if (!saveRecordOfDir(currentDir)) return false;//保存
                        found = true;
                        break;
                    }
                }
                if (!found){
                    status = "[FATAL] Inconsistent record in belonged directory";
                    return false;
                }
            }
            else {
                status = "[FATAL] Inconsistent record in belonged directory";
                return false;
            }
        }
        else{
            status = "Target is not a file";
            return false;
        }
        if (!saveBytesOfFile(target.getData(),target.getStartingSector()))
            return false;
        //确认写入成功，才保存FAT
        saveFAT();
        return true;
    }

    /**
     * 向指定路径的文件写入新数据，数据为byte数组形式
     * 【注意】新数据将覆盖文件对象中的既有数据！
     * @param filePath
     * @param buffer
     * @return 若目标已以读模式打开，或目标为只读，或写入失败，返回false；写入成功，返回true
     */
    public boolean writeFile(String filePath,byte[] buffer){
        MyFile target = null;
        boolean toWrite = false;
        System.err.println("Write: "+filePath);
        for (String i: openedFiles.keySet()){
            if (filePath.equals(i)){
                target = openedFiles.get(i);
                toWrite = openedStatus.get(i);
                break;
            }
        }
        if (target==null){
            if (!openFile(filePath,true)) return false;
            target = openedFiles.get(filePath);
            toWrite = true;
        }
        if (!toWrite){
            status = "File has been opened in READ mode";
            return false;
        }
        String dirToBeFound = filePath.substring(0,filePath.lastIndexOf("/"));
        MyFile currentDir = getFile(dirToBeFound);
        if (currentDir==null) currentDir=ROOT_DIR;
        List<MyFile> subFiles = currentDir.getSubFiles();
        boolean found = false;
        if (target.isNormalFile()||target.isReadOnlyFile()||target.isSystemFile()){
            if (subFiles.contains(target)){
                byte[] records = currentDir.getData();
                byte[] recordOfFile = target.getAttributes();
                for (int i=0;i<records.length;i+=8){
                    byte[] record = Arrays.copyOfRange(records,i,i+8);
                    System.out.println(Arrays.toString(record));
                    if (Arrays.equals(record,recordOfFile)){
                        byte[] newRecords = new byte[records.length];
                        target.setData(buffer);
                        target.countLength();
                        if (records.length>8){
                            if (i>0)
                                System.arraycopy(records,0,newRecords,0,i);
                            System.arraycopy(target.getAttributes(),0,newRecords,i,8);
                            i+=8;
                            if (i<records.length)
                                System.arraycopy(records,i,newRecords,i,records.length-i);
                        }
                        currentDir.setData(newRecords);
                        found = true;
                        break;
                    }
                }
                if (!found){
                    status = "[FATAL] Inconsistent record in belonged directory";
                    return false;
                }
            }
            else {
                status = "[FATAL] Inconsistent record in belonged directory";
                return false;
            }
        }
        else{
            status = "Target is not a file";
            return false;
        }
        if (!saveBytesOfFile(target.getData(),target.getStartingSector())
                || !saveRecordOfDir(currentDir))
            return false;
        //确认写入成功，才保存FAT
        saveFAT();
        return true;
    }

    /**
     * 关闭指定路径的文件（读文件、读目录、写文件之后另外执行）
     * @param filePath
     */
    public void closeFile(String filePath){
        for (String i : openedFiles.keySet()){
            if (filePath.equals(i)){
                System.err.println("Close: "+filePath);
                openedFiles.remove(i);
                openedStatus.remove(i);
                break;
            }
        }
    }

    /**
     * 删除指定的一个文件（可删除目录，无论目录是否为空）
     * 【返回】若文件/目录已打开，或找不到，或试图删除根目录，则返回false；删除成功，返回true
     * @param filePath
     * @return 视上述情况返回
     */
    public boolean deleteFile(String filePath){
        boolean hasOpened = false;
        for (String i: openedFiles.keySet()){
            if (filePath.equals(i)){
                hasOpened = true;
            }
        }
        if (hasOpened){
            status = "File/Directory has been opened";
            return false;
        }
         //检查文件是否被打开
        String dirToBeFound = filePath.substring(0,filePath.lastIndexOf("/"));
        MyFile target = getFile(filePath);
        MyFile currentDir = getFile(dirToBeFound);


        if (currentDir==null||target==null){
            status = "File/Directory not found";
            return false;
        }
        if (target==ROOT_DIR){
            status = "Root directory cannot be deleted";
            return false;
        }
        List<MyFile> subFiles = currentDir.getSubFiles();
        boolean found = false;
        if (subFiles.contains(target)){
            byte[] records = currentDir.getData();
            byte[] recordOfFile = target.getAttributes();
            for (int i=0;i<records.length;i+=8){
                byte[] record = Arrays.copyOfRange(records,i,i+8);
                System.out.println(Arrays.toString(record));
                if (Arrays.equals(record,recordOfFile)){//匹配文件内容
                    byte[] newRecords = new byte[(records.length>8)?records.length-8:sectorSize];
                    if (records.length>8){
                        if (i>0)
                            System.arraycopy(records,0,newRecords,0,i);
                        if ((i+8)<records.length)
                            System.arraycopy(records,i+8,newRecords,i,records.length-(i+8));
                    }
                    subFiles.remove(target);
                    currentDir.setSubFiles(subFiles);//移除删除的子目录
                    currentDir.setData(newRecords);
                    if (!saveRecordOfDir(currentDir)) return false;
                    freeSector(target.getStartingSector());//释放磁盘空间
                    saveFAT();
                    found = true;
                    break;
                }
            }
            if (!found){
                status = "[FATAL] Inconsistent record in belonged directory";
                return false;
            }
        }
        else {
            status = "[FATAL] Inconsistent record in belonged directory";
            return false;
        }
        return true;
    }

    /**
     * 删除指定的目录，该目录须为空
     * @param dirPath
     * @return 若目录不为空，返回false；否则返回调用deleteFile()的结果
     */
    public boolean deleteEmptyDir(String dirPath){
        MyFile target = getFile(dirPath);
        if (target!=null){
            List<MyFile> subFiles = target.getSubFiles();
            if (subFiles!=null && subFiles.size()>0){
                status = "Directory not empty";
                return false;
            }
        }
        return deleteFile(dirPath);
    }

    /**
     * 改变一个未打开之文件的只读、系统、普通文件属性
     * （不允许对目录修改或修改为目录；4=普通 2=系统 1=只读）
     * @param filePath
     * @param attribute
     * @return 若目标已打开，或未找到，或试图修改目录/修改为目录，则返回false；修改成功，返回true
     */
    public boolean changeAttribute(String filePath,byte attribute){
        boolean hasOpened = false;
        for (String i: openedFiles.keySet()){
            if (filePath.equals(i)){
                hasOpened = true;
            }
        }
        if (hasOpened){
            status = "File has been opened";
            return false;
        }

        MyFile target = getFile(filePath);
        if (target==null){
            status = "File not found";
            return false;
        }
        return target.setAttribute(attribute);
    }

    int getDiskAmount(){
        return cpu.getDiskCapacity();
    }

    MyFile getRootDir() {return ROOT_DIR;}
    /**
     * 以JSON格式，生成整个分区的目录树（根据前端要求量身订造）
     * @return JSON格式的目录树
     */
    public String getJsonDirTree(){
        return parser.getJsonTree();
    }

    /**
     * 获取所有演示可执行文件的路径
     * @return 记录路径的List
     */
    public List<String> getSamplePaths(){
        return generator.getSamplePaths();
    }

    /**
     * 获取文件系统的状态描述（一般在执行一次文件操作之后，若结果为false，应取得状态描述并显示）
     * @return （对发生之错误的）状态描述
     */
    public String getStatus(){
        return status;
    }

    /**
     * 获取全分区的区块分配情况（空闲为false；已占用或有错误为true）
     * @return 记录分配情况的ArrayList
     */
    public List<Boolean> getFAT(){
        List<Boolean> status = new ArrayList<>();
        for (int i=0;i<FAT.length;i++){
            status.add(i,(FAT[i]!=0));
        }
        return status;
    }
}
