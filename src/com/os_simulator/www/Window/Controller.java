package com.os_simulator.www.Window;


import com.os_simulator.www.system.SystemCore;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
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
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class Controller{

    public static SystemCore systemCore = new SystemCore();//激活后台


    public static boolean dark=false;

    private ReentrantLock lock = new ReentrantLock();//资源锁
    private ArrayList<String> appList = new ArrayList<>();//应用名称列表
    private ArrayList<String> superAppList = new ArrayList<>();//后台窗口应用列表
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
    private ImageView imageView1 = new ImageView(new Image("/icons/app.png"));
    private boolean isFullScreen = true;//全屏参数
    private Stage primaryStage;//引用主窗口
    ArrayList<String> names=new ArrayList<>();
    void addname(){
        names.add("weixin");
        names.add("qq");
        names.add("weibo");
        names.add("itunes");
        names.add("taobao");
        names.add("idea");
        names.add("eclipse");
        names.add("zhihu");
        names.add("photoshops");
        names.add("chrome");
    }
    public Controller(Stage primaryStage) {
        addname();
        int i=0;
        this.primaryStage = primaryStage;

        for (String path:systemCore.getSamplePaths()){
            System.out.println(path);
            String name =names.get(i);
            appList.add(name);
            pathHashMap.put(name,path);
            i++;
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



        superAppList.add(cpuWin.getName());
        superAppList.add(mainMemoryWin.getName());
        superAppList.add(diskWin.getName());
        superAppList.add(dictionaryWin.getName());
        superAppList.add(deviceWin.getName());
        superAppList.add(terminalWin.getName());


        //顶部面板设置；
        StackPane topPane = new StackPane();
        topPane.setStyle("-fx-background-color: #D3D3D3");
        topPane.minWidthProperty().bind(baseWidth);//属性绑定
        topPane.maxWidthProperty().bind(baseWidth);
        topPane.setMinHeight(30);
        topPane.setMaxHeight(30);
        Controller controller=this;
        menuBar.minWidthProperty().bind(topPane.minHeightProperty());
        Menu menu = new Menu("    菜单    ");//菜单按钮
        menu.setStyle("-fx-text-fill:white");
        MenuItem close = new MenuItem("关机");//关机按钮
        MenuItem setter = new MenuItem("设置");
        MenuItem reset = new MenuItem("窗口重置");
        MenuItem dark=new MenuItem("夜间模式");
        MenuItem test=new MenuItem("xx");
        Menu help_menu = new Menu( "帮助");
        MenuItem command = new MenuItem("终端命令指南");
        MenuItem shortcut = new MenuItem("快捷键指南");
        MenuItem member = new MenuItem("课设成员");
        Menu window_menu = new Menu("窗口");
        Menu goTo_menu = new Menu("前往");
        MenuItem cpuWindow=new MenuItem("CPU状态");
        MenuItem diskWindow=new MenuItem("磁盘状态");
        MenuItem memoryWindow=new MenuItem("内存状态");
        MenuItem deviceWindow=new MenuItem("设备状态");
        cpuWindow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!cpuWin.isOpen()){
                    controller.positonI = (++controller.positonI)%controller.position.length;
                    controller.getCenterPane().getChildren().add(cpuWin.stage);
                    cpuWin.setTranslate(controller.position[controller.positonI]);
                    cpuWin.setOpen(true);
                }else{
                    cpuWin.getStage().toFront();
                }
            }
        });
        diskWindow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!diskWin.isOpen()){
                    controller.positonI = (++controller.positonI)%controller.position.length;
                    controller.getCenterPane().getChildren().add(diskWin.stage);
                    diskWin.setTranslate(controller.position[controller.positonI]);
                    diskWin.setOpen(true);
                }else{
                    diskWin.getStage().toFront();
                }
            }
        });
        memoryWindow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!mainMemoryWin.isOpen()){
                    controller.positonI = (++controller.positonI)%controller.position.length;
                    controller.getCenterPane().getChildren().add(mainMemoryWin.stage);
                    mainMemoryWin.setTranslate(controller.position[controller.positonI]);
                    mainMemoryWin.setOpen(true);
                }else{
                    mainMemoryWin.getStage().toFront();
                }
            }
        });
        deviceWindow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!deviceWin.isOpen()){
                    controller.positonI = (++controller.positonI)%controller.position.length;
                    controller.getCenterPane().getChildren().add(deviceWin.stage);
                    deviceWin.setTranslate(controller.position[controller.positonI]);
                    deviceWin.setOpen(true);
                }else{
                    deviceWin.getStage().toFront();
                }
            }
        });

        goTo_menu.getItems().addAll(cpuWindow,diskWindow,memoryWindow,deviceWindow);


        Menu show_menu = new Menu("显示");
        Menu edit_menu = new Menu("编辑");
        Menu file_menu = new Menu("文件");
        MenuItem item=new MenuItem("xx");
        file_menu.getItems().addAll(item);

        file_menu.addEventHandler(Menu.ON_SHOWING, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if(!dictionaryWin.isOpen()){
                    controller.positonI = (++controller.positonI)%controller.position.length;
                    controller.getCenterPane().getChildren().add(dictionaryWin.stage);
                    dictionaryWin.setTranslate(controller.position[controller.positonI]);
                    dictionaryWin.setOpen(true);
                }else{
                    dictionaryWin.getStage().toFront();
                }
            }

        });

        Menu finder_menu = new Menu("访达");
        Menu apple_menu = new Menu();
        Menu blank0_menu =  new Menu("                                        ");
        Menu black1_menu = new Menu("                                         ");
        Menu black2_menu = new Menu("                                        ");
        Menu black3_menu = new Menu("                                              ");
        Menu black4_menu = new Menu("                                              ");
        Menu black5_menu = new Menu("sss");


        apple_menu.setGraphic(new ImageView(new Image("/icons/black_apple.png",25,25,false,false)));
        apple_menu.getItems().add(test);
        menu.getItems().addAll(close,setter,reset,dark);
        help_menu.getItems().addAll(command,shortcut,member);
        //menuBar.getMenus().addAll(menu);


        menuBar.getMenus().addAll(apple_menu,menu,finder_menu,file_menu,edit_menu,show_menu,goTo_menu,window_menu,help_menu);
        //menuBar.setUseSystemMenuBar(true);


        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        //SimpleDateFormat time = new SimpleDateFormat("E HH:mm");

        Text timeText = new Text();//当前时间文本

        Thread runningTime = new Thread(){//内部类线程，不停获取当前时间
            @Override
            public void run() {
                while(true)
                {
                    timeText.setText(time.format(new Date()));

                }
            }
        };//获取当前时间的线程；
        runningTime.start();//线程运行
        timeText.setFont(Font.font(15));//时间文本字体

        Image wifi_image = new Image("/icons/wifi2_1.png",25,25,false,false);
        ImageView wifi_imageView =  new ImageView(wifi_image);
        Image sougou_image = new Image("/icons/sougou_4.png",30,30,false,false);
        ImageView sougou_imageView = new ImageView(sougou_image);
        Image dianliang_image = new Image("/icons/dianliang_2.png",30,30,false,false);
        ImageView dianliang_imageView = new ImageView(dianliang_image);
        Image fangdajing_image = new Image("/icons/fangdajing_1.png",30,30,false,false);
        ImageView fangdajing_imageView = new ImageView(fangdajing_image);

        HBox work_box =  new HBox();
        work_box.setMinWidth(30);
        work_box.getChildren().addAll(timeText,sougou_imageView,wifi_imageView,dianliang_imageView,fangdajing_imageView);
        work_box.setAlignment(Pos.CENTER_RIGHT);
        //work_box.setMargin(dianliang_imageView,new Insets(20));
        work_box.setSpacing(15);



        topPane.getChildren().addAll(menuBar,timeText);


        //底部面板设置；
        HBox bottomPane = new HBox(5);//面板创建
        bottomPane.setAlignment(Pos.CENTER);//节点居中
        bottomPane.minWidthProperty().bind(baseWidth);//属性绑定
        bottomPane.maxWidthProperty().bind(baseWidth);
        bottomPane.setMinHeight(0);
        bottomPane.setMaxHeight(0);

        Button appsButton = new Button("应用列表");//创建应用列表窗口快捷键
        appsButton.setText("");
        imageView1.setFitHeight(70);
        imageView1.setFitWidth(70);
        appsButton.setGraphic(imageView1);
        appsButton.setMinSize(80,30);
        appsButton.setStyle(
                "-fx-background-radius: 5em; " +
                        "-fx-min-width: 100px; " +
                        "-fx-min-height: 100px; " +
                        "-fx-max-width: 100px; " +
                        "-fx-max-height: 100px;"
        );
        for (String string:superAppList)
            bottomPane.getChildren().add(((SuperWin)winHashMap.get(string)).getButton());
        bottomPane.getChildren().addAll(appsButton);//添加按钮
        AppPane appPane = new AppPane(this,appList);

        //中间面板设置；
        centerPane.minWidthProperty().bind(baseWidth);//属性绑定
        centerPane.maxWidthProperty().bind(baseWidth);
        centerPane.minHeightProperty().bind(baseHeight.subtract(80));
        centerPane.maxHeightProperty().bind(baseHeight.subtract(80));

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

        //终端命令指南按钮事件
        command.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                String text =
                                "1.create : create /usr/tes.e  [创建一个带空字符的普通文件]\n"+
                                "2.delete : delete /usr/tes.e  [删除文件]\n"+
                                "3.cat : cat /usr/tes.e  [查看文件的内容]\n"+
                                "4.copy : copy /usr/tes.e /drv/tes.e  [复制文件到其他的目录]\n"+
                                "5.mv : mv /usr/tes.e /drv/tes.e  [移动文件到其他的目录]\n"+
                                "6.echo : echo \"ABC\" > /usr/tes.e  [重定向输入文本到文件中,双引号包含输入内容]\n"+
                                "7.mkdir : mkdir /usr/bin  [创建一个目录]\n"+
                                "8.rmdir : rmdir /usr/bin  [删除一个目录]\n"+
                                "9.exe : exe /usr/000.e  [执行一个可执行的文件]\n"+
                                "10.time : time  [查看当前系统的时间片]\n"+
                                "11.clear : clear  [清屏]\n"+
                                "12.poweroff : poweroff  [关机]\n"+
                                "13.system : system  [系统开发者]\n"+
                                "14.compile : compile /usr/abc.s [在当前目录下生成编译文件,编译前必须删除以前的编译文件]\n"+
                                "15.close : close /usr/005.e  [手动关闭文件]\n"
                             ;

                creatHelpWin("终端命令指南",text);
            }
        });

        //快捷键指南按钮事件
        shortcut.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                String text =
                        "S:打开设置面板\n"+
                                "Q:关机\n"+
                                "D:切换外观\n"+
                                "Z:切换壁纸\n"+
                                "T:打开终端"
                        ;
                creatHelpWin("快捷键指南",text);
            }
        });

        //课设人员按钮事件
        member.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                String text = "课程设计的开发人员：\n================\n\t朱勇杰,林明凭\n\t林嘉渝,梁晓嘉\n\t彭志凯\n================\n";
                creatHelpWin("课设成员",text);
            }
        });


        dark.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Controller.dark=!Controller.dark;

                if (Controller.dark)
                {
                    dark.setText("日间模式");

                    menuBar.setStyle("-fx-background-color: #1C1D22");
                    controller.getImageView().setImage(new Image("/Background/Catalina Night.jpg"));
                    timeText.setFill(Color.WHITE);


                   // apple_menu.setGraphic(new ImageView(new Image("/icons/black_apple.png",25,25,false,false)));
                    apple_menu.setGraphic(new ImageView(new Image("/icons/white_apple.png",25,25,false,false)));

                    for(String s:superAppList)
                    {
                        winHashMap.get(s).rectangle.setFill(Color.valueOf("#4A4644"));//黑带长条设置
                        winHashMap.get(s).winName.setFill(Color.WHITE);//窗口名称设置
                    }
                }
                else{
                    dark.setText("夜间模式");
                    menuBar.setStyle("-fx-background-color: white");
                    controller.getImageView().setImage(new Image("/Background/El Capitan.jpg"));
                    timeText.setFill(Color.BLACK);

                    apple_menu.setGraphic(new ImageView(new Image("/icons/black_apple.png",25,25,false,false)));


                    for(String s:superAppList)
                    {
                        winHashMap.get(s).rectangle.setFill(Color.valueOf("#D3D3D3"));//黑带长条设置
                        winHashMap.get(s).winName.setFill(Color.BLACK);//窗口名称设置

                    }

                }

            }
        });



        /**

         *@description: 【快捷键的设置，T打开终端，Q关闭程序，D切换模式，S打开设置，Z切换壁纸】
         * 暂时没有优化代码，因此代码耦合度比较高【实现功能第一，优化第二】

         * @param:  按键TQSZ
         * @return:
         * @author: Jamkung
         * @date: 2020/10/16 13:07
         * @throws
         */

        base.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode().name().equals(KeyCode.T.getName())){
                    System.out.println("键盘按下了按键=T");
                    System.out.println("打开终端");
                    if(!terminalWin.isOpen()){
                        controller.positonI = (++controller.positonI)%controller.position.length;
                        controller.getCenterPane().getChildren().add(terminalWin.stage);
                        terminalWin.setTranslate(controller.position[controller.positonI]);
                        terminalWin.setOpen(true);
                    }else{
                        terminalWin.getStage().toFront();
                    }
                }else if (event.getCode().name().equals(KeyCode.S.getName())){
                    System.out.println("键盘按下了按键=S");
                    System.out.println("打开设置");
                    if(!controllerSetter.isOpen()){
                        positonI = (++positonI)%position.length;
                        centerPane.getChildren().add(controllerSetter.getStage());
                        controllerSetter.getStage().setTranslateX(position[positonI]);
                        controllerSetter.getStage().setTranslateY(position[positonI]);
                        controllerSetter.setOpen(true);
                    }
                }else if (event.getCode().name().equals(KeyCode.D.getName())){
                    System.out.println("键盘按下了按键=D");
                    System.out.println("切换颜色（黑/白）模式");
                    Controller.dark=!Controller.dark;

                    if (Controller.dark)
                    {
                        dark.setText("日间模式");
                        menuBar.setStyle("-fx-background-color: #1C1D22");
                        controller.getImageView().setImage(new Image("/Background/Catalina Night.jpg"));
                        timeText.setFill(Color.WHITE);

                        apple_menu.setGraphic(new ImageView(new Image("/icons/white_apple.png",25,25,false,false)));

                        //try
                        //menu.setStyle(("-fx-text-fill:white"));
                        //menuBar.setStyle(("-fx-text-fill:white"));
                        //menu.setText("-fx-text-fill:white");



                        for(String s:superAppList)
                        {
                            winHashMap.get(s).rectangle.setFill(Color.valueOf("#4A4644"));//黑带长条设置
                            winHashMap.get(s).winName.setFill(Color.WHITE);//窗口名称设置
                        }
                    }
                    else{
                        dark.setText("夜间模式");
                        menuBar.setStyle("-fx-background-color: white");
                        controller.getImageView().setImage(new Image("/Background/El Capitan.jpg"));
                        timeText.setFill(Color.BLACK);

                        apple_menu.setGraphic(new ImageView(new Image("/icons/black_apple.png",25,25,false,false)));

                        for(String s:superAppList)
                        {
                            winHashMap.get(s).rectangle.setFill(Color.valueOf("#D3D3D3"));//黑带长条设置
                            winHashMap.get(s).winName.setFill(Color.BLACK);//窗口名称设置

                        }

                    }

                }else if(event.getCode().name().equals(KeyCode.Q.getName())){
                    System.out.println("键盘按下了按键=Q");
                    System.out.println("关闭程序");
                    System.exit(0);
                }else if(event.getCode().name().equals(KeyCode.Z.getName())){
                    System.out.println("键盘按下了按键=Z");
                    System.out.println("切换壁纸");
                    String[] backgrounds = new File("res/Background").list();
                    List<String> backgroundList = FXCollections.observableArrayList(backgrounds);
                    int index = (int) (Math.random()* backgroundList.size());
                    System.out.println(backgroundList.get(index));
                    boolean isFullScreen = true;
                    try {
                        controllerSetter.controller.getImageView().setImage(new Image("/Background/" + backgroundList.get(index)));
                    }catch (Exception e){
                        System.err.println("Background error!");
                    }
                    controllerSetter.controller.getPrimaryStage().setFullScreen(isFullScreen);
                    controllerSetter.controller.setFullScreen(isFullScreen);
                }
            }
        });


        // 鼠标右键菜单及功能设置
        ContextMenu rightButtonMenu = new ContextMenu();
        MenuItem setting = new MenuItem("设置(S)");
        MenuItem closeMyWin = new MenuItem("关机(Q)");//关机按钮
        MenuItem darkWhite = new MenuItem("切换外观(D)");//深色主题和浅色主题
        MenuItem nextPic = new MenuItem("切换壁纸(Z)");
        MenuItem termial = new MenuItem("打开终端(T)");


        rightButtonMenu.getItems().addAll(setting,closeMyWin,darkWhite,nextPic, termial);


        termial.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if(!terminalWin.isOpen()){
                    controller.positonI = (++controller.positonI)%controller.position.length;
                    controller.getCenterPane().getChildren().add(terminalWin.stage);
                    terminalWin.setTranslate(controller.position[controller.positonI]);
                    terminalWin.setOpen(true);
                }else{
                    terminalWin.getStage().toFront();
                }
            }
        });

        closeMyWin.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                System.exit(0);
            }
        });

        darkWhite.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Controller.dark=!Controller.dark;

                if (Controller.dark)
                {
                    dark.setText("日间模式");
                    menuBar.setStyle("-fx-background-color: #1C1D22");
                    controller.getImageView().setImage(new Image("/Background/Catalina Night.jpg"));
                    timeText.setFill(Color.WHITE);

                    //菜单栏文字设置白色
                   // menu.setStyle(("-fx-text-fill:white"));
                    //menu.setFill(Color.WHITE);
                    apple_menu.setGraphic(new ImageView(new Image("/icons/white_apple.png",25,25,false,false)));



                    for(String s:superAppList)
                    {
                        winHashMap.get(s).rectangle.setFill(Color.valueOf("#4A4644"));//黑带长条设置
                        winHashMap.get(s).winName.setFill(Color.WHITE);//窗口名称设置



                    }
                }
                else{
                    dark.setText("夜间模式");
                    menuBar.setStyle("-fx-background-color: white");
                    controller.getImageView().setImage(new Image("/Background/El Capitan.jpg"));
                    timeText.setFill(Color.BLACK);

                    apple_menu.setGraphic(new ImageView(new Image("/icons/black_apple.png",25,25,false,false)));



                    for(String s:superAppList)
                    {
                        winHashMap.get(s).rectangle.setFill(Color.valueOf("#D3D3D3"));//黑带长条设置
                        winHashMap.get(s).winName.setFill(Color.BLACK);//窗口名称设置


                    }

                }
            }
        });

        setting.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if(!controllerSetter.isOpen()){
                    positonI = (++positonI)%position.length;
                    centerPane.getChildren().add(controllerSetter.getStage());
                    controllerSetter.getStage().setTranslateX(position[positonI]);
                    controllerSetter.getStage().setTranslateY(position[positonI]);
                    controllerSetter.setOpen(true);
                }
            }
        });

        nextPic.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                String[] backgrounds = new File("res/Background").list();
                List<String> backgroundList = FXCollections.observableArrayList(backgrounds);
                int index = (int) (Math.random()* backgroundList.size());
                System.out.println(backgroundList.get(index));
                boolean isFullScreen = true;
                try {
                    controllerSetter.controller.getImageView().setImage(new Image("/Background/" + backgroundList.get(index)));
                }catch (Exception e){
                    System.err.println("Background error!");
                }
                controllerSetter.controller.getPrimaryStage().setFullScreen(isFullScreen);
                controllerSetter.controller.setFullScreen(isFullScreen);
            }
        });

        /**
         **
         * @description: 右键单开菜单的功能，这两段代码耦合度很高，复制粘贴没有做抽取成组件方式调用，功能基本能实现（除了不会终端的打开命令）
         * @param: 鼠标右键就能触发菜单
         * @return:
         * @author: Jamkung
         * @date: 2020/10/16 14:13
         */

        centerPane.addEventFilter(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println(event.getX());//获取点击的x坐标
                System.out.println(event.getY());//获取点击的y坐标
                //判断是不是使用的右键点击
                if(event.getButton().name().equals(MouseButton.SECONDARY.name())){
                    System.out.println("右键点击");
                    System.out.println("键盘按下了按键=右键" + event.getButton().name());
                    System.out.println("appList");
                    System.out.println(superAppList);
                    rightButtonMenu.show(base, event.getScreenX(), event.getScreenY());

                }else{
                    rightButtonMenu.hide();
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
                                    "-fx-min-width: 120px; " +
                                    "-fx-min-height: 120px; " +
                                    "-fx-max-width: 120px; " +
                                    "-fx-max-height: 120px;"
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
                                    "-fx-min-width: 100px; " +
                                    "-fx-min-height: 100px; " +
                                    "-fx-max-width: 100px; " +
                                    "-fx-max-height: 100px;"
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
            this.getWinHashMap().put(name, win);
            positonI = (++positonI) % position.length;
            win.setTranslate(position[positonI]);
        }
    }


    //创建窗口的方法，用于帮助窗口的创建
    public void creatHelpWin(String name,String text){
        int id = -1;
        HelpWin win = new HelpWin(this,name,id,text);
        this.getCenterPane().getChildren().add(win.getStage());
        this.getWinHashMap().put(name, win);
        positonI = (++positonI) % position.length;
        win.setTranslate(position[positonI]);

    }


    public static boolean isDark() {
        return dark;
    }

    public static void setDark(boolean dark) {
        Controller.dark = dark;
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

    public ArrayList<String> getAppList() {
        return appList;
    }
}


//应用列表窗口类
class AppPane{
    private boolean isOpen = false;//是否显示在屏幕上
    private StackPane stage = new StackPane();
    private HBox scene = new HBox(5);
    private Controller controller;//引用controller
    int i=0;
    ArrayList<ImageView> imageviews = new ArrayList<>();
    private ImageView i1 = new ImageView(new Image("/icons/weixin.png"));
    private ImageView i2 = new ImageView(new Image("/icons/qq.png"));
    private ImageView i3 = new ImageView(new Image("/icons/weibo.png"));
    private ImageView i4 = new ImageView(new Image("/icons/itunes.png"));
    private ImageView i5 = new ImageView(new Image("/icons/taobao.png"));
    private ImageView i6 = new ImageView(new Image("/icons/idea.png"));
    private ImageView i7 = new ImageView(new Image("/icons/ec.png"));
    private ImageView i8 = new ImageView(new Image("/icons/zhihu.jpg"));
    private ImageView i9 = new ImageView(new Image("/icons/ps.jpg"));
    private ImageView i10 = new ImageView(new Image("/icons/google.png"));

    public void imageviews() {
        i1.setFitWidth(60);
        i1.setFitHeight(60);
        i2.setFitHeight(60);
        i2.setFitWidth(60);
        i3.setFitHeight(80);
        i3.setFitWidth(80);
        i4.setFitHeight(60);
        i4.setFitWidth(60);
        i5.setFitHeight(60);
        i5.setFitWidth(60);
        i6.setFitHeight(60);
        i6.setFitWidth(60);
        i7.setFitHeight(60);
        i7.setFitWidth(60);
        i8.setFitHeight(60);
        i8.setFitWidth(60);
        i9.setFitHeight(60);
        i9.setFitWidth(60);
        i10.setFitHeight(60);
        i10.setFitWidth(60);
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
        this.controller = controller;
        stage.translateYProperty().bind(controller.baseHeightProperty().subtract(190));//属性绑定
        stage.minWidthProperty().bind(controller.baseWidthProperty());
        stage.maxWidthProperty().bind(controller.baseWidthProperty());
        stage.setMinHeight(0);
        stage.setMaxHeight(0);
        scene.setAlignment(Pos.CENTER);
        stage.getChildren().add(scene);
        imageviews();
        //对应用注册表内全部应用创建应用启动按钮
        for (String app:appList){
            Button button = new Button(" ");
            button.setGraphic(imageviews.get(i));
            button.setMinSize(40,40);
            i++;
            //应用启动按钮事件
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (controller.getWinHashMap().get(app) == null) {
                        controller.creatWin(app, controller.getPathHashMap().get(app));
                    }else{
                        controller.getWinHashMap().get(app).getStage().toFront();
                    }
                }
            });
            button.setStyle(
                    "-fx-background-radius: 10em; " +
                            "-fx-min-width: 100px; " +
                            "-fx-min-height: 100px; " +
                            "-fx-max-width: 100px; " +
                            "-fx-max-height: 100px;"
            );
            //应用启动按钮事件
            button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
//                    button.setMinSize(80,60);
                    button.setStyle(
                            "-fx-background-radius: 10em; " +
                                    "-fx-min-width: 120px; " +
                                    "-fx-min-height: 120px; " +
                                    "-fx-max-width: 120px; " +
                                    "-fx-max-height: 120px;"
                    );
                }
            });
            button.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
//                    button.setMinSize(40,40);
                    button.setStyle(
                            "-fx-background-radius: 10em; " +
                                    "-fx-min-width: 100px; " +
                                    "-fx-min-height: 100px; " +
                                    "-fx-max-width: 100px; " +
                                    "-fx-max-height: 100px;"
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