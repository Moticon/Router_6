package com.moticon.network.daemons;

import android.util.Log;

import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.RoutingRecord;
import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.network.datagram_fields.LL3PAddressField;
import com.moticon.network.datagram_fields.LRPRouteCountField;
import com.moticon.network.datagram_fields.LRPSequenceNumber;
import com.moticon.network.datagram_fields.NetworkDistancePair;
import com.moticon.network.datagrams.LRPDatagram;
import com.moticon.network.table.RoutingTable;
import com.moticon.network.table.Table;
import com.moticon.support.BootLoader;
import com.moticon.support.Factory;
import com.moticon.support.LabException;
import com.moticon.support.ParentActivity;
import com.moticon.support.Utilities;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by moticon on 1/1/2017.
 */

public class LRPDaemon implements Runnable, Observer {
    RoutingTable forwardingTable;
    RoutingTable routingTable;

    int sequenceNumber;

    ARPDaemon arpDaemon;

    private static LRPDaemon thisInstance = new LRPDaemon();

    LRPDaemon(){
        forwardingTable = new RoutingTable();
        routingTable = new RoutingTable();
        sequenceNumber = 0;
    }

    public static LRPDaemon getInstance(){ return thisInstance;}

    @Override
    public void run() {
        ParentActivity.getInstance().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(Constants.logTag, " - Running expire Records method in forwarding and routing table.");
                updateRoutes();
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class){
            arpDaemon = ARPDaemon.getInstance();
            arpDaemon.addObserver(this);
        } else if (observable.getClass() == ARPDaemon.class) { // the arp daemon just removed arp entries.
            ArrayList<TableRecord> removedRecords = (ArrayList<TableRecord>) o;
            // you now have a list of removed ARP Records.
            // iterate through it in a for loop. THe key for an ARP record is the LL3P address
            //    of that record. Hand it to the method for each table to remove all routes
            //    that come from this node.
            for (TableRecord tmp : removedRecords){
                forwardingTable.removeRoutesFrom(tmp.getKey());
                routingTable.removeRoutesFrom(tmp.getKey());
            }
        }
    }

    public void testUpdateRoutes(){updateRoutes();}

    /**
     * Remove Adjacency is passed an LL3P address. We have just lost this node as a neighbor
     * All routes from this node should be removed.
     * The adjacency we inserted should be removed.
     * @param ll3PAddress
     */
    public void removeAdjacency(Integer ll3PAddress){
        Log.i(Constants.logTag, "Looks like neighbor " + Utilities.getNetworkNumberFromLL3P(ll3PAddress) + "." +
                                        Utilities.getHostNumberFromLL3P(ll3PAddress) + " Just went down. Removing routes.");
        routingTable.removeRoutesFrom(ll3PAddress);
        /*
        When the routing record for an adjacent node is entered the next hop is the ll3p address
        of the adjacent router. The com.moticon.network is the com.moticon.network number of the next router.
        THUS: The key of the record to remove is the adjacent neighbor's com.moticon.network combined with
        the neighbor's ll3p address.
         */
        routingTable.removeItem(Utilities.getNetworkNumberFromLL3P(ll3PAddress), ll3PAddress);
        forwardingTable.removeRoutesFrom(ll3PAddress);
        forwardingTable.removeItem(Utilities.getNetworkNumberFromLL3P(ll3PAddress), ll3PAddress);
    }

    /**
     * THis is a very busy method.  It should probably be broken down into calls into other
     * sub methods.
     */
    private void updateRoutes(){
        //Log.i(Constants.logTag, "Checking routing table for expired routes");
        routingTable.expireRecords(Constants.ROUTER_TIMEOUT_VALUE);
        //Log.i(Constants.logTag, "Checking forwarding table for expired routes");
        forwardingTable.expireRecords(Constants.ROUTER_TIMEOUT_VALUE);

        /*
          add a route to yourself to the routing table.
         */
        //Log.i(Constants.logTag, "LRP DAEMON -- about to add the routing record for myself to the table. >>>>>");
        RoutingRecord mySelf = new RoutingRecord(Utilities.getNetworkNumberFromLL3P(Constants.MY_LL3P_ADDRESS),
                                Constants.DISTANCE_TO_MYSELF, Constants.MY_LL3P_ADDRESS);
        routingTable.addIfBetter(mySelf);
        //Log.i(Constants.logTag, "LRP DAEMON -- SUCCESSFULLY ADDED/UPDATED the routing record for myself to the table. >>>>>");
        /*
        get a list of attached nodes from the ARP table, which knows current LL3P attached networks.
         */
        ArrayList<Integer> attachedNodeList = arpDaemon.getAttachedNodes();
        //Log.i(Constants.logTag, "Attached Node LIst"+attachedNodeList.toString());
        //Log.i(Constants.logTag, "LRP DAEMON -- Created LIst of attached nodes. Seen just before this. <<<<<<<<<<<<<<<<>>>>>");
        /*
         walk through the list. for each attached node add a route to the routing table for that com.moticon.network
           distance 0.
         */
        for(Integer nextHopLL3P : attachedNodeList){

            //Log.i(Constants.logTag, "LRP DAEMON -- WORKING WITH LISt FROM ARP Table. Adding node . >>>>>" + Integer.toHexString(nextHopLL3P));
            routingTable.addIfBetter(new RoutingRecord(Utilities.getNetworkNumberFromLL3P(nextHopLL3P),
                                    Constants.DISTANCE_TO_NEIGHBOR, nextHopLL3P));
            //Log.i(Constants.logTag, "LRP DAEMON -- successfully added table. . >>>>>");
        }

        // Now get the best routes from teh routing table, tell the forwarding table
        // to add them.
        updateForwardingTableFromRoutingTable();
        /*
        now use the list of attached nodes to send a routing update to each attached node.

        Here's the process:
         - initialize the parts of an LRP that won't change for this round
            --  my host number, the sequence number.
            --  also get the factory reference
            -- initialize a couple of arrayLists that will be reused.
         */
        Factory factory = Factory.getInstance();
        ArrayList<TableRecord> bestRoutes = null; // used for storing best routes from the routing table.
        LL3PAddressField sourceLL3P = (LL3PAddressField)
                factory.getDatagramHeaderField(Constants.LL3P_SOURCE_ADDRESS_FIELD, Constants.MY_LL3P_ADDRESS_STRING);
        sequenceNumber = (sequenceNumber + 1) % 16; // should only be 0 - 15.

        // now make a sequence Field.
        LRPSequenceNumber sequenceField = (LRPSequenceNumber) factory.getDatagramHeaderField(Constants.LRP_SEQUENCE_FIELD,
                Integer.toHexString(sequenceNumber));
        Log.i(Constants.logTag, "LRP DAEMON -- about to make the LRP. >>>>>");
        // For every neighbor do this loop:
        //     get the routelist excluding that neighbor
        //  if there is at least 1 route to send...
        //     build an arrayList of those com.moticon.network distance pair information bits
        for(Integer neighborLL3P : attachedNodeList){
            //Log.i(Constants.logTag, "Inside Loop to send LRP To NEIGHBOR " + Integer.toHexString(neighborLL3P) + ". >>>>>");
            bestRoutes = routingTable.getRouteListExcluding(neighborLL3P);
            //Log.i(Constants.logTag, "LRP DAEMON -- There are "+Integer.valueOf(bestRoutes.size())+ " routes >>>>");
            if (bestRoutes.size() > 0){
                LRPRouteCountField routeCountField = (LRPRouteCountField) factory.getDatagramHeaderField(Constants.LRP_ROUTE_COUNT_FIELD,
                                                                                Integer.toHexString(bestRoutes.size()));
                ArrayList<NetworkDistancePair> routeUpdates = new ArrayList();
                for (TableRecord route : bestRoutes){
                    // create a networkDistancePair field object and add it to the array.
                    routeUpdates.add( (NetworkDistancePair) factory.getDatagramHeaderField(Constants.LRP_NETWORK_DISTANCE_PAIR_FIELD,
                            ((RoutingRecord) route).toHexString()));
                }
                //Log.i(Constants.logTag, " LRP DAEMON ---------Created com.moticon.network-distance array in LRPDaemon -- "+routeUpdates.toString());
                // create the LRP packet.
                LRPDatagram lrpPacket = new LRPDatagram(sourceLL3P, sequenceField, routeCountField, routeUpdates );
                //Log.i(Constants.logTag, "Created LRP Datagram for update. Transmission string:" + lrpPacket.toString());
                // try to send the packet. The ARPDaemon's getLL2P address method throws an exception if it can't find
                // MAC Address.
                //Log.i(Constants.logTag, "LRP DAEMON -- trying to send LRP >>>>>");
                try {
                    Integer neighborLl2pAddress = ARPDaemon.getInstance().getLL2PAddress(neighborLL3P);
                    //Log.i(Constants.logTag, "LRP DAEMON -- got LL2P. It's " + Integer.toHexString(neighborLl2pAddress) + ". >>>>>");
                    LL2PDaemon.getInstance().sendLRPUpdate(lrpPacket, neighborLl2pAddress);

                    //Log.i(Constants.logTag, "LRP DAEMON -- sent routing update successfully >>>>>");
                } catch (LabException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i(Constants.logTag, "LRP DAEMON --DONE ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^. >>>>>");
    }

    public Table getRoutingTable (){return routingTable;}
    public Table getForwardingTable() {return forwardingTable;}

    //todo - need to add method to process incoming lrp packet.
    public void processLRPPacket(LRPDatagram lrpPacket, Integer sourceLL2PAddress){
        Integer sourceLL3P = lrpPacket.getSourceLL3P();
        ARPDaemon.getInstance().addARPEntry(sourceLL2PAddress, sourceLL3P);
        ArrayList<NetworkDistancePair> routes = lrpPacket.getRouteInformationArray();
        //for (NetworkDistancePair route : routes){
        int i=0;
        while (i<routes.size()){
            NetworkDistancePair route = routes.get(i);
            //RoutingRecord thisRoute = new RoutingRecord(route.getNetwork(), route.getDistance()+1, sourceLL3P);
            routingTable.addRoute(new RoutingRecord(route.getNetwork(), route.getDistance()+1, sourceLL3P));
            i++;
        }
            // NOTE - Add one to the distance we were told since we are one hop farther away!

        updateForwardingTableFromRoutingTable();
    }

    /**
     * This method simply gets the best routes from the routing table and adds them to the
     * forwarding table. The method "addRoutes(...)" checks to see if the route replaces, upgrades, touches
     * an existing route - or is a new route that needs to be added.
     */
    private void updateForwardingTableFromRoutingTable(){
        // Now get the best routes from teh routing table, tell the forwarding table
        // to add them.
        //Log.i(Constants.logTag, "LRP DAEMON -- adding routes to forwarding from routing table . >>>>> -- # ROUTES is: " + Integer.toString(routingTable.getBestRoutes().size()));
        forwardingTable.addRoutes(routingTable.getBestRoutes());
        //Log.i(Constants.logTag, "LRP DAEMON -- successfully added routes to the forwarding table. . >>>>>");
    }
    // todo after you do this you're done with lrp!  Then time to write LL3P
    // ll3p shouldn't be too hard. receive, send, forward.

    public Integer getNextHopForNetwork(Integer ll3PAddress) throws LabException{
            return forwardingTable.getNextHop(ll3PAddress);
    }

}
