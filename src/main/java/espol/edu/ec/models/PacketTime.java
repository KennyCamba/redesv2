package espol.edu.ec.models;

import org.pcap4j.packet.Packet;

import java.sql.Timestamp;

public class PacketTime {
    private Timestamp timestamp;
    private Packet packet;

    public PacketTime(Timestamp timestamp, Packet packet) {
        this.timestamp = timestamp;
        this.packet = packet;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
