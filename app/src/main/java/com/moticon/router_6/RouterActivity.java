package com.moticon.router_6;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.moticon.UI.AddAdjacencyDialog;
import com.moticon.UI.ChangeL3AddressDialog;
import com.moticon.UI.UIManager;
import com.moticon.network.Constants;
import com.moticon.network.daemons.LL1Daemon;
import com.moticon.support.BootLoader;

public class RouterActivity extends AppCompatActivity implements AddAdjacencyDialog.AdjacencyPairListener {
    UIManager uiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router);

        /*
         * call the bootloader to get things going.
         */
        BootLoader bootLoader = new BootLoader(this);
        uiManager = UIManager.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.showIPAddress){
            uiManager.displayMessage("Your IP address is "+ Constants.IP_ADDRESS);
        } else if (item.getItemId() == R.id.setAdjacency){
            DialogFragment dialog = new AddAdjacencyDialog();
            dialog.show(getFragmentManager(), "add_adjacency_dialog");
        } else if (item.getItemId() == R.id.runRouterTest){
            TestRouterFunction.getInstance().runTest();
        } else if (item.getItemId() == R.id.openMessenger){
            uiManager.openMessengerWindow();
        }else if (item.getItemId() == R.id.modifyLL2P){
            DialogFragment dialog = new ChangeL3AddressDialog();
            dialog.show(getFragmentManager(), "Change Layer 2");
        }else if (item.getItemId() == R.id.modifyLL3P){
            DialogFragment dialog = new ChangeL3AddressDialog();
            dialog.show(getFragmentManager(), "Change Layer 3");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFinishedEditDialog(String ipAddress, String ll2PAddress) {
        LL1Daemon.getInstance().addAdjacency(ll2PAddress, ipAddress);
    }

    public Constants getConstants(){return Constants.getInstance();}
}
