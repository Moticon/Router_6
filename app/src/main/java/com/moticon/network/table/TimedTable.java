package com.moticon.network.table;

import android.util.Log;

import com.moticon.network.Constants;
import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.network.RecordTypes.TableRecordClass;
import com.moticon.support.LabException;

import java.util.ArrayList;

/**
 * Created by moticon on 12/29/2016.
 */

public class TimedTable extends Table {
    /*
    The parent class already has:
     - table - it's an arraylist<TableRecord).
      - addItem(TableRecord)    - adds record matching record passed in
      - getItem(TableRecord)    - returns record matching record passed in
      - getItem(Integer key)    - returns item matching Integer Key passed in
      - removeItem(Integer key) - removes item matching key.
      - clear()                 - clears the table of all records.

      This class adds methods to deal with timer events. It can be used with a routing record
         or an arp record. methods added need to be:
         - expireRecords(Integer maxAgeAllowed)
         - touch(TableRecord) - touch and update the time of the matching record.
         - touch(Integer key) - touch and update the time of the record matching the key.
         -
     */

    public TimedTable(){
        super();
    }

    public ArrayList<TableRecord> expireRecords(Integer maxAgeAllowed){
        ArrayList<TableRecord> removedNodes = new ArrayList<>();
        /*
           The table object is a synchronized object for threadsafe operations.
           This "synchronized()" method forces synchronization and should help prevent
           concurrent access errors.

           There is a hitch that this approach attempts to avoid.  If you remove a record
           from the table then you can't continue to iterate through it.  So what we do
           here is to get one record from a different private method, which throws an exception
           if there are no records expired.
         */
        synchronized (table) {
            TableRecord nextExpiredRecord = null;
            try {
                nextExpiredRecord = findExpiredRecord(maxAgeAllowed);
            } catch (LabException e) {
                Log.i(Constants.logTag, "No records expired.");
                return removedNodes;
            }
            while (nextExpiredRecord!=null){
                table.remove(nextExpiredRecord);
                Log.i(Constants.logTag, "-------------Removing Record" + nextExpiredRecord.toString());
                removedNodes.add(nextExpiredRecord);
                try {
                    nextExpiredRecord = findExpiredRecord(maxAgeAllowed);
                } catch (LabException e) {
                    Log.i(Constants.logTag, "NO more expired records. ");
                    return removedNodes;
                }
            }
        }
        return removedNodes;
    }

    private TableRecord findExpiredRecord(Integer maxAgeAllowed) throws LabException {
        synchronized (table) {
            for (TableRecord record : table) {
                Log.i(Constants.logTag, "Checking record for expiration: " + ((TableRecordClass) record).toString());
                if (((TableRecordClass) record).getCurrentAgeInSeconds() > maxAgeAllowed) {
                    return record;
                }
            }
        }
        throw new LabException("No more expired records");
    }


    public void touch(TableRecord record){
        /*
           The table object is a synchronized object for threadsafe operations.
           This "synchronized()" method forces synchronization and should help prevent
           concurrent access errors.
         */
        synchronized (table) {
            try {
                TableRecordClass localRecord = (TableRecordClass) getItem(record.getKey());
                localRecord.updateTime();
            } catch (LabException e) {
                e.printStackTrace();
            }
        }
    }

    public void touch(Integer key){
        /*
           The table object is a synchronized object for threadsafe operations.
           This "synchronized()" method forces synchronization and should help prevent
           concurrent access errors.
         */
        synchronized (table) {
            try {
                TableRecordClass localRecord = (TableRecordClass) this.getItem(key);
                localRecord.updateTime();
            } catch (LabException e) {
                e.printStackTrace();
            }
        }
    }

}
