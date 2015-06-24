package com.techlung.android.glow.logic;

public class Action {

	public static final int DOWNLOAD_SUCCESS = 10;
	public static final int DOWNLOAD_FAIL = 20;	

	public static final int LOAD_SUCCESS = 30;
	public static final int LOAD_FAIL = 40;	
	
	public static final int VERSIONS_SUCCESS = 50;
	public static final int VERSIONS_FAIL = 60;	
	
	private Object state;
	private int actionId;
	
	public Action(int actionId) {
		this.actionId = actionId;
	}

	public Object getState() {
		return state;
	}

	public void setState(Object state) {
		this.state = state;
	}

	public int getActionId() {
		return actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
	}
	
	
	
	
}
