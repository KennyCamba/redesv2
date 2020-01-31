package espol.edu.ec.views;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.PacketTime;
import espol.edu.ec.models.Panel;
import espol.edu.ec.models.WebApplication;
import espol.edu.ec.packetsniffer.Const;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import org.pcap4j.packet.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApplicationsTraffic extends Panel {

    private ScrollPane scrollPane;
    private GridPane grid;
    private List<ApplicationView> applications;
    private List<String> ips;
    private int init;

    public ApplicationsTraffic() {
        super("Trafico de aplicaciones");
        scrollPane = new ScrollPane();
        applications = new ArrayList<>();
        ips = new ArrayList<>();
        grid = new GridPane();
        init = 0;
        initCtrl();
    }

    private void initCtrl() {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        grid.setHgap(Const.H1);
        scrollPane.setContent(grid);
        this.getChildren().add(scrollPane);
    }

    @Override
    public void run() {
        super.run();
        scheduledExecutorService.scheduleAtFixedRate(()->{
            if(!pause){
                List<PacketTime> packets = CapturePackets.getInstance().getCapturePackets();
                for(int i=init; i<packets.size(); i++){
                    PacketTime p = packets.get(i);
                    update(p.getPacket(), packets.size());
                }
                init = packets.size();
                packets.clear();
            }
        }, 0, 3000, TimeUnit.MILLISECONDS);
    }

    private void update(Packet packet, int size) {
        if(!packet.contains(DnsPacket.class)){
            if(packet.contains(TcpPacket.class) || packet.contains(UdpPacket.class)){
                IpV4Packet ipv4 = packet.get(IpV4Packet.class);
                try {
                    int ind;
                    if((ind = ips.indexOf(ipv4.getHeader().getSrcAddr().getHostAddress())) != -1){
                        Platform.runLater(()->{
                            ApplicationView av = applications.get(ind);
                            av.setPercentage(av.getCount()+1, size);
                            order();
                        });
                    }else{
                        InetAddress src = InetAddress.getByName(ipv4.getHeader().getSrcAddr().getHostAddress());
                        WebApplication web = new WebApplication(src);
                        ApplicationView view = new ApplicationView(web);
                        ips.add(ipv4.getHeader().getSrcAddr().getHostAddress());
                        Platform.runLater(()->{
                            applications.add(view);
                            view.setPercentage(1, size);
                            order();
                        });
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void order() {
        grid.getChildren().clear();
        Collections.sort(applications);
        int i = 0;
        for(ApplicationView app: applications){
            grid.addRow(i++, app.getIcon(), app.getName(), app.getPb(), app.getPi());
        }
    }

    @Override
    public void stop() {
        super.stop();
        grid.getChildren().clear();
        applications.clear();
        ips.clear();
    }

    /*@Override
    public void runOffline() {
        List<PacketTime> packetTimes = CapturePackets.getInstance().getCapturePackets();
        for(PacketTime packet: packetTimes){
            update(packet.getPacket(), packetTimes.size());
        }
    }*/
}
