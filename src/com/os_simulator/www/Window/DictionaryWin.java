package com.os_simulator.www.Window;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class DictionaryWin extends SuperWin {

    private TreeItem<String> rootItem = new TreeItem<>("OS_simulator");
    private String fileDir = systemCore.getDiskDir(); // 从后台获得系统的全局目录

    private Image image = new Image("/icons/dir.png");
    private ImageView imageView = new ImageView(image);

    public DictionaryWin(Controller controller) {
        super(controller,"磁盘目录");
        rootItem.setExpanded(false); // 程序启动时不自动展开根节点
        TreeView<String> tree = new TreeView<>(rootItem);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(tree);


        button.setText("");
        imageView.setFitHeight(70);
        imageView.setFitWidth(70);
        button.setGraphic(imageView);
        button.setStyle(
                "-fx-background-radius: 10em; " +
                        "-fx-min-width: 100px; " +
                        "-fx-min-height: 100px; " +
                        "-fx-max-width: 100px; " +
                        "-fx-max-height: 100px;"
        );
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                {
                    button.setStyle(
                            "-fx-background-radius: 5em; " +
                                    "-fx-min-width: 120px; " +
                                    "-fx-min-height: 120px; " +
                                    "-fx-max-width: 120px; " +
                                    "-fx-max-height: 120px;"
                    );
                }
//                appsButton.setTranslateY(appsButton.getTranslateY()-5);
            }
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                {
                    button.setStyle(
                            "-fx-background-radius: 5em; " +
                                    "-fx-min-width: 100px; " +
                                    "-fx-min-height: 100px; " +
                                    "-fx-max-width: 100px; " +
                                    "-fx-max-height: 100px;"
                    );
                }
//                appsButton.setTranslateY(appsButton.getTranslateY()+5);
            }
        });
        setPane(stackPane);
    }

    /**
     * 解析Json字符串
     *
     * @param jsonString
     *        Json数据字符串
     * @throws ParseException
     */
    public  JSONObject parseJson(String jsonString) throws ParseException, JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        System.out.println(jsonObject);

        return jsonObject;
    }



    /**
     * genage修改json的解析，使用简单第归遍历数据， 重新建立目录结构（可以第归到更深的目录）
     * @param jsonObject
     * @param root
     * @throws JSONException
     */
    private void paintTree(JSONObject jsonObject, TreeItem<String> root) throws JSONException {
//        //当遇上根目录
//        if (jsonObject.has("root")){
//            TreeItem<String> item = new TreeItem<>("OS");
//            root.getChildren().add(item);
//        }
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
                    TreeItem<String> item = new TreeItem<>(getNameByJsonKey(jsonFile, "file"+(1+i)));
                    root.getChildren().add(item);
                    System.out.println(item.getValue());
                }
            }
        }

    }

    /**
     * 通过JSONobject获取文件夹和文件的名字
     * @param jsonOB
     * @param key
     * @return
     */
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

    @Override
    public void update() {
        if (isUpdate){
            isUpdate = false;
            try {
                String JSONTmp = systemCore.getDiskDir();
                JSONObject jsonObject = parseJson(JSONTmp);
                if (jsonObject == null){
                    //当后台获取的字符串出错，继续使用
                    jsonObject = parseJson(fileDir);
                }else {
                    //没有出错则继续保存最近一次获得的字符串
                    fileDir = JSONTmp;
                }
                rootItem.getChildren().clear();
                TreeItem<String> root = new TreeItem<>("root");
                rootItem.getChildren().add(root);
                paintTree(jsonObject, root);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){

    }
}
