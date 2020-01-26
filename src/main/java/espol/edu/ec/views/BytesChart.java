package espol.edu.ec.views;

import espol.edu.ec.controllers.CapturePackets;
import espol.edu.ec.models.Panel;
import javafx.application.Platform;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.pcap4j.packet.Packet;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BytesChart extends Panel {
    private LineChart<String, Number> bytes;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private XYChart.Series<String, Number> series;
    private int init;

    public BytesChart() {
        super(new String("Transmisión de Bytes".getBytes(), StandardCharsets.UTF_8));
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        bytes = new LineChart<>(xAxis, yAxis);
        series = new XYChart.Series<>();
        init = 0;
        initCtrl();
    }

    private void initCtrl() {
        series.setName("Bytes");
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false);
        yAxis.setLabel("Bytes");
        yAxis.setAnimated(false);
        bytes.setTitle("Bytes transmitidos");
        bytes.setAnimated(false);
        bytes.getData().add(series);
        this.getChildren().add(bytes);
    }

    @Override
    public void run() {
        super.run();
        scheduledExecutorService.scheduleAtFixedRate(()->{
            if(!pause){
                List<Packet> packets = CapturePackets.getInstance().getCapturePackets();
                for(int i=init; i<packets.size(); i++){
                    LocalTime now = LocalTime.now();
                    Packet p = packets.get(i);
                    Platform.runLater(()->{
                        if(series.getData().size() > 40)
                            series.getData().remove(0);
                        series.getData().add(new XYChart.Data<>(now.format(DateTimeFormatter.ISO_TIME), p.length()));
                    });
                }
                init = packets.size();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

    }

    @Override
    public void stop() {
        super.stop();
        series.getData().clear();
    }
}
