package com.moticon.network.datagrams;

import com.moticon.network.Constants;
import com.moticon.network.datagram_fields.TextPayload;
import com.moticon.support.Factory;

/**
 * Created by pat.smith on 12/6/2016.
 */

public class TextDatagram implements Datagram {
    private TextPayload payload;

    public TextDatagram(String text){
        payload = (TextPayload) Factory.getInstance().getDatagramHeaderField(Constants.GENERIC_TEXT_PAYLOAD, text);
    }
    @Override
    public String toSummaryString() {
        return payload.toString();
    }

    @Override
    public String toProtocolExplanationString() {
        return payload.explainSelf();
    }

    @Override
    public String toHexString() {
        return payload.toHexString();
    }

    public String toString() { return payload.toString();}
}
