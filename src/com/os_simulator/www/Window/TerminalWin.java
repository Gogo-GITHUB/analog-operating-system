package com.os_simulator.www.Window;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Created by Sean on 2016/12/4.
 */
public class TerminalWin extends SuperWin {

    private List<String> history = new ArrayList<>();
//    private Stack<String> history = new Stack<>();

    private Image image = new Image("/icons/terminal.png");
    private ImageView imageView = new ImageView(image);


    String command;
    String string2;

    int time = 0;

    public TerminalWin(Controller controller){

        super(controller, "终端处理", 600, 450);

        VBox pane = new VBox();

        TextArea textArea = new TextArea();
        textArea.setMaxSize(600,420);
        textArea.setMinSize(600,420);
        textArea.setStyle("-fx-text-fill:white");
        textArea.setStyle("-fx-control-inner-background: black;");
        /*textArea.setStyle("-fx-text-fill: rgba(0, 0, 0, 0.64);");*/
        textArea.setEditable(false);

//        TextArea textArea1 = new TextArea("Peters-MacBook-Pro1:");
//        textArea1.setMaxSize(150,30);
//        textArea1.setMinSize(150,30);
//        textArea1.setStyle("-fx-text-fill:white");
//        textArea1.setStyle("-fx-control-inner-background: black;");
//        textArea1.setEditable(false);

        TextField textField = new TextField();
        textField.setText("Peters-MacBook-Pro:");
        textField.setMaxSize(600,30);
        textField.setMinSize(600,30);

        textField.setStyle("-fx-text-fill:white");
        textField.setStyle("-fx-control-inner-background: black;");
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
        pane.getChildren().addAll(textArea ,textField);
        setPane(pane);

        textField.setOnKeyReleased(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER){
                    time = 1;
                    command = textField.getText().substring(19);
                    history.add(command);
                    textField.setText("Peters-MacBook-Pro:");
                    textField.positionCaret(19);
                    string2 = systemCore.executeShellCommand(command);
                    if (command == "" && command == null){
//                        string2 = "指令错误";
                    }else {
                        controller.getWinHashMap().get("磁盘目录").setUpdate(true);
                    }
                    textArea.appendText("Peters-MacBook-Pro:"+command+"\nPeters-MacBook-Pro:"+string2+"\n");
                    controller.getWinHashMap().get("磁盘状态").setUpdate(true);

                    switch (command){
                        case "clear":
                            textArea.setText("");
                            break;
                        case "time":
                            textArea.appendText("当前系统运行了"+systemCore.getSystemTime()+"个时间片\n");
                            break;
                        case "system":
                            textArea.setText("");
                            textArea.setText("课程设计的开发人员：\n================\n\t朱勇杰,林明凭\n\t林嘉渝,梁晓嘉\n\t彭志凯\n================\n");
                            break;

                    }
                }
                if (event.getCode() == KeyCode.F1){
                    if (history.size() > history.size()-time && history.size() - time > 0){
                        textField.setText(history.get(history.size()-time));
                        time++;
                    }
                }
                if (event.getCode() == KeyCode.F2){
                    time--;
                    if (history.size()-time > 0 && time > 0){
                        textField.setText(history.get(history.size()-time));
                    }
                }
            }
        });



    }

    @Override
    public void update() {

    }
}

