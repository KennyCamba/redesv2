/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.packetsniffer;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.Panel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import espol.edu.ec.views.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.pcap4j.core.PcapNetworkInterface;

/**
 *
 * @author kcamb
 */
public class MainPane extends BorderPane{
    private final ComboBox<PcapNetworkInterface> devices;
    private final List<PcapNetworkInterface> listDevices;
    //private ActionButton play;
    //private ActionButton stop;
    private Button play;
    private Button stop;
    private double top;
    private List<Panel> charts;
    private Timer timer;
    
    public MainPane(List<PcapNetworkInterface> devices){
        this.listDevices = devices;
        this.devices = new ComboBox<>();
        //play = new ActionButton(new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "play.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true));
        //stop = new ActionButton(new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "stop.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true));
        play = new Button("play");
        stop = new Button("stop");
        charts = new ArrayList<>();
        timer = new Timer(0);
        init();
        events();
    }
    
    
    private void init(){
        charts.add(new PacketTable());
        charts.add(new BytesChart());
        charts.add(new ProtocolChart());
        charts.add(new PortsChart());
        charts.add(new Stats());
        loadComboBox(); 
        topPanel();
        fullScreen();
    }

    private void play(){
        timer = new Timer(5);
        Thread t = new Thread(timer);
        t.start();
        for(Panel panel: charts){
            panel.run();
        }
    }

    private void pause(){
        timer.pause();
        for(Panel panel: charts){
            panel.setPause(true);
        }
    }

    private void resume(){
        timer.resume();
        for(Panel panel: charts){
            panel.setPause(false);
        }
    }

    public void stop(){
        timer.stop();
        CapturePackets.getInstance().stop();
        for(Panel panel: charts){
            panel.stop();
        }
    }

    private void loadComboBox() {
        this.devices.setConverter(new StringConverter<PcapNetworkInterface>() {
            @Override
            public String toString(PcapNetworkInterface object) {
                return object.getName();
            }

            @Override
            public PcapNetworkInterface fromString(String string) {
                return null;
            }
        }); 
        this.devices.getItems().addAll(listDevices);
    }

    private void topPanel() {
        stop.setDisable(true); 
        HBox content = new HBox();
        content.setSpacing(Const.W1);
        content.setAlignment(Pos.CENTER_LEFT); 
        Text txt = new Text("Dispositivo de red");
        txt.setFont(Font.font(Const.H5 * 0.4)); 
        content.setPadding(new Insets(Const.H1, Const.W1, Const.H1, Const.W1)); 
        content.getChildren().addAll(txt, devices, play, stop);
        this.setTop(content);
        top = this.getTop().getBoundsInLocal().getHeight();
    }

    private void fullScreen(){
        Collections.shuffle(charts);
        lefPanel(charts.get(0));
        rightPanel(charts.get(1), charts.get(2));
    }

    private void events() {
        //Image pause = new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "pause.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true);
        //Image pImg = new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "play.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true);
        //Image replay = new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "replay.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true);
        CapturePackets capture = CapturePackets.getInstance();
        play.setOnMouseClicked(e -> {
           //if(play.getImage().equals(pause)){
            if(play.getText().equals("pause")){
               capture.pause();
               //play.setImage(replay);
                play.setText("replay");
               this.pause();
           //}else if(play.getImage().equals(replay)){
            }else if(play.getText().equals("replay")){
               try {
                   capture.play();
                   //play.setImage(pause);
                   play.setText("pause");
                   this.resume();
               } catch (Exception ex) {
                   Logger.getLogger(MainPane.class.getName()).log(Level.SEVERE, null, ex);
               }
           }else{
                PcapNetworkInterface device = devices.getValue();
                if(device == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!!");
                    alert.setHeaderText(new String("Error de acción".getBytes(), StandardCharsets.UTF_8));
                    alert.setContentText(new String("No ha sleccionado ningún dispositivo de red".getBytes(), StandardCharsets.UTF_8));
                    alert.show();
                }else{
                    capture.setDevice(device);
                    try {
                        capture.play();
                        //play.setImage(pause);
                        play.setText("pause");
                        stop.setDisable(false);
                        this.play();
                    } catch (Exception ex) {
                        Logger.getLogger(MainPane.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
           }
           
        });
        stop.setOnMouseClicked(e-> {
            capture.stop();
            //play.setImage(pImg);
            play.setText("play");
            stop.setDisable(true);
            this.stop();
        }); 
    }

    private void lefPanel(Panel chart){
        TitledPane pane = new TitledPane(chart.getTitle(), chart);
        this.setLeft(pane);
        pane.setMinHeight(Const.HEIGHT - top);
        pane.setMinWidth(Const.W50);
        if(!(chart instanceof PacketTable))
            chart.setPadding(new Insets(Const.H5, Const.H5, Const.H5, Const.H5));
    }

    private void rightPanel(Panel chartTop, Panel chartBottom){
        VBox vBox = new VBox();
        vBox.setMinHeight(Const.HEIGHT - top);
        vBox.setMinWidth(Const.W50);

        TitledPane pane1 = new TitledPane(chartTop.getTitle(), chartTop);
        pane1.setMinHeight(Const.H50 - (top/2));
        if(!(chartTop instanceof PacketTable))
            chartTop.setPadding(new Insets(Const.W1, Const.W1, Const.W1, Const.W1));

        TitledPane pane2 = new TitledPane(chartBottom.getTitle(), chartBottom);
        pane2.setMinHeight(Const.H50 - (top/2));
        if(!(chartBottom instanceof PacketTable))
            chartBottom.setPadding(new Insets(Const.W1, Const.W1, Const.W1, Const.W1));

        vBox.getChildren().addAll(pane1, pane2);
        this.setRight(vBox);
    }

    private class Timer implements Runnable {
        private int time;
        private boolean pause;
        private boolean isRun;
        private int limit;

        Timer(int limit){
            time = 0;
            pause = false;
            isRun = true;
            this.limit = limit;
        }
        @Override
        public void run() {
                while (isRun){
                    System.out.print("");
                    if(!pause){
                        sleep();
                        if(time > 0 && time%limit == 0){
                            Platform.runLater(MainPane.this::fullScreen);
                        }
                        time ++;
                    }
                }
        }

        private void sleep(){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void stop(){
            isRun = false;
            pause = false;
        }

        public void pause(){
            pause = true;
        }

        public void resume(){
            pause = false;
        }
    }
}
