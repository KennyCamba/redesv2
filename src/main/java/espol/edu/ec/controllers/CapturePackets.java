/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espol.edu.ec.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
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
    private List<Packet> capturePackets;
    
    private CapturePackets(){
        capturePackets = new ArrayList<>();
        isRun = false;
        pause = false;
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
            t.start();
        }else if(pause){
            pause = false;
        }else {
            throw new Exception("Action not allowed!");
        }
    }
    
    public void pause(){
        if(device != null && isRun)
            pause = true;
    }
    
    public void stop(){
        if(device != null && isRun){
            pause = false;
            isRun = false;
            capturePackets.clear();
        }
    }
    
    private void capture(){
        PacketListener listener = (Packet packet) -> {
            capturePackets.add(packet);
            System.out.println(packet);
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
    
    public List<Packet> getCapturePackets(){
        Object obj = ((ArrayList<Packet>)this.capturePackets).clone();
        return obj instanceof ArrayList ? (ArrayList<Packet>)obj: null;
    }
}
