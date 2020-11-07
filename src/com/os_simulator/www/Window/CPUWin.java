package com.os_simulator.www.Window;

import com.os_simulator.www.system.MemoryBlock;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;


public class CPUWin extends SuperWin{

    private ObservableList<String>  preparingProcessIds = FXCollections.observableArrayList();//就绪队列进程ID；
    private ObservableList<String> cloggingProcessIds = FXCollections.observableArrayList();//阻塞队列进程ID；

    //左侧面板相关节点
    private TextField textField1 = new TextField();
    private TextField textField2 = new TextField();
    private TextField textField3 = new TextField();
    private TextField textField4 = new TextField();

    private Image image = new Image("/icons/CPU.png");
    private ImageView imageView = new ImageView(image);

    public CPUWin(Controller controller){
        super(controller,"CPU状态");//创建底层窗口
        //左侧面板设置；
        VBox leftPane = new VBox(5);//面板创建
        leftPane.setPadding(new Insets(10));//面板外边距设置
        Text text1 = new Text("运行中进程ID:");
        Text text2 = new Text("剩余时间片:");
        Text text3 = new Text("正在执行指令:");
        Text text4 = new Text("系统时间：");
        text1.setStyle("-fx-font-family: Times New Roman");
        text1.setStyle("-fx-font-weight:bold");
        text2.setStyle("-fx-font-family: Times New Roman");
        text2.setStyle("-fx-font-weight:bold");
        text3.setStyle("-fx-font-family: Times New Roman");
        text3.setStyle("-fx-font-weight:bold");
        text4.setStyle("-fx-font-family: Times New Roman");
        text4.setStyle("-fx-font-weight:bold");

        textField1.setEditable(false);
        textField1.setMaxSize(100,20);
        textField2.setEditable(false);
        textField2.setMaxSize(100,20);
        textField3.setEditable(false);
        textField3.setMaxSize(100,20);
        textField4.setEditable(false);
        textField4.setMaxSize(100,20);

        textField1.setStyle("-fx-font-family: Times New Roman");
        textField1.setStyle("-fx-font-weight:bold");
        textField2.setStyle("-fx-font-family: Times New Roman");
        textField2.setStyle("-fx-font-weight:bold");
        textField3.setStyle("-fx-font-family: Times New Roman");
        textField3.setStyle("-fx-font-weight:bold");
        textField4.setStyle("-fx-font-family: Times New Roman");
        textField4.setStyle("-fx-font-weight:bold");

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
        button.setText("");

        leftPane.getChildren().addAll(text1,textField1,text2,textField2,text3,textField3,text4,textField4);//添加节点

        //中间面板
        VBox centerPane = new VBox(5);//创建面板
        centerPane.setPadding(new Insets((10)));//面板外边距设置
        ListView listView1 = new ListView(preparingProcessIds);//创建ListView
        listView1.setMaxSize(120,200);//ListView大小设置
        Text text5 = new Text("就绪队列：");
        text5.setStyle("-fx-font-family: Times New Roman");
        text5.setStyle("-fx-font-weight:bold");
        centerPane.getChildren().addAll(text5,listView1);//添加节点

        //右侧面板
        VBox rightPane = new VBox(5);//创建面板
        rightPane.setPadding(new Insets(10));//面板外边距设置
        ListView listView2 = new ListView(cloggingProcessIds);//创建ListView
        listView2.setMaxSize(120,200);//ListView大小设置
        Text text6 = new Text("阻塞队列：");
        text6.setStyle("-fx-font-family: Times New Roman");
        text6.setStyle("-fx-font-weight:bold");
        rightPane.getChildren().addAll(text6,listView2);//添加节点

        //主面板设置；
        BorderPane pane = new BorderPane();//创建面板
        scene.setLeft(leftPane);//左侧面板置左；
        pane.setCenter(centerPane);
        pane.setRight(rightPane);

        //模拟scene设置；

        this.setPane(pane);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                        textField1.setText(systemCore.getRunningID()+"");
                        textField2.setText(systemCore.getRemainTimeBlock()+"");
                        textField3.setText(systemCore.getExecuteCommand());
                        textField4.setText(systemCore.getSystemTime()+"");
                        flash();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    /**
     *
     */
    //更新数据
    @Override
    public void update(){

    }

    private void flash(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!preparingProcessIds.isEmpty())
                    preparingProcessIds.clear();
                if (!systemCore.getReadyID().isEmpty()) {
                    for (int i : systemCore.getReadyID())
                        preparingProcessIds.add(i + "");
                    preparingProcessIds.add("");
                }

                if (!cloggingProcessIds.isEmpty())
                    cloggingProcessIds.clear();
                if (!systemCore.getWaitID().isEmpty()) {
                    for (int i : systemCore.getWaitID())
                        cloggingProcessIds.add(i + "");
                    cloggingProcessIds.add("");
                }
            }
        });
    }


}
