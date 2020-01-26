package espol.edu.ec.views;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.PacketRaw;
import espol.edu.ec.models.Panel;
import espol.edu.ec.packetsniffer.Const;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.pcap4j.packet.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PacketTable extends Panel {

    private TableView<PacketRaw> table;
    private TableColumn<PacketRaw, Integer> number;
    private TableColumn<PacketRaw, String> capturedAt;
    private TableColumn<PacketRaw, String> protocol;
    private TableColumn<PacketRaw, Integer> length;
    private TableColumn<PacketRaw, String> description;
    private int init;
    
    public PacketTable(){
        super("Tabla de Paquetes");
        table = new TableView<>();
        number = new TableColumn<>(new String("N°".getBytes(), StandardCharsets.UTF_8));
        capturedAt = new TableColumn<>("Tiempo");
        protocol = new TableColumn<>("Protocolo");
        length = new TableColumn<>("Bytes");
        description = new TableColumn<>(new String("Información".getBytes(), StandardCharsets.UTF_8));
        init = 0;
        initCtl();
    }

    private void initCtl() {
        number.setCellValueFactory(new PropertyValueFactory<>("number"));
        number.setMinWidth(Const.W50 * 0.1);
        capturedAt.setCellValueFactory(new PropertyValueFactory<>("captureAt"));
        capturedAt.setMinWidth(Const.W50 * 0.1);
        protocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        protocol.setMinWidth(Const.W50 * 0.1);
        length.setCellValueFactory(new PropertyValueFactory<>("length"));
        length.setMinWidth(Const.W50 * 0.1);
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        description.setMinWidth(Const.W50 * 0.5);
        table.getColumns().addAll(number, capturedAt, protocol, length, description);
        this.getChildren().add(table);
        events();
    }

    private void events() {
        table.getItems().addListener((ListChangeListener<PacketRaw>)(c -> {
            c.next();
            int size = table.getItems().size();
            if(size > 0)
                table.scrollTo(size - 1);
        }));
    }

    @Override
    public void run() {
        super.run();
        scheduledExecutorService.scheduleAtFixedRate(()->{
            if(!pause){
                List<Packet> packets = CapturePackets.getInstance().getCapturePackets();
                for(int i=init; i<packets.size(); i++){
                    Packet packet = packets.get(i);
                    update(i+1, packet);
                    if(table.getItems().size() > 700)
                        Platform.runLater(() -> table.getItems().remove(0));
                }
                init = packets.size();
            }

        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void update(int i, Packet packet) {
        if(packet.contains(IpV4Packet.class)){
           IpV4Packet ipv4 = packet.get(IpV4Packet.class);
           LocalTime now = LocalTime.now();
           String dst = ipv4.getHeader().getDstAddr().toString();
           String src = ipv4.getHeader().getSrcAddr().toString();
            int length = ipv4.getHeader().length();
            String description = "Src: " + src + "\t" + "Dst: " + dst;
            PacketRaw packetRaw = new PacketRaw(i, now.format(DateTimeFormatter.ISO_TIME), "IPv4", length, description);
            Platform.runLater(()-> table.getItems().add(packetRaw));
        }
        if(packet.contains(EthernetPacket.class)){
            EthernetPacket ethernet = packet.get(EthernetPacket.class);
            LocalTime now = LocalTime.now();
            String dst = ethernet.getHeader().getDstAddr().toString();
            String src = ethernet.getHeader().getSrcAddr().toString();
            int length = ethernet.getHeader().length();
            String description = "Src: " + src + "\t" + "Dst: " + dst;
            PacketRaw packetRaw = new PacketRaw(i, now.format(DateTimeFormatter.ISO_TIME), "Ethernet", length, description);
            Platform.runLater(()-> table.getItems().add(packetRaw));
        }
        if(packet.contains(TcpPacket.class)){
            TcpPacket tcp = packet.get(TcpPacket.class);
            LocalTime now = LocalTime.now();
            String dst = tcp.getHeader().getDstPort().toString();
            String src = tcp.getHeader().getSrcPort().toString();
            int length = tcp.getHeader().length();
            String description = "Src: " + src + "\t" + "Dst: " + dst;
            PacketRaw packetRaw = new PacketRaw(i, now.format(DateTimeFormatter.ISO_TIME), "TCP", length, description);
            Platform.runLater(()-> table.getItems().add(packetRaw));
        }
        if(packet.contains(UdpPacket.class)){
            UdpPacket udp = packet.get(UdpPacket.class);
            LocalTime now = LocalTime.now();
            String dst = udp.getHeader().getDstPort().toString();
            String src = udp.getHeader().getSrcPort().toString();
            int length = udp.getHeader().length();
            String description = "Src: " + src + "\t" + "Dst: " + dst;
            PacketRaw packetRaw = new PacketRaw(i, now.format(DateTimeFormatter.ISO_TIME), "UDP", length, description);
            Platform.runLater(()-> table.getItems().add(packetRaw));
        }
        if(packet.contains(DnsPacket.class)){
            DnsPacket dns = packet.get(DnsPacket.class);
            LocalTime now = LocalTime.now();
            String info = dns.getHeader().getAdditionalInfo().toString();
            String ques = dns.getHeader().getQuestions().toString();
            String ans = dns.getHeader().getAnswers().toString();
            int length = dns.getHeader().length();
            String description = "Info: " + info + "\n" + "Questions: " + ques + "\nAnswers: " + ans;
            PacketRaw packetRaw = new PacketRaw(i, now.format(DateTimeFormatter.ISO_TIME), "DNS", length, description);
            Platform.runLater(()-> table.getItems().add(packetRaw));
        }
    }

    @Override
    public void stop() {
        super.stop();
        table.getItems().clear();
    }
}
