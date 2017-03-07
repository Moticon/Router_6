package com.moticon.network.RecordTypes;

/**
 * Created by pat.smith on 11/21/2016.
 */

public interface TableRecord extends Comparable<TableRecord> {
    Integer getAgeInSeconds();

    Integer getKey();
}
