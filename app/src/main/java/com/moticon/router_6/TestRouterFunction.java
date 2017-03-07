package com.moticon.router_6;

import com.moticon.network.daemons.LRPDaemon;

/**
 * Created by pat.smith on 1/31/2017.
 */
public class TestRouterFunction {
    private static TestRouterFunction ourInstance = new TestRouterFunction();

    public static TestRouterFunction getInstance() {
        return ourInstance;
    }

    private TestRouterFunction() {
    }

    public void runTest(){
        LRPDaemon.getInstance().testUpdateRoutes();

    }
}
