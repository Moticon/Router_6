package com.moticon.UI;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.network.table.Table;
import com.moticon.network.table.TableInterface;
import com.moticon.router_6.R;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by pat.smith on 11/30/2016.
 */

public class SingleTableUI implements Observer {
    protected Activity parentActivity;
    // tableToDisplay is the table object that manages the stuff.  We need to observe and possibly observe the manater.
    protected Table tableToDisplay;
    protected List<TableRecord> tableList;
    protected ListView tableListView;
    private ArrayAdapter adapter;

    /**
     * Constructor for TableUI
     * @param parent  - The parent activity for context, etc
     * @param view  - The ListView object that will contain the table to display (R.id.listView)
     * @param table  - the table object that is to be displayed as a list.
     */
    public SingleTableUI(Activity parent, int view, TableInterface table){
        parentActivity = parent;
        tableToDisplay = (Table) table;
        tableList = table.getTable();
        adapter = new ArrayAdapter(parentActivity.getBaseContext(),
                R.layout.simple_table_row, table.getTable());
        tableListView = (ListView) parentActivity.findViewById(view);
        tableListView.setAdapter(adapter);
        //tableToDisplay.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass().equals(tableToDisplay.getClass())) {
            updateView();
        }
    }

    public void updateView(){
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
