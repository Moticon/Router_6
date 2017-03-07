package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by moticon on 2/5/2017.
 */

public class LL3PChecksum implements DatagramHeaderField {
    private Integer checksum;

    public LL3PChecksum(String typeString){
        checksum = Integer.valueOf(typeString, 16);
    }

    @Override
    public String explainSelf() {
        return new String("LL3P checksum. Contents: "+ toHexString());
    }

    @Override
    public String toHexString() {
        return Utilities.padHexString(Integer.toHexString(checksum), Constants.LL3P_CHECKSUM_LENGTH);
    }

    @Override
    public String toString() {return toHexString();}
}
