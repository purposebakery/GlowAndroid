package com.techlung.android.glow.model;

import java.io.File;
import java.util.ArrayList;

import com.techlung.android.glow.io.ParameterReader;
import com.techlung.android.glow.settings.Common;

public class GlowData {

	private ArrayList<Tract> pamphlets = new ArrayList<Tract>();
	private Contact contact = new Contact();

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
	
	public void loadContact(File f) {
		ParameterReader pr = new ParameterReader(f);

		contact.setEmail(pr.readParameterString(Common.CONTACT_EMAIL));
		contact.setWww(pr.readParameterString(Common.CONTACT_WWW));
		contact.setPhone(pr.readParameterString(Common.CONTACT_PHONE));
		contact.setShop(pr.readParameterString(Common.CONTACT_SHOP));
		contact.setAppUrl(pr.readParameterString(Common.CONTACT_APP_URL));
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
	
	
	
	
}
