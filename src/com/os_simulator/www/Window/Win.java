package com.os_simulator.www.Window;

import com.os_simulator.www.system.SystemCore;
import com.sun.javafx.geom.RoundRectangle2D;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Created by Sean on 2016/11/1.
 */
public abstract class Win {


    protected boolean isUpdate = true;//是否启动更新
    private int thickness = 25;
    private double x,y;//用以拖动效果的参数
    protected String name;//窗口名称
    protected Controller controller;//引用controller
    protected SystemCore systemCore;//应用后台
    protected BorderPane scene = new BorderPane();//模拟scene
    protected StackPane stage = new StackPane();//模拟stage



    public Win(Controller controller, String name, int width, int height){

        this.controller = controller;
        this.systemCore = controller.getSystemCore();
        this.name = name;

        Pane topPane = new Pane();//窗口顶部
        topPane.setMaxSize(width,thickness);
        topPane.setMinSize(width,thickness);
        Rectangle rectangle = new Rectangle(0,0,width,thickness);//黑带长条；
        Circle closeButton = new Circle(thickness,0.5*thickness,0.3*thickness);//关闭按钮；
        Circle minButton = new Circle(2*thickness,0.5*thickness,0.3*thickness);
        Circle maxButton = new Circle(3*thickness,0.5*thickness,0.3*thickness);


        Text winName = new Text("  "+name);//窗口名称
        winName.setTranslateX(0.4*width);
        winName.setTranslateY(20);
        rectangle.setFill(Color.valueOf("#D3D3D3"));//黑带长条设置

        closeButton.setFill(Color.valueOf("#EE6B65"));//关闭按键设置
        minButton.setFill(Color.valueOf("#FCBE53"));
        maxButton.setFill(Color.valueOf("#68C25B"));

        winName.setFill(Color.BLACK);//窗口名称设置
        topPane.getChildren().addAll(rectangle,closeButton,minButton,maxButton,winName);//添加节点
        scene.setTop(topPane);

        Canvas canvas = new Canvas(width,height+thickness);//创建画板
        GraphicsContext gc = canvas.getGraphicsContext2D();//获取画板笔
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(0,0,width,height,5,5);
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(3);//边框厚度设置
        gc.strokeRoundRect(0,0,width,height,5,5);
        stage.setMaxSize(width,height+thickness);//模拟scene大小设置
        stage.setMinSize(width,height+thickness);
        stage.getChildren().addAll(canvas,scene);

        //拖动准备
        stage.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.toFront();
                x = event.getSceneX()-stage.getTranslateX();
                y = event.getSceneY()-stage.getTranslateY();
            }
        });

        //拖动过程
        stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setTranslateX(event.getSceneX() - x);
                stage.setTranslateY(event.getSceneY() - y);
            }
        });

        //关闭按钮设置

        maxButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.closeWin(name);
            }
        });
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.closeWin(name);
            }
        });

        //窗口置顶
        stage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.toFront();
            }
        });
    }

    //设置窗口大小
    public Win(Controller controller, String id){
        this(controller,id,400,300);
    }

    //窗口更新
    public abstract void update() throws Exception;

    //关闭窗口
    public void close(){
        if(controller.getWinHashMap().remove(name)!=null)
            System.out.println("Close:"+name+" succeed");
        else System.out.println("Close:"+name+" failed");
    }

    public void setTranslate(double i){
        stage.setTranslateX(i);
        stage.setTranslateY(i);
    }

    public final Pane getStage(){
        return stage;
    }

    public final void setPane(Pane pane){
        this.scene.setCenter(pane);
    }

    public final String getName(){
        return name;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }
}
