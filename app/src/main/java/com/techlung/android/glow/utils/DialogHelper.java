package com.techlung.android.glow.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.widget.TextView;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;

public class DialogHelper {
    private static ProgressDialog progressDialog;

    public static void showProgressDialog() {
        progressDialog = new ProgressDialog(GlowActivity.getInstance());
        progressDialog.setMessage(GlowActivity.getInstance().getString(R.string.alert_loading));
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

    public static void showInfoAlert(Context context) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(context);
        builder.setMessage(Html.fromHtml(GlowData.getInstance().getInfo()));
        builder.setTitle(R.string.info_title);
        builder.setPositiveButton(R.string.info_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showErrorAlert(Context context, final String exceptionLogs) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(context);
        builder.setMessage(R.string.exceptionlogs_message);
        builder.setTitle(R.string.exceptionlogs_title);
        builder.setPositiveButton(R.string.alert_Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Mailer.sendErrorLog(GlowActivity.getInstance(), exceptionLogs);
                GlowActivity.getInstance().recreate();
            }
        });
        builder.setNegativeButton(R.string.alert_No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                GlowActivity.getInstance().recreate();
            }
        });
        builder.show();
    }
}
