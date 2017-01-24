package com.techlung.android.glow.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;

public class Mailer {
    public static void sendNewsletterRequest(Activity activity) {
        String message = activity.getResources().getString(R.string.newsletter_mail_hello) + "\n\n" + activity.getResources().getString(R.string.newsletter_mail_message) + "\n\n" + activity.getResources().getString(R.string.newsletter_mail_bye);

        StringBuffer buffer = new StringBuffer();
        buffer.append("mailto:");
        buffer.append(GlowData.getInstance().getContact().getEmail());
        buffer.append("?subject=");
        buffer.append(activity.getResources().getString(R.string.newsletter_mail_subject));
        buffer.append("&body=");
        buffer.append(message);
        String uriString = buffer.toString().replace(" ", "%20");

        activity.startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)), activity.getResources().getString(R.string.newsletter_mail_subject)));
    }

}
