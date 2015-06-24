package com.techlung.android.glow.dialogs;

import com.techlung.android.glow.R;
import com.techlung.android.glow.model.Contact;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.utils.Mailer;
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

public class MoreDialog {
	public static void show(FragmentActivity activity) {

		FragmentManager fm = activity.getSupportFragmentManager();
		MoreDialogFragment dialog = new MoreDialogFragment();

		Contact contact = GlowData.getInstance().getContact();

		dialog.setShopUrl(contact.getShop());

		dialog.show(fm, MoreDialogFragment.TAG);
	}

	public static class MoreDialogFragment extends DialogFragment {

		public static final String TAG = MoreDialogFragment.class.getName();

		private String shopUrl;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.dialog_more, container);

			view.findViewById(R.id.more_shop_container).setOnClickListener(new ShopButtonListener(getActivity(), this, shopUrl));
			view.findViewById(R.id.more_newsletter_container).setOnClickListener(new NewsletterButtonListener(getActivity(), this));

			// OK
			Button button = (Button) view.findViewById(R.id.button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MoreDialogFragment.this.dismiss();
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
			int y = ToolBox.convertDpToPixel(300, getActivity());

			Window window = getDialog().getWindow();
			window.setLayout(x, y);
			window.setGravity(Gravity.CENTER);
		}

		// LISTENER
		public static class ShopButtonListener implements OnClickListener {
			private String url;
			private Activity a;
			private DialogFragment dialog;

			ShopButtonListener(Activity a, DialogFragment dialog, String url) {
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

		// LISTENER
		public static class NewsletterButtonListener implements OnClickListener {
			private Activity a;
			private DialogFragment dialog;

			NewsletterButtonListener(Activity a, DialogFragment dialog) {				
				this.a = a;
				this.dialog = dialog;
			}

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				Mailer.sendNewsletterRequest(a);
			}
		}

		public String getShopUrl() {
			return shopUrl;
		}

		public void setShopUrl(String shopUrl) {
			this.shopUrl = shopUrl;
		}
	}
}
