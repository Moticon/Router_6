package com.moticon.network.table;

import com.moticon.network.RecordTypes.TableRecord;
import com.moticon.support.LabException;

import java.util.List;

/**
 * Created by pat.smith on 11/22/2016.
 */

public interface TableInterface {

    //List<TableRecord> getTableAsArrayList();

    List<TableRecord> getTable();

    TableRecord addItem(TableRecord record);

    TableRecord getItem(TableRecord record) throws LabException;

    TableRecord removeItem(Integer key);

    TableRecord getItem(Integer key) throws LabException;

    void clear();
}
