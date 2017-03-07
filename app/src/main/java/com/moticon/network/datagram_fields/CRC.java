package com.moticon.network.datagram_fields;

/**
 * Created by moticon on 11/13/2016.
 */

    // TODO: Implement an ErrorCheck interface or interface hierarchy
    // this would allow you to have a single "errorCheck" field in each packet.
    // the field would be handed a string of bytes that are used to create an error check code
    // the individual implementations would return "good or bad" and also break out code for sniffer
    // com.moticon.UI.

public class CRC implements DatagramHeaderField {
    String data;

    public CRC(String inputString){
        data = inputString;
    }

    @Override
    public String toString() {
        return data.substring(0,4);
    }

    @Override
    public String explainSelf() {
        return new String("CRC: " + data.substring(0,4));
    }

    /*
     * this will eventually convert the 2 byte value to a 4-nibble hex string.
     */
    @Override
    public String toHexString(){
        // later this will be a hex number. for now, return four hex chars.
        return data.substring(0,4);
    }

}
