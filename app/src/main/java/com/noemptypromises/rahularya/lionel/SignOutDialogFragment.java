package com.noemptypromises.rahularya.lionel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SignOutDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("All user data will be deleted.")
                .setTitle("Sign out?")
                .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().getSharedPreferences("usercreds", 0).edit().clear().commit();
                        SharedPreferences check = getActivity().getSharedPreferences("active", 0);
                        SharedPreferences.Editor editor = check.edit();
                        editor.putBoolean("active", false);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), WelcomeScreen.class);
                        intent.putExtra("redirect", false);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}