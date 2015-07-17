package com.techlung.android.glow.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.widget.EditText;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(Html.fromHtml(GlowData.getInstance().getInfo()));
        builder.setTitle(R.string.info_title);
        builder.setPositiveButton(R.string.alert_thanks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showErrorAlert(Context context, final String exceptionLogs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    public static void showShareAppAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText editText = new EditText(context);
        editText.setText(context.getString(R.string.share_app_text) + "\n" +
                "\n" + GlowData.getInstance().getContact().getAppUrl() + "\n\n" + context.getString(R.string.share_regards));
        int paddingPx = ToolBox.convertDpToPixel(16, context);
        editText.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        builder.setView(editText);
        builder.setTitle(context.getString(R.string.share_app_dialog_title));
        builder.setPositiveButton(R.string.alert_share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendShareMessage(editText.getText().toString());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showShareTractAlert(final Context context, final Tract tract) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText editText = new EditText(context);
        editText.setText(context.getString(R.string.share_tract_text) + "\n\n" + tract.getUrl() + "\n\n" + context.getString(R.string.share_regards));
        int paddingPx = ToolBox.convertDpToPixel(16, context);
        editText.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        builder.setView(editText);
        builder.setTitle(context.getString(R.string.share_tract_dialog_title));
        builder.setPositiveButton(R.string.alert_share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendShareMessage(editText.getText().toString());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private static void sendShareMessage(String message) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        String chooserMessage = GlowActivity.getInstance().getResources().getString(R.string.alert_share);
        GlowActivity.getInstance().startActivity(Intent.createChooser(share, chooserMessage));
    }
}
