package com.moticon.support;

import android.app.Activity;

/**
 * Created by pat.smith on 11/11/2016.
 *
 * This is a derivation of the singleton class.  I can't create the activity because the Android OS has already done that.
 *
 * However I can have the activity instantiate this by passing itself to this class.
 *
 * From this point forward no one else can change the activity object. If they try to create a new
 * object it does nothing.
 */

public class ParentActivity {
    private static ParentActivity thisSingletonClass = new ParentActivity();
    private static Activity parentActivity;

    protected ParentActivity(){
    }

    /**
     * This returns the static activity object reference for the parent activity.
     * @return
     */
    public static ParentActivity getInstance(){
        return thisSingletonClass;
    }

    public static void setActivity(Activity myParent){parentActivity = myParent;}
    public Activity getActivity(){ return parentActivity;}
}
