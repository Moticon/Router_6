package com.moticon.support;

import android.util.Log;

import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.ARPRecord;
import com.moticon.network.RecordTypes.AdjacencyRecord;
import com.moticon.network.RecordTypes.RoutingRecord;
import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.network.datagram_fields.*;
import com.moticon.network.datagram_fields.DatagramHeaderField;
import com.moticon.network.datagrams.ARPDatagram;
import com.moticon.network.datagrams.Datagram;
import com.moticon.network.datagrams.LL3PDatagram;
import com.moticon.network.datagrams.LRPDatagram;
import com.moticon.network.datagrams.TextDatagram;

/**
 * Created by pat.smith on 11/11/2016. ---
 *
 * Factory patterns are classically created to allow objects to be created according through
 *    an interface that is created in the factory.  the interface can be programmed to create
 *    an super class object (and this also dynamically create other concrete subclasses).
 *
 *    In this program there really isn't a need for that. However I will still implement a factory
 *    class for the creation of packets, frames, etc.  Thus this should have a creation method
 *    that creates a datagram (datagram is a superclass / interface -- still determining this as
 *    I write this).  Thus other classes will be able to ask the factory for new datagram objects
 *    generically, but can also ask for specific objects to be created - determined at run time.
 *
 *
 */

public class Factory {
    private static Factory factory = new Factory();

    protected Factory() {}

    public static Factory getInstance() {
        return factory;
    }

    /**
     * GetDatagramHeaderField - This method returns a field object that can be used to build
     * a datagram header.  The method accepts an int for the field Type, and a string for the
     * contents of the field.
     * @param fieldType
     * @param fieldContents
     * @return
     */
    public DatagramHeaderField getDatagramHeaderField(int fieldType, String fieldContents){

        switch (fieldType){
            case Constants.LL2P_SOURCE_ADDRESS :
                return new LL2PAddressField(fieldContents, true);
            case Constants.LL2P_DESTINATION_ADDRESS:
                return new LL2PAddressField(fieldContents, false);
            case Constants.LL2P_TYPE:
                return new LL2PTypeField(fieldContents);
            case Constants.GENERIC_TEXT_PAYLOAD:
                return new TextPayload(fieldContents);
            case Constants.LL2P_CRC:
                return new CRC(fieldContents);
            case Constants.DATAGRAM_PAYLOAD:
                return new DatagramPayload(fieldContents);
            case Constants.LL3P_SOURCE_ADDRESS_FIELD :
                return new LL3PAddressField(fieldContents, true);
            case Constants.LL3P_DESTINATION_ADDRESS_FIELD :
                return new LL3PAddressField(fieldContents, false);
            case Constants.LL3P_TYPE_FIELD:
                return new LL3PTypeField(fieldContents);
            case Constants.LL3P_IDENTIFIER_FIELD:
                return new LL3PIdentifierField(fieldContents);
            case Constants.LL3P_TTL_FIELD:
                return new LL3PTTLField(fieldContents);
            case Constants.LL3P_CHECKSUM_FIELD:
                return new LL3PChecksum(fieldContents);
            case Constants.LRP_SOURCE_LL3P_FIELD :
                return new LL3PAddressField(fieldContents, true);
            case Constants.LRP_SEQUENCE_FIELD :
                return new LRPSequenceNumber(fieldContents);
            case Constants.LRP_NETWORK_DISTANCE_PAIR_FIELD :
                return new NetworkDistancePair(fieldContents);
            case Constants.LRP_ROUTE_COUNT_FIELD:
                return new LRPRouteCountField(fieldContents);
            default:
                Log.d(Constants.logTag, "Attempting to build field # "+ Integer.toString(fieldType)+" but no case has been written for it!!");
                return null;
        }
    }

    public TableRecord getTableRecord(int recordType){
        switch (recordType){
            case Constants.ADJACENCYTABLE_RECORD:
                return new AdjacencyRecord();
            case Constants.ARPTABLE_RECORD:
                return new ARPRecord();
            case Constants.ROUTINGTABLE_RECORD:
                return new RoutingRecord();
            default:
                return null;
        }
    }

    public Datagram getLayer2Payload(Integer type, String contents){
        switch (type){
            case Constants.LL2P_TYPE_IS_ARP_REQUEST:
                return new ARPDatagram(contents);
            case Constants.LL2P_TYPE_IS_ARP_REPLY:
                return new ARPDatagram(contents);
            case Constants.LL2P_TYPE_IS_ECHO_REQUEST:
                return new TextDatagram(contents);
            case Constants.LL2P_TYPE_IS_ECHO_REPLY:
                return new TextDatagram(contents);
            case Constants.LL2P_TYPE_IS_LRP:
                return new LRPDatagram(contents);
            case Constants.LL2P_TYPE_IS_LL3P:
                return new LL3PDatagram(contents);
            case Constants.LL2P_TYPE_IS_RESERVED:
            default:
                return new TextDatagram(contents);
        }
    }

    public DatagramHeaderField getLayer3PayloadField(Integer type, String contents){
        switch (type) {
            default:
                return new DatagramPayload(new TextDatagram(contents));
        }
    }

}
