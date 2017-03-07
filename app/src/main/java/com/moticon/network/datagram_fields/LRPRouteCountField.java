package com.moticon.network.datagram_fields;

/**
 * Created by pat.smith on 1/31/2017.
 */

public class LRPRouteCountField implements DatagramHeaderField {
    private Integer routeCount;

    public LRPRouteCountField(String value){
        routeCount = Integer.valueOf(value, 16);
    }

    @Override
    public String explainSelf() {
        return "Route Count in this LRP UPdate: " + Integer.toString(routeCount) + " routes.";
    }

    @Override
    public String toHexString() {
        return Integer.toHexString(routeCount);
    }

    @Override
    public String toString(){
        return toHexString();
    }

    public Integer getRouteCount(){return routeCount;}
}
