package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by moticon on 12/30/2016.
 */

public class LL3PAddressField implements DatagramHeaderField {
    private Integer address;
    private Integer networkNumber;
    private Integer hostNumber;
    private boolean isSourceAddress;
    private String explanation;

    public LL3PAddressField(Integer addressValue, boolean isSourceAddress){
        address = addressValue;
        this.isSourceAddress = isSourceAddress;
        setExplanation();
        saveHostAndNetworkNumbers();
    }

    public LL3PAddressField(String addressString, boolean isSourceAddress) {
        address = Integer.valueOf(addressString, 16);
        this.isSourceAddress = isSourceAddress;
        saveHostAndNetworkNumbers();
        setExplanation();
    }

    private void setExplanation(){
        if (isSourceAddress)
            explanation = new String("Source LL3P: ");
        else
            explanation = new String("Dest LL3P: ");
        explanation = explanation + "; Network: " + Utilities.padHexString(Integer.toHexString(networkNumber), Constants.LL3P_NETWORK_PORTION_LENGTH)
                                  + " Host: " + Utilities.padHexString(Integer.toHexString(hostNumber), Constants.LL3P_HOST_PORTION_LENGTH);
    }

    private void saveHostAndNetworkNumbers(){
        networkNumber = address / 256;
        hostNumber = address % 256;
    }

    @Override
    public String explainSelf() {
        return explanation;
    }

    @Override
    public String toHexString() {
        return Utilities.padHexString(Integer.toHexString(address), Constants.LL3P_ADDRESS_LENGTH);
    }

    @Override
    public String toString() {
        return toHexString();
    }

    public Integer getHostNumber() {
        return hostNumber;
    }

    public Integer getAddress() {
        return address;
    }

    public Integer getNetworkNumber() {
        return networkNumber;
    }
}
