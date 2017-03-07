package com.moticon.support;

import com.moticon.UI.TableUI;
import com.moticon.UI.UIManager;
import com.moticon.network.Constants;
import com.moticon.network.daemons.ARPDaemon;
import com.moticon.network.daemons.LRPDaemon;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by moticon on 12/29/2016.
 */

public class Scheduler implements Observer {
    private ARPDaemon arpDaemon;
    private TableUI tableUI;
    private LRPDaemon lrpDaemon;
    private static Scheduler ourInstance = new Scheduler();

    private ScheduledThreadPoolExecutor threadManager;

    public Scheduler(){}

    public static Scheduler getInstance() {
        return ourInstance;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class){
            arpDaemon = ARPDaemon.getInstance();
            tableUI = UIManager.getInstance().getTableUI();
            lrpDaemon = LRPDaemon.getInstance();

            threadManager = new ScheduledThreadPoolExecutor(Constants.THREAD_COUNT);

            threadManager.scheduleAtFixedRate(arpDaemon,
                                                Constants.ROUTER_BOOT_TIME,
                                                Constants.ARP_CHECK_INTERVAL,
                                                TimeUnit.SECONDS);

            threadManager.scheduleAtFixedRate(tableUI,
                                                Constants.ROUTER_BOOT_TIME,
                                                Constants.UI_UPDATE_INTERVAL,
                                                TimeUnit.SECONDS);

            threadManager.scheduleAtFixedRate(lrpDaemon,
                                                Constants.ROUTER_BOOT_TIME,
                                                Constants.ROUTE_UPDATE_VALUE,
                                                TimeUnit.SECONDS);
        }
    }
}
