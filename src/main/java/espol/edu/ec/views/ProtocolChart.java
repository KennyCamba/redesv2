/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.views;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.Panel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import org.pcap4j.packet.DnsPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

/**
 *
 * @author kcamb
 */
public class ProtocolChart extends Panel {
    private PieChart chart;
    private PieChart.Data tcp;
    private PieChart.Data udp;
    private PieChart.Data dns;
    private PieChart.Data ipv4;
    private PieChart.Data ethernet;
    //private PieChart.Data icmp;
    private Map<String, Integer> data;
    
    private Runnable run;
    private int tiempo = 0;

    public ProtocolChart() {
        super("Diagrama de protocolos");
        tcp = new PieChart.Data("TCP", 0);
        udp = new PieChart.Data("UDP", 0);
        dns = new PieChart.Data("DNS", 0);
        ipv4 = new PieChart.Data("IPv4", 0);
        ethernet = new PieChart.Data("ETHERNET", 0);
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(tcp, udp, dns, ipv4, ethernet);
        chart = new PieChart(pieChartData);
        data = new HashMap<>();
        data.put("TCP", 0);
        data.put("UDP", 0);
        data.put("DNS", 0);
        data.put("IPv4", 0);
        data.put("ETHERNET", 0);
        //initCtl();
    }

    public void initCtl() {
        chart.setTitle("Imported Fruits");
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER); 
        sp.getChildren().add(chart);
        this.getChildren().add(sp);
        run = () -> {
            Iterator<Packet> iter = CapturePackets.getInstance().getCapturePackets().iterator();
            while(iter.hasNext()){
                System.out.print("");
                while(iter.hasNext()){
                    calculate(iter.next());
                    if(tiempo >= 10){
                        update();
                        tiempo = 0;
                    }
                }
            }
        };
        Thread t1 = new Thread(run);
        Thread t2 = new Thread(new Timer());
        t1.start();
        t2.start();
    }
    
    private void update() {
        Platform.runLater(() ->{
           for(String k: data.keySet()){
               switch (k) {
                   case "TCP":
                       tcp.setPieValue(tcp.getPieValue() + data.get(k));
                       break;
                   case "UDP":
                       udp.setPieValue(udp.getPieValue() + data.get(k));
                       break;
                   case "IPV4":
                       ipv4.setPieValue(ipv4.getPieValue() + data.get(k));
                       break;
                   case "ETHERNET":
                       ethernet.setPieValue(ethernet.getPieValue() + data.get(k));
                       break;
                   case "DNS":
                       dns.setPieValue(dns.getPieValue() + data.get(k));
                       break;
               }
           }
        });
    }

    private void calculate(Packet next) {
        if(next.contains(IpV4Packet.class)){
            data.put("IPV4", data.get("IPV4") + 1);
        }else if(next.contains(EthernetPacket.class)){
            data.put("ETHERNET", data.get("ETHERNET") + 1);
        }else if(next.contains(TcpPacket.class)){
            data.put("TCP", data.get("TCP") + 1);
        }else if(next.contains(UdpPacket.class)){
            data.put("UDP", data.get("UDP") + 1);
        }else if(next.contains(DnsPacket.class)){
            data.put("DNS", data.get("DNS") + 1);
        }
    }
    
    private class Timer implements Runnable{

        @Override
        public void run() {
            while(true){
                try { 
                    Thread.sleep(1000);
                    tiempo ++;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProtocolChart.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
        }
        
    }
}
