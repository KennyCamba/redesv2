package espol.edu.ec.views;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.Panel;
import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PortsChart extends Panel {

    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private BarChart<String, Number> chart;
    private XYChart.Series<String, Number> sources;
    private XYChart.Series<String, Number> destinations;
    private int init;
    private List<String> knowPortsSrc;
    private List<String> knowPortsDst;

    public PortsChart() {
        super("Puertos TCP y UDP");
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        chart = new BarChart<>(xAxis, yAxis);
        sources = new XYChart.Series<>();
        destinations = new XYChart.Series<>();
        init = 0;
        knowPortsSrc = new LinkedList<>();
        knowPortsDst = new LinkedList<>();
        initCtl();
    }

    private void initCtl() {
        xAxis.setLabel("Puertos");
        yAxis.setLabel("Total");
        chart.setTitle("Puertos TCP y UDP");
        sources.setName("src");
        destinations.setName("dst");
        chart.getData().addAll(sources, destinations);
        this.getChildren().add(chart);
    }

    @Override
    public void run() {
        super.run();
        scheduledExecutorService.scheduleAtFixedRate(()->{
            if(!pause){
                List<Packet> packets = CapturePackets.getInstance().getCapturePackets();
                for(int i=init; i<packets.size(); i++){
                    Packet packet = packets.get(i);
                    update(packet);
                }
                init = packets.size();
            }

        }, 0, 1, TimeUnit.SECONDS);
    }

    private void update(Packet packet) {
        if(packet.contains(TcpPacket.class)){
            TcpPacket tcp = packet.get(TcpPacket.class);
            updateSrc(tcp.getHeader().getSrcPort().name(), tcp.getHeader().getSrcPort().valueAsInt());
            updateDst(tcp.getHeader().getDstPort().name(), tcp.getHeader().getDstPort().valueAsInt());
        }else if(packet.contains(UdpPacket.class)){
            UdpPacket udp = packet.get(UdpPacket.class);
            updateSrc(udp.getHeader().getSrcPort().name(), udp.getHeader().getSrcPort().valueAsInt());
            updateDst(udp.getHeader().getDstPort().name(), udp.getHeader().getDstPort().valueAsInt());
        }
    }

    private void updateDst(String name, int value) {
        if(value >= 0 && value < 1024){
            int index;
            if((index = knowPortsDst.indexOf(name)) != -1){
                Platform.runLater(() -> {
                    XYChart.Data<String, Number> data =  destinations.getData().get(index);
                    data.setYValue(data.getYValue().intValue() + 1);
                });
            }else {
                knowPortsDst.add(0, name);
                Platform.runLater(()->
                        destinations.getData().add(0, new XYChart.Data<>(name+" ("+value+")", 1))
                );
            }
        }
    }

    private void updateSrc(String name, int value) {
        if(value >= 0 && value < 1024){
            int index;
            if((index = knowPortsSrc.indexOf(name)) != -1){
                Platform.runLater(() -> {
                    XYChart.Data<String, Number> data =  sources.getData().get(index);
                    data.setYValue(data.getYValue().intValue() + 1);
                });
            }else {
                knowPortsSrc.add(0, name);
                Platform.runLater(()->
                        sources.getData().add(0, new XYChart.Data<>(name+" ("+value+")", 1))
                );
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        chart.getData().clear();
    }
}
