package com.moticon.support;

import android.os.AsyncTask;
import android.util.Log;

import com.moticon.network.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by pat.smith on 11/18/2016.
 */
public class GetIPAddress {
    InetAddress returnAddress;

    private static GetIPAddress ourInstance = new GetIPAddress();

    public static GetIPAddress getInstance() {
        return ourInstance;
    }

    private GetIPAddress() {
/*        try {
            returnAddress = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException e){
            e.printStackTrace();
        }*/
    }

    public InetAddress getInetAddress(String hostname){

        /*
        This took forever to figure out.

        The goal of this is to provide a generic method to get the IP address of a URL name.
        This will be used when asking the name server for an IP address to go with the LL2P
        address.

        You can't use the "getByName("www.oc.edu")" method on the com.moticon.UI thread. So this
        calls an AsynchTask private class to do that on a new thread.

        The problem is that the main method goes on before the answer is returned by the remote
        nameserver, and so the method returns a null pointer.

        In order to force it to wait I have to use the "get()" method of the AsyncTask.

        But there is one more thing that needed to be done.  If you use the get() method,
        it will return the object from the return of the doInBackground method.

        However, it WON'T do this if you have an onPostExecute() method in the class.

        So I remved the onPostExecute() from the private class and now get waits for the thread
        to finish.
         */
        // create the object to manage the thread.
        ThreadedNameServer thread = new ThreadedNameServer();
        // spin off the thread. Log a message to that effect.
        thread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, hostname);
        Log.d(Constants.logTag, " -- Started Thread. getting ready to wait for results.");
        // use the get method to wait on the thread before proceeding. Also, don't include
        // an "onPostExecute()" method in the private AsyncTask object.
        try {
            returnAddress = thread.get(2000, TimeUnit.MILLISECONDS);
            Log.d(Constants.logTag, " -- Results received.  return address = "+returnAddress.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return returnAddress;
    }

    private class ThreadedNameServer extends AsyncTask<String, Void, InetAddress> {

        @Override
        protected InetAddress doInBackground(String... name) {
            InetAddress address = null;
            try {

                address = InetAddress.getByName(name[0]);
            } catch (UnknownHostException e){
                e.printStackTrace();
            }
            return address;
        }

        /*
        @Override
        protected void onPostExecute(InetAddress inetAddress) {
            returnAddress = inetAddress;
            Log.d(Constants.logTag, "in post execute. .  return address = >> "+returnAddress.toString());
        }
*/

    }
}
