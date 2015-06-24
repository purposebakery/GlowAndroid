package com.techlung.android.glow;

import com.techlung.android.glow.io.ContentStorageLoader;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.adapter.TractPairArrayAdapter;
import com.techlung.android.glow.ui.SelectionFlowFragment;
import com.techlung.android.glow.ui.SelectionFlowItem;
import com.techlung.android.glow.ui.SelectionListFragment;
import com.techlung.android.glow.ui.TractFragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class GlowActivity extends FragmentActivity implements
		TractPairArrayAdapter.Callback {
	private Settings settings;

	//private SelectionListFragment selectionFragment;
	private SelectionFlowFragment selectionFlowFragment;
    private TractFragment tractFragment;

	public enum State {
		SELECTION, TRACT
	}

	public State currentState;

	private static GlowActivity instance;

	public static GlowActivity getInstance() {
		return instance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;

		// Init Settings
		settings = Settings.getInstance(this);
		settings.load();

		setContentView(R.layout.glow_main);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		checkFirstStart();

		loadContent();

		showSelectionPhone();
	}

	protected void onPause() {
		super.onPause();
		settings.save();
	}

	private void initTractFragment() {
		FragmentManager fm = getSupportFragmentManager();
		tractFragment = (TractFragment) fm.findFragmentByTag(TractFragment.TAG);
		if (tractFragment == null) {
			tractFragment = new TractFragment();
		}
	}

    /*
	private void initSelectionFragment() {
		FragmentManager fm = getSupportFragmentManager();
		selectionFragment = (SelectionListFragment) fm.findFragmentByTag(SelectionListFragment.TAG);
		if (selectionFragment == null) {
			selectionFragment = new SelectionListFragment();
		}
	}*/

    private void initSelectionFlowFragment() {
        FragmentManager fm = getSupportFragmentManager();
        selectionFlowFragment = (SelectionFlowFragment) fm.findFragmentByTag(SelectionFlowFragment.TAG);
        if (selectionFlowFragment == null) {
            selectionFlowFragment = new SelectionFlowFragment();
        }
    }

	private void checkFirstStart() {
		if (settings.isFirstStart()) {
			ContentStorageLoader csl = new ContentStorageLoader(this);
			csl.unpackAsset();
			settings.setFirstStart(false);
		}
	}

	private void loadContent() {
		ContentStorageLoader csl = new ContentStorageLoader(this);
		csl.load();
	}

	@Override
	public void onBackPressed() {

		if (currentState == State.SELECTION) {
			super.onBackPressed();
		} else if (currentState == State.TRACT) {
			showSelectionPhone();
		}
	}

	public void showSelectionPhone() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		initSelectionFlowFragment();

		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
				R.anim.slide_in_right, R.anim.slide_out_left);

		ft.replace(R.id.root, selectionFlowFragment, SelectionListFragment.TAG);
		ft.commit();
		fm.executePendingTransactions();

		this.currentState = State.SELECTION;
	}

	public void showTractPhone(Tract p) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		initTractFragment();

		tractFragment.setPamphlet(p);

		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
				R.anim.slide_in_left, R.anim.slide_out_right);

		ft.replace(R.id.root, tractFragment, TractFragment.TAG);
		ft.commit();

		this.currentState = State.TRACT;
	}

	@Override
	public void onTractSelected(Tract p) {
		showTractPhone(p);
	}

}
