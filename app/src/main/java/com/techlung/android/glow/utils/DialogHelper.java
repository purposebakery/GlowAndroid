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
        //TextView text = new TextView(context);
        //text.setText(Html.fromHtml(GlowData.getInstance().getInfo()));
        //builder.setView(text);
        builder.setTitle(R.string.info_title);
        builder.setPositiveButton(R.string.info_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
