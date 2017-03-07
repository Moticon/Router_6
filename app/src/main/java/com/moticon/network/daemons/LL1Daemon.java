package com.moticon.network.daemons;

import android.os.AsyncTask;
import android.util.Log;

import com.moticon.UI.UIManager;
import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.AdjacencyRecord;
import com.moticon.network.datagrams.LL2PFrame;
import com.moticon.network.table.Table;
import com.moticon.support.BootLoader;
import com.moticon.support.Factory;
import com.moticon.support.FrameLogger;
import com.moticon.support.GetIPAddress;
import com.moticon.support.LabException;
import com.moticon.support.PacketInformation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by pat.smith on 11/14/2016.
 */
public class LL1Daemon extends Observable implements Observer {
    private static LL1Daemon ourInstance = new LL1Daemon();
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private InetAddress groupAddress;
    private InetAddress nameServerAddress;
    private Table adjacencyTable;
    private GetIPAddress nameServer;
    private Factory factory;
    private UIManager uiManager;
    private LL2PDaemon ll2PDaemon;

    public static LL1Daemon getInstance() {
        return ourInstance;
    }

    private LL1Daemon() {
        adjacencyTable = new Table();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class){
            // called by the bootloader... wire this object to other objects it needs.
            // for example, later this will include LL2Daemon.
            openSockets();
            nameServer = GetIPAddress.getInstance();
            factory = Factory.getInstance();
            addObserver(FrameLogger.getInstance());
            uiManager = UIManager.getInstance();
            ll2PDaemon = LL2PDaemon.getInstance();
            new ReceiveUnicastFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, receiveSocket);
        }
    }

    public void removeAdjacency(AdjacencyRecord recordToRemove){
        adjacencyTable.removeItem(recordToRemove.getKey());
    }

    public Table getAdjacencyTable(){
        return adjacencyTable;
    }

    /**
     * Called by the Table com.moticon.UI manager when the user enters an LL2P address to add the adjacency.
     * @param ll2pAddress
     */
    public void addAdjacency(String ll2pAddress){
        Integer address;
        try {
            address = Integer.valueOf(ll2pAddress, 16);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        // here call the name server and pass a string of the domain name built from IP address
        //   and the nameserver's unique name.
        InetAddress ipAddress = nameServer.getInetAddress(ll2pAddress + ".babelfish.oc.edu");
        AdjacencyRecord adjacencyRecord = (AdjacencyRecord) factory.getTableRecord(Constants.ADJACENCYTABLE_RECORD);
        adjacencyRecord.setLL2PAddress(address);
        adjacencyRecord.setIPAddress(ipAddress);
        adjacencyTable.addItem(adjacencyRecord);
        ll2PDaemon.sendARPRequest(address);
/*        setChanged();
        notifyObservers(adjacencyRecord);*/
    }

    /**
     * Called by the Table com.moticon.UI manager when the user enters an LL2P address to add the adjacency.
     * @param ll2pAddress
     */
    public void addAdjacency(String ll2pAddress, String ipString){
        Integer address;
        try {
            address = Integer.valueOf(ll2pAddress, 16);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        // here call the name server and pass a string of the domain name built from IP address
        //   and the nameserver's unique name.
        InetAddress ipAddress = nameServer.getInetAddress(ipString);
        AdjacencyRecord adjacencyRecord = (AdjacencyRecord) factory.getTableRecord(Constants.ADJACENCYTABLE_RECORD);
        adjacencyRecord.setLL2PAddress(address);
        adjacencyRecord.setIPAddress(ipAddress);
        // add the new adjacency record to the table.
        adjacencyTable.addItem(adjacencyRecord);
/*
        setChanged();
        notifyObservers(adjacencyRecord);*/

        ll2PDaemon.sendARPRequest(adjacencyRecord.getLL2PAddress());
    }

    /**
     * Datagram Socket opens the socket for sending a UDP Packet.  Here we simply open
     *   a UDP port.
     *
     */
    private void openSockets(){
        sendSocket = null;  // defined in case the open port method fails.
        // try to open the socket.  This is a send socket so we don't declare the port number.
        try {
            sendSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        // open the receive port. declare the port number to listen on.
        receiveSocket = null;
        try {
            receiveSocket = new DatagramSocket(Constants.UDP_PORT); // receive port defined above.
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendFrame(LL2PFrame frame){
        String frameToSend = new String(frame.toString());
        boolean foundValidAddress = true;
        /*
         * Here we get the ip address for the MAC frame from the adjacencyTable and use it
         * to fetch the actual Internet address data structure for use in opening the port.
         */
        uiManager.displayMessage("Sending Packet");
        InetAddress IPAddress = null;
        try {
            AdjacencyRecord record = (AdjacencyRecord) adjacencyTable.getItem(frame.getDestinationAddress());
            IPAddress = record.getIPAddress();
        } catch (LabException e) {
            foundValidAddress = false;
        }

        if (foundValidAddress) {
            // create datagram for sending.
            DatagramPacket sendPacket = new DatagramPacket(frameToSend.getBytes(), // string, converted to bytes.
                    frameToSend.length(), // # of bytes.
                    IPAddress, // IP address retrieved above
                    Constants.UDP_PORT); // port 9999 as of feb 1, 2012
            //send the packet to the remote system.  Use the Async task private class for this.
            new SendUnicastFrame().execute(new PacketInformation(sendSocket, sendPacket));
            setChanged();
            notifyObservers(frame);
            //soundPlayer.playPacketSentSound();
        } else {
            uiManager.displayMessage("Attempt to send to unknown LL2P: "+frame.getDestinationAddressField().toHexString());
            //soundPlayer.playBadPacketSound();
        }
    }

    /*
    TODO: Redesign this part of the ll1daemon.  Have 2 unique classes to send and receive UDP packets.
    The receive class has a constructor that sets up the port and gets the LL1Daemon's reference (in an update)
    The receive class has a runnable that does the following:
        1. set up the port on 4999
        2. post a read
        3. when the read is complete get on the com.moticon.UI thread and send the bytes back to the LL1 Daemon.
             -- the ll1daemon will notify the logger and pass them up to the ll2daemon
        4. return in an infinite loop back to step 2.  T

     The send thread has a constructor that sets up a send port
     The send thread has a runnable that simply sends the bytes in the port.
        - this thread runs and dies, so you must always spin off a thread to run it?
     */

    /** send UDP Packet
     * A private Async class to send packets out of the com.moticon.UI thread.
     */
    private class SendUnicastFrame extends AsyncTask<PacketInformation, Void, Void> {
        @Override
        protected Void doInBackground(PacketInformation... arg0) {
            /*
            A PacketInformation class has a UDP socket and a UDP Datagram packet
            Use the socket to send the packet.
             */
            PacketInformation pktInfo = arg0[0];
            try {
                pktInfo.getSocket().send(pktInfo.getPacket());
                Log.i(Constants.logTag, ">>>>>>>>>Sent frame: "+new String(pktInfo.getPacket().getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ReceiveUnicastFrame extends AsyncTask<DatagramSocket, Void, byte[]> {

        @Override
        protected byte[] doInBackground(DatagramSocket... socketList) {

            byte[] receiveData = new byte[1024];
            DatagramSocket unicastSocket = socketList[0];

            /*
            I'm getting a weird intermittant null object for the socket.  I can't find where
            this is coming from but I suspect it relates to a thread problem. In order to avoid it
            I am checking for the unicastSocket to be null. If it is then I'm opening a new one
            here.
             */
            if (unicastSocket==null){
                try {
                    unicastSocket = new DatagramSocket(Constants.UDP_PORT); // receive port defined above.
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            // create a datagram packet to receive the UPD data.
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            Log.d(Constants.logTag, "Inside rx unicast Thread");
            try {
                unicastSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int bytesReceived = receivePacket.getLength();
            byte[] frameBytes = new String(receivePacket.getData()).substring(0,bytesReceived).getBytes();

            Log.d(Constants.logTag, "Received bytes: "+ new String(frameBytes));
            return frameBytes;
        }

        @Override
        protected void onPostExecute(byte[] frameBytes) {
            LL2PFrame ll2PFrame = new LL2PFrame(frameBytes);
            setChanged();
            notifyObservers(ll2PFrame);

            // pass this LL2P Frame to the LL2PDaemon when you create one...
            ll2PDaemon.processLL2PFrame(ll2PFrame);
            // spin off a new thread to listen for packets.
            if (receiveSocket==null){
                try {
                    receiveSocket = new DatagramSocket(Constants.UDP_PORT); // receive port defined above.
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            new ReceiveUnicastFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, receiveSocket);
        }
    }

}
