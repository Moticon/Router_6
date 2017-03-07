package com.moticon.network.daemons;

import android.util.Log;

import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.ARPRecord;
import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.network.table.Table;
import com.moticon.network.table.TimedTable;
import com.moticon.support.BootLoader;
import com.moticon.support.Factory;
import com.moticon.support.LabException;
import com.moticon.support.ParentActivity;
import com.moticon.support.RunnableObserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * Created by moticon on 12/29/2016.
 */

public class ARPDaemon extends Observable implements RunnableObserver {
    private TimedTable arpTable;
    private static ARPDaemon ourInstance = new ARPDaemon();
    Factory factory;

    public ARPDaemon(){
        arpTable = new TimedTable();
    }

    public static ARPDaemon getInstance(){return ourInstance;}

    @Override
    public void run() {
        ParentActivity.getInstance().getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expireARPEntries();
            }
        });
    }

    private void expireARPEntries(){
        Log.i(Constants.logTag, "In ARPDaemon - Running expire Records method in ARP table -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        ArrayList<TableRecord> removedRecords = arpTable.expireRecords(Constants.ARP_TIMEOUT_VALUE);
        Log.i(Constants.logTag, "In ARPDaemon. Just received expired records. There were " + Integer.toString(removedRecords.size()) + " of them.");
        // if you removed anything you need to notify the observers.  This includes the com.moticon.UI for
        // the table display, which needs to update the objects.  and also the LRP Daemon which
        //  keeps the routing table, relying on the ARP table.
        if (removedRecords.size() > 0) { // notify the com.moticon.UI and others who might be interested!
            Log.i(Constants.logTag, " In ARPDaemon, notifying observers after removing records");
            setChanged();
            notifyObservers(removedRecords);
            Log.i(Constants.logTag, "IN ARPDaemon.  Finished NOtifying observers. See if this comes back again.... +++++++++++++++++++++++oooooooo ");
/*            synchronized (arpTable) {
                for (TableRecord record : removedRecords) {
                    LRPDaemon.getInstance().removeAdjacency(((ARPRecord) record).getLL3PAddress());
                }
            }*/
        }
        Log.i(Constants.logTag, "Completely finished with ARP Daemon runnable. &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
    }

    public Integer getLL2PAddress(Integer LL3PAddress) throws LabException {
            ARPRecord record = (ARPRecord) arpTable.getItem(LL3PAddress);
            return record.getLL2PAddress();
    }

    public void addARPEntry(Integer layer2Address, Integer layer3Address){
        try {
            arpTable.getItem(layer3Address); // if this succeeds with no exception the record is already in there. Just update it.
            arpTable.touch(layer3Address);
        } catch (LabException e) {
            //e.printStackTrace();
            Log.e(Constants.logTag, "No record in table so adding new record.");
            ARPRecord arpRecord = (ARPRecord) factory.getTableRecord(Constants.ARPTABLE_RECORD);
            arpRecord.setLL2PAddress(layer2Address);
            arpRecord.setLL3PAddress(layer3Address);
            arpTable.addItem(arpRecord);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class){
            factory = Factory.getInstance();
        }
    }

    public Table getARPTable() { return arpTable;}

    public ArrayList<Integer> getAttachedNodes(){
        ArrayList<Integer> nodeList = new ArrayList<>();

        List<TableRecord> arpList = arpTable.getTable();
        ARPRecord tmp = null;
        Iterator<TableRecord> iterator = arpList.iterator();
        while (iterator.hasNext()){
            tmp = (ARPRecord) iterator.next();
            nodeList.add(tmp.getLL3PAddress());
        }
        return nodeList;
    }

    public void sendARPRequest(Integer ll2pAddress){
        LL2PDaemon.getInstance().sendARPRequest(ll2pAddress);
    }

    public void processARPRequest(Integer ll2pAddress, Integer sourceLL3PAddress){
        addARPEntry(ll2pAddress, sourceLL3PAddress);
        LL2PDaemon.getInstance().sendARPReplay(ll2pAddress);
    }
}
