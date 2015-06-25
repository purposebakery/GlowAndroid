package com.techlung.android.glow;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.techlung.android.glow.io.ContentStorageLoader;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.ui.SelectionFlowFragment;
import com.techlung.android.glow.ui.SelectionFlowItem;
import com.techlung.android.glow.ui.TractFragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class GlowActivity extends FragmentActivity {
    public static final int TRANSITION_SPEED = 300;
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

        checkFirstStart();
        loadContent();

        setContentView(R.layout.glow_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		initTractFragment();
		initSelectionFragment();

		showSelection();
	}

	protected void onPause() {
		super.onPause();
		settings.save();
	}

	private void initTractFragment() {
		tractFragment = (TractFragment) getSupportFragmentManager().findFragmentById(R.id.tract);
        tractFragment.getView().setVisibility(View.GONE);
	}

    private void initSelectionFragment() {
        selectionFlowFragment = (SelectionFlowFragment) getSupportFragmentManager().findFragmentById(R.id.selection);
        selectionFlowFragment.getView().setVisibility(View.VISIBLE);
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
			showSelection();
		}
	}

	public void showSelection() {

        YoYo.with(Techniques.SlideOutRight).duration(TRANSITION_SPEED).playOn(tractFragment.getView());
        selectionFlowFragment.getView().setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInLeft).duration(TRANSITION_SPEED).playOn(selectionFlowFragment.getView());

		this.currentState = State.SELECTION;
	}

    public void showTract(Tract tract) {
        tractFragment.setTract(tract);

        YoYo.with(Techniques.SlideOutLeft).duration(TRANSITION_SPEED).playOn(selectionFlowFragment.getView());
        tractFragment.getView().setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInRight).duration(TRANSITION_SPEED).playOn(tractFragment.getView());
        this.currentState = State.TRACT;
    }

}
