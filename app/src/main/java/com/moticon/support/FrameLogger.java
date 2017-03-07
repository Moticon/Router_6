package com.moticon.support;

import com.moticon.UI.UIManager;
import com.moticon.network.daemons.LL1Daemon;
import com.moticon.network.datagrams.LL2PFrame;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by pat.smith on 11/14/2016.
 */
public class FrameLogger extends Observable implements Observer {
    private ArrayList<LL2PFrame> frameList;
    private LL1Daemon ll1Daemon;

    private static FrameLogger ourInstance = new FrameLogger();

    public static FrameLogger getInstance() {
        return ourInstance;
    }

    private FrameLogger() {
        frameList = new ArrayList<LL2PFrame>();
    }

    /*
     * the update method is called when the router's Layer 1 daemon sends or receives a
     * frame.  The job here is to add the frame to the arrayList.
     *
     * Here we are expecting the object to be an LL2P Frame object.
     */
    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == LL1Daemon.class) {
            if (o.getClass().equals(LL2PFrame.class)) {
                frameList.add((LL2PFrame) o);
                setChanged();
                notifyObservers();
            }
        } else if (observable.getClass() == BootLoader.class) {
            ll1Daemon = LL1Daemon.getInstance();
            addObserver(UIManager.getInstance().getSnifferUI());
        }

    }

    public ArrayList<LL2PFrame> getFrameList(){
        return frameList;
    }

    // for testing only... don't normally expect to remove frames from the history list
    public void removeOneFrame(){
        if (frameList.size() > 0)
            frameList.remove(0);
        setChanged();
        notifyObservers();
    }
}
