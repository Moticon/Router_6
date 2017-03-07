package com.moticon.network.datagrams;

import com.moticon.network.Constants;
import com.moticon.network.datagram_fields.LL3PAddressField;
import com.moticon.support.Factory;

/**
 * Created by moticon on 12/30/2016.
 */

public class ARPDatagram implements Datagram {
    LL3PAddressField ll3pAddress;

    public ARPDatagram(String address){
        ll3pAddress = (LL3PAddressField) Factory.getInstance().getDatagramHeaderField(Constants.LL3P_SOURCE_ADDRESS_FIELD, address);
    }

    @Override
    public String toSummaryString() {
        return " | ARP contains LL3P Address " + ll3pAddress.toString();
    }

    @Override
    public String toProtocolExplanationString() {
        return "ARP Packet" + Constants.newline + ll3pAddress.explainSelf();
    }

    @Override
    public String toHexString() {
        return ll3pAddress.toHexString();
    }


    @Override
    public String toString(){
        return toHexString();
    }

    public LL3PAddressField getLl3pAddress() { return ll3pAddress;}
}
