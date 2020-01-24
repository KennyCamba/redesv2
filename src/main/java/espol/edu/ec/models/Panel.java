/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.models;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author kcamb
 */
public class Panel extends StackPane{
    private String title;
    
    public Panel(String title){
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
    
    
}
