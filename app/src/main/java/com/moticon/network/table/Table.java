package com.moticon.network.table;

import android.util.Log;

import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.support.LabException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

/**
 * Created by pat.smith on 11/30/2016.
 */
public class Table extends Observable implements TableInterface {
    protected List<TableRecord> table;

    public Table(){
        /**
         * This creates a synchronized Arraylist object, which should be thread safe.
         *  -- Note that before you access this you should place acces in a synchronized block.
         */
        table = Collections.synchronizedList(new ArrayList<TableRecord>());
        //synchronizedList(ArrayList<>());
    }

    public List<TableRecord> getTable(){
        return table;
    }

    @Override
    public TableRecord addItem(TableRecord newEntry) {
        /*
           The table object is a synchronized object for threadsafe operations.
           This "synchronized()" method forces synchronization and should help prevent
           concurrent access errors.
         */
        synchronized (table) {
            try {
                table.add(newEntry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
            return newEntry;
        }
    }

    @Override
    public TableRecord getItem(TableRecord record) throws LabException {
        return null;
    }

    @Override
    public TableRecord removeItem(Integer key) {
        TableRecord tmp= null;
        /*
           The table object is a synchronized object for threadsafe operations.
           This "synchronized()" method forces synchronization and should help prevent
           concurrent access errors.
         */
        synchronized (table) {
            try {
                tmp = getItem(key);
                table.remove(tmp);
            } catch (LabException e) {
                //e.printStackTrace();
                Log.e(Constants.logTag, "No Record exists with Key "+ Integer.toHexString(key) + ".  Nothing Deleted.");
            }
            updateDisplay();
            return tmp;
        }
    }

    @Override
    public TableRecord getItem(Integer key) throws LabException {
        /*
           The table object is a synchronized object for threadsafe operations.
           This "synchronized()" method forces synchronization and should help prevent
           concurrent access errors.
         */
        synchronized (table) {
            for (TableRecord record : table) {
                Log.i(Constants.logTag, "Fetching Record, Key Sought = " + Integer.toHexString(key) + ":    " +
                        ". Examining record " + record.toString());
                if (record.getKey().equals(key)) {
                    Log.i(Constants.logTag, "FOUND RECORD WITH KEY " + Integer.toHexString(key) );
                    return record;
                }
            }
            throw new LabException("No Record found in table for key: " + Integer.toHexString(key));
        }
    }

    private void updateDisplay(){
        setChanged();
        notifyObservers();
    }

    @Override
    public void clear() {
        table.clear();
        setChanged();
        notifyObservers();
    }

}
