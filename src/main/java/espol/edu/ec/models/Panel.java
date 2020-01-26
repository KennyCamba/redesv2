/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.models;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 * @author kcamb
 */
public class Panel extends StackPane{
    private String title;
    protected ScheduledExecutorService scheduledExecutorService;
    protected boolean pause;

    public Panel(String title){
        this.title = title;
        this.pause = false;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle(){
        return title;
    }

    public void run(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void stop(){
        if(scheduledExecutorService != null){
            scheduledExecutorService.shutdownNow();
            pause = false;
        }

    }

    public void setPause(boolean pause){
        this.pause = pause;
    }
    
}
