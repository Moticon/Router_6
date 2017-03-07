package com.moticon.network.RecordTypes;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * Created by pat.smith on 11/22/2016.
 */

public class ARPRecord extends TableRecordClass {
    private Integer LL2PAddress;
    private Integer LL3PAddress;

    public ARPRecord(){
        super();
        LL2PAddress = new Integer(0);
        LL3PAddress = new Integer(0);
    }

    public ARPRecord(Integer ll2p, Integer ll3p){
        super();
        LL2PAddress = ll2p;
        LL3PAddress = ll3p;
    }

    @Override
    public Integer getKey(){
        return LL3PAddress;
    }

    public Integer getLL2PAddress() {
        return LL2PAddress;
    }

    public void setLL2PAddress(Integer LL2PAddress) {
        this.LL2PAddress = LL2PAddress;
    }

    public Integer getLL3PAddress() {
        return LL3PAddress;
    }

    public void setLL3PAddress(Integer LL3PAddress) {
        this.LL3PAddress = LL3PAddress;
    }

    public String toString(){
        return "LL2P: " + Utilities.padHexString(Integer.toHexString(LL2PAddress), Constants.LL2P_ADDRESS_LENGTH)
                + ";        LL3P: " + Utilities.padHexString(Integer.toHexString(LL3PAddress), Constants.LL3P_ADDRESS_LENGTH)
                + ";        AGE: " + Integer.toString(this.getCurrentAgeInSeconds()) + "s";
    }
}