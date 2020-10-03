package com.os_simulator.www.Window;

import com.os_simulator.www.system.MemoryBlock;
import com.os_simulator.www.system.SystemCore;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zach on 2016/12/4.
 */
public class MainMemoryWin extends SuperWin {

    private SystemCore core = null;

    private List<MemoryBlock> list = new LinkedList<>();

    private Text text1 = new Text("内存容量："+systemCore.getMemoryCapacity());
    private Text text2 = new Text("内存使用："+systemCore.getMemoryUsed());

    private Image image = new Image("/icons/memory.png");
    private ImageView imageView = new ImageView(image);

    public MainMemoryWin(Controller controller) {
        super(controller, "内存状态", 450, 300);
        Pane pane = memdatadisplay();
        text1.setTranslateX(10);
        text1.setFont(new Font(20));
        text2.setTranslateX(10);
        text2.setFont(new Font(20));
        this.setPane(pane);

        button.setText("");
        button.setGraphic(imageView);

    }

    @Override
    public void update() {
        //获取一次systemCore
        if (isUpdate){
            if (systemCore != null){
                core= systemCore;
                isUpdate = false;
            }
        }
        text2.setText("内存使用："+systemCore.getMemoryUsed());
    }

    private Pane memdatadisplay(){
        Pane pane = new Pane();
        VBox vBox = new VBox();


        int MemoryCapacity = 2048;
        Canvas canvas = new Canvas(500,300);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ArrayList<Color> colorList = new ArrayList<Color>();
        for(int k=0;k<30;k++){
            double redRandom = Math.random()*100;
            double greenRandom = Math.random()*100;
            double blueRandom = Math.random()*100;
            double opacityRandom = Math.random()*100;
            int key=0;
            Color color=new Color(redRandom/100,greenRandom/100,blueRandom/100,opacityRandom/100);
            if(color!=Color.LIGHTBLUE&&color!=Color.WHITE){
                colorList.add(color);
            }
        }
        Thread thread1 = new Thread(new Runnable() {
            int MemoryUsed = 0;
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gc.clearRect(0,0,500,300);
                    if (core != null){
                        MemoryUsed=core.getMemoryUsed();
                        list = core.getMemDetail();
                    }
//                    gc.fillText("内存容量："+MemoryCapacity+"\n"+"内存使用："+MemoryUsed,25,50);
                    paint(gc, list,colorList);
                }

            }
        });
        thread1.setDaemon(true);
        thread1.start();
        vBox.getChildren().addAll(text1,text2,canvas);
        pane.getChildren().add(vBox);
        return pane;
    }

    public void paint(GraphicsContext gc, List<MemoryBlock> list,ArrayList<Color> colorList){
        int i=list.size();
        int rectWidth=25;
        int m=0;
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(10,10,430,50);
        gc.setFill(Color.WHITE);
        gc.fillArc(20,20, 10,30,90,180, ArcType.ROUND);
        gc.fillRect(25,20,400,30);
        gc.fillArc(20+400,20, 10,30,270,180, ArcType.ROUND);
        for(MemoryBlock memoryBlock : list){
            gc.setFill(colorList.get(m));
            gc.fillRect(25+rectWidth*m,20,rectWidth,30);
            if(m==0) {
                gc.fillArc(20, 20, 10, 30, 90, 180, ArcType.ROUND);
            }
            if(m==i-1){
                gc.fillArc(20+rectWidth*(m+1),20, 10,30,270,180, ArcType.ROUND);
            }
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(memoryBlock.getThreadID()),25+rectWidth*m+rectWidth/3, 20+20);
            m++;
        }
    }
}
