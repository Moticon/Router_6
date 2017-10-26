package com.moticon.network.datagrams;

import com.moticon.network.Constants;
import com.moticon.network.datagram_fields.CRC;
import com.moticon.network.datagram_fields.DatagramPayload;
import com.moticon.network.datagram_fields.LL2PAddressField;
import com.moticon.network.datagram_fields.LL2PTypeField;
import com.moticon.support.Factory;
import com.moticon.support.Utilities;

import static com.moticon.network.Constants.LL2P_ADDRESS_LENGTH;
import static com.moticon.network.Constants.LL2P_DESTINATION_OFFSET;
import static com.moticon.network.Constants.LL2P_PAYLOAD_OFFSET;
import static com.moticon.network.Constants.LL2P_SOURCEADDRESS_OFFSET;
import static com.moticon.network.Constants.LL2P_TYPE_LENGTH;
import static com.moticon.network.Constants.LL2P_TYPE_OFFSET;
import static com.moticon.network.Constants.newline;

/**
 * Created by moticon on 11/13/2016.
 */

public class LL2PFrame implements Datagram {
    /*
     *  For all these in 2018, Consider declaring them as Interface types - DatagramHeaderFields.
     *   - Then for all methods in the class they should be using the interface methdos for header fields.
     *
     *   For the unusual case where an object's unique method is needed then you could typecast it.
     *     - for example, usually people will need the destination address as an integer.
     *     instead of providing amethod that returns the destination address field object,
     *     we honor the principle of "least knowledge". We don't actually return the field, we
       *     have a method that returns the integer tha tis needed. In that method we typecast
       *     the datagramHeaderField object into a LL2PAddressField object to get to its method
       *     that returns an integer (the method that *isn't* part of the DatagramHeaderField interface.
       *
       *     Thus we are honoring both "Coding to an interface" and "principle of least knowledge"
       *     and we eliminate all teh type casting in teh constructor, and also the factory method is
       *     returning the type of object we're really using.
       *
       *     Credit to Corbin Young for the discussion that brought this thought to mind.
       *
       *     TODO: Implement the above everywhere it makes sense!!
     */
    private LL2PAddressField destinationAddress;
    private LL2PAddressField sourceAddress;
    private LL2PTypeField type;
    private DatagramPayload payload;
    private CRC crc;

    public LL2PFrame(String destinationAddr, String sourceAddr, String type, String payload){
        Factory factory = Factory.getInstance();
        destinationAddress = (LL2PAddressField) factory.getDatagramHeaderField(Constants.LL2P_DESTINATION_ADDRESS, destinationAddr);
        sourceAddress = (LL2PAddressField) factory.getDatagramHeaderField(Constants.LL2P_SOURCE_ADDRESS, sourceAddr);
        this.type = (LL2PTypeField) factory.getDatagramHeaderField(Constants.LL2P_TYPE, type);
        makePayloadField(this.type.getType(), payload);
        crc = (CRC) factory.getDatagramHeaderField(Constants.LL2P_CRC,
                        destinationAddress.toString() + sourceAddress.toString() + type.toString() + payload.toString());
    }

    public LL2PFrame(byte[] frameBytes){
        Factory factory = Factory.getInstance();
        String frame = new String(frameBytes);
        String destAddrString = frame.substring(LL2P_DESTINATION_OFFSET, LL2P_DESTINATION_OFFSET+2*LL2P_ADDRESS_LENGTH);
        String srcAddrString =  frame.substring(LL2P_SOURCEADDRESS_OFFSET, LL2P_SOURCEADDRESS_OFFSET+ 2*LL2P_ADDRESS_LENGTH);
        String typeString =  frame.substring(LL2P_TYPE_OFFSET, LL2P_TYPE_OFFSET + 2*LL2P_TYPE_LENGTH);
        String payloadString = frame.substring(LL2P_PAYLOAD_OFFSET, frame.length()-Constants.LL2P_CRC_LENGTH*2);
        String CRCString = frame.substring(frame.length()-Constants.LL2P_CRC_LENGTH*2, frame.length());
        destinationAddress = (LL2PAddressField) factory.getDatagramHeaderField(Constants.LL2P_DESTINATION_ADDRESS,
                                destAddrString);
        sourceAddress = (LL2PAddressField) factory.getDatagramHeaderField(Constants.LL2P_SOURCE_ADDRESS,
                               srcAddrString);
        type = (LL2PTypeField) factory.getDatagramHeaderField(Constants.LL2P_TYPE, typeString);
        makePayloadField(type.getType(), payloadString);
        crc = (CRC) factory.getDatagramHeaderField(Constants.LL2P_CRC, CRCString);
    }

    private void makePayloadField(Integer type, String payloadText){
        Datagram payloadPacket = Factory.getInstance().getLayer2Payload(type, payloadText);
        payload = new DatagramPayload(payloadPacket);
    }

    @Override
    public String toString() {
        return destinationAddress.toString() + sourceAddress.toString() +
                            type.toString() + payload.toString() + crc.toString();
    }

    @Override
    public String toSummaryString() {
        return destinationAddress.toString() + " | " +
                sourceAddress.toString() + " | " + payload.getSummaryString();
    }

    @Override
    public String toProtocolExplanationString() {
        return "LL2P" + newline +
                " " + destinationAddress.explainSelf() + newline +
                " " + sourceAddress.explainSelf() + newline +
                " " + type.explainSelf() + newline +
                " " + crc.explainSelf() + newline +
                "----------------" + newline +
                payload.explainSelf();
                //  no payload in the breakout. it's in a lower window
                // payload.explainSelf() + newline +
    }

    @Override
    public String toHexString() {
        return destinationAddress.toHexString() + sourceAddress.toHexString() +
                type.toHexString() + payload.toHexString() + crc.toHexString();
    }

    public String getHexDisplayString(){
        return Utilities.toIndexedHexDisplay(this.toHexString());
    }

    public LL2PAddressField  getDestinationAddressField() {return destinationAddress;}

    public Integer getDestinationAddress(){ return destinationAddress.getAddress();}

    public Integer getSourceAddress(){ return sourceAddress.getAddress();}

    public LL2PAddressField getSourceAddressField() {return sourceAddress;}

    public Integer getType(){return type.getType();}

    public byte[] getPayloadAsHexBytes(){ return payload.toHexString().getBytes();}

    public Datagram getPayload() { return payload.getPacket();}
}
