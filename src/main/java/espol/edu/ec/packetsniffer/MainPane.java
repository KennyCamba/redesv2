/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.packetsniffer;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.PacketTime;
import espol.edu.ec.models.Panel;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import espol.edu.ec.views.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.pcap4j.core.PcapNetworkInterface;

/**
 *
 *
 */
public class MainPane extends BorderPane{
    private final ComboBox<PcapNetworkInterface> devices;
    private final List<PcapNetworkInterface> listDevices;
    private Button play;
    private Button stop;
    private double top;
    private List<Panel> charts;
    private Timer timer;
    private MenuBar menu;
    private Menu state;
    private Menu chart;
    private  Menu file;
    private boolean isStatic;
    private VBox right;
    private MenuItem export;
    private MenuItem importMenu;
    private Stage stage;
    private FileChooser chooser;
    
    public MainPane(Stage stage, List<PcapNetworkInterface> devices){
        this.stage = stage;
        this.listDevices = devices;
        this.devices = new ComboBox<>();
        play = new Button("play");
        stop = new Button("stop");
        charts = new ArrayList<>();
        timer = new Timer(0);
        menu = new MenuBar();
        state = new Menu("Comportamiento");
        chart = new Menu("Graficos");
        file = new Menu("Archivo");
        isStatic = false;
        right = new VBox();
        chooser = new FileChooser();
        init();
        events();
        createMenu();
    }

    private void createMenu() {
        export = new MenuItem("Exportar (*.pcap)");
        importMenu = new MenuItem("Importar (*.pcap)");
        export.setDisable(true);
        exportEvent();
        importEvent();
        MenuItem exit = new MenuItem("Salir");
        file.getItems().addAll(importMenu, export, exit);
        exit.setOnAction(e -> Platform.exit());
        CheckMenuItem random = new CheckMenuItem("Cambio aleatorio");
        CheckMenuItem stati = new CheckMenuItem("Estatico");
        random.setSelected(true);
        state.getItems().addAll(random, stati);
        random.setOnAction(e -> {
            stati.setSelected(false);
            isStatic = false;
        });
        stati.setOnAction(e -> {
            random.setSelected(false);
            isStatic = true;
        });
        Menu main = new Menu("Principal");
        Menu second1 = new Menu("Secundario 1");
        Menu second2 = new Menu("Secundario 2");
        for(Panel p: charts){
            CheckMenuItem menuItem1 = new CheckMenuItem(p.getTitle());
            menuItem1.setOnAction(e-> {
                String name = menuItem1.getText();
                for(Panel panel: charts){
                    if(panel.getTitle().equals(name)){
                        lefPanel(p);
                        for(MenuItem item: main.getItems()){
                            CheckMenuItem checkMenuItem = (CheckMenuItem)item;
                            if(checkMenuItem != menuItem1)
                                checkMenuItem.setSelected(false);
                        }
                    }
                }
            });
            CheckMenuItem menuItem2 = new CheckMenuItem(p.getTitle());
            menuItem2.setOnAction(e-> {
                String name = menuItem2.getText();
                for(Panel panel: charts){
                    if(panel.getTitle().equals(name)){
                        rightTopPanel(p);
                        for(MenuItem item: second1.getItems()){
                            CheckMenuItem checkMenuItem = (CheckMenuItem)item;
                            if(checkMenuItem != menuItem2)
                                checkMenuItem.setSelected(false);
                        }
                    }
                }
            });
            CheckMenuItem menuItem3 = new CheckMenuItem(p.getTitle());
            menuItem3.setOnAction(e-> {
                String name = menuItem3.getText();
                for(Panel panel: charts){
                    if(panel.getTitle().equals(name)){
                        rightBottomPanel(p);
                        for(MenuItem item: second2.getItems()){
                            CheckMenuItem checkMenuItem = (CheckMenuItem)item;
                            if(checkMenuItem != menuItem3)
                                checkMenuItem.setSelected(false);
                        }
                    }
                }
            });
            main.getItems().add(menuItem1);
            second1.getItems().add(menuItem2);
            second2.getItems().add(menuItem3);
        }
        chart.getItems().addAll(main, second1, second2);
        menu.getMenus().addAll(file, state, chart);
    }

    private void importEvent() {
        importMenu.setOnAction(e-> {
            if(!stop.isDisable()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Esta seguro que desea importar un archivo?");
                alert.setContentText("Los datos actuales seran eliminados");
                stage.setAlwaysOnTop(true);
                alert.initOwner(stage);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    open();
                }
            }else{
                open();
            }
        });
    }

    private void open(){
        chooser.setTitle("Importar");
        File file = chooser.showOpenDialog(stage);
        if(file != null){
            CapturePackets.getInstance().openOffline(file.getAbsolutePath());
            load();
        }
    }

    private void exportEvent() {
        export.setOnAction(e-> {
            chooser.setTitle("Exportar");
            chooser.setInitialFileName("export.pcap");
            File file = chooser.showSaveDialog(stage);
            if(file != null){
                CapturePackets.getInstance().savePackets(file.getAbsolutePath());
            }
        });
    }

    private void init(){
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pcap Files", "*.pcap"));
        charts.add(new PacketTable());
        charts.add(new BytesChart());
        charts.add(new ProtocolChart());
        charts.add(new PortsChart());
        charts.add(new Stats());
        loadComboBox(); 
        topPanel();
        right.setMinHeight(Const.HEIGHT - top);
        right.setMinWidth(Const.W50);
        this.setRight(right);
        fullScreen();
    }

    private void play(){
        //this.stop();
        if(!isStatic){
            timer = new Timer(60);
            Thread t = new Thread(timer);
            t.start();
        }
        for(Panel panel: charts){
            panel.run();
        }
        state.setDisable(true);

    }

    private void load(){
        for(Panel panel: charts){
            panel.runOffline();
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
        state.setDisable(false);
        export.setDisable(true);
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
        StackPane sp = new StackPane();
        Rectangle rectangle = new Rectangle(Const.WIDTH, Const.H1, Color.TRANSPARENT);
        menu.setVisible(false);
        rectangle.setOnMouseEntered(e -> menu.setVisible(true));
        menu.setOnMouseExited(e -> menu.setVisible(false));
        sp.setAlignment(Pos.TOP_CENTER);
        HBox content = new HBox();
        content.setSpacing(Const.W1);
        content.setAlignment(Pos.CENTER_LEFT); 
        Text txt = new Text("Dispositivo de red");
        txt.setFont(Font.font(Const.H5 * 0.4)); 
        content.setPadding(new Insets(Const.H1, Const.W1, Const.H1, Const.W1)); 
        content.getChildren().addAll(txt, devices, play, stop);
        sp.getChildren().addAll(content, rectangle, menu);
        this.setTop(sp);
        top = this.getTop().getBoundsInLocal().getHeight();
    }

    private void fullScreen(){
        Collections.shuffle(charts);
        lefPanel(charts.get(0));
        rightTopPanel(charts.get(1));
        rightBottomPanel(charts.get(2));
    }

    private void events() {
        CapturePackets capture = CapturePackets.getInstance();
        play.setOnMouseClicked(e -> {
            if(play.getText().equals("pause")){
               capture.pause();
                play.setText("replay");
               this.pause();
            }else if(play.getText().equals("replay")){
               try {
                   capture.play();
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
                    alert.initOwner(stage);
                    alert.show();
                }else{
                    export.setDisable(false);
                    capture.setDevice(device);
                    try {
                        capture.play();
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

    private void rightTopPanel(Panel chartTop){
        TitledPane pane1 = new TitledPane(chartTop.getTitle(), chartTop);
        pane1.setMinHeight(Const.H50 - (top/2));
        if(!(chartTop instanceof PacketTable))
            chartTop.setPadding(new Insets(Const.W1, Const.W1, Const.W1, Const.W1));
        right.getChildren().add(0, pane1);
    }

    private void rightBottomPanel(Panel chartBottom){
        TitledPane pane2 = new TitledPane(chartBottom.getTitle(), chartBottom);
        pane2.setMinHeight(Const.H50 - (top/2));
        if(!(chartBottom instanceof PacketTable))
            chartBottom.setPadding(new Insets(Const.W1, Const.W1, Const.W1, Const.W1));
        right.getChildren().add(1, pane2);
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
