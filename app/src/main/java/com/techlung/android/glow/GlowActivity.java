package com.techlung.android.glow;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.techlung.android.glow.io.ContentStorageLoader;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.ui.SelectionViewController;
import com.techlung.android.glow.ui.TractViewController;
import com.techlung.android.glow.ui.dialogs.ContactDialog;
import com.techlung.android.glow.ui.dialogs.MoreDialog;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GlowActivity extends FragmentActivity {
    public static final int TRANSITION_SPEED = 300;
    public static final int TRANSITION_SPEED_BACK_ARROW = 200;
    private Settings settings;

	//private SelectionListFragment selectionFragment;
	private SelectionViewController selectionFlowFragment;
    private TractViewController tractFragment;

	private ViewPager pager;

	public enum State {
		SELECTION, TRACT
	}

	public State currentState;
    private boolean isRunning;

	View headerBackArrow;

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

		initViewPager();
		initMenu();

		changeState(State.SELECTION);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.save();

    }

    protected void onPause() {
		super.onPause();
        isRunning = false;
	}

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

	private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.main_pager);
		pager.setAdapter(new MyAdapter());
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view;

			if (position == 0) {
				selectionFlowFragment = new SelectionViewController(container);
				view = selectionFlowFragment.getView();
			} else {
				tractFragment = new TractViewController(container);
				view = tractFragment.getView();
			}
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}
	}

	private void initMenu() {

		View.OnClickListener headerBackOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentState == State.TRACT) {
					onBackPressed();
				}
			}
		};

		View logo = findViewById(R.id.header_logo);
		logo.setOnClickListener(headerBackOnClickListener);
		headerBackArrow = findViewById(R.id.header_back_arrow);
		headerBackArrow.setOnClickListener(headerBackOnClickListener);

		findViewById(R.id.more_container).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MoreDialog.show(GlowActivity.this);
					}
				});

		findViewById(R.id.share_container).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						share();
					}
				});

		findViewById(R.id.contact_container).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ContactDialog.show(GlowActivity.this);
					}
				});

	}

	private void share() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	private String getShareText(){
		if (currentState == State.SELECTION) {
			return getResources().getString(R.string.share_app_text) + " " + GlowData.getInstance().getContact().getAppUrl();
		} else if (currentState == State.TRACT) {
			return getResources().getString(R.string.share_tract_text) + " " + tractFragment.getTract().getUrl();
		}
		return null;
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

		pager.setCurrentItem(0);

/*        YoYo.with(Techniques.SlideOutRight).duration(TRANSITION_SPEED).playOn(tractFragment.getView());
        selectionFlowFragment.getView().setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInLeft).duration(TRANSITION_SPEED).playOn(selectionFlowFragment.getView());
*/
		changeState(State.SELECTION);
	}

    public void showTract(Tract tract) {
        tractFragment.setTract(tract);

		/*
        YoYo.with(Techniques.SlideOutLeft).duration(TRANSITION_SPEED).playOn(selectionFlowFragment.getView());
		tractFragment.getView().setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInRight).duration(TRANSITION_SPEED).playOn(tractFragment.getView());
*/
		pager.setCurrentItem(1);
		changeState(State.TRACT);
    }

	private void changeState(State state) {
		this.currentState = state;

		if (state == State.TRACT) {
			headerBackArrow.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceInLeft).duration(TRANSITION_SPEED).playOn(headerBackArrow);
		} else {
            YoYo.with(Techniques.SlideOutLeft).duration(TRANSITION_SPEED).playOn(headerBackArrow);
		}
	}


    public boolean isRunning() {
        return isRunning;
    }
}
