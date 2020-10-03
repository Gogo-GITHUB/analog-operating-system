package com.os_simulator.www.Window;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * Created by Sean on 2016/12/4.
 */
public abstract class SuperWin extends Win {

    protected boolean isOpen = false;//是否已显示在桌面上

    protected Button button = new Button();//快捷键

    public SuperWin(Controller controller, String name, int width, int height) {
        super(controller, name, width, height);
        button.setText(name);

        button.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 50px; " +
                        "-fx-min-height: 50px; " +
                        "-fx-max-width: 50px; " +
                        "-fx-max-height: 50px;"
        );

        //快捷键点击事件
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!isOpen()){
                    controller.positonI = (++controller.positonI)%controller.position.length;
                    controller.getCenterPane().getChildren().add(stage);
                    setTranslate(controller.position[controller.positonI]);
                    setOpen(true);
                }else{
                    getStage().toFront();
                }
            }
        });

        //快捷键的动态效果
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
//                button.setTranslateY(button.getTranslateY()-30);
                button.setStyle(
                        "-fx-background-radius: 5em; " +
                                "-fx-min-width: 70px; " +
                                "-fx-min-height: 70px; " +
                                "-fx-max-width: 70px; " +
                                "-fx-max-height: 70px;"
                );
            }
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
//                button.setTranslateY(button.getTranslateY()+30);
                button.setStyle(
                        "-fx-background-radius: 5em; " +
                                "-fx-min-width: 50px; " +
                                "-fx-min-height: 50px; " +
                                "-fx-max-width: 50px; " +
                                "-fx-max-height: 50px;"
                );
            }
        });
    }
    public SuperWin(Controller controller,String name){
        this(controller,name,400,300);
    }

    //关闭窗口
    public void close(){
        this.setOpen(false);
    }

    @Override
    public abstract void update();

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Button getButton() {
        return button;
    }




}
