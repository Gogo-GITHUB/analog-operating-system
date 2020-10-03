package com.os_simulator.www.system.software;

import javafx.scene.control.TreeItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by geange on 16-12-6.
 */
public class JsonParser {

    public static String JSON = "{\n" +
            "    \"root\": \"OS/2007-08-31 12:39/16384/root catalog\",\n" +
            "    \"subFolder\": [\n" +
            "        {\n" +
            "            \"folder1\": \"usr/2007-08-31 12:39/0/folder\",\n" +
            "            \"subFolder\": [],\n" +
            "            \"file\": [\n" +
            "                {\n" +
            "                    \"file1\": \"000.e/2007-08-31 12:39/8/e file\",\n" +
            "                    \"content\": \"ﾂﾂﾂﾂ A\\t\uFFC0\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"file2\": \"001.e/2007-08-31 12:39/9/e file\",\n" +
            "                    \"content\": \"ﾂﾂﾂﾂ\\tAO\\u0005\uFFC0\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"file3\": \"002.e/2007-08-31 12:39/6/e file\",\n" +
            "                    \"content\": \"ﾂ\\tAO\\u0007\uFFC0\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"folder2\": \"etc/2007-08-31 12:39/0/folder\",\n" +
            "            \"subFolder\": [],\n" +
            "            \"file\": [\n" +
            "                {\n" +
            "                    \"file1\": \"003.e/2007-08-31 12:39/6/e file\",\n" +
            "                    \"content\": \"ﾂ\\tAO\\u000f\uFFC0\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"file2\": \"004.e/2007-08-31 12:39/9/e file\",\n" +
            "                    \"content\": \"ﾗ\\u0001AAAAAA\uFFC0\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"file3\": \"005.e/2007-08-31 12:39/8/e file\",\n" +
            "                    \"content\": \"ﾙ\\tOOOOO\uFFC0\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"folder3\": \"drv/2007-08-31 12:39/0/folder\",\n" +
            "            \"subFolder\": [],\n" +
            "            \"file\": [\n" +
            "                {\n" +
            "                    \"file1\": \"006.e/2007-08-31 12:39/8/e file\",\n" +
            "                    \"content\": \"ﾇﾗﾧﾅﾕﾥ\\b\uFFC0\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"file2\": \"007.e/2007-08-31 12:39/8/e file\",\n" +
            "                    \"content\": \"ﾘﾨ\\u000fAAAA\uFFC0\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"file3\": \"008.e/2007-08-31 12:39/10/e file\",\n" +
            "                    \"content\": \"\\u000fAAAAAAAA\uFFC0\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ],\n" +
            "    \"file\": [\n" +
            "        {\n" +
            "            \"file1\": \"009.e/2007-08-31 12:39/9/e file\",\n" +
            "            \"content\": \"\\u0002OOOOOOO\uFFC0\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    public static TreeItem<String> rootItem = new TreeItem<>("OS");


    private void paintTree(JSONObject jsonObject, TreeItem<String> root) throws JSONException {

        //当遇上根目录
        if (jsonObject.has("root")){
            TreeItem<String> item = new TreeItem<>("OS");
            rootItem.getChildren().add(item);
        }
        if (jsonObject.has("subFolder")){

            JSONArray rootsubFolder = jsonObject.getJSONArray("subFolder");

            for (int i = 0; i<rootsubFolder.length(); i++){
                JSONObject jsob = rootsubFolder.getJSONObject(i);
                TreeItem<String> item = new TreeItem<>(getNameByJsonKey(jsob, "folder"+(1+i)));
                root.getChildren().add(item);
                System.out.println(item.getValue());
                paintTree(jsob, item);

            }
        }
        if (jsonObject.has("file")){
            JSONArray rootFile = jsonObject.getJSONArray("file");
            if (rootFile.length() != 0){
                //添加子树
                for (int i = 0; i<rootFile.length(); i++){
                    JSONObject jsonFile = rootFile.getJSONObject(i);
                    TreeItem<String> item = new TreeItem<>(getNameByJsonKey(jsonFile, "file"+(++i)));
                    root.getChildren().add(item);
                    System.out.println(item.getValue());
                }
            }
        }

    }

    private String getNameByJsonKey(JSONObject jsonOB, String key){
        try {
            String content = jsonOB.getString(key);
            String[] fileInfo = content.split("/");
            return fileInfo[0];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args){

        try {
            new JsonParser().paintTree(new JSONObject(JSON), rootItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
