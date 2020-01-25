package espol.edu.ec.views;

import espol.edu.ec.models.Panel;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class BytesChart extends Panel {
    private LineChart<String, Number> bytes;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;

    public BytesChart() {
        super("Transmisión de Bytes");
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        bytes = new LineChart<>(xAxis, yAxis);
        initCtrl();
    }

    private void initCtrl() {
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false);
        yAxis.setLabel("Bytes");
        yAxis.setAnimated(false);
        bytes.setTitle("");
    }


}
