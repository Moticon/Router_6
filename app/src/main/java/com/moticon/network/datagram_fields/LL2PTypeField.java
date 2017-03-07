package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by pat.smith on 11/11/2016.
 */

public class LL2PTypeField implements DatagramHeaderField {
    private Integer type;
    private String explanation;


    public LL2PTypeField(Integer typeValue){
        type = typeValue;
        setExplanation();
    }

    public LL2PTypeField(String typeValue) {
        type = Integer.valueOf(typeValue, 16);
        setExplanation();
    }

    private void setExplanation(){
        explanation = new String("Type: ");
        switch (type) {
            case Constants.LL2P_TYPE_IS_LL3P : explanation = explanation + "LL3P (";
                break;
            case Constants.LL2P_TYPE_IS_RESERVED : explanation = explanation + "Reserved (";
                break;
            case Constants.LL2P_TYPE_IS_LRP : explanation = explanation + "LRP (";
                break;
            case Constants.LL2P_TYPE_IS_ECHO_REQUEST : explanation = explanation + "ECHO REQUEST (";
                break;
            case Constants.LL2P_TYPE_IS_ECHO_REPLY : explanation = explanation + "LL2P ECHO REPLY (";
                break;
            case Constants.LL2P_TYPE_IS_ARP_REQUEST : explanation = explanation + "ARP_REQUEST (";
                break;
            case Constants.LL2P_TYPE_IS_ARP_REPLY : explanation = explanation + "ARP REPLY (";
                break;
            default: explanation = explanation + "UNKNOWN (";
                break;
        }
        explanation = explanation + Integer.toHexString(type)+")";
    }

    @Override
    public String toString(){
        return toHexString();
    }

    @Override
    public String explainSelf() {
        return explanation;
    }

    @Override
    public String toHexString(){
        return Utilities.padHexString(Integer.toHexString(type), Constants.LL2P_TYPE_LENGTH);
    }

    public Integer getType(){ return this.type;}
}
