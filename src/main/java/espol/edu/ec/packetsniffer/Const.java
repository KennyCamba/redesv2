/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.packetsniffer;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 *
 * @author kcamb
 */
public class Const {
    private Const(){
        //
    }
    
    private static final Rectangle2D SCREEN = Screen.getPrimary().getVisualBounds();
    public static final double WIDTH = SCREEN.getWidth();
    public static final double HEIGHT = SCREEN.getHeight();
    public static final double W1 = WIDTH * 0.01;
    public static final double W5 = WIDTH * 0.05;
    public static final double W10 = WIDTH * 0.10;
    public static final double W20 = WIDTH * 0.20;
    public static final double W25 = WIDTH * 0.25;
    public static final double W50 = WIDTH * 0.50;
    public static final double W75 = WIDTH * 0.75;
    public static final double H1 = HEIGHT * 0.01;
    public static final double H5 = HEIGHT * 0.05;
    public static final double H10 = HEIGHT * 0.10;
    public static final double H20 = HEIGHT * 0.20;
    public static final double H25 = HEIGHT * 0.25;
    public static final double H50 = HEIGHT * 0.50;
    public static final double H75 = HEIGHT * 0.75;
}
