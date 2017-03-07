package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by moticon on 2/5/2017.
 */

public class LL3PTTLField implements DatagramHeaderField {
    private Integer ttl;

    public LL3PTTLField(String typeString){
        ttl = Integer.valueOf(typeString, 16);
    }

    public void decrementTTL() {ttl = ttl - 1;}
    public Integer getTtl(){return ttl;}

    @Override
    public String explainSelf() {
        return new String("Time To Live: "+ Integer.toString(ttl) + " (Contents: "+ toHexString() + ")");
    }

    @Override
    public String toHexString() {
        return Utilities.padHexString(Integer.toHexString(ttl), Constants.LL3P_TTL_LENGTH);
    }

    @Override
    public String toString() {return toHexString();}
}
