package com.techlung.android.glow.model;

public class Contact {

	private String email = "";
	private String phone = "";
	private String www = "";
	private String shop = "";
	
	public void clear() {
		email = "";
		phone = "";
		www = "";
		shop = "";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWww() {
		return www;
	}

	public void setWww(String www) {
		this.www = www;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}
	

	
	/*
	public void fillLayout(LinearLayout contact, Activity a) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 10, 10, 10);

		// Emails
		for (int i = 0; i < getEmail().size(); ++i) {
			LinearLayout emailRow = new LinearLayout(a);
			emailRow.setOrientation(LinearLayout.HORIZONTAL);
			emailRow.setLayoutParams(params);
			emailRow.setGravity(Gravity.CENTER);
			contact.addView(emailRow);

			ImageButton emailButton = new ImageButton(a);
			emailButton.setImageResource(android.R.drawable.ic_menu_send);
			emailRow.addView(emailButton);
			emailButton.setOnClickListener(new EmailButtonListener(getEmail().get(i), a));

			TextView emailLabelView = new TextView(a);
			emailLabelView.setText(R.string.pamphlet_email);
			emailLabelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			emailLabelView.setTextColor(Color.LTGRAY);
			emailRow.addView(emailLabelView);

			TextView emailView = new TextView(a);
			emailView.setText(Html.fromHtml(getEmail().get(i)));
			emailView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			emailView.setTextColor(Color.LTGRAY);
			emailRow.addView(emailView);

		}

		// Phone
		for (int i = 0; i < getPhone().size(); ++i) {
			LinearLayout phoneRow = new LinearLayout(a);
			phoneRow.setOrientation(LinearLayout.HORIZONTAL);
			phoneRow.setLayoutParams(params);
			phoneRow.setGravity(Gravity.CENTER);
			contact.addView(phoneRow);

			ImageButton phoneButton = new ImageButton(a);
			phoneButton.setImageResource(android.R.drawable.ic_menu_call);
			phoneRow.addView(phoneButton);
			phoneButton.setOnClickListener(new PhoneButtonListener(getPhone().get(i), a));

			TextView phoneLabelView = new TextView(a);
			phoneLabelView.setText(R.string.pamphlet_phone);
			phoneLabelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			phoneLabelView.setTypeface(Typeface.DEFAULT_BOLD);
			phoneLabelView.setTextColor(Color.GREEN);
			phoneRow.addView(phoneLabelView);

			TextView phoneView = new TextView(a);
			phoneView.setText(getPhone().get(i));
			phoneView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			phoneView.setTypeface(Typeface.DEFAULT_BOLD);
			phoneView.setTextColor(Color.GREEN);
			phoneRow.addView(phoneView);

		}

		// WWW
		for (int i = 0; i < getWww().size(); ++i) {
			LinearLayout wwwRow = new LinearLayout(a);
			wwwRow.setOrientation(LinearLayout.HORIZONTAL);
			wwwRow.setLayoutParams(params);
			wwwRow.setGravity(Gravity.CENTER);
			contact.addView(wwwRow);
			
			ImageButton wwwButton = new ImageButton(a);
			wwwButton.setImageResource(android.R.drawable.ic_menu_view);
			wwwRow.addView(wwwButton);
			wwwButton.setOnClickListener(new WwwButtonListener(getWww().get(i),a));

			TextView wwwLabelView = new TextView(a);
			wwwLabelView.setText(R.string.pamphlet_www);
			wwwLabelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			wwwLabelView.setTextColor(Color.BLUE);
			wwwRow.addView(wwwLabelView);

			TextView wwwView = new TextView(a);
			wwwView.setText(getWww().get(i));
			wwwView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			wwwView.setTextColor(Color.BLUE);
			wwwRow.addView(wwwView);
		}
	}*/
	
}
