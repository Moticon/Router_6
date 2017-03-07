package com.moticon.UI;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.moticon.router_6.R;

/**
 * Created by moticon on 12/27/2016.
 */

public class AddAdjacencyDialog extends DialogFragment {
    private EditText ipAddressEditText;
    private EditText ll2pAddressEditText;
    private Button addAdjacencyButton;
    private Button cancelAddAdjacencyButton;

    /*
     * this interface provides a connection to another class that implements it.
     * The other class will have a method that we can call and pass the two items we're
     * interested in using.
     *
     * I have placed this in teh main activity class, but I now wonder if I could write it to be
     * in some other class. Perhaps a dialogHander class.  Wherever we call this method, that
     * method has to be in the main activity so we return to the main com.moticon.UI Class.
     */
    public interface AdjacencyPairListener {
        void onFinishedEditDialog(String ipAddress, String ll2PAddress);
    }

    public AddAdjacencyDialog() {
        // empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_adjacency_layout, container, false);
        getDialog().setTitle("Add Adjacency");

        ipAddressEditText = (EditText) rootView.findViewById(R.id.ipAddressEditText);
        ll2pAddressEditText = (EditText) rootView.findViewById(R.id.ll2pAddressEditText);
        addAdjacencyButton = (Button) rootView.findViewById(R.id.addAdjacencyButton);
        cancelAddAdjacencyButton = (Button) rootView.findViewById(R.id.cancelAddAdjacencyButton);

        addAdjacencyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AdjacencyPairListener activity = (AdjacencyPairListener) getActivity();
                activity.onFinishedEditDialog(ipAddressEditText.getText().toString(),
                        ll2pAddressEditText.getText().toString());
                dismiss();
            }
        });

        cancelAddAdjacencyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return rootView;
        /* -- previous default return.
        return super.onCreateView(inflater, container, savedInstanceState);
         */
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
