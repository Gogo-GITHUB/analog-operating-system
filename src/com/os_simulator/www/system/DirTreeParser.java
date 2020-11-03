package com.os_simulator.www.system;
import java.util.List;

/**

 *
 * 【目录树解析器】生成JSON格式的文件系统目录树；特供，由文件系统调用
 */
class DirTreeParser {
    private FileSystem fileSystem;
    private MyFile ROOT_DIR;
    private static String date = "2007-08-31 12:39";
    DirTreeParser(FileSystem fileSystem){
        this.fileSystem = fileSystem;
        ROOT_DIR = fileSystem.getRootDir();
    }

    String getJsonTree(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\"root\":\"OS/");
        builder.append(date);
        builder.append("/");
        builder.append(fileSystem.getDiskAmount());
        builder.append("/root catalog\",");
        parseDirToJson(ROOT_DIR,builder);
        builder.append("}");
        return builder.toString();
    }

    private void parseDirToJson(MyFile currentDir, StringBuilder builder){
        int dirCount=1;
        int fileCount=1;
        List<MyFile> subFiles = currentDir.getSubFiles();
        builder.append("\"subFolder\":[");
        if (subFiles!=null){
            for (MyFile file: subFiles){
                if (file.isDirectory()){
                    builder.append("{\"folder");
                    builder.append(dirCount++);
                    builder.append("\":\"");
                    byte[] rawName = file.getName();
                    char[] fileName = new char[rawName.length];
                    for (int j=0;j<rawName.length;j++) fileName[j]=(char)rawName[j];
                    builder.append(fileName);
                    builder.append("/");
                    builder.append(date);
                    builder.append("/0/folder\",");
                    parseDirToJson(file,builder);
                    builder.append("},");
                }
            }
            if (dirCount>1)
                builder.deleteCharAt(builder.lastIndexOf(","));
        }
        builder.append("],\"file\":[");
        if (subFiles!=null){
            for (MyFile file: subFiles){
                if (file.isNormalFile() || file.isReadOnlyFile() || file.isSystemFile()){
                    builder.append("{\"file");
                    builder.append(fileCount++);
                    builder.append("\":\"");
                    byte[] rawName = file.getName();
                    char[] fileName = new char[rawName.length];
                    for (int j=0;j<rawName.length;j++) fileName[j]=(char)rawName[j];
                    builder.append(fileName);
                    builder.append(".");
                    builder.append((char)file.getExtName());
                    builder.append("/");
                    builder.append(date);
                    builder.append("/");
                    builder.append(file.getLength());
                    builder.append("/");
                    builder.append((char)file.getExtName());
                    builder.append(" file\",\"content\":\"");
                    //删除返回的文本文件的内容
//                    byte[] rawData = file.getData();
//                    int bytesToRead = file.getLength();
//                    char[] chars = new char[bytesToRead];
//                    for (int i=0;i<bytesToRead;i++){
//                        chars[i] = (char)rawData[i];
//                    }
                    builder.append("");
                    builder.append("\"},");
                }
            }
            if (fileCount>1)
                builder.deleteCharAt(builder.lastIndexOf(","));
        }
        builder.append("],");
    }
}

