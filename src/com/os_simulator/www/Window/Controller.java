package com.os_simulator.www.Window;


import com.os_simulator.www.system.SystemCore;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class Controller{

    public static SystemCore systemCore = new SystemCore();//激活后台
    private ReentrantLock lock = new ReentrantLock();//资源锁
    private ArrayList<String> appList = new ArrayList<>();//应用名称列表
    private ArrayList<String> superAppLiat = new ArrayList<>();//后台窗口应用列表
    private HashMap<String,Win> winHashMap = new HashMap<>();//哈希表，存放窗口名称和窗口
    private HashMap<String,String> pathHashMap = new HashMap<>();//存放应用名称和路径
    private CPUWin cpuWin = new CPUWin(this);//创建cpuu状态窗口
    private DeviceWin deviceWin = new DeviceWin(this);//创建设备状态窗口
    private DiskWin diskWin = new DiskWin(this);//创建磁盘状态窗口
    private ControllerSetter controllerSetter = new ControllerSetter(this);//创建设置窗口窗口
    private DictionaryWin dictionaryWin = new DictionaryWin(this);
    private MainMemoryWin mainMemoryWin = new MainMemoryWin(this);
    private TerminalWin terminalWin = new TerminalWin(this);
    private Pane base = new Pane();//创建基底
    final int[] position = {20,40,60,80,100,120};//存储坐标列表，用以设定窗口建立的坐标
    int positonI = 0;//当前坐标列表下标
    private Pane centerPane = new Pane();//中间面板创建，外部可设置
    private MenuBar menuBar = new MenuBar();//设置菜单栏，外部可设置
    private DoubleProperty baseWidth = new SimpleDoubleProperty((int) Toolkit.getDefaultToolkit().getScreenSize().width);//界面宽度
    private DoubleProperty baseHeight = new SimpleDoubleProperty((int)Toolkit.getDefaultToolkit().getScreenSize().height);//界面高度
    private ImageView imageView = new ImageView(new Image("/Background/El Capitan.jpg"));
    private ImageView imageView1 = new ImageView(new Image("/icons/app.jpg"));
    private boolean isFullScreen = true;//全屏参数
    private Stage primaryStage;//引用主窗口

    public Controller(Stage primaryStage) {


        this.primaryStage = primaryStage;

        for (String path:systemCore.getSamplePaths()){
            System.out.println(path);
            String[] string = path.split("/");
            String name = string[string.length-1];
            appList.add(name);
            pathHashMap.put(name,path);
        }

        //运行更新模块
        UpdateCenter updateCenter = new UpdateCenter(this);
        new Thread(updateCenter).start();

        winHashMap.put(cpuWin.getName(),cpuWin);//存放cpu窗口
        winHashMap.put(deviceWin.getName(),deviceWin);//存放设备窗口
        winHashMap.put(diskWin.getName(),diskWin);//存放磁盘窗口
        winHashMap.put(controllerSetter.getName(),controllerSetter);//存放应用列表窗口
        winHashMap.put(dictionaryWin.getName(),dictionaryWin);
        winHashMap.put(mainMemoryWin.getName(),mainMemoryWin);
        winHashMap.put(terminalWin.getName(),terminalWin);
        superAppLiat.add(cpuWin.getName());
        superAppLiat.add(mainMemoryWin.getName());
        superAppLiat.add(diskWin.getName());
        superAppLiat.add(dictionaryWin.getName());
        superAppLiat.add(deviceWin.getName());
        superAppLiat.add(terminalWin.getName());


        //顶部面板设置；
        StackPane topPane = new StackPane();
        topPane.minWidthProperty().bind(baseWidth);//属性绑定
        topPane.maxWidthProperty().bind(baseWidth);
        topPane.setMinHeight(30);
        topPane.setMaxHeight(30);
        menuBar.minWidthProperty().bind(topPane.minHeightProperty());
        Menu menu = new Menu("    菜单    ");//菜单按钮
        MenuItem close = new MenuItem("——关机——");//关机按钮
        MenuItem setter = new MenuItem("——设置——");
        MenuItem reset = new MenuItem("—窗口重置—");
        menu.getItems().addAll(close,setter,reset);
        menuBar.getMenus().addAll(menu);
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        Text timeText = new Text();//当前时间文本
        Thread runningTime = new Thread(){//内部类线程，不停获取当前时间
            @Override
            public void run() {
                while(true)
                    timeText.setText(time.format(new Date()));
            }
        };//获取当前时间的线程；
        runningTime.start();//线程运行
        timeText.setFont(Font.font(15));//时间文本字体
        topPane.getChildren().addAll(menuBar,timeText);



        //底部面板设置；
        HBox bottomPane = new HBox(5);//面板创建
        bottomPane.setAlignment(Pos.CENTER);//节点居中
        bottomPane.minWidthProperty().bind(baseWidth);//属性绑定
        bottomPane.maxWidthProperty().bind(baseWidth);
        bottomPane.setMinHeight(40);
        bottomPane.setMaxHeight(40);

        Button appsButton = new Button("应用列表");//创建应用列表窗口快捷键
        appsButton.setText("");
        imageView1.setFitHeight(30);
        imageView1.setFitWidth(30);
        appsButton.setGraphic(imageView1);
        appsButton.setMinSize(80,30);
        appsButton.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 50px; " +
                        "-fx-min-height: 50px; " +
                        "-fx-max-width: 50px; " +
                        "-fx-max-height: 50px;"
        );
        for (String string:superAppLiat)
            bottomPane.getChildren().add(((SuperWin)winHashMap.get(string)).getButton());
        bottomPane.getChildren().addAll(appsButton);//添加按钮
        AppPane appPane = new AppPane(this,appList);

        //中间面板设置；
        centerPane.minWidthProperty().bind(baseWidth);//属性绑定
        centerPane.maxWidthProperty().bind(baseWidth);
        centerPane.minHeightProperty().bind(baseHeight.subtract(70));
        centerPane.maxHeightProperty().bind(baseHeight.subtract(70));

        //主要面板设置；
        BorderPane pane = new BorderPane();//创建面板
        pane.setBottom(bottomPane);//底部面板置底
        pane.setCenter(centerPane);//中间面板置中
        pane.setTop(topPane);

        //模拟scene设置；
        imageView.fitHeightProperty().bind(baseHeightProperty());
        imageView.fitWidthProperty().bind(baseWidthProperty());
        Rectangle rectangle = new Rectangle(0,0);//边缘边框
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(null);
        rectangle.heightProperty().bind(baseHeight);//属性绑定
        rectangle.widthProperty().bind(baseWidth);
        base.minWidthProperty().bind(baseWidth);
        base.maxWidthProperty().bind(baseWidth);
        base.minHeightProperty().bind(baseHeight);
        base.maxHeightProperty().bind(baseHeight);
        base.setPadding(new Insets(10,10,10,10));
        base.getChildren().addAll(imageView,rectangle,pane);

        //关机按钮事件
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        //设置按钮事件
        setter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!controllerSetter.isOpen()){
                    positonI = (++positonI)%position.length;
                    centerPane.getChildren().add(controllerSetter.getStage());
                    controllerSetter.getStage().setTranslateX(position[positonI]);
                    controllerSetter.getStage().setTranslateY(position[positonI]);
                    controllerSetter.setOpen(true);
                }
            }
        });

        //窗口全关按钮事件
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Win win:winHashMap.values()){
                    win.setTranslate(0);
                }
            }
        });

        appsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!appPane.isOpen()){
                    centerPane.getChildren().add(appPane.getStage());
                    appPane.setOpen(true);
                }
                else{
                    centerPane.getChildren().remove(appPane.getStage());
                    appPane.setOpen(false);
                }
            }
        });

        //应用列表窗口快捷键的动态效果
        appsButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!appPane.isOpen()){
                    appsButton.setStyle(
                            "-fx-background-radius: 5em; " +
                                    "-fx-min-width: 70px; " +
                                    "-fx-min-height: 70px; " +
                                    "-fx-max-width: 70px; " +
                                    "-fx-max-height: 70px;"
                    );
                }
//                appsButton.setTranslateY(appsButton.getTranslateY()-5);
            }
        });
        appsButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!appPane.isOpen()){
                    appsButton.setStyle(
                            "-fx-background-radius: 5em; " +
                                    "-fx-min-width: 50px; " +
                                    "-fx-min-height: 50px; " +
                                    "-fx-max-width: 50px; " +
                                    "-fx-max-height: 50px;"
                    );
                }
//                appsButton.setTranslateY(appsButton.getTranslateY()+5);
            }
        });
    }


    //关闭窗口的方法
    public void closeWin(String name){
        lock.lock();
        Win win = winHashMap.get(name);
        win.close();
        centerPane.getChildren().remove(win.getStage());
        lock.unlock();
    }


    //创建窗口的方法，多用于可运行文件窗口的创建
    public void creatWin(String name,String path){
        int id = -1;
        try {
            id = systemCore.startApplication(path);
            System.out.println("Open:" + name);
        }catch (Exception e){
            System.err.println("Open:" +name+" fail");
        }finally {
            FileWin win = new FileWin(this,name,id);
            this.getCenterPane().getChildren().add(win.getStage());
            this.getWinHashMap().put(win.getName(), win);
            positonI = (++positonI) % position.length;
            win.setTranslate(position[positonI]);
        }
    }

    public Pane getBase() {
        return base;
    }

    public HashMap<String, Win> getWinHashMap() {
        return winHashMap;
    }

    public Pane getCenterPane() {
        return centerPane;
    }


    public MenuBar getMenuBar() {
        return menuBar;
    }

    public DoubleProperty baseWidthProperty() {
        return baseWidth;
    }

    public DoubleProperty baseHeightProperty() {
        return baseHeight;
    }

    public void setSize(double baseWidth,double baseHeight){
        baseWidthProperty().set(baseWidth);
        baseHeightProperty().set(baseHeight);
    }

    public ImageView getImageView() {
        return imageView;
    }


    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public SystemCore getSystemCore() {
        return systemCore;
    }

    public void setSystemCore(SystemCore systemCore) {
        this.systemCore = systemCore;
    }

    public HashMap<String, String> getPathHashMap() {
        return pathHashMap;
    }


    public ReentrantLock getLock() {
        return lock;
    }

}


//应用列表窗口类
class AppPane{
    private boolean isOpen = false;//是否显示在屏幕上
    private StackPane stage = new StackPane();
    private HBox scene = new HBox(5);
    private Controller controller;//引用controller

    ArrayList<ImageView> imageviews = new ArrayList<>();
    private ImageView i1 = new ImageView(new Image("/icons/weixin.jpg"));
    private ImageView i2 = new ImageView(new Image("/icons/qq.jpg"));
    private ImageView i3 = new ImageView(new Image("/icons/weibo.jpg"));
    private ImageView i4 = new ImageView(new Image("/icons/bili.jpg"));
    private ImageView i5 = new ImageView(new Image("/icons/taobao.jpg"));
    private ImageView i6 = new ImageView(new Image("/icons/idea.jpg"));
    private ImageView i7 = new ImageView(new Image("/icons/ec.jpg"));
    private ImageView i8 = new ImageView(new Image("/icons/zhihu.jpg"));
    private ImageView i9 = new ImageView(new Image("/icons/ps.jpg"));
    private ImageView i10 = new ImageView(new Image("/icons/code.jpg"));

    public void imageviews() {
        i1.setFitWidth(30);
        i1.setFitHeight(30);
        i2.setFitHeight(30);
        i2.setFitWidth(30);
        i3.setFitHeight(30);
        i3.setFitWidth(30);
        i4.setFitHeight(30);
        i4.setFitWidth(30);
        i5.setFitHeight(30);
        i5.setFitWidth(30);
        i6.setFitHeight(30);
        i6.setFitWidth(30);
        i7.setFitHeight(30);
        i7.setFitWidth(30);
        i8.setFitHeight(30);
        i8.setFitWidth(30);
        i9.setFitHeight(30);
        i9.setFitWidth(30);
        i10.setFitHeight(30);
        i10.setFitWidth(30);
        imageviews.add(i1);
        imageviews.add(i2);
        imageviews.add(i3);
        imageviews.add(i4);
        imageviews.add(i5);
        imageviews.add(i6);
        imageviews.add(i7);
        imageviews.add(i8);
        imageviews.add(i9);
        imageviews.add(i10);
    }

    public AppPane(Controller controller, ArrayList<String> appList){
        int i=0;
        this.controller = controller;
        stage.translateYProperty().bind(controller.baseHeightProperty().subtract(160));//属性绑定
        stage.minWidthProperty().bind(controller.baseWidthProperty());
        stage.maxWidthProperty().bind(controller.baseWidthProperty());
        stage.setMinHeight(60);
        stage.setMaxHeight(60);
        scene.setAlignment(Pos.CENTER);
        stage.getChildren().add(scene);
        imageviews();
        //对应用注册表内全部应用创建应用启动按钮
        for (String app:appList){
            Button button = new Button(app);
            button.setGraphic(imageviews.get(i));
            button.setMinSize(40,40);
            i++;
            //应用启动按钮事件
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (controller.getWinHashMap().get(app) == null) {
                        controller.creatWin(app,controller.getPathHashMap().get(app));
                    }else{
                        controller.getWinHashMap().get(app).getStage().toFront();
                    }
                }
            });
            button.setStyle(
                    "-fx-background-radius: 5em; " +
                            "-fx-min-width: 50px; " +
                            "-fx-min-height: 50px; " +
                            "-fx-max-width: 50px; " +
                            "-fx-max-height: 50px;"
            );
            //应用启动按钮事件
            button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
//                    button.setMinSize(80,60);
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
//                    button.setMinSize(40,40);
                    button.setStyle(
                            "-fx-background-radius: 5em; " +
                                    "-fx-min-width: 50px; " +
                                    "-fx-min-height: 50px; " +
                                    "-fx-max-width: 50px; " +
                                    "-fx-max-height: 50px;"
                    );
                }
            });
            scene.getChildren().add(button);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }


    public StackPane getStage() {
        return stage;
    }

}

//OS的设置窗口
class ControllerSetter extends SuperWin{

    private int fullScreenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().width;//获取屏幕分辨率，用于全屏效果
    private int fullScreenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().height;
    ComboBox comboBox;//OS分辨率选择框
    ComboBox backgroundSelect;//壁纸选择框
//    TextField bizhiAddress;//壁纸地址输入框

    public ControllerSetter(Controller controller) {
        super(controller, "设置窗口");
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5);
        pane.setVgap(5);

        Text fenbianlv = new Text("分辨率设置：");
        ObservableList<String> options = FXCollections.observableArrayList("全屏","800*600","1280*720","1024*768");
        comboBox = new ComboBox(options);
        comboBox.setPromptText("全屏");
        String[] backgrounds = new File("res/Background").list();
        ObservableList<String> backgroundList = FXCollections.observableArrayList(backgrounds);
        backgroundSelect = new ComboBox(backgroundList);
        backgroundSelect.setPromptText("El Capitan.jpg");
        Button save = new Button("应用");
        Text bizhi = new Text("壁纸设置：");


        pane.add(fenbianlv,0,0);//布局
        pane.add(comboBox,1,0);
        pane.add(bizhi,0,1);
        pane.add(backgroundSelect,1,1);
        pane.add(save,2,0);
        setPane(pane);

        //保存设置事件
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    work();
            }
        });
    }

    @Override
    public void update() {

    }

    //更新OS的显示
    public void work(){
        boolean isFullScreen = true;
        try {
            controller.getImageView().setImage(new Image("/Background/" + backgroundSelect.getValue()));
        }catch (Exception e){
            System.err.println("Background error!");
        }
        switch (comboBox.getValue().toString()){
            case "全屏":controller.setSize(fullScreenWidth,fullScreenHeight);
                isFullScreen = true;
                break;
            case "800*600":controller.setSize(800,600);
                isFullScreen = false;
                break;
            case "1280*720":controller.setSize(1280,720);
                isFullScreen = false;
                break;
            case "1024*768":controller.setSize(1024,768);
                isFullScreen = false;
            default:break;
        }
        controller.getPrimaryStage().setFullScreen(isFullScreen);
        controller.setFullScreen(isFullScreen);
    }


}