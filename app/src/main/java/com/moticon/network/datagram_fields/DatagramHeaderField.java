package com.moticon.network.datagram_fields;

/**
 * Created by pat.smith on 11/11/2016.
 */

public interface DatagramHeaderField {

    String toString(); // this returns a string value appropriate for the field.
    String explainSelf(); // this returns a string that explains what the data in the field means.
    String toHexString(); // this provides a hex version of self.  Used in the lowest sniffer window.
                            //  hex characters (numbers) stay hex. Text are returned as hex values.
    /*
    In 2018 add these two methods.
    1. String toAsciiString(); - returns an ascii string
    2. toTransmissionString(); - returns the string that will be transmitted.

    Change "toString()" to return whatever you want to see in debugging and by default when
        inspecting the field.
     */
}
