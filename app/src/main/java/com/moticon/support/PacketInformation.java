package com.moticon.support;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by pat.smith on 11/16/2016.
 */

public class PacketInformation {
    private DatagramSocket socket;
    private DatagramPacket packet;


    public PacketInformation(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public DatagramPacket getPacket() {
        return packet;
    }
}
