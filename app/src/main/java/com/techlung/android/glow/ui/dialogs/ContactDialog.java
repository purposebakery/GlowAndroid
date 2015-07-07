package com.techlung.android.glow.ui.dialogs;

import com.techlung.android.glow.R;
import com.techlung.android.glow.model.Contact;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.utils.ToolBox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ContactDialog {
	public static void show(FragmentActivity activity) {

		FragmentManager fm = activity.getSupportFragmentManager();
		ContactDialogFragment dialog = new ContactDialogFragment();
		
		Contact contact = GlowData.getInstance().getContact();

		dialog.setPhone(contact.getPhone());
		dialog.setMail(contact.getEmail());
		dialog.setWww(contact.getWww());
	
		dialog.show(fm, ContactDialogFragment.TAG);
	}

	public static class ContactDialogFragment extends DialogFragment {

		public static final String TAG = ContactDialogFragment.class.getName();

		private String phone;
		private String mail;
		private String www;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.dialog_contact, container);

			TextView phoneView = (TextView) view.findViewById(R.id.contact_phone);
			phoneView.setText(phone);
			View phoneContainer = view.findViewById(R.id.contact_phone_container);
			phoneContainer.setOnClickListener(new PhoneButtonListener(getActivity(), this, phone));

			TextView mailView = (TextView) view.findViewById(R.id.contact_mail);
			mailView.setText(this.mail);
			View mailContainer = view.findViewById(R.id.contact_mail_container);
			mailContainer.setOnClickListener(new EmailButtonListener(getActivity(), this, mail));

			TextView wwwView = (TextView) view.findViewById(R.id.contact_www);
			wwwView.setText(this.www);
			View wwwContainer = view.findViewById(R.id.contact_www_container);
			wwwContainer.setOnClickListener(new WwwButtonListener(getActivity(), this, www));

			Button button = (Button) view.findViewById(R.id.button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ContactDialogFragment.this.dismiss();
				}
			});
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);

			return view;
		}

		@Override
		public void onResume() {
			super.onResume();

			int x = ToolBox.convertDpToPixel(300, getActivity());
			int y = ToolBox.convertDpToPixel(370, getActivity());

			Window window = getDialog().getWindow();
			window.setLayout(x, y);
			window.setGravity(Gravity.CENTER);
		}
		
		// LISTENER 

		public static class PhoneButtonListener implements OnClickListener {
			private String number;
			private Activity a;
			private DialogFragment dialog;

			PhoneButtonListener(Activity a, DialogFragment dialog, String number) {
				this.number = number;
				this.a = a;
				this.dialog = dialog;
			}

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:+" + this.number.trim()));
				a.startActivity(callIntent);

			}
		}

		public static class EmailButtonListener implements OnClickListener {
			private String mail;
			private Activity a;
			private DialogFragment dialog;

			EmailButtonListener(Activity a, DialogFragment dialog, String mail) {
				this.mail = mail;
				this.a = a;
				this.dialog = dialog;
			}

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", this.mail, null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, a.getResources().getString(R.string.contact_email_subject));
				a.startActivity(Intent.createChooser(emailIntent, a.getResources().getString(R.string.contact_email_chooserTitle)));
			}
		}

		public static class WwwButtonListener implements OnClickListener {
			private String url;
			private Activity a;
			private DialogFragment dialog;

			WwwButtonListener(Activity a, DialogFragment dialog, String url) {
				if (!url.startsWith("http")) {
					url = "http://" + url;
				}
				this.url = url;
				this.a = a;
				this.dialog = dialog;
			}

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				a.startActivity(i);
			}
		}
		
		// GETTER / SETTER

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getWww() {
			return www;
		}

		public void setWww(String www) {
			this.www = www;
		}

	}
}
