package com.moticon.UI;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.moticon.support.BootLoader;
import com.moticon.support.ParentActivity;

import java.util.Observable;
import java.util.Observer;
import com.moticon.support.BootLoader;

/**
 * Created by pat.smith on 11/11/2016.
 */

public class UIManager implements Observer {
    private static UIManager uiManager = null;
    private SnifferUI snifferUI;
    private TableUI tableUI;
    private Messenger messenger;

    private Activity myActivity;
    private Context context;

    protected UIManager(){
        // get the activity variable from the singleton Activity.

        snifferUI = new SnifferUI();
        tableUI = new TableUI();
        messenger = new Messenger();
    }

    public static UIManager getInstance(){
        if (uiManager == null)
            uiManager = new UIManager();
        return uiManager;
    }

    public void displayMessage(String message, int length){
        Toast msg = Toast.makeText(context, message, length);
        msg.show();
    }

    public void displayMessage(String message){
        displayMessage(message, Toast.LENGTH_LONG);
    }


    @Override
    public void update(Observable observable, Object o) {
        if (observable.getClass() == BootLoader.class) {
            myActivity = ParentActivity.getInstance().getActivity();
            context = myActivity.getBaseContext();
            messenger.finishCreatingMessenger();
            setupWidgets();
        }
    }

    private void setupWidgets(){

    }

    public SnifferUI getSnifferUI() { return snifferUI;}
    public TableUI getTableUI() { return tableUI; }
    public Messenger getMessenger() {
        return messenger;
    }

    public void openMessengerWindow(){
        messenger.openMessengerWindow();
    }
}
