package com.moticon.network;

import com.moticon.UI.ChangeL3AddressDialog;
import com.moticon.UI.UIManager;
import com.moticon.support.BootLoader;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by pat.smith on 11/11/2016.
 */

public class Constants implements Observer, ChangeL3AddressDialog.EditNameDialogListener {
    static Constants constants = new Constants();

    // my routers' addresses
    public static Integer MY_LL2P_ADDRESS               = 0x314159;
    public static Integer MY_LL3P_ADDRESS               = 0x0F01;
    public static String MY_LL3P_ADDRESS_STRING         = Integer.toHexString(MY_LL3P_ADDRESS);
    public static int THREAD_COUNT                      = 4;

    public static Integer MAX_TTL = 15;
    public static String LL3P_IDENTIFIER = "FACE";

    // dialog specification
    public static final int changeLL3PAddress = 100;
    public static final int changeLL2PAddress = 101;

    // Global names and strings
    public static String routerName = new String("Magrathea");
    public static String logTag = new String("MAGRATHEA: ");

    // Field Constants - used for polymorphic field object creation
    final public static int LL2P_SOURCE_ADDRESS        = 21;
    final public static int LL2P_DESTINATION_ADDRESS   = 22;
    final public static int LL2P_TYPE                  = 23;
    final public static int LL2P_CRC                   = 24;

    final public static int LL3P_SOURCE_ADDRESS_FIELD  = 31;
    final public static int LL3P_DESTINATION_ADDRESS_FIELD = 32;
    final public static int LL3P_TYPE_FIELD            = 33;
    final public static int LL3P_IDENTIFIER_FIELD      = 34;
    final public static int LL3P_TTL_FIELD             = 35;
    final public static int LL3P_CHECKSUM_FIELD        = 36;

    final public static int LRP_SOURCE_LL3P_FIELD      = 41;
    final public static int LRP_SEQUENCE_FIELD         = 42;
    final public static int LRP_ROUTE_COUNT_FIELD      = 43;
    final public static int LRP_NETWORK_DISTANCE_PAIR_FIELD  = 44;


    final public static int GENERIC_TEXT_PAYLOAD       = 51;
    final public static int DATAGRAM_PAYLOAD           = 52;

    // Table Record Types
    final public static int ADJACENCYTABLE_RECORD      = 101;
    final public static int ARPTABLE_RECORD            = 102;
    final public static int ROUTINGTABLE_RECORD        = 103;

    final public static int LL2P_TYPE_IS_LL3P          = 0x8001;
    final public static int LL2P_TYPE_IS_RESERVED      = 0x8002;
    final public static int LL2P_TYPE_IS_LRP           = 0x8003;
    final public static int LL2P_TYPE_IS_ECHO_REQUEST  = 0x8004;
    final public static int LL2P_TYPE_IS_ECHO_REPLY    = 0x8005;
    final public static int LL2P_TYPE_IS_ARP_REQUEST   = 0x8006;
    final public static int LL2P_TYPE_IS_ARP_REPLY     = 0x8007;
    final public static int LL2P_TYPE_IS_TEXT          = 0x8008;

    // LL3P Types
    final public static int LL3P_TYPE_TEXT             = 0x8001;

    public static String newline = System.getProperty("line.separator");

    // LL2P Parsing constants
    public static int LL2P_ADDRESS_LENGTH = 3; // bytes.
    public static int LL2P_TYPE_LENGTH = 2;
    public static int LL2P_CRC_LENGTH = 2;
    public static int LL3P_NETWORK_PORTION_LENGTH = 1;
    public static int LL3P_HOST_PORTION_LENGTH = 1;

    // LRP Parsing Constants
    public static int LRP_SOURCENODE_OFFSET = 0;
    public static int LRP_SOURCENODE_LENGTH = 2;
    public static int LRP_SEQUENCE_AND_ROUTECOUNT_OFFSET = 2; // this is one byte with two 4 fit fields!
    public static int LRP_SEQUENCE_AND_ROUTECOUNT_LENGTH = 1; // Sequence number is high order bits, routecount low order bits
    public static int LRP_NETWORK_DISTANCE_PAIR_OFFSET = 3;
    public static int LRP_NETWORK_DISTANCE_PAIR_LENGTH = 2;

    // Parsing Network Distance Pair strings
    public static int NETWORK_DISTANCE_PAIR_NETWORK_OFFSET = 0;
    public static int NETWORK_DISTANCE_PAIR_DISTANCE_OFFSET = 1;
    public static int NETWORK_DISTANCE_PAIR_FIELD_LENGTH = 1;

    // Timing constants
    public static int ROUTER_BOOT_TIME = 10;
    public static int ROUTE_UPDATE_VALUE = 20; // should be 30 later TODO: change this to 30 seconds.
    public static int ARP_CHECK_INTERVAL = 20; // should be 30 or maybe 60 later
    public static int ARP_TIMEOUT_VALUE = ARP_CHECK_INTERVAL * 3;
    public static int ROUTER_TIMEOUT_VALUE = ROUTE_UPDATE_VALUE * 3;
    public static int UI_UPDATE_INTERVAL = 1;

    // LL2P Offsets -- in bytes
    final public static int LL2P_DESTINATION_OFFSET = 0;
    final public static int LL2P_SOURCEADDRESS_OFFSET = LL2P_DESTINATION_OFFSET + 2 * LL2P_ADDRESS_LENGTH;
    final public static int LL2P_TYPE_OFFSET = LL2P_SOURCEADDRESS_OFFSET + 2 * LL2P_ADDRESS_LENGTH;
    final public static int LL2P_PAYLOAD_OFFSET = LL2P_TYPE_OFFSET + 2 * LL2P_TYPE_LENGTH;

    // LL3P Offsets -- in bytes
    final public static int LL3P_SOURCE_OFFSET              = 0;
    final public static int LL3P_DESTINATION_ADDRESS_OFFSET = 2;
    final public static int LL3P_TYPE_OFFSET                = 4;
    final public static int LL3P_IDENTIFIER_OFFSET          = 6;
    final public static int LL3P_TTL_OFFSET                 = 8;
    final public static int LL3P_PAYLOAD_OFFSET             = 9;


    // LL3P Field Lengths
    final public static int LL3P_ADDRESS_LENGTH             = 2;
    final public static int LL3P_TYPE_LENGTH                = 2;
    final public static int LL3P_IDENTIFIER_LENGTH          = 2;
    final public static int LL3P_TTL_LENGTH                 = 1;
    final public static int LL3P_CHECKSUM_LENGTH            = 2;

    // Routing Constants
    final public static int DISTANCE_TO_MYSELF = 0;
    final public static int DISTANCE_TO_NEIGHBOR = 1;
    // Other Constants
    final public static int MAX_NETWORK_NUMBER = 15;
    final public static int MIN_NETWORK_NUMBER = 1;

    //IP and layer 1 constants
    final public static int UDP_PORT = 49999;
    final public static int UDP_MULTICAST_PORT = 49998;
    final public static String MULTICAST_IP_ADDRESS = new String("224.42.42.42");
    public static String IP_ADDRESS;		// the IP address of this system will be stored here in dotted decimal notation
    public static String IP_ADDRESS_PREFIX; // the prefix will be stored here
    /*
     * Constructor for Constants -- will eventually find out my IP address and do other nice
     * things that need to be set up in the constants file.
     */
    protected Constants (){
        IP_ADDRESS = getLocalIpAddress(); // call the local method to get the IP address of this device.
        // This next part is here to be used later if you're working on many devices. You can build an "if" statement to
        //   dynamically set your LL2P and LL3P addresses.
        //String androidId = "" + android.provider.Settings.Secure.getString(parentActivity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        int lastDot = IP_ADDRESS.lastIndexOf(".");
        int secondDot = IP_ADDRESS.substring(0, lastDot-1).lastIndexOf(".");
        IP_ADDRESS_PREFIX = IP_ADDRESS.substring(0, secondDot+1);
    }

    /**
     * getLocalIPAddress - this function goes through the com.moticon.network interfaces, looking for one that has a valid IP address.
     * Care must be taken to avoid a loopback address.
     * @return - a string containing the IP address in dotted decimal notation.
     */
    public String getLocalIpAddress() {
        String address= null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static public Constants getInstance(){
        return constants;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class){
            // called by the bootloader... wire this object to other objects it needs.
            // nothing right now.
        }
    }

    @Override
    public void onFinishEditDialog(int valueToChange, String inputText) {
        switch (valueToChange){
            case changeLL2PAddress:
                MY_LL2P_ADDRESS = Integer.valueOf(inputText, 16);
                break;
            case changeLL3PAddress:
                MY_LL3P_ADDRESS = Integer.valueOf(inputText, 16);
                MY_LL3P_ADDRESS_STRING = inputText;
            default:
        }
    }
}
