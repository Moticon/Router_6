package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by moticon on 2/5/2017.
 */

public class LL3PIdentifierField implements DatagramHeaderField {
    private Integer identifier;

    public LL3PIdentifierField(String typeString){
        identifier = Integer.valueOf(typeString, 16);
    }

    @Override
    public String explainSelf() {
        return new String("LL3P Identifier. Contents: "+ toHexString());
    }

    @Override
    public String toHexString() {
        return Utilities.padHexString(Integer.toHexString(identifier), Constants.LL3P_IDENTIFIER_LENGTH);
    }

    @Override
    public String toString() {return toHexString();}
}
