package com.moticon.network.datagrams;

import com.moticon.network.Constants;
import com.moticon.network.datagram_fields.DatagramPayload;
import com.moticon.network.datagram_fields.LL3PAddressField;
import com.moticon.network.datagram_fields.LL3PChecksum;
import com.moticon.network.datagram_fields.LL3PIdentifierField;
import com.moticon.network.datagram_fields.LL3PTTLField;
import com.moticon.network.datagram_fields.LL3PTypeField;
import com.moticon.support.Factory;
import com.moticon.support.Utilities;

/**
 * Created by moticon on 2/5/2017.
 */

public class LL3PDatagram implements Datagram {
    private LL3PAddressField sourceAddress;
    private LL3PAddressField destinationAddress;
    private LL3PTTLField ttl;
    private LL3PIdentifierField identifier;
    private LL3PTypeField type;
    private LL3PChecksum checksum;
    private DatagramPayload payload;

    public LL3PDatagram(byte[] packet){
        parseString(new String(packet));
    }

    public LL3PDatagram(String packet){
        parseString(packet);
    }

    public LL3PDatagram(LL3PAddressField sourceAddress, LL3PAddressField destinationAddress,
                        LL3PTTLField ttl, LL3PIdentifierField identifier, LL3PTypeField type,
                        LL3PChecksum checksum, DatagramPayload payload) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.ttl = ttl;
        this.identifier = identifier;
        this.type = type;
        this.checksum = checksum;
        this.payload = payload;
    }

    public LL3PDatagram(Integer destAddress, String payload) {
        Factory factory = Factory.getInstance();
        destinationAddress = (LL3PAddressField) factory.getDatagramHeaderField(Constants.LL3P_SOURCE_ADDRESS_FIELD,
                Utilities.padHexString(Integer.toHexString(destAddress), Constants.LL3P_ADDRESS_LENGTH));
        sourceAddress = (LL3PAddressField) factory.getDatagramHeaderField(Constants.LL3P_DESTINATION_ADDRESS_FIELD, Constants.MY_LL3P_ADDRESS_STRING);
        ttl = (LL3PTTLField) factory.getDatagramHeaderField(Constants.LL3P_TTL_FIELD,
                Utilities.padHexString(Integer.toHexString(Constants.MAX_TTL), Constants.LL3P_TTL_LENGTH));
        identifier = (LL3PIdentifierField) factory.getDatagramHeaderField(Constants.LL3P_IDENTIFIER_FIELD, Constants.LL3P_IDENTIFIER);
        type = (LL3PTypeField) factory.getDatagramHeaderField(Constants.LL3P_TYPE_FIELD,
                Utilities.padHexString(Integer.toHexString(Constants.LL3P_TYPE_TEXT), Constants.LL3P_TYPE_LENGTH));
        checksum = (LL3PChecksum) factory.getDatagramHeaderField(Constants.LL3P_CHECKSUM_FIELD, "0000");
        this.payload = (DatagramPayload) factory.getLayer3PayloadField(Constants.LL3P_TYPE_TEXT, payload);
    }

    private void parseString(String packet){
        Factory factory = Factory.getInstance();
        sourceAddress = (LL3PAddressField)  factory.getDatagramHeaderField(Constants.LL3P_SOURCE_ADDRESS_FIELD,
                packet.substring(Constants.LL3P_SOURCE_OFFSET, Constants.LL3P_ADDRESS_LENGTH*2+Constants.LL3P_SOURCE_OFFSET));
        destinationAddress = (LL3PAddressField)  factory.getDatagramHeaderField(Constants.LL3P_DESTINATION_ADDRESS_FIELD,
                packet.substring(Constants.LL3P_DESTINATION_ADDRESS_OFFSET*2, Constants.LL3P_ADDRESS_LENGTH*2+Constants.LL3P_DESTINATION_ADDRESS_OFFSET*2));
        ttl = (LL3PTTLField) factory.getDatagramHeaderField(Constants.LL3P_TTL_FIELD,
                packet.substring(Constants.LL3P_TTL_OFFSET*2, Constants.LL3P_TTL_LENGTH*2+Constants.LL3P_TTL_OFFSET*2));
        identifier = (LL3PIdentifierField) factory.getDatagramHeaderField(Constants.LL3P_IDENTIFIER_FIELD,
                packet.substring(Constants.LL3P_IDENTIFIER_OFFSET*2, Constants.LL3P_IDENTIFIER_LENGTH*2+Constants.LL3P_IDENTIFIER_OFFSET*2));
        type = (LL3PTypeField) factory.getDatagramHeaderField(Constants.LL3P_TYPE_FIELD,
                packet.substring(Constants.LL3P_TYPE_OFFSET*2, Constants.LL3P_TYPE_LENGTH*2+Constants.LL3P_TYPE_OFFSET*2));
        checksum = (LL3PChecksum) factory.getDatagramHeaderField(Constants.LL3P_CHECKSUM_FIELD,
                packet.substring(packet.length() - Constants.LL3P_CHECKSUM_LENGTH*2, packet.length()));
        payload = (DatagramPayload) factory.getLayer3PayloadField(Constants.GENERIC_TEXT_PAYLOAD,
                packet.substring(Constants.LL3P_PAYLOAD_OFFSET*2, packet.length() - Constants.LL3P_CHECKSUM_LENGTH*2));
    }


    @Override
    public String toSummaryString() {
        return sourceAddress.toHexString() + " | "
                + destinationAddress.toHexString() + " | "
                + "LL3P Packet. ";
    }

    @Override
    public String toProtocolExplanationString() {
        return "--------------" + Constants.newline
                + sourceAddress.explainSelf() + Constants.newline
                + destinationAddress.explainSelf() + Constants.newline
                + type.explainSelf() + Constants.newline
                + identifier.explainSelf() + Constants.newline
                + ttl.explainSelf() + Constants.newline
                + payload.explainSelf() + Constants.newline
                + checksum.explainSelf() + Constants.newline;
    }

    @Override
    public String toHexString() {
        return sourceAddress.toHexString() + destinationAddress.toHexString()
                + type.toHexString() + identifier.toHexString() + ttl.toHexString()
                + payload.toHexString() + checksum.toHexString();
    }

    @Override
    public String toString() {
        return sourceAddress.toString() + destinationAddress.toString()
                + type.toString() + identifier.toString() + ttl.toString()
                + payload.toString() + checksum.toString();
    }

    public Integer getSourceAddress(){ return sourceAddress.getAddress();}
    public Integer getDestinationAddress(){ return destinationAddress.getAddress();}
    public void decrementTTL() { ttl.decrementTTL();}
    public Integer getTTL(){ return ttl.getTtl();}
    public String getPayloadString(){return payload.toString();}
}
