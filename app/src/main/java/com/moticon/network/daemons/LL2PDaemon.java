package com.moticon.network.daemons;

import android.util.Log;

import com.moticon.UI.UIManager;
import com.moticon.network.Constants;
import com.moticon.network.datagrams.ARPDatagram;
import com.moticon.network.datagrams.LL2PFrame;
import com.moticon.network.datagrams.LL3PDatagram;
import com.moticon.network.datagrams.LRPDatagram;
import com.moticon.support.BootLoader;
import com.moticon.support.Factory;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by pat.smith on 12/7/2016.
 */
public class LL2PDaemon implements Observer {
    private static LL2PDaemon ourInstance = new LL2PDaemon();

    private LL1Daemon ll1Daemon;
    private UIManager uiManager;
    private Factory factory;
    private ARPDaemon arpDaemon;

    public static LL2PDaemon getInstance() {
        return ourInstance;
    }

    private LL2PDaemon() {
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class){
            ll1Daemon = LL1Daemon.getInstance();
            uiManager = UIManager.getInstance();
            factory = Factory.getInstance();
            arpDaemon = ARPDaemon.getInstance();
        }
    }

    public void processLL2PFrame(LL2PFrame frame){
        if (!frame.getDestinationAddressField().getAddress().equals(Constants.MY_LL2P_ADDRESS)){
            // this isn't us. Log it, raise a toast to notify user.
            Log.d(Constants.logTag, "Received Frame with incorrect MAC Address: " + frame.toString());
            uiManager.displayMessage("Frame with Address "+frame.getDestinationAddressField().toHexString()+" Received. Not us!");
            return;
        }
        switch (frame.getType()) {
            case Constants.LL2P_TYPE_IS_ECHO_REQUEST:
                answerEchoRequest(frame);
                break;
            case Constants.LL2P_TYPE_IS_ECHO_REPLY:
                displayEchoReply(frame);
                break;
            case Constants.LL2P_TYPE_IS_ARP_REQUEST:
                // get the source LL3P Address and tell the arpdaemon about it.
                // the arp daemon is responsible for handling this and originating a reply.
                Integer sourceLL3PAddress = ((ARPDatagram) frame.getPayload()).getLl3pAddress().getAddress();
                arpDaemon.processARPRequest(frame.getSourceAddress(), sourceLL3PAddress);
                break;
            case Constants.LL2P_TYPE_IS_ARP_REPLY:
                // get the source LL3P Address and tell the arpdaemon about it.
                Integer ll3PAddress = ((ARPDatagram) frame.getPayload()).getLl3pAddress().getAddress();
                arpDaemon.addARPEntry(frame.getSourceAddress(), ll3PAddress);
                break;
            case Constants.LL2P_TYPE_IS_LRP:
                LRPDaemon.getInstance().processLRPPacket((LRPDatagram) frame.getPayload(), frame.getSourceAddress());
                break;
            case Constants.LL2P_TYPE_IS_LL3P:
                LL3PDaemon.getInstance().processLL3PPacket((LL3PDatagram) frame.getPayload(), frame.getSourceAddress());
                break;
            default:
                uiManager.displayMessage("Frame has bad TYPE field: "+frame.toString());
        }
    }

    public void sendARPReplay(Integer destinationLL2Paddress){
        LL2PFrame arpReply = new LL2PFrame(Integer.toHexString(destinationLL2Paddress),
                Integer.toHexString(Constants.MY_LL2P_ADDRESS),
                Integer.toHexString(Constants.LL2P_TYPE_IS_ARP_REPLY),
                Constants.MY_LL3P_ADDRESS_STRING);
        ll1Daemon.sendFrame(arpReply);
    }

    public void sendLL3PFrame(Integer ll2pDestination, LL3PDatagram packet){
        LL2PFrame frame =  new LL2PFrame(Integer.toHexString(ll2pDestination),
                Integer.toHexString(Constants.MY_LL2P_ADDRESS),
                Integer.toHexString(Constants.LL2P_TYPE_IS_LL3P),
                packet.toString());
        ll1Daemon.sendFrame(frame);
    }


    public void sendARPRequest(Integer ll2pAddr){
        LL2PFrame arpRequest = new LL2PFrame(Integer.toHexString(ll2pAddr),
                                            Integer.toHexString(Constants.MY_LL2P_ADDRESS),
                                            Integer.toHexString(Constants.LL2P_TYPE_IS_ARP_REQUEST),
                                            Constants.MY_LL3P_ADDRESS_STRING);
        ll1Daemon.sendFrame(arpRequest);
    }

    public void sendLRPUpdate(LRPDatagram lrpDatagram, Integer neighborLL2P){
        LL2PFrame lrpFrame = new LL2PFrame(Integer.toHexString(neighborLL2P),
                                            Integer.toHexString(Constants.MY_LL2P_ADDRESS),
                                            Integer.toHexString(Constants.LL2P_TYPE_IS_LRP),
                                            lrpDatagram.toString());
        ll1Daemon.sendFrame(lrpFrame);
    }

    private void answerEchoRequest(LL2PFrame frame){
        ll1Daemon.sendFrame(new LL2PFrame(frame.getSourceAddressField().toHexString(),
                frame.getDestinationAddressField().toHexString(),
                Integer.toHexString(Constants.LL2P_TYPE_IS_ECHO_REPLY),
                frame.getPayload().toString()));
    }

    private void displayEchoReply(LL2PFrame frame){
        uiManager.displayMessage("Received Echo Reply from " + frame.getDestinationAddressField().toHexString() +
                                "; Replay text: " + frame.getPayload().toString());
    }
}
