package com.moticon.network.datagrams;

/**
 * Created by pat.smith on 11/11/2016.
 */

public interface Datagram {
    /*
     *  These are the required to string methods
     */

    // toString returns a string of hex characters.
    String toString();

    // toSummaryString returns a summary of what's in the packet that can fit on one line.
    String toSummaryString();

    // toProtocolExplanationString returns a breakout string (possibly with newlines)
    //   that displays the fields and interprets them if possible.
    String toProtocolExplanationString();

    // Must also provide a hex version of self for the lowest sniffer window
    String toHexString();

    /*
    In 2018 Add teh following methods:
    1. String toAsciiString();  returns the ascii string for this datagram, simply built from field methods of the same name.
    2. toTransmissionString();  returns the string to be transmitted.

    Modify toString to return what you'd like to see from the debugger and by default.
     */
}
