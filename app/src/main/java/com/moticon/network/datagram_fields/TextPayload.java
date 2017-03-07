package com.moticon.network.datagram_fields;

import com.moticon.support.Utilities;

/**
 * Created by pat.smith on 11/11/2016.
 */

public class TextPayload implements DatagramHeaderField {
    private String payload;

    public TextPayload(String payload){ this.payload = payload;}

    @Override
    public String toString() {
        return payload;
    }

    @Override
    public String explainSelf() {
        return new String("Payload: >>" + payload + "<<");
    }

    @Override
    public String toHexString() {
        return Utilities.asciiToHexChars(payload);
    }

}
