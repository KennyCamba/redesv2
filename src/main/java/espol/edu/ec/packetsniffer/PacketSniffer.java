/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.packetsniffer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import javax.imageio.ImageIO;

/**
 *
 * @author kcamb
 */
public class PacketSniffer extends Application{
    private List<PcapNetworkInterface> devices;
    private MainPane mainPane;

    public static void main(String[] args) {
        InetAddress addr1 = null;
        InetAddress addr2 = null;
        InetAddress addr3 = null;
        InetAddress addr4 = null;
        String ip1 = "191.99.255.205";
        String ip2 = "192.168.0.4";
        String ip3 = "200.124.235.194";
        String ip4 = "140.82.113.4";
        try {
            addr1 = InetAddress.getByName(ip1);
            System.out.println(addr1.isSiteLocalAddress());
            System.out.println(addr1.getHostName());
            System.out.println("-----------------------------------");
            addr2 = InetAddress.getByName(ip2);
            System.out.println(addr2.isSiteLocalAddress());
            System.out.println(addr2.getHostName());
            System.out.println("-----------------------------------");
            addr3 = InetAddress.getByName(ip3);
            System.out.println(addr3.isSiteLocalAddress());
            System.out.println(addr3.getHostName());
            System.out.println("-----------------------------------");
            addr4 = InetAddress.getByName(ip4);
            System.out.println(addr4.isSiteLocalAddress());
            System.out.println(addr4.getHostName());
            //WritableImage image = SwingFXUtils.toFXImage(ImageIO.read(new URL(addr4.getHostName() + "/favicon.ico")), null);
            //ImageView imageView = new ImageView(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
