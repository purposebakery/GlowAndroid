package com.techlung.android.glow.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.techlung.android.glow.GlowActivity;

public class DialogHelper {
    private static ProgressDialog progressDialog;

    public static void showProgressDialog() {
        progressDialog = new ProgressDialog(GlowActivity.getInstance());
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    public static void showSimpleAlert(Context context, int idTitleText, int idMessageText, int idButtonText) {

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(idTitleText))
                .setMessage(context.getResources().getString(idMessageText))
                .setPositiveButton(context.getResources().getString(idButtonText), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                }).show();
    }
}
