package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by pat.smith on 11/11/2016.
 */

public class LL2PAddressField implements DatagramHeaderField {
    private Integer address;
    private boolean isSourceAddress;
    private String explanation;


    public LL2PAddressField(Integer addressValue, boolean isSourceAddress){
        address = addressValue;
        this.isSourceAddress = isSourceAddress;
        setExplanation();;
    }

    public LL2PAddressField(String addressString, boolean isSourceAddress) {
        address = Integer.valueOf(addressString, 16);
        this.isSourceAddress = isSourceAddress;
        setExplanation();
    }

    private void setExplanation(){
        if (isSourceAddress)
            explanation = new String("Source LL2P: " + toHexString());
        else
            explanation = new String("Dest LL2P: " + toHexString());
    }

    public boolean isSourceAddress(){ return isSourceAddress;}

    @Override
    public String toString(){
        return Utilities.padHexString(Integer.toHexString(address), Constants.LL2P_ADDRESS_LENGTH);
    }

    @Override
    public String explainSelf() {
        return explanation;
    }

    @Override
    public String toHexString(){
        return Utilities.padHexString(Integer.toHexString(address), Constants.LL2P_ADDRESS_LENGTH);
    }

    public Integer getAddress(){ return this.address;}
}
