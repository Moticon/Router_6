package com.moticon.network.datagrams;

import com.moticon.network.Constants;
import com.moticon.network.datagram_fields.LL3PAddressField;
import com.moticon.network.datagram_fields.LRPRouteCountField;
import com.moticon.network.datagram_fields.LRPSequenceNumber;
import com.moticon.network.datagram_fields.NetworkDistancePair;
import com.moticon.support.Factory;

import java.util.ArrayList;

/**
 * Created by pat.smith on 1/31/2017.
 */

public class LRPDatagram implements Datagram {
    private LL3PAddressField sourceLL3P;
    private LRPSequenceNumber sequenceNumber;
    private LRPRouteCountField routeCount;
    private ArrayList<NetworkDistancePair> routeInformationArray;

    public LRPDatagram(String packet){
        Factory factory = Factory.getInstance();
        sourceLL3P = (LL3PAddressField) factory.getDatagramHeaderField(Constants.LRP_SOURCE_LL3P_FIELD,
                packet.substring(Constants.LRP_SOURCENODE_OFFSET, Constants.LRP_SOURCENODE_LENGTH*2));
        /*
        The next two fields share a single byte. Thus the high order bits in the next byte (1 Hex character)
        are the sequence number and the second half of the byte holds the route count. This
        requires that they both have the same "offset in bytes". Of course in Hex characters this results in
        adding 1 to hte offset of the pair to reach the second, low order part of the nibble.
         */
        sequenceNumber = (LRPSequenceNumber) factory.getDatagramHeaderField(Constants.LRP_SEQUENCE_FIELD,
                packet.substring(Constants.LRP_SEQUENCE_AND_ROUTECOUNT_OFFSET*2,
                        Constants.LRP_SEQUENCE_AND_ROUTECOUNT_OFFSET*2 + Constants.LRP_SEQUENCE_AND_ROUTECOUNT_LENGTH));
        routeCount = (LRPRouteCountField) factory.getDatagramHeaderField(Constants.LRP_ROUTE_COUNT_FIELD,
                packet.substring(Constants.LRP_SEQUENCE_AND_ROUTECOUNT_OFFSET*2+1,
                        Constants.LRP_SEQUENCE_AND_ROUTECOUNT_OFFSET*2 +1 + Constants.LRP_SEQUENCE_AND_ROUTECOUNT_LENGTH));
        /*
        Here we have to build teh array of com.moticon.network distance pairs. There are *routeCount* of them. Calculate a new offset for
        each one, then pass that substring to the factory to get a field for it, finally typecast that as a networkDistancePair
        object and add it to the array of com.moticon.network distance pairs. WHEW!!
         */
        routeInformationArray = new ArrayList<>();
        for (int i=0; i<routeCount.getRouteCount(); i++){
            int offset = (Constants.LRP_NETWORK_DISTANCE_PAIR_OFFSET*2) + (2*i*Constants.LRP_NETWORK_DISTANCE_PAIR_LENGTH);
            int endOffset = offset + 2*Constants.LRP_NETWORK_DISTANCE_PAIR_LENGTH;
            routeInformationArray.add((NetworkDistancePair) factory.getDatagramHeaderField(Constants.LRP_NETWORK_DISTANCE_PAIR_FIELD,
                    packet.substring(offset, endOffset)));
        }
    }

    public LRPDatagram(LL3PAddressField sourceLL3P, LRPSequenceNumber sequenceNumber,
                       LRPRouteCountField routeCount, ArrayList<NetworkDistancePair> routeList){
        this.sourceLL3P = sourceLL3P;
        this.sequenceNumber = sequenceNumber;
        this.routeCount = routeCount;
        routeInformationArray = new ArrayList<>();
        this.routeInformationArray.addAll((routeList));// Crashed here.
    }


    @Override
    public String toSummaryString() {
        // Build a string here!
        return new String("LRP from "
                          + " " + sourceLL3P.toHexString()
                          + " with "
                          + Integer.toString(routeInformationArray.size()))
                          + " routes.";
    }

    @Override
    public String toProtocolExplanationString() {
        String explanationString = sourceLL3P.explainSelf()
                                   + Constants.newline + sequenceNumber.explainSelf()
                                   + Constants.newline + routeCount.explainSelf();
        for (NetworkDistancePair pair : routeInformationArray) {
            explanationString = explanationString + Constants.newline + pair.explainSelf();
        }
        return explanationString;
    }

    @Override
    public String toHexString() {
        String hexString = sourceLL3P.toHexString() + sequenceNumber.toHexString() +
                routeCount.toHexString();
        for (NetworkDistancePair pair : routeInformationArray){
            hexString = hexString + pair.toHexString();
        }
        return hexString;
    }

    @Override
    public String toString(){
        return toHexString();
    }

    public Integer getSourceLL3P() {
        return sourceLL3P.getAddress();
    }

    public Integer getSequenceNumber() {
        return sequenceNumber.getSequenceNumber();
    }

    public Integer getRouteCount() {
        return routeCount.getRouteCount();
    }

    public ArrayList<NetworkDistancePair> getRouteInformationArray() {
        return routeInformationArray;
    }
}
