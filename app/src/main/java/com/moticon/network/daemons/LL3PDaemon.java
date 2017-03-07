package com.moticon.network.daemons;

import com.moticon.UI.Messenger;
import com.moticon.UI.UIManager;
import com.moticon.network.Constants;
import com.moticon.network.datagrams.LL3PDatagram;
import com.moticon.support.BootLoader;
import com.moticon.support.LabException;
import com.moticon.support.Utilities;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by moticon on 2/5/2017.
 */

public class LL3PDaemon implements Observer {
    ARPDaemon arpDaemon;
    LL2PDaemon ll2PDaemon;
    LRPDaemon lrpDaemon;
    private Messenger messenger;

    private static LL3PDaemon ourInstance = new LL3PDaemon();

    public static LL3PDaemon getInstance() {return ourInstance;}

    private LL3PDaemon() {};

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class){
            arpDaemon = ARPDaemon.getInstance();
            ll2PDaemon = LL2PDaemon.getInstance();
            lrpDaemon = LRPDaemon.getInstance();
            messenger = UIManager.getInstance().getMessenger();
        }
    }

    public void sendPayload(String message, Integer ll3pAddress){
        sendLL3PToNextHop(new LL3PDatagram(ll3pAddress, message));

    }

    private void sendLL3PToNextHop(LL3PDatagram packet){
        try {
            Integer ll3pNextHop = lrpDaemon.getNextHopForNetwork(Utilities.getNetworkNumberFromLL3P(packet.getDestinationAddress()));
            Integer ll2pNextHop = arpDaemon.getLL2PAddress(ll3pNextHop);
            ll2PDaemon.sendLL3PFrame(ll2pNextHop, packet);
        } catch (LabException e) {
            e.printStackTrace();
            UIManager.getInstance().displayMessage("No Next hop for LL3P ");
        }

    }

    public void processLL3PPacket(LL3PDatagram packet, Integer ll2pSource){
        // update the ARP table.
        arpDaemon.addARPEntry(ll2pSource, packet.getSourceAddress());
        if (packet.getDestinationAddress().equals(Constants.MY_LL3P_ADDRESS)){
            // this is destined for us. Process it.
            // nothing to do right now. Display it.
/*            UIManager.getInstance().displayMessage("Received LL3P Packet" + Constants.newline
                        + packet.toProtocolExplanationString());*/
            messenger.receiveMessage(packet.getSourceAddress(), packet.getPayloadString());
        } else { // destined for someone else
            if (packet.getTTL().equals(0)){
                // this packet died...
                UIManager.getInstance().displayMessage("Packet arrived but TTL expired. Packet is" + packet.toString());
            } else {
                packet.decrementTTL();
                // todo: recalculate checksum if you implement full checksum.
                sendLL3PToNextHop(packet);
            }
        }
    }
}
