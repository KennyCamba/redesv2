/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.packetsniffer;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.views.ActionButton;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import espol.edu.ec.views.ProtocolChart;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
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
    private ActionButton play;
    private ActionButton stop;
    private double top;
    ProtocolChart pc = new ProtocolChart();
    
    public MainPane(List<PcapNetworkInterface> devices){
        this.listDevices = devices;
        this.devices = new ComboBox<>();
        play = new ActionButton(new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "play.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true));
        stop = new ActionButton(new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "stop.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true));
        init();
        events();
    }
    
    
    private void init(){
        loadComboBox(); 
        topPanel();
        lefPanel();
        rightPanel();
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

    private void events() {
        Image pause = new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "pause.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true);
        Image pImg = new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "play.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true);
        Image replay = new Image(Paths.get("espol", "edu", "ec", "resources", "imgs", "replay.png").toString(), Const.H5*0.4, Const.H5*0.4, true, true);
        CapturePackets cp = CapturePackets.getInstance();
        play.setOnMouseClicked(e -> {
           if(play.getImage().equals(pause)){
               cp.pause();
               play.setImage(replay);
               pc.setPause(true);
           }else if(play.getImage().equals(replay)){
               try {
                   cp.play(); 
                   play.setImage(pause);
                   pc.setPause(false);
               } catch (Exception ex) {
                   Logger.getLogger(MainPane.class.getName()).log(Level.SEVERE, null, ex);
               }
           }else{
               cp.setDevice(devices.getValue()); 
                try {
                    cp.play();
                    pc.run();
                    play.setImage(pause);
                    stop.setDisable(false); 
                } catch (Exception ex) {
                    Logger.getLogger(MainPane.class.getName()).log(Level.SEVERE, null, ex);
                }
           }
           
        });
        stop.setOnMouseClicked(e-> {
            cp.stop();
            play.setImage(pImg); 
            stop.setDisable(true);
            pc.stop();
        }); 
    }

    private void lefPanel(){
        TitledPane pane = new TitledPane("t1", pc);
        this.setLeft(pane);
        pane.setMinHeight(Const.HEIGHT - top);
        pane.setMinWidth(Const.W50);
        pc.setPadding(new Insets(Const.H5, Const.H5, Const.H5, Const.H5));
    }

    private void rightPanel(){
        VBox vBox = new VBox();
        vBox.setMinHeight(Const.HEIGHT - top);
        vBox.setMinWidth(Const.W50);
        TitledPane pane1 = new TitledPane();
        pane1.setMinHeight(Const.H50 - (top/2));
        TitledPane pane2 = new TitledPane();
        pane2.setText("Panel");
        pane2.setMinHeight(Const.H50 - (top/2));
        vBox.getChildren().addAll(pane1, pane2);
        this.setRight(vBox);
    }

}
