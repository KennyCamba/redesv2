/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.models;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 *
 * @author kcamb
 */
public class TablePane extends Panel{
    private final Pane root = new Pane(new Label("Tabla"));
    
    public TablePane(){
        super("Tabla de paquetes capturados");
        this.getChildren().add(root);
    }


    
    
}
