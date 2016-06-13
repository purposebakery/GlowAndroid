package com.techlung.android.glow.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;

public class ContactUtil {

    public static void doPhoneContact(Context context) {
        String phone = GlowData.getInstance().getContact().getPhone();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:+" + phone.trim()));
        context.startActivity(callIntent);
    }

    public static void doMailContact(Context context) {
        String mail = GlowData.getInstance().getContact().getEmail();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.contact_email_subject));
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.contact_email_chooserTitle)));
    }

    public static void doWWWContact(Context context) {
        String www = GlowData.getInstance().getContact().getWww();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(!www.startsWith("http") ? "http://" + www : www));
        context.startActivity(i);
    }
}
