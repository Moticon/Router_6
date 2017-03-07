package com.moticon.network.RecordTypes;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by pat.smith on 11/22/2016.
 */


public class AdjacencyRecord extends TableRecordClass {
    private Integer LL2PAddress;
    private InetAddress IPAddress;

    public AdjacencyRecord (){
        super();
        LL2PAddress = new Integer(0);
        try {
            IPAddress = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public AdjacencyRecord(Integer ll2p, InetAddress ipAddress){
        super();
        this.IPAddress = ipAddress;
        this.LL2PAddress = ll2p;
    }

    public Integer getLL2PAddress() {
        return LL2PAddress;
    }

    public void setLL2PAddress(Integer LL2PAddress) {
        this.LL2PAddress = LL2PAddress;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String toString(){
        return new String("LL2P: " + Integer.toHexString(LL2PAddress) + "; IP: " + IPAddress.getHostAddress());
    }

    @Override
    public Integer getKey(){
        return LL2PAddress;
    }
}
