package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.network.datagrams.Datagram;
import com.moticon.support.Factory;

/**
 * Created by pat.smith on 12/6/2016.
 */

public class DatagramPayload implements DatagramHeaderField {
    Datagram packet;

    /**
     * this is used when the frame or packet has a payload object already created.
     * @param payload
     */
    public DatagramPayload(Datagram payload){
        packet = payload;
    }

    /**
     * use this one when you have to create a generic text payload...
     * @param payloadText
     */
    public DatagramPayload(String payloadText){
        Factory.getInstance().getLayer2Payload(Constants.LL2P_TYPE_IS_RESERVED, payloadText);
    }

    @Override
    public String explainSelf() {
        return packet.toProtocolExplanationString();
    }

    @Override
    public String toHexString() {
        return packet.toHexString();
    }

    public Datagram getPacket () { return packet;}

    public String toString() { return packet.toString();}

    public String getSummaryString() { return packet.toSummaryString();}
}
