package com.os_simulator.www.Window;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

/**
 * Created by Sean on 2016/10/26.
 */
public class HelpWin extends Win{

    private boolean isUpdate = true;
    private int flag = -1000;
    private int x = -1000;
    private int id;
    private StringBuilder stringBuilder = new StringBuilder();
    private TextArea textArea = new TextArea();

    public HelpWin(Controller controller, String name, int id ,String text){
        super(controller,name,610,325);
        Pane pane = new Pane();
        textArea.setTranslateX(10);
        textArea.setTranslateY(10);
        textArea.setMinSize(590,280);
        textArea.setMaxSize(590,280);
        textArea.setEditable(false);


        pane.getChildren().add(textArea);
        this.setPane(pane);

        this.id = id;
        stringBuilder.append(text);
    }

    @Override
    public void update(){
        textArea.setText(stringBuilder.toString());
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
