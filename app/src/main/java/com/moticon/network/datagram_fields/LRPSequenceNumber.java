package com.moticon.network.datagram_fields;

/**
 * Created by pat.smith on 1/31/2017.
 */

public class LRPSequenceNumber implements DatagramHeaderField {
    Integer sequenceNumber;

    public LRPSequenceNumber(String value){
        sequenceNumber = Integer.valueOf(value, 16);
        if (sequenceNumber > 15)
            sequenceNumber = sequenceNumber % 16;
    }

    @Override
    public String explainSelf() {
        return "LRP Sequence Number 0x" + toHexString();
    }

    @Override
    public String toHexString() {
        return Integer.toHexString(sequenceNumber);
    }

    @Override
    public String toString() {
        return toHexString();
    }

    public Integer getSequenceNumber() {return sequenceNumber;}
}
