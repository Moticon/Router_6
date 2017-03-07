package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by moticon on 2/5/2017.
 */

public class LL3PTypeField implements DatagramHeaderField {
    private Integer type;

    public LL3PTypeField(String typeString){
        type = Integer.valueOf(typeString, 16);
    }

    @Override
    public String explainSelf() {
        String returnString =  new String("LL3P Type Field. Contents: "+
                Utilities.padHexString(Integer.toHexString(type), Constants.LL3P_TYPE_LENGTH));
        switch (type) {
            case Constants.LL3P_TYPE_TEXT:
                returnString = returnString + " - Text Payload";
                break;
            default:
                returnString = returnString + "Type Unknown!";
                break;
        }
        return returnString;
    }

    @Override
    public String toHexString() {
        return Utilities.padHexString(Integer.toHexString(type), Constants.LL2P_TYPE_LENGTH);
    }

    @Override
    public String toString() {
        return toHexString();
    }
}
