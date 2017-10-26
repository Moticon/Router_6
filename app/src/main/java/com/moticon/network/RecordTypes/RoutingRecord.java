package com.moticon.network.RecordTypes;

import com.moticon.network.datagram_fields.NetworkDistancePair;
import com.moticon.support.Utilities;

/**
 * Created by pat.smith on 11/22/2016.
 */

public class RoutingRecord extends TableRecordClass {
    private NetworkDistancePair networkDistancePair;
    private Integer nextHop; // this is an LL3P Address

    // These table records require a key to be selected to allow unique identification of records
    // within tables.   THe key for a routing table is the combination of the next hop
    // and the com.moticon.network number of the destination com.moticon.network. Note that this is because you'll
    // never learn about the same com.moticon.network from a source (next hop) in two different ways.
    private Integer key;

    public RoutingRecord(){
        super();
        networkDistancePair = new NetworkDistancePair(0,0);
        nextHop = 0;
    }

    public RoutingRecord(Integer networkNumber, Integer distance, Integer nextHop){
        super();
        networkDistancePair = new NetworkDistancePair(networkNumber, distance);
        this.nextHop = nextHop;
        /*
        Key calculation - max distance is 15. Max LL3P is 255 (though the standard allows 255).
        TO calculate the key multiple the networkNumber*256 and add the ll3p.
        This essentially creates number with the nextHop (source ll3p) in the 16's and 1's place
        and the distance in the 256's place.
         */
        key = networkNumber*256*256 + nextHop;
    }

    public Integer getNetworkNumber() {
        return networkDistancePair.getNetwork();
    }

    public void setNetworkNumber(Integer networkNumber) {
        networkDistancePair.setNetwork(networkNumber);
    }

    public Integer getDistance() {
        return networkDistancePair.getDistance();
    }

    public void setDistance(Integer distance) {
        networkDistancePair.setDistance(distance);
    }

    public Integer getNextHop() {
        return nextHop;
    }

    public void setNextHop(Integer nextHop) {
        this.nextHop = nextHop;
    }

    public String toHexString(){
        return networkDistancePair.toHexString();
    }

    public String toString(){
/*        return "Key: " + Integer.toHexString(key)
                + " (" + Integer.toHexString((key/256)/256) + "." +  Integer.toHexString(key%(256*256)) + "); "
                + networkDistancePair.toString() + "; Next Hop "
                + Utilities.LL3PToString(nextHop) + "; Age: "
                + Integer.toString(getCurrentAgeInSeconds()) + "s";*/
        return networkDistancePair.toString() + "; Next Hop "
                + Utilities.LL3PToString(nextHop) + "; Age: "
                + Integer.toString(getCurrentAgeInSeconds()) + "s";
    }

    @Override
    public Integer getKey(){ return key; }
}