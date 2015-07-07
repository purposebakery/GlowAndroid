package com.techlung.android.glow;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.techlung.android.glow.io.ContentStorageLoader;
import com.techlung.android.glow.model.Contact;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.ui.SelectionViewController;
import com.techlung.android.glow.ui.TractViewController;

public class GlowActivity extends FragmentActivity {
    public static final int TRANSITION_SPEED = 300;
    public static final boolean DEBUG = false;

    private Settings settings;
    private DrawerLayout mDrawerLayout;
    //private ActionBarDrawerToggle mDrawerToggle;

    private SelectionViewController selectionFlowFragment;
    private TractViewController tractFragment;

    private ViewPager pager;

    public enum State {
        SELECTION, TRACT
    }

    public State currentState;
    private boolean isRunning;

    private View shareButton;

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
        initShareButton();
        initDrawer();

        changeState(State.SELECTION);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_launcher,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };*/

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        findViewById(R.id.drawer_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);



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

    private void initDrawer() {
        Contact contact = GlowData.getInstance().getContact();

        final String phone = contact.getPhone();
        final String mail = contact.getEmail();
        final String www = !contact.getWww().startsWith("http") ? "http://" + contact.getWww() : contact.getWww();

        TextView phoneView = (TextView) findViewById(R.id.contact_phone);
        phoneView.setText(phone);
        View phoneContainer = findViewById(R.id.contact_phone_container);
        phoneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+" + phone.trim()));
                startActivity(callIntent);
            }
        });

        TextView mailView = (TextView) findViewById(R.id.contact_mail);
        mailView.setText(mail);
        View mailContainer = findViewById(R.id.contact_mail_container);
        mailContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_subject));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_email_chooserTitle)));
            }
        });

        TextView wwwView = (TextView) findViewById(R.id.contact_www);
        wwwView.setText(www);
        View wwwContainer = findViewById(R.id.contact_www_container);
        wwwContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(www));
                startActivity(i);
            }
        });

    }

    private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.main_pager);
        pager.setAdapter(new MyAdapter());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    changeState(State.SELECTION);
                } else {
                    changeState(State.TRACT);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
            container.removeView((View) object);
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
    }

    private void initShareButton() {
        shareButton = findViewById(R.id.share_button);
        shareButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share();
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

    private String getShareText() {
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
        changeState(State.SELECTION);
    }

    public void showTract(Tract tract) {
        tractFragment.setTract(tract);

        pager.setCurrentItem(1);
        changeState(State.TRACT);
    }

    private void changeState(State state) {
        this.currentState = state;

        if (state == State.TRACT) {
            fadeInAndOutShare();
        } else {
            fadeInAndOutShare();
        }
    }

    private void fadeInAndOutShare() {
        YoYo.with(Techniques.TakingOff).duration(TRANSITION_SPEED).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                YoYo.with(Techniques.Landing).duration(TRANSITION_SPEED).playOn(shareButton);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        }).playOn(shareButton);
    }


    public boolean isRunning() {
        return isRunning;
    }
}
