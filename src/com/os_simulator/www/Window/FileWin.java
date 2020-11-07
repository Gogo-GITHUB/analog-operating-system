package com.os_simulator.www.Window;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;


public class FileWin extends Win{

    private boolean isUpdate = true;
    private int flag = -1000;
    private int x = -1000;
    private int id;
    private StringBuilder stringBuilder = new StringBuilder();
    private TextArea textArea = new TextArea();

    public FileWin(Controller controller, String name, int id){
        super(controller,name);
        Pane pane = new Pane();
        textArea.setTranslateX(10);
        textArea.setTranslateY(10);
        textArea.setMinSize(380,250);
        textArea.setMaxSize(380,250);
        textArea.setStyle("-fx-font-family: Times New Roman");
        textArea.setStyle("-fx-font-weight:bold");
        pane.getChildren().add(textArea);
        this.setPane(pane);

        this.id = id;
        stringBuilder.append("PID:"+id+"\n"+this.getName()+" is running"+"\n==================\n");

    }

    @Override
    public void update(){
        textArea.setEditable(false);
        if(isUpdate) {
            int y = controller.getSystemCore().getX(id);
            if (y == flag){
                stringBuilder.append("==================\n"+this.getName()+" ends");
                isUpdate = false;
            }
            else if (x != y) {
                stringBuilder.append("x=" + y + "\n");
                x = y;
            }
            textArea.setText(stringBuilder.toString());
        }
    }

    public void close(){
        super.close();
        systemCore.closeApplication(id);
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public void setStringBuilder(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

}
