package espol.edu.ec.views;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.PacketTime;
import espol.edu.ec.models.Panel;
import espol.edu.ec.packetsniffer.Const;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.pcap4j.core.PcapStat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Stats extends Panel {

    private GridPane content;
    private Text receivedValue;
    private Text droppedValue;
    private Text droppedByIfValue;
    private Text capturedValue;
    private Text timeValue;
    private Text speedValue;
    private Text bytesValue;
    private int value;

    public Stats() {
        super(new String("Estadísticas".getBytes(), StandardCharsets.UTF_8));
        content = new GridPane();
        receivedValue = new Text("0");
        droppedValue = new Text("0");
        droppedByIfValue = new Text("0");
        capturedValue = new Text("0");
        timeValue = new Text(":00");
        speedValue = new Text("0");
        bytesValue = new Text("0");
        value = 0;
        initCtl();
    }

    private void initCtl(){
        Text received = getText("Paquetes recibidos:");
        Text dropped = getText("Paquetes perdidos:");
        Text droppedByIf = getText("Paquetes perdidos por Interfaz:");
        Text captured = getText("Paquetes capturados:");
        Text time = getText("Tiempo capturando:");
        Text speed = getText("Velocidad de captura:");
        Text bytes = getText("Bytes capturados:");
        content.addColumn(0, received, dropped, droppedByIf, captured, time, speed, bytes);
        content.addColumn(1, receivedValue, droppedValue, droppedByIfValue, capturedValue, timeValue, speedValue, bytesValue);
        this.getChildren().add(content);
        content.setHgap(Const.W1);
        content.setVgap(Const.H1);
    }

    private Text getText(String text){
        Text output = new Text(text);
        output.setFont(Font.font(output.getFont().getFamily(), FontWeight.BOLD, output.getFont().getSize()));
        return output;
    }

    @Override
    public void run() {
        super.run();
        scheduledExecutorService.scheduleAtFixedRate(()->{
            if(!pause){
                PcapStat stats = CapturePackets.getInstance().getStats();
                Platform.runLater(()->{
                    long num = stats.getNumPacketsCaptured();
                    receivedValue.setText(String.valueOf(stats.getNumPacketsReceived()));
                    droppedValue.setText(String.valueOf(stats.getNumPacketsDropped()));
                    droppedByIfValue.setText(String.valueOf(stats.getNumPacketsDroppedByIf()));
                    capturedValue.setText(String.valueOf(num));
                    double speed = ((double)num)/(CapturePackets.getInstance().getCurrentTime()/1000.0);
                    speedValue.setText(String.format("%.2f [p/s]", speed));
                    bytesValue.setText(CapturePackets.getInstance().getTotalBytes() + " [bytes]");
                    timeValue.setText(getTime());
                    value++;
                });
            }

        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void runOffline() {
        List<PacketTime> list = CapturePackets.getInstance().getCapturePackets();
        Platform.runLater(()->{
            receivedValue.setText(String.valueOf(list.size()));
            droppedValue.setText("No disponible");
            droppedByIfValue.setText("No disponible");
            capturedValue.setText(String.valueOf(list.size()));
            speedValue.setText("No disponible");
            bytesValue.setText(CapturePackets.getInstance().getTotalBytes() + " [bytes]");
            timeValue.setText("No disponible");
        });
    }

    @Override
    public void stop() {
        super.stop();
        receivedValue.setText("0");
        droppedValue.setText("0");
        droppedByIfValue.setText("0");
        capturedValue.setText("0");
        timeValue.setText(":00");
        speedValue.setText("0");
        bytesValue.setText("0");
        value = 0;
    }

    private String getTime(){
        int seg = value%60;
        int min = value/60;
        int hour = min/60;
        String text;
        if(seg < 10){
            text = ":0" + seg;
        }else{
            text = ":" + seg;
        }
        if(min < 10 && min > 0){
            text = "0" + min + text;
        }else if(min > 9) {
            text = min + text;
        }
        if(hour < 10 && hour > 0){
            text = "0" + hour + ":" + text;
        }else if(hour > 9){
            text = hour + ":" + text;
        }
        return text;
    }
}
