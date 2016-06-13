package com.techlung.android.glow.model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.utils.IOUtils;
import com.techlung.android.glow.io.ParameterReader;
import com.techlung.android.glow.Common;

public class GlowData {

	private ArrayList<Tract> pamphlets = new ArrayList<Tract>();
	private Contact contact = new Contact();
	private String info = "";

	private static GlowData instance;
	public static GlowData getInstance() {
		if (instance == null) {
			instance = new GlowData();
		}
		return instance;
	}
	private GlowData() {

	}

	public void clear() {
		pamphlets.clear();
		contact.clear();
	}
	
	public void addPamphlet(Tract p) {
		if (p == null || p.getId() == null || p.getId().equals("")) {
			throw new IllegalStateException("Empty Pamphlet");
		}
		pamphlets.add(p);
	}
	
	public void loadContact(InputStream is) {
		ParameterReader pr = new ParameterReader(is);

		contact.setEmail(pr.readParameterString(Common.CONTACT_EMAIL));
		contact.setWww(pr.readParameterString(Common.CONTACT_WWW));
		contact.setPhone(pr.readParameterString(Common.CONTACT_PHONE));
		contact.setShop(pr.readParameterString(Common.CONTACT_SHOP));
		contact.setAppUrl(pr.readParameterString(Common.CONTACT_APP_URL));
	}

	public void loadInfo(InputStream is) {
		try {
			info = IOUtils.readStream(is);

			PackageInfo pInfo = GlowActivity.getInstance().getPackageManager().getPackageInfo(GlowActivity.getInstance().getPackageName(), 0);
			String version = pInfo.versionName;

			info = info.replace("#TAG#", version);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}


	// Getter / Setter
	public ArrayList<Tract> getPamphlets() {
		return pamphlets;
	}
	public void setPamphlets(ArrayList<Tract> pamphlets) {
		this.pamphlets = pamphlets;
	}
	
	public Tract getTract(String id) {
		for (Tract t : getPamphlets()) {
			if (t.getId().equals(id)) {
				return t;
			}
		}
		return null;
	}

	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}


	
}
