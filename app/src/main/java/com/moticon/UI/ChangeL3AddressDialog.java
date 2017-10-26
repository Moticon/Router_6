package com.moticon.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.moticon.network.Constants;
import com.moticon.router_6.R;
import com.moticon.router_6.RouterActivity;

/**
 * Created by pat.smith on 3/7/2017.
 */

public class ChangeL3AddressDialog extends DialogFragment implements
        TextView.OnEditorActionListener {

    public interface EditNameDialogListener {
        void onFinishEditDialog(int valueToChange, String inputText);
    }

    private EditText mEditText;

    public ChangeL3AddressDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_textedit, container);
        mEditText = (EditText) view.findViewById(R.id.inputEditText);
        getDialog().setTitle(this.getTag());

        // Show soft keyboard automatically
        mEditText.requestFocus();
        mEditText.setOnEditorActionListener(this);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EditNameDialogListener activity = (EditNameDialogListener) ((RouterActivity) getActivity()).getConstants();
            if (this.getTag().equals("Change Layer 3")){
                activity.onFinishEditDialog(Constants.changeLL3PAddress, mEditText.getText().toString());
            } else if (this.getTag().equals("Change Layer 2")){
                activity.onFinishEditDialog(Constants.changeLL2PAddress, mEditText.getText().toString());
            }
            this.dismiss();
            return true;
        }
        return false;
    }
}
