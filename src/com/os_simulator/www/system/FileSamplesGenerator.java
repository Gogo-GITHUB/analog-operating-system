package com.os_simulator.www.system;
import java.util.List;
import java.util.LinkedList;
/**
 * Created by Demkors Infinity (iDemkors) in 2016/11/30
 * iDemkors允许您自由参考并引用此源代码之内容。如有疑问，请咨询iDemkors或大神。
 * iDemkors promises you can have a view of the source code and/or use it freely.
 * If having any question, contact iDemkors or God.
 *
 * 【演示文件生成器】生成3个目录、10个演示可执行文件（如没有）；特供，由文件系统调用
 */
class FileSamplesGenerator {
    private FileSystem fileSystem;
    private List<String> paths;
    private static byte[][] codes = {
      /* 10个程序代码：
      !A2,!A2,!A2,!A2,X=0,X++,X=9,end
      !A2,!A2,!A2,!A2,X=9,X++,X--,X=5,end
      !A2,X=9,X++,X--,X=7,end
      !A2,X=9,X++,X--,X=F,end
      !B7,X=1,X++,X++,X++,X++,X++,X++,end
      !B9,X=9,X--,X--,X--,X--,X--,end
      !A7,!B7,!C7,!A5,!B5,!C5,X=8,end
      !B8,!C8,X=F,X++,X++,X++,X++,end
      X=F,X++,X++,X++,X++,X++,X++,X++,X++,end
      X=2,X--,X--,X--,X--,X--,X--,X--,end
      */
            {(byte)0B10000010,(byte)0B10000010,(byte)0B10000010,(byte)0B10000010,0B00100000,0B01000001,0B00001001,(byte)0B11000000},
            {(byte)0B10000010,(byte)0B10000010,(byte)0B10000010,(byte)0B10000010,0B00001001,0B01000001,0B01001111,0B00000101,(byte)0B11000000},
            {(byte)0B10000010,0B00001001,0B01000001,0B01001111,0B00000111,(byte)0B11000000},
            {(byte)0B10000010,0B00001001,0B01000001,0B01001111,0B00001111,(byte)0B11000000},
            {(byte)0B10010111,0B00000001,0B01000001,0B01000001,0B01000001,0B01000001,0B01000001,0B01000001,(byte)0B11000000},
            {(byte)0B10011001,0B00001001,0B01001111,0B01001111,0B01001111,0B01001111,0B01001111,(byte)0B11000000},
            {(byte)0B10000111,(byte)0B10010111,(byte)0B10100111,(byte)0B10000101,(byte)0B10010101,(byte)0B10100101,0B00001000,(byte)0B11000000},
            {(byte)0B10011000,(byte)0B10101000,0B00001111,0B01000001,0B01000001,0B01000001,0B01000001,(byte)0B11000000},
            {0B00001111,0B01000001,0B01000001,0B01000001,0B01000001,0B01000001,0B01000001,0B01000001,0B01000001,(byte)0B11000000},
            {0B00000010,0B01001111,0B01001111,0B01001111,0B01001111,0B01001111,0B01001111,0B01001111,(byte)0B11000000}
    };
    FileSamplesGenerator(FileSystem fileSystem){
        this.fileSystem = fileSystem;
        paths = new LinkedList<>();
    }
    void generate(){
        char[] rawName = {'0','0','0','.','e'};
        int i;
        boolean test;
        String fileName = new String(rawName);
        String dirPath = "/usr";
        for (i=0;i<9;){
            if (i>=3)
                dirPath = "/etc";
            if (i>=6)
                dirPath = "/drv";
            fileSystem.createFile(dirPath,fileSystem.dirType);
            for (int j=0;j<3;i++,j++){
                String filePath = dirPath + "/" + fileName;
                fileSystem.createFile(filePath,fileSystem.fileType);
                test = fileSystem.writeFile(filePath,codes[i]);
                if (!test) System.out.println(fileSystem.getStatus());
                fileSystem.closeFile(filePath);
                rawName[2]++;
                fileName = new String(rawName);
                paths.add(filePath);
                System.err.println("Created: "+filePath);
            }
        }
        dirPath = "/" + fileName;
        fileSystem.createFile(dirPath,fileSystem.fileType);
        test = fileSystem.writeFile(dirPath,codes[i]);
        if (!test) System.out.println(fileSystem.getStatus());
        fileSystem.closeFile(dirPath);
        paths.add(dirPath);
        System.err.println("Created: "+dirPath);
    }
    List<String> getSamplePaths(){
        return paths;
    }
}
