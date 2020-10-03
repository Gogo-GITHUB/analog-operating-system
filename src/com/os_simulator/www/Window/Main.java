package com.os_simulator.www.Window;

import com.os_simulator.www.system.SystemCore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private double x,y;


    @Override
    public void start(Stage primaryStage) throws Exception {

        StackPane readyPane = new StackPane();//欢迎界面

        readyPane.setStyle("-fx-background-color:#7EC0EE");
        Text wellcome = new Text("Welcome");
        wellcome.setFont(new Font(50));
        readyPane.getChildren().add(wellcome);
        Scene readyScene = new Scene(readyPane, 1280, 750);
        primaryStage.setScene(readyScene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();

        Controller controller = new Controller(primaryStage);;

        primaryStage.minWidthProperty().bind(controller.baseWidthProperty());//属性绑定
        primaryStage.minHeightProperty().bind(controller.baseHeightProperty());
        primaryStage.maxWidthProperty().bind(controller.baseWidthProperty());
        primaryStage.maxHeightProperty().bind(controller.baseHeightProperty());

        controller.getMenuBar().addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                    x = event.getScreenX() - primaryStage.getX();
                    y = event.getScreenY() - primaryStage.getY();
            }
        });

        controller.getMenuBar().addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!controller.isFullScreen()) {
                    primaryStage.setX(event.getScreenX() - x);
                    primaryStage.setY(event.getScreenY() - y);
                }
            }
        });

        Scene scene = new Scene(controller.getBase());//主界面
        readyPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setScene(scene);
            }
        });



    }
    public static void main(String[] args) {

        launch(args);



    }
}
