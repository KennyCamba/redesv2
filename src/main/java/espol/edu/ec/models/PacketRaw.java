package espol.edu.ec.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PacketRaw {
    private SimpleIntegerProperty number;
    private SimpleStringProperty captureAt;
    private SimpleStringProperty protocol;
    private SimpleIntegerProperty length;
    private SimpleStringProperty description;

    public PacketRaw(int number, String time, String protocol, int length, String description){
        this.number = new SimpleIntegerProperty(number);
        this.captureAt = new SimpleStringProperty(time);
        this.protocol = new SimpleStringProperty(protocol);
        this.length = new SimpleIntegerProperty(length);
        this.description = new SimpleStringProperty(description);
    }

    public int getNumber() {
        return number.get();
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public String getCaptureAt() {
        return captureAt.get();
    }

    public void setCaptureAt(String captureAt) {
        this.captureAt.set(captureAt);
    }

    public String getProtocol() {
        return protocol.get();
    }

    public void setProtocol(String protocol) {
        this.protocol.set(protocol);
    }

    public int getLength() {
        return length.get();
    }

    public void setLength(int length) {
        this.length.set(length);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
