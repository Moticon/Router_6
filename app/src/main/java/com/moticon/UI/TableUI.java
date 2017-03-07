package com.moticon.UI;

import android.app.Activity;

import com.moticon.network.daemons.ARPDaemon;
import com.moticon.network.daemons.LL1Daemon;
import com.moticon.network.daemons.LRPDaemon;
import com.moticon.router_6.R;
import com.moticon.support.BootLoader;
import com.moticon.support.ParentActivity;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by pat.smith on 11/30/2016.
 */

public class TableUI implements Runnable, Observer {
    private SingleTableUI adjacencyUI;
    private SingleTableUI arpTableUI;
    private SingleTableUI routingTableUI;
    private SingleTableUI forwardingTableUI;


    /**
     * Empty Constructor - build and start everything after the bootloader finishes for us.
     */
    public TableUI(){}

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class) {
            Activity parentActivity = ParentActivity.getInstance().getActivity();

            // now that I know all the other objects.. I can connect to the screen widgets.
            connectWidgets();

            // create the sub com.moticon.UI managers for each table.
            adjacencyUI = new AdjacencyTableUI(parentActivity, R.id.adjacencyTableListView,
                    LL1Daemon.getInstance().getAdjacencyTable(), LL1Daemon.getInstance());
            arpTableUI = new SingleTableUI(parentActivity, R.id.arpTableListView, ARPDaemon.getInstance().getARPTable());
            routingTableUI = new SingleTableUI(parentActivity, R.id.routingTableListView, LRPDaemon.getInstance().getRoutingTable());
            forwardingTableUI = new SingleTableUI(parentActivity, R.id.forwardingTableListView, LRPDaemon.getInstance().getForwardingTable());
        }
    }

    private void connectWidgets(){

    }

    @Override
    public void run() {
        routingTableUI.updateView();
        forwardingTableUI.updateView();
        arpTableUI.updateView();
    }
}
