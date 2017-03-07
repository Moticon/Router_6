package com.moticon.support;

import com.moticon.network.Constants;

import java.util.Calendar;

import static com.moticon.network.Constants.newline;

/**
 * Created by moticon on 11/13/2016.
 */

public class Utilities {

    static public String toIndexedHexDisplay(String inputHexString){
        String returnString = new String("");
        String asciiString = new String("");
        String hexByte = new String("");
        Integer byteValue;
        for (Integer i = 0; i < inputHexString.length(); i = i+2){
            if ((i % 16) == 0) {
                returnString = returnString + padHexString(Integer.toHexString(i), 2) + "   ";          // get the character at i and convert it to its ASCII Hex value.
                // start a new ascii string
                asciiString = "";
            }
            hexByte = ""+inputHexString.charAt(i) + inputHexString.charAt(i+1);
            returnString = returnString + hexByte + " ";
            byteValue = Integer.valueOf(hexByte, 16);
            if ((byteValue > 31) && (byteValue < 128)) // this is a printable character.
               asciiString = asciiString + integerToAsciiString(byteValue);
            else // this is unprintable.
                asciiString = asciiString + "."; // unprintable chars are '.'
            // every 8 characters go to a new line and put the offset value.
            if ((i % 16) == 14) {
                returnString = returnString + "    " + asciiString + newline;
            }
        }
        // need to pad the output bytes with spaces and put the remainding ascii characters on the display.
        if ((inputHexString.length() % 16) != 0){
            for (int i = (inputHexString.length() % 16); i < 16; i = i+2){
                returnString = returnString + "   "; // add 2 blank hex spaces plus an interbyte space.
            }
            returnString = returnString + "    " + asciiString + newline;
        }
        return returnString;
    }

    /**
     * Pad Hex String - User provides number of bytes, not length o fstring
     * @param inputString
     * @param byteCount - total bytes to display (3 bytes produces 6 chars)
     * @return
     */
    static public String padHexString(String inputString, int byteCount){
        int numberOfcharacters = byteCount * 2;
        String returnString = new String(inputString).toUpperCase();
        for (int i=inputString.length(); i < numberOfcharacters; i++){
            returnString = "0" + returnString;
        }
        return returnString;
    }

    static public String integerToAsciiString(Integer integer){
        String asciiString = new String("");
        while (integer > 0){
            // find the remainder after dividing by 256 (this is the rightmost byte.
            asciiString = Character.toString((char) (integer % 256)) + asciiString;
            integer = integer / 256;
        }
        return asciiString;
    }

    static public String asciiToHexChars(String asciiString){
        String hexChars = new String("");
        for (int i = 0; i < asciiString.length(); i++){
            hexChars = hexChars + Integer.toHexString(asciiString.charAt(i));
        }
        return hexChars;
    }

    /**
     * This static variable is the value for the number of seconds in the current time since some time back in the 70's.
     * It's used to calculate the number of seconds since the program began by the method which follows.
     */
    public static long baseDateSeconds = Calendar.getInstance().getTimeInMillis()/1000;

    /**
     * This method returns the number of seconds since the program began.
     * @return
     */
    public static int getTimeInSeconds(){
        return (int) (Calendar.getInstance().getTimeInMillis()/1000 - baseDateSeconds);
    }

    public static String LL3PToString(Integer ll3pAddress){
        return padHexString(Integer.toHexString(ll3pAddress/256), Constants.LL3P_NETWORK_PORTION_LENGTH) + "." +
                padHexString(Integer.toHexString(ll3pAddress % 256), Constants.LL3P_HOST_PORTION_LENGTH);
    }

    public static Integer getNetworkNumberFromLL3P(Integer ll3pAddress){
        return ll3pAddress/256;
    }

    public static Integer getHostNumberFromLL3P(Integer ll3pAddress){
        return ll3pAddress % 256;
    }
}
