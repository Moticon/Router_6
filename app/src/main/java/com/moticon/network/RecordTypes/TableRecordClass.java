package com.moticon.network.RecordTypes;

import com.moticon.support.Utilities;

/**
 * Created by pat.smith on 11/22/2016.
 */

public class TableRecordClass implements TableRecord {
    private int lastTimeTouched;

    public TableRecordClass(){
        updateTime();
    }

    @Override
    public Integer getAgeInSeconds() {
        return null;
    }

    @Override
    public Integer getKey() {
        return null;
    }

    public void updateTime(){
        lastTimeTouched = Utilities.getTimeInSeconds();
    }

    public int getCurrentAgeInSeconds(){
        return Utilities.getTimeInSeconds() - lastTimeTouched;
    }

    @Override
    public int compareTo(TableRecord tableRecord) {
        return getKey().compareTo(tableRecord.getKey());
    }
}
