/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.views;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author kcamb
 */
public class ActionButton extends StackPane{
    private Rectangle back;
    private ImageView img;
    
    public ActionButton(Image image){
        this.img = new ImageView(image);
        this.back = new Rectangle(image.getWidth() + image.getWidth()*0.5, image.getHeight() + image.getHeight()*0.5, Color.rgb(125, 194, 219));
        initNode();
        events();
    }

    private void initNode() {
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.DARKGRAY); 
        back.setEffect(shadow); 
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(back, img);
    }

    private void events() {
        this.setOnMouseEntered(e-> {
            this.setCursor(Cursor.HAND); 
            back.setFill(Color.rgb(139, 211, 224));
        });
        this.setOnMouseExited(e-> {
            this.setCursor(Cursor.DEFAULT);
            back.setFill(Color.rgb(125, 194, 219));
        });
        this.setOnMousePressed(e-> back.setFill(Color.rgb(115, 222, 221)));
        this.setOnMouseReleased(e-> back.setFill(Color.rgb(125, 194, 219)));
    }
    
    public void setImage(Image image){
        img.setImage(image); 
    }
    
    public Image getImage(){
        return img.getImage();
    }
}
