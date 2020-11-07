package com.os_simulator.www.Window;


import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class DeviceWin extends SuperWin {

    private Image image = new Image("/icons/devices.png");
    private ImageView imageView = new ImageView(image);

    Pane pane = new Pane();
    Text head = new Text(140, 30, "Assigned(分配)\t\t\tOccupied(占用)\t\t\tWait(等待)");
    Text A1 = new Text(10, 60, "Device A(1)");
    Text A2 = new Text(10, 105, "Device A(2)");
    Text B1 = new Text(10, 150, "Device B(1)");
    Text B2 = new Text(10, 195, "Device B(2)");
    Text B3 = new Text(10, 240, "Device B(3)");
    Text C1 = new Text(10, 285, "Device C(1)");
    Text C2 = new Text(10, 330, "Device C(2)");
    Text C3 = new Text(10, 375, "Device C(3)");
    Text r1 = new Text(160, 62, "false");
    Text occupiedProcess1 = new Text(330, 60, "null");
    Text waitProcess1 = new Text(500, 60, "null");
    Text r2 = new Text(160, 107, "false");
    Text occupiedProcess2 = new Text(330, 105, "null");
    Text waitProcess2 = new Text(500, 105, "null");
    Text r3 = new Text(160, 152, "false");
    Text occupiedProcess3 = new Text(330, 150, "null");
    Text waitProcess3 = new Text(500, 150, "null");
    Text r4 = new Text(160, 197, "false");
    Text occupiedProcess4 = new Text(330, 195, "null");
    Text waitProcess4 = new Text(500, 195, "null");
    Text r5 = new Text(160, 242, "false");
    Text occupiedProcess5 = new Text(330, 240, "null");
    Text waitProcess5 = new Text(500, 240, "null");
    Text r6 = new Text(160, 287, "false");
    Text occupiedProcess6 = new Text(330, 285, "null");
    Text waitProcess6 = new Text(500, 285, "null");
    Text r7 = new Text(160, 332, "false");
    Text occupiedProcess7 = new Text(330, 330, "null");
    Text waitProcess7 = new Text(500, 330, "null");
    Text r8 = new Text(160, 377, "false");
    Text occupiedProcess8 = new Text(330, 375, "null");
    Text waitProcess8 = new Text(500, 375, "null");

    public DeviceWin(Controller controller) {
        super(controller, "设备状态",700,430);

        pane.getChildren().addAll(head, A1, A2, B1, B2, B3, C1, C2, C3);
        pane.getChildren().addAll(r1, occupiedProcess1, waitProcess1);
        pane.getChildren().addAll(r2, occupiedProcess2, waitProcess2);
        pane.getChildren().addAll(r3, occupiedProcess3, waitProcess3);
        pane.getChildren().addAll(r4, occupiedProcess4, waitProcess4);
        pane.getChildren().addAll(r5, occupiedProcess5, waitProcess5);
        pane.getChildren().addAll(r6, occupiedProcess6, waitProcess6);
        pane.getChildren().addAll(r7, occupiedProcess7, waitProcess7);
        pane.getChildren().addAll(r8, occupiedProcess8, waitProcess8);
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
        button.setText("");
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
        setPane(pane);

    }

    public void paintInfo(Pane pane, String device, boolean assign, String occupy, String wait) {

        switch(device) {

            case "A1":
                r1.setText(""+ assign);
                occupiedProcess1.setText(occupy);
                waitProcess1.setText(wait);
                break;
            case "A2":
                r2.setText(""+ assign);
                occupiedProcess2.setText(occupy);
                waitProcess2.setText(wait);
                break;
            case "B1":
                r3.setText(""+ assign);
                occupiedProcess3.setText(occupy);
                waitProcess3.setText(wait);
            case "B2":
                r4.setText(""+ assign);
                occupiedProcess4.setText(occupy);
                waitProcess4.setText(wait);
                break;
            case "B3":
                r5.setText(""+ assign);
                occupiedProcess5.setText(occupy);
                waitProcess5.setText(wait);
                break;
            case "C1":
                r6.setText(""+ assign);
                occupiedProcess6.setText(occupy);
                waitProcess6.setText(wait);
                break;
            case "C2":
                r7.setText(""+ assign);
                occupiedProcess7.setText(occupy);
                waitProcess7.setText(wait);
                break;
            case "C3":
                r8.setText(""+ assign);
                occupiedProcess8.setText(occupy);
                waitProcess8.setText(wait);
        }

    }


    @Override
    public void update() {
        paintInfo(pane, "A1", systemCore.getDevicesStatus("A1"), "null", systemCore.getWaitDevicesList("A").toString());
        paintInfo(pane, "A2", systemCore.getDevicesStatus("A2"), "null", "");
        paintInfo(pane, "B1", systemCore.getDevicesStatus("B1"), "null", systemCore.getWaitDevicesList("B").toString());
        paintInfo(pane, "B2", systemCore.getDevicesStatus("B2"), "null", "");
        paintInfo(pane, "B3", systemCore.getDevicesStatus("B3"), "null", "");
        paintInfo(pane, "C1", systemCore.getDevicesStatus("C1"), "null", systemCore.getWaitDevicesList("C").toString());
        paintInfo(pane, "C2", systemCore.getDevicesStatus("C2"), "null", "");
        paintInfo(pane, "C3", systemCore.getDevicesStatus("C3"), "null", "");
    }

}
