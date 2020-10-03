package com.os_simulator.www.Window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sean on 2016/10/26.
 */
public class DiskWin extends SuperWin{

    private int LENGTH = 12;
    private int PANDING = 10;

    ArrayList<Rectangle> rectangles = new ArrayList<>();

    private Image image = new Image("/icons/hardDisk.png");
    private ImageView imageView = new ImageView(image);


    public DiskWin(Controller controller) {
        super(controller, "磁盘状态",400,300);

        VBox pane = new VBox(5);
        HBox hBox = new HBox(5);
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(5);
        flowPane.setVgap(5);
//        Text explaination = new Text("Note:  Red Rectangle - Occupied   Light Green Rectangle - Unoccupied");
        Text head = new Text("Note: ");
        Text occupied = new Text(" Occupied ");
        Text unoccupied = new Text(" Unoccupied ");
        Rectangle a = new Rectangle(LENGTH,LENGTH);
        a.setFill(Color.RED);
        Rectangle b = new Rectangle(LENGTH,LENGTH);
        b.setFill(Color.LIGHTGREEN);
        hBox.getChildren().addAll(head,a,occupied,b,unoccupied);
        pane.getChildren().addAll(hBox,flowPane);
//        pane.getChildren().add(explaination);

        for(int i = 0; i < 256; i++) {
            Rectangle r = new Rectangle(0, 0, LENGTH, LENGTH);
            rectangles.add(r);
            flowPane.getChildren().add(r);
        }

        button.setGraphic(imageView);
        button.setText("");


        pane.setPadding(new Insets(PANDING, PANDING+5, PANDING, PANDING));
        setPane(pane);
    }

    @Override
    public void update() {
        if (isUpdate) {
            int i = 0;
            for (boolean j : systemCore.getDiskBlocks()) {
                if (j) {
                    rectangles.get(i).setFill(Color.RED);
                } else {
                    rectangles.get(i).setFill(Color.LIGHTGREEN);
                }
                i++;
            }
            isUpdate = false;
        }
    }

}