package com.moticon.support;

import android.app.Activity;


import com.moticon.UI.UIManager;
import com.moticon.network.Constants;
import com.moticon.network.daemons.ARPDaemon;
import com.moticon.network.daemons.LL1Daemon;
import com.moticon.network.daemons.LL2PDaemon;
import com.moticon.network.daemons.LL3PDaemon;
import com.moticon.network.daemons.LRPDaemon;

import java.util.Observable;

/**
 * Created by pat.smith on 11/11/2016.
 *
 * This class is called one time to boot the router.
 *
 * It should be called by (and only by) the mainActivity's OnCreate() method.
 *
 * This class starts the daemons to make the router work.  Daemons are singleton classes so
 *   it simply needs to instantiate them
 */

public class BootLoader extends Observable {

    public BootLoader(Activity parentActivity) {
        bootRouter(parentActivity);
    }

    /**
     * This is the only method here.  It's purpose is to create the daemons and boot the router.
     *
     * All the classes initialized by this boot process must observe the bootloader. The
     * bootloader will notify them when it's done
     */
    private void bootRouter(Activity parentActivity){
        /*
         * Initialize any objects here that are not singletons.
         *
         * All singletons are already created.
         *
         * This guy adds those classes as observers.
         */
        ParentActivity.setActivity(parentActivity);

        addObserver(LL1Daemon.getInstance());
        addObserver(Constants.getInstance());
        addObserver(FrameLogger.getInstance());
        addObserver(UIManager.getInstance());
        addObserver(UIManager.getInstance().getSnifferUI()); // this is ugly! too much cohesion.
        addObserver(UIManager.getInstance().getTableUI());
        addObserver(LL2PDaemon.getInstance());
        addObserver(Scheduler.getInstance());
        addObserver(ARPDaemon.getInstance());
        addObserver(LRPDaemon.getInstance());
        addObserver(LL3PDaemon.getInstance());
        setChanged();
        notifyObservers();

        UIManager uiManager = UIManager.getInstance();
        uiManager.displayMessage("Router "+ Constants.routerName + " is up and running!");

        test();
    }

    private void test(){
        //LL1Daemon.getInstance().testList();
    }

}
