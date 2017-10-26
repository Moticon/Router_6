package com.moticon.network.table;

import android.util.Log;

import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.RoutingRecord;
import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.support.LabException;
import com.moticon.support.Utilities;

import java.util.ArrayList;

/**
 * Created by moticon on 1/1/2017.
 */

public class RoutingTable extends TimedTable {
    /*
    The parent class already has:
      - table - it's an arraylist<TableRecord).
      - getTableAsArrayList()   - returns an arrayList for com.moticon.UI
      - addItem(TableRecord)    - adds record matching record passed in
      - getItem(TableRecord)    - returns record matching record passed in
      - getItem(Integer key)    - returns item matching Integer Key passed in
      - removeItem(Integer key) - removes item matching key.
      - clear()                 - clears the table of all records.
      - expireRecords(Integer maxAgeAllowed)
      - touch(TableRecord) - touch and update the time of the matching record.
      - touch(Integer key) - touch and update the time of the record matching the key.

     */

    public RoutingTable(){
        super();
    }

    public void addRoute(RoutingRecord newRoute){
        addItem(newRoute);
    }

    /**
     * Add an item. If the item already exists we simply touch it.
     *    if this is a better route than previously known from this source, remove old and add this
     *    if this route is worse, then we must still replace, this is because we've been
     *    given a new route distance from this source
     * @param newEntry
     * @return
     */
    @Override
    public TableRecord addItem(TableRecord newEntry){
        try {
            RoutingRecord entry = (RoutingRecord) getItem(newEntry.getKey());
            // if we're still here we found the record. Compare and either add or touch.
            // we know it's the com.moticon.network from this source.
            if (entry.getDistance().equals(((RoutingRecord) newEntry).getDistance()))
                touch(entry.getKey());
            else {
                // replace and remove.  You must do this no matter what the distance is.
                table.add(newEntry);
                removeItem(entry.getKey());
            }
        } catch (LabException e) {
            // if we landed here then we didn't find a record for this com.moticon.network from this source.
            table.add(newEntry);
        }
        updateDisplay();
        return newEntry;
    }


    private void updateDisplay(){
        setChanged();
        notifyObservers();
    }

    /**
     * given a com.moticon.network number and source com.moticon.network - remove that
     *    works simply by combining these into a key and then calling the regular remove item
     *    to do the work.
     * @param networkNumber
     * @param sourceLL3P
     */
    public void removeItem(Integer networkNumber, Integer sourceLL3P){
        removeItem(networkNumber*256*256 + sourceLL3P);
    }

    public void addIfBetter(TableRecord newEntry){
        try {
            RoutingRecord entry = (RoutingRecord) getItem(newEntry.getKey());
            // if we're still here we found the record. Compare and either add or touch.
            // we know it's the com.moticon.network from this source.
            if (entry.getDistance().equals(((RoutingRecord) newEntry).getDistance()))
                touch(entry.getKey());
            else if (((RoutingRecord) newEntry).getDistance() < entry.getDistance()){
                // replace and remove.  You must do this no matter what the distance is.
                addItem(newEntry);
                removeItem(entry.getKey());
            }
        } catch (LabException e) {
            // if we landed here then we didn't find a record for this com.moticon.network from this source.
            addItem(newEntry);
        }
        updateDisplay();
    }

    /**
     * This returns the ll3p address of the best route in teh table.
     *   it throws a lab exception if nothing was found - forcing the caller to deal with this problem
     * @param networkNumber
     * @return
     * @throws LabException
     */
    public Integer getNextHop(Integer networkNumber) throws LabException{
            return getBestRoute(networkNumber).getNextHop();
    }

    public ArrayList<TableRecord> getRouteListExcluding(Integer sourceLL3P){
        ArrayList<TableRecord> returnTable = new ArrayList<>();
        /*
           The table object is a synchronized object for threadsafe operations.
           This "synchronized()" method forces synchronization and should help prevent
           concurrent access errors.
         */
        synchronized (table) {
            for (TableRecord routeRecord : table){
                if (!(((RoutingRecord) routeRecord).getNextHop().equals(sourceLL3P)) &&
                    !((RoutingRecord) routeRecord).getNetworkNumber().equals(Utilities.getNetworkNumberFromLL3P(sourceLL3P)))   {
                    returnTable.add(routeRecord);
                }
            }
        }
        return returnTable;
    }

    /*
    this method looks strange but the reason for it as follows.
    You can't loop through a table, removing records as you find them.
    You must start over your loop when you remove one because you've modified
    the structure in a thread.  The way I get around this is to call a method that
    removes only one entry.  If it removes it then it returns true. IF true was
    returned then we call it again looking for more. Eventually they're all removed and
    the method called will return false.
     */
    public void removeRoutesFrom(Integer sourceLL3P){
        synchronized (table) {
            while ( removeOneRouteFromNeighbor(sourceLL3P))
                ;
        }
    }



    /*
     This method looks for one record in the table from the neighbor specified.
     If it finds one then it removes it and returns true.  The reason for this is
     that when you remove a record from a thread-accessed table you must start over.
     The calling method will keep calling this one until no record is removed.
     */
    private boolean removeOneRouteFromNeighbor(Integer neighborLL3P){
        synchronized (table) {
            for (TableRecord routeRecord : table){
                if (((RoutingRecord) routeRecord).getNextHop().equals(neighborLL3P))  {
                    removeItem(routeRecord.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This uses the getBestRoute method below. IT cycles through all networks and
     * gets the best route for each, building a list to return.
     * @return
     */
    public ArrayList<TableRecord> getBestRoutes(){
        ArrayList<TableRecord> returnList = new ArrayList<>();
        synchronized (table) {
            for (Integer networkNumber = Constants.MIN_NETWORK_NUMBER;
                 networkNumber <= Constants.MAX_NETWORK_NUMBER; networkNumber++) {
                try {
                    Log.i(Constants.logTag, "Getting best route for Network #"+networkNumber.toString());
                    RoutingRecord nextRecord = getBestRoute(networkNumber);
                    Log.i(Constants.logTag, "Route for Network #"+networkNumber.toString()+" is: "+nextRecord.toString());
                    // make a new copy of the routing for the forwarding table. This ensures each
                    // has a unique copy and they are not working by reference on the same record.
                    returnList.add(new RoutingRecord(nextRecord.getNetworkNumber(), nextRecord.getDistance(), nextRecord.getNextHop()));
                    // Use to do it this way. Seemed to be causing problems.
                    // returnList.add(nextRecord);
                } catch (LabException e) {
                    // do nothing. this just means there were no routes for that network.
                }
            }
            Log.i(Constants.logTag, " ----- Best Routes for all networks - LIST FOLLOWS++++++++++++++++++++++++++++++++++++++");
            for (TableRecord record : returnList){
                Log.i(Constants.logTag," -----------RECORD: "+record.toString());
            }
            return returnList;
        }
    }

    /**
     * Used to find the best record for a given com.moticon.network number. Note that this works
     *  well for finding a best route from the routing table, and also for finding a best
     *  route from the forwarding table for a forwarding decision.
     * @param networkNumber
     * @return
     */
    public RoutingRecord getBestRoute(Integer networkNumber) throws LabException{
        RoutingRecord returnRecord = null;
        boolean found = false;
        Integer bestDistance = 16; // max distance is 15, so this should be a good "max"
        synchronized (table) {
            for (TableRecord routeRecord : table){
                if ((((RoutingRecord) routeRecord).getNetworkNumber().equals(networkNumber)) &&
                    (((RoutingRecord) routeRecord).getDistance() < bestDistance)){
                       found = true;
                       returnRecord = (RoutingRecord) routeRecord;
                }
            }
        }
        if (!found){
            throw new LabException("No Route found for com.moticon.network "+ Integer.toHexString(networkNumber));
        }
        return returnRecord;
    }

    public void addRoutes(ArrayList<TableRecord> routesToAdd) {
        synchronized (table) {
            for(TableRecord record : routesToAdd){
                addIfBetter(record);
            }
        }
    }

}
