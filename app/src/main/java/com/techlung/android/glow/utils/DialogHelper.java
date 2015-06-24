package com.techlung.android.glow.utils;

import android.app.ProgressDialog;

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
}
