package com.techlung.android.glow.logic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class GlowAlertDialog {

	public static void show(Activity a, int idTitleText, int idMessageText, int idButtonText) {
		
		new AlertDialog.Builder(a)
		.setTitle(a.getResources().getString(idTitleText))
		.setMessage(a.getResources().getString(idMessageText))
		.setPositiveButton(a.getResources().getString(idButtonText), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// continue with delete
			}
		}).show();
	}
}
