package com.techlung.android.glow.notification;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.settings.Preferences;


public class NotificationReceiver extends BroadcastReceiver {

    public static final String DEACTIVATE_NOTIFICATION = "DEACTIVATE_NOTIFICATION";
    public static final String OPEN_APP = "OPEN_APP";
    public static final String SHOW_NOTIFICATION = "SHOW_NOTIFICATION";
    public static final int NOTIFICATION_ID = 1000;

    public NotificationReceiver() {

    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Preferences.initPreferences(context);

        if (intent.getAction() != null && intent.getAction().equals(DEACTIVATE_NOTIFICATION)) {
            // Deactivate Notifications
            Preferences.setNotificationEnabled(false);
            android.app.NotificationManager mNotificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(NOTIFICATION_ID);
            Toast.makeText(context, R.string.notification_deactivate_toast, Toast.LENGTH_LONG).show();
        } else if (intent.getAction() != null && intent.getAction().equals(OPEN_APP)) {
            // Open App
            android.app.NotificationManager mNotificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(NOTIFICATION_ID);
            context.startActivity(new Intent(context, GlowActivity.class).putExtra(GlowActivity.FROM_NOTIFICATION, true));
        } else {
            // Show Notification
            if (Preferences.isNotificationEnabled()) {
                showNotification(context);
                NotificationManager.setNextNotification(context, false);
            }
        }
    }

    private void showNotification(Context context) {
        Intent deactivateIntent = new Intent(context, NotificationReceiver.class);
        deactivateIntent.setAction(DEACTIVATE_NOTIFICATION);
        PendingIntent deactivatePendingIntent = PendingIntent.getBroadcast(context, 0, deactivateIntent, 0);

        Intent openAppIntent = new Intent(context, NotificationReceiver.class);
        openAppIntent.setAction(OPEN_APP);
        PendingIntent openAppPendingIntent = PendingIntent.getBroadcast(context, 0, openAppIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_logo_notification)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(context.getString(R.string.notification_message))
                        .addAction(R.drawable.ic_block, context.getString(R.string.notification_deactivate), deactivatePendingIntent)
                        .addAction(R.drawable.ic_launch, context.getString(R.string.notification_open_app), openAppPendingIntent);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, GlowActivity.class);
        resultIntent.putExtra(GlowActivity.FROM_NOTIFICATION, true);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of

// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(GlowActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.


        mNotificationManager.notify(NOTIFICATION_ID,  mBuilder.build());
    }

}
