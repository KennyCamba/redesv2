/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import espol.edu.ec.models.PacketTime;
import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.Packet;

/**
 *
 * @author kcamb
 */
public class CapturePackets {
    private static CapturePackets instance = new CapturePackets();
    private PcapNetworkInterface device;
    private PcapHandle handle;
    private Runnable run;
    private boolean isRun;
    private boolean pause;
    private List<PacketTime> capturePackets;
    private int bytes;
    private long init;
    private long timePause;
    
    private CapturePackets(){
        capturePackets = new ArrayList<>();
        isRun = false;
        pause = false;
        bytes = 0;
        init = 0;
        timePause = 0;
        run = () -> {
            while(isRun){
                System.out.print("");
                if(!pause){
                    capture();
                }
            }
        };
    }
    
    public static CapturePackets getInstance() {
        return instance;
    }
    
    public void setDevice(PcapNetworkInterface device){
        this.device = device;
    }
    
    public void play() throws Exception{
        if(device != null && !isRun){
            int snapshotLength = 65536; // in bytes   
            int readTimeout = 100; // in milliseconds                   
            handle = device.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, readTimeout);
            isRun = true;
            Thread t = new Thread(run);
            init = System.currentTimeMillis();
            t.start();
        }else if(pause){
            pause = false;
            timePause = System.currentTimeMillis() - timePause;
        }else {
            throw new Exception("Action not allowed!");
        }
    }
    
    public void pause(){
        if(device != null && isRun){
            pause = true;
            timePause = System.currentTimeMillis();
        }
    }
    
    public void stop(){
        if(device != null && isRun){
            pause = false;
            isRun = false;
            capturePackets.clear();
            init = 0;
            timePause = 0;
            bytes = 0;
        }
    }
    
    private void capture(){
        PacketListener listener = (Packet packet) -> {
            capturePackets.add(new PacketTime(new Timestamp(System.currentTimeMillis()),  packet));
            bytes += packet.length();
            //System.out.println(packet);
        };
        try {
            int maxPackets = 1;
            try {
                handle.loop(maxPackets, listener);
            } catch (PcapNativeException | NotOpenException ex) {
                Logger.getLogger(CapturePackets.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InterruptedException e) {
            Logger.getLogger(CapturePackets.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public List<PacketTime> getCapturePackets(){
        Object obj = ((ArrayList<PacketTime>)this.capturePackets).clone();
        return obj instanceof ArrayList ? (ArrayList<PacketTime>)obj: null;
    }

    public PcapStat getStats(){
        try {
            return handle.getStats();
        } catch (PcapNativeException | NotOpenException e) {
            return null;
        }
    }

    public int getTotalBytes(){
        return bytes;
    }

    public long getCurrentTime(){
        return device != null && isRun ? System.currentTimeMillis() - init - timePause: 0;
    }

    public void savePackets(String path){
        try {
            PcapDumper dumper = handle.dumpOpen(path);
            List<PacketTime> packets = getCapturePackets();
            for(PacketTime packet: packets){
                dumper.dump(packet.getPacket(), packet.getTimestamp());
            }
            dumper.close();
        } catch (PcapNativeException | NotOpenException e) {
            Logger.getLogger(e.getMessage());
        }
    }

    public void openOffline(String pcapfile){
        this.stop();
        try {
            handle = Pcaps.openOffline(pcapfile);
            Packet packet;
            while ((packet = handle.getNextPacket()) != null){
                capturePackets.add(new PacketTime(handle.getTimestamp(), packet));
                bytes += packet.length();
            }
        } catch (PcapNativeException | NotOpenException e) {
            Logger.getLogger(e.getMessage());
        }
    }
}
