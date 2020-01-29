/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.views;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.PacketTime;
import espol.edu.ec.models.Panel;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
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
    private int init;

    public ProtocolChart() {
        super("Diagrama de protocolos");
        init = 0;
        tcp = new PieChart.Data("TCP", 0);
        udp = new PieChart.Data("UDP", 0);
        dns = new PieChart.Data("DNS", 0);
        ipv4 = new PieChart.Data("IPv4", 0);
        ethernet = new PieChart.Data("ETHERNET", 0);
        chart = new PieChart();
        chart.getData().addAll(tcp, udp, ipv4, dns, ethernet);
        initCtl();
    }

    private void initCtl() {
        chart.setTitle("Protocolos");
        StackPane sp = new StackPane();
        sp.setAlignment(Pos.CENTER);
        sp.getChildren().add(chart);
        this.getChildren().add(sp);
    }

    @Override
    public void run(){
        super.run();
        scheduledExecutorService.scheduleAtFixedRate(()->{
            if(!pause){
                List<PacketTime> packets = CapturePackets.getInstance().getCapturePackets();
                for(int i=init; i<packets.size(); i++){
                    Packet packet = packets.get(i).getPacket();
                    update(packet);
                }
                init = packets.size();
            }

        }, 0, 1, TimeUnit.SECONDS);

    }

    private void update(Packet next) {
        if(next.contains(IpV4Packet.class)){

            Platform.runLater(() -> ipv4.setPieValue(ipv4.getPieValue() + 1));
        }
        if(next.contains(EthernetPacket.class)){
            Platform.runLater(() -> ethernet.setPieValue(ethernet.getPieValue() + 1));
        }
        if(next.contains(TcpPacket.class)){
            Platform.runLater(() -> tcp.setPieValue(tcp.getPieValue() + 1));
        }
        if(next.contains(UdpPacket.class)){
            Platform.runLater(() -> udp.setPieValue(udp.getPieValue() + 1));
        }
        if(next.contains(DnsPacket.class)){
            Platform.runLater(() -> dns.setPieValue(dns.getPieValue() + 1));
        }
    }

    @Override
    public void runOffline() {
        List<PacketTime> packetTimes = CapturePackets.getInstance().getCapturePackets();
        for(PacketTime packet: packetTimes){
            update(packet.getPacket());
        }
    }

    @Override
    public void stop() {
        super.stop();
        ipv4.setPieValue(0);
        ethernet.setPieValue(0);
        tcp.setPieValue(0);
        udp.setPieValue(0);
        dns.setPieValue(0);
    }
}
