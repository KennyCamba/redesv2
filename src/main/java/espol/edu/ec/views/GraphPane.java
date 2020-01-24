/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.views;

import espol.edu.ec.models.Panel;
import espol.edu.ec.packetsniffer.Const;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 *
 * @author kcamb
 */
public class GraphPane<E extends Panel> extends VBox{
    private final ComboBox<E> titles;
    private final Rectangle bar;
    private final StackPane title;
    private double height;
    private double widht;
    private final Pane content;
    private final Text txtBar;
    private HBox components;
    private final List<E> screens;
    
    public GraphPane(double widht, double height, List<E> screens){
        this.widht = widht;
        this.height = height;
        this.screens = screens;
        content = new Pane();
        txtBar = new Text("Titulo");
        titles = new ComboBox<>(); 
        titles.getItems().addAll(screens); 
        bar = new Rectangle(widht, Const.H5*0.6, Color.DARKGRAY);
        title = new StackPane();
        init();
    }
    
    private void init(){
        title.setAlignment(Pos.CENTER); 
        title.getChildren().add(bar);
        title.setMaxWidth(widht); 
        components = new HBox();
        components.setPadding(new Insets(0, 5, 0, 5));
        components.setAlignment(Pos.CENTER_LEFT);
        DropShadow sh = new DropShadow();
        sh.setColor(Color.GREY);
        sh.setOffsetX(0); 
        sh.setOffsetY(2);
        bar.setEffect(sh);
        components.getChildren().addAll(txtBar/*, titles*/);
        components.setSpacing(widht - txtBar.getLayoutBounds().getWidth() - widht*0.08);  
        title.getChildren().add(components);
        titles.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))); 
        titles.setMaxHeight(0);
        titles.setMaxWidth(0); 
        this.getChildren().addAll(title, content);
        this.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));  
        this.setPrefHeight(height); 
        //events();
    }

    private void events() {
        titles.setOnAction(e -> {
            E obj = titles.getValue();
            if(obj != null){ 
                Platform.runLater(() -> {
                    txtBar.setText(obj.toString()); 
                    components.setSpacing(widht - txtBar.getLayoutBounds().getWidth() - widht*0.08); 
                    content.getChildren().clear();
                    content.getChildren().add(obj);
                    //titles.getItems().clear();
                    //titles.getItems().addAll(screens);
                });
            }
            
            
        });
    }

    public void setContent(E pane){
        //if(!titles.getItems().contains(pane)){
            //titles.getItems().clear();
            //titles.getItems().addAll(screens);
            txtBar.setText(pane.toString());
            components.setSpacing(widht - txtBar.getLayoutBounds().getWidth() - widht*0.08);  
            Platform.runLater(() -> {
                titles.getItems().clear();
                titles.getItems().addAll(screens);
            });
        //}
    }
    
    
    
}
