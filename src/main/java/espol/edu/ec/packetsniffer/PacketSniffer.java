/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.packetsniffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

/**
 *
 * @author kcamb
 */
public class PacketSniffer extends Application{
    private List<PcapNetworkInterface> devices;
    private MainPane mainPane;

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void init() {
        try {
            devices = getAllDevices();
        } catch (IOException ex) {
            devices = new ArrayList<>();
        }
    }
    
    @Override
    public void start(Stage stage) {
        this.mainPane = new MainPane(stage, devices);
        stage.setScene(new Scene(this.mainPane));
        stage.setFullScreen(true);
        stage.getScene().getStylesheets().add("espol/edu/ec/css/style.css");
        stage.show();
    }
    
    @Override
    public void stop(){
        this.mainPane.stop();
    }
    
    private List<PcapNetworkInterface> getAllDevices() throws IOException{
        List<PcapNetworkInterface> allDevs;
        try {
            allDevs = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            throw new IOException(e.getMessage());
        }

        if (allDevs == null || allDevs.isEmpty()) {
            throw new IOException("No NIF to capture.");
        }
        return allDevs;
    }

}
