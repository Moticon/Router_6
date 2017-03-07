package com.moticon.network.datagram_fields;

import com.moticon.network.Constants;
import com.moticon.support.Utilities;

/**
 * This will be used by Routing Record in storing routes, it will also be used by the routing
 * protocol when creating the com.moticon.network-distance pair list.
 * Created by pat.smith on 11/22/2016.
 */

public class NetworkDistancePair implements DatagramHeaderField {
    private Integer network;
    private Integer distance;

    public NetworkDistancePair(Integer network, Integer distance) {
        this.network = network;
        this.distance = distance;
    }

    public NetworkDistancePair(String contents){
        String networkString = contents.substring(
                Constants.NETWORK_DISTANCE_PAIR_NETWORK_OFFSET*2,
                Constants.NETWORK_DISTANCE_PAIR_FIELD_LENGTH*2);
        network = Integer.valueOf(networkString, 16);
        String distanceString = contents.substring(
                Constants.NETWORK_DISTANCE_PAIR_DISTANCE_OFFSET*2,
                Constants.NETWORK_DISTANCE_PAIR_DISTANCE_OFFSET*2+Constants.NETWORK_DISTANCE_PAIR_FIELD_LENGTH*2);
        distance = Integer.valueOf(distanceString, 16);

    }

    public Integer getNetwork() {
        return network;
    }

    public void setNetwork(Integer network) {
        this.network = network;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "NET: " + network + "; Dist: " + distance;
    }

    @Override
    public String explainSelf() {
        return "NetworkDistancePair{" +
                "com.moticon.network=" + network +
                ", distance=" + distance +
                '}';
    }

    @Override
    public String toHexString() {
        return Utilities.padHexString(Integer.toHexString(network), 1) +
                Utilities.padHexString(Integer.toHexString(distance), 1);
    }

}
