package com.techlung.android.glow.model;

public class Contact {

	private String email = "";
	private String phone = "";
	private String www = "";
	private String shop = "";
	private String appUrl = "";
	
	public void clear() {
		email = "";
		phone = "";
		www = "";
		shop = "";
		appUrl = "";
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

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

}
