package com.moticon.UI;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.AdjacencyRecord;
import com.moticon.network.daemons.LL1Daemon;
import com.moticon.network.datagrams.LL2PFrame;
import com.moticon.network.table.TableInterface;

import java.util.Observable;

/**
 * Created by pat.smith on 11/30/2016.
 *
 * This table extends the base table by adding a button and button features.
 *
 * This feels like it has too many connections with other classes.
 *
 * Right now it knows about the table, the Ll1Daemon, and the LL2Daemon.
 */

public class AdjacencyTableUI extends SingleTableUI {
    LL1Daemon ll1Daemon;

    public AdjacencyTableUI(Activity parent, int view, TableInterface table, Observable tableManager) {
        super(parent, view, table);
        ll1Daemon = (LL1Daemon)tableManager;
        tableListView.setOnItemClickListener(sendEchoRequest);
        tableListView.setOnItemLongClickListener(removeAdjacency);
    }

    /**
     * This method will need to eventually ask the LL2P Daemon to send an echo request to
     * the LL2P address in the record that was clicked on the screen.
     */
    private AdapterView.OnItemClickListener sendEchoRequest = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            AdjacencyRecord touchedRecord = (AdjacencyRecord) tableList.get(i);
            // this is temporary until LL2P Daemon is written.
            LL2PFrame frameToSend = new LL2PFrame(Integer.toHexString(touchedRecord.getLL2PAddress()),
                                                    Integer.toHexString(Constants.MY_LL2P_ADDRESS),
                                                    Integer.toHexString(Constants.LL2P_TYPE_IS_ECHO_REQUEST),
                                                    "Dummy Frame!!");
            LL1Daemon.getInstance().sendFrame(frameToSend);
            UIManager.getInstance().displayMessage("Touched " + touchedRecord.toString());
        }
    };

    /**
     * this removes the record.  I have a question about whether this should go directly to the
     * table and remove the record. For now I have it going directly to the table. No need
     * to involve the LL1 Daemon on this.
     */
    private AdapterView.OnItemLongClickListener removeAdjacency = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            AdjacencyRecord touchedRecord = (AdjacencyRecord) tableList.get(i);
            tableToDisplay.removeItem(touchedRecord.getKey());
            return false;
        }
    };
}
