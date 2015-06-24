package com.techlung.android.glow.utils;

import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

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
	    
	    /*
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		intent.putExtra(Intent.EXTRA_EMAIL, Content.getContact().getEmail());
		intent.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.newsletter_mail_subject));
		
		String message = activity.getResources().getString(R.string.newsletter_mail_hello) + "\n\n" + activity.getResources().getString(R.string.newsletter_mail_message) + "\n\n" + activity.getResources().getString(R.string.newsletter_mail_bye); 
		
		intent.putExtra(Intent.EXTRA_TEXT, message);
		Intent mailer = Intent.createChooser(intent, activity.getResources().getString(R.string.newsletter_mail_subject));
		activity.startActivity(mailer);*/
	}
}
