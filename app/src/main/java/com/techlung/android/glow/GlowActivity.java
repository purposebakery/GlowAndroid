package com.techlung.android.glow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.techlung.android.glow.enums.UserType;
import com.techlung.android.glow.io.ContentStorageLoader;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Preferences;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.settings.SettingsActivity;
import com.techlung.android.glow.ui.SelectionViewController;
import com.techlung.android.glow.ui.TractViewController;
import com.techlung.android.glow.utils.ContactUtil;
import com.techlung.android.glow.utils.DialogHelper;
import com.techlung.android.glow.utils.Mailer;
import com.techlung.android.glow.utils.ToolBox;

public class GlowActivity extends BaseActivity {
    public static final String TAG = GlowActivity.class.getName();

    public static final String FROM_NOTIFICATION = "FROM_NOTIFICATION";

    public static final int TRANSITION_SPEED = 300;
    public static final boolean DEBUG = false;

    private Settings settings;
    private DrawerLayout mDrawerLayout;
    private View drawerToggle;
    private SelectionViewController selectionFlowFragment;
    private TractViewController tractFragment;

    private ViewPager pager;

    private TextView menuTractTitle;
    private ImageView menuTractImage;
    private View menuTractHeaderBox;

    public enum State {
        SELECTION, TRACT
    }

    public State currentState = State.SELECTION;
    public int currentScrollPosition = 0;
    public float currentPagerScrollPosition = 0;
    private boolean isRunning;
    float screenWidthPx = 0;

    private FloatingActionButton shareButton;
    private FloatingActionButton shareButtonClose;
    private boolean shareButtonOpen;
    private View shareButtonDistributorSpecific;
    private View shareButtonDistributorGeneral;
    private View shareButtonDistributorShare;
    private View shareHideMask;

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
        screenWidthPx = ToolBox.getScreenWidthPx(GlowActivity.this);

        setContentView(R.layout.glow_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                drawerToggle.setScaleX(1 - slideOffset);
                drawerToggle.setScaleY(1 - slideOffset);
                drawerToggle.setRotation(slideOffset * 180);

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

        drawerToggle = findViewById(R.id.drawer_toggle);
        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        checkFirstStart();

        loadContent();

    }

    private void initViews() {

        initMenu();
        initViewPager();
        initShareButton();
        initDrawer();
    }

    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;

        updateShareButtonIcon();
    }

    private void initDrawer() {

        View phoneContainer = findViewById(R.id.contact_phone_container_drawer);
        phoneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doPhoneContact(GlowActivity.this);
            }
        });

        View mailContainer = findViewById(R.id.contact_mail_container_drawer);
        mailContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doMailContact(GlowActivity.this);
            }
        });

        View wwwContainer = findViewById(R.id.contact_www_container_drawer);
        wwwContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doWWWContact(GlowActivity.this);
            }
        });

        findViewById(R.id.more_shop_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shop = GlowData.getInstance().getContact().getShop();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(!shop.startsWith("http") ? "http://" + shop : shop));
                startActivity(i);
            }
        });


        findViewById(R.id.more_newsletter_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mailer.sendNewsletterRequest(GlowActivity.this);
            }
        });

        findViewById(R.id.more_share_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showShareAppAlert(GlowActivity.this);
            }
        });


        findViewById(R.id.more_settings_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GlowActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.glow_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showInfoAlert(GlowActivity.this);
            }
        });

    }

    public int getMenuElementsTranslationX() {
        return (int)(screenWidthPx - (currentPagerScrollPosition * screenWidthPx));
    }

    public void updateMenuElementPositions() {
        if (tractFragment != null) {
            menuTractImage.setTranslationX(tractFragment.getMenuTractImageTranslationX() + getMenuElementsTranslationX());
            menuTractTitle.setTranslationX(tractFragment.getMenuTractTitleTranslationX() + getMenuElementsTranslationX());
        }
    }

    private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.main_pager);
        pager.setAdapter(new MyAdapter());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 0) {
                    currentPagerScrollPosition = positionOffset;

                    menuTractHeaderBox.setTranslationX(getMenuElementsTranslationX());
                    updateMenuElementPositions();

                } else if (position == 1) {
                    currentPagerScrollPosition = 1;

                    menuTractImage.setTranslationX(tractFragment.getMenuTractImageTranslationX());
                    menuTractTitle.setTranslationX(tractFragment.getMenuTractTitleTranslationX());
                    menuTractHeaderBox.setTranslationX(0);

                }
            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    currentPagerScrollPosition = 0;
                    changeState(State.SELECTION);
                } else {
                    currentPagerScrollPosition = 1;
                    changeState(State.TRACT);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                selectionFlowFragment.hideTouchOverlays();

                int scrollPosition = Math.round(selectionFlowFragment.getScrollPosition());
                if (scrollPosition < 0) {
                    scrollPosition = 0;
                } else if (scrollPosition > GlowData.getInstance().getPamphlets().size() - 1) {
                    scrollPosition = GlowData.getInstance().getPamphlets().size() - 1;
                }

                if (scrollPosition != currentScrollPosition) {
                    currentScrollPosition = scrollPosition;
                    tractFragment.setTract(GlowData.getInstance().getPamphlets().get(scrollPosition));
                }
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
                tractFragment = new TractViewController(container, findViewById(R.id.header));
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
        menuTractImage = (ImageView) findViewById(R.id.activity_glow_pamphlet_list_image);
        menuTractTitle = (TextView) findViewById(R.id.activity_glow_pamphlet_list_title);
        menuTractHeaderBox = findViewById(R.id.tract_header_box);

        float screenWidthPx = ToolBox.getScreenWidthPx(GlowActivity.this);
        menuTractImage.setTranslationX(screenWidthPx);
        menuTractTitle.setTranslationX(screenWidthPx);
        menuTractHeaderBox.setTranslationX(screenWidthPx);
        menuTractHeaderBox.setVisibility(View.VISIBLE);
    }

    private void initShareButton() {
        shareHideMask = findViewById(R.id.share_hide_mask);
        shareHideMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareButtonOpen) {
                    toggleShareClickDistributor();
                }
            }
        });

        shareButton = (FloatingActionButton) findViewById(R.id.share_dynamic_button);
        shareButton.setVisibility(View.GONE);


        shareButtonClose = (FloatingActionButton) findViewById(R.id.share_dynamic_button_close);
        shareButtonClose.setVisibility(View.GONE);

        shareButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleShareClick();
                    }
                });
        shareButtonClose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleShareClick();
                    }
                });


        shareButtonDistributorGeneral = findViewById(R.id.share_dynamic_general);
        shareButtonDistributorSpecific = findViewById(R.id.share_dynamic_specific);
        shareButtonDistributorShare = findViewById(R.id.share_dynamic_share);

        shareButtonDistributorShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareButtonOpen) {
                    toggleShareClickDistributor();
                    shareTract();
                }
            }
        });

        shareButtonDistributorGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareButtonOpen) {
                    toggleShareClickDistributor();
                    DialogHelper.showGeneralShareDialog(GlowActivity.this);
                }
            }
        });

        shareButtonDistributorSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareButtonOpen) {
                    toggleShareClickDistributor();
                    DialogHelper.showTractInfoDialog(GlowActivity.this, tractFragment.getTract());
                }
            }
        });

        updateShareButtonIcon();
    }

    private void updateShareButtonIcon() {
        if (shareButton != null) {
            if (Preferences.getUserType() == UserType.READER) {
                shareButton.setImageResource(R.drawable.ic_action_share);
            } else if (Preferences.getUserType() == UserType.DISTRIBUTOR) {
                shareButton.setImageResource(R.drawable.ic_sms);
            }
        }
    }

    private void toggleShareClick() {
        if (Preferences.getUserType() == UserType.READER) {
            shareTract();
        } else if (Preferences.getUserType() == UserType.DISTRIBUTOR) {
            toggleShareClickDistributor();
        }
    }

    private void shareTract() {
        DialogHelper.showShareTractAlert(GlowActivity.this, tractFragment.getTract());
    }

    private void toggleShareClickDistributor() {
        if (shareButtonOpen) {
            animateShareButtonDefaultHide(shareButtonDistributorSpecific);
            animateShareButtonDefaultHide(shareButtonDistributorGeneral);
            animateShareButtonDefaultHide(shareButtonDistributorShare);

            shareButtonClose.animate().setDuration(100).alpha(0);

            shareHideMask.animate().alpha(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    shareHideMask.setVisibility(View.GONE);
                }
            });
            //shareButtonDistributorSpecific.animate().translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator(2));
        } else {
            int distanceDp = 80;

            shareButtonClose.setVisibility(View.VISIBLE);
            shareButtonClose.animate().setDuration(100).alpha(1);

            shareHideMask.setVisibility(View.VISIBLE);

            shareHideMask.animate().alpha(0.5f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    shareHideMask.setVisibility(View.VISIBLE);
                }
            });
            animateShareButtonDefaultShow(shareButtonDistributorSpecific, distanceDp);
            animateShareButtonDefaultShow(shareButtonDistributorGeneral, distanceDp * 2);
            animateShareButtonDefaultShow(shareButtonDistributorShare, distanceDp * 3);
            //shareButtonDistributorSpecific.animate().translationY(-1 * ToolBox.convertDpToPixel(80, this)).setDuration(300);

        }

        shareButtonOpen = !shareButtonOpen;
    }

    private ViewPropertyAnimator animateShareButtonDefaultShow(final View view, int yPositionUpDp) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        return view.animate().translationY(-1 * ToolBox.convertDpToPixel(yPositionUpDp, this)).setDuration(300).setInterpolator(new DecelerateInterpolator(2)).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    private ViewPropertyAnimator animateShareButtonDefaultHide(final View view) {
        return view.animate().translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator(2)).alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
    }
    /*
    private void animateYPosition(View view, int positionDp) {
        TranslateAnimation animate = new TranslateAnimation(0,0,view.getTranslationY(),ToolBox.convertDpToPixel(positionDp, this));
        animate.setDuration(300);
        //animate.setFillAfter(true);
        animate.setInterpolator(new DecelerateInterpolator(2));
        view.startAnimation(animate);
        //view.setVisibility(View.GONE);
    }*/


    private void checkFirstStart() {
        if (settings.isFirstStart()) {
            DialogHelper.showCheckAndSetUserType(this);
            settings.setFirstStart(false);
        }
    }


    private void loadContent() {
        AsyncTask<Void, Void, Void> loadTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ContentStorageLoader csl = new ContentStorageLoader(GlowActivity.this);
                csl.load();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                initViews();

                YoYo.with(Techniques.FadeIn).duration(TRANSITION_SPEED).playOn(pager);
            }
        };
        loadTask.execute();
    }

    @Override
    public void onBackPressed() {
        if (currentState == State.SELECTION) {
            super.onBackPressed();
        } else if (currentState == State.TRACT) {
            showSelection();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (currentState == State.TRACT) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                tractFragment.scrollPageDown();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                tractFragment.scrollPageUp();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
    public void showSelection() {
        pagerToSelection();
    }

    public void showTract(Tract tract) {
        tractFragment.setTract(tract);

        DialogHelper.showScrollDialog(this);

        pagerToTract();
    }

    private void pagerToSelection() {
        changeState(State.SELECTION);

        pager.setCurrentItem(0, true);
        pager.scrollTo(0, 0);
    }

    private void pagerToTract() {
        changeState(State.TRACT);

        pager.scrollTo(1,0);
        pager.setCurrentItem(1, true);
    }

    private void changeState(State state) {
        if (this.currentState == state) {
            return;
        }

        this.currentState = state;

        if (shareButtonOpen) {
            toggleShareClickDistributor();
        }

        if (state == State.TRACT) {
            shareButton.setScaleX(1);
            shareButton.setScaleY(1);
            YoYo.with(Techniques.BounceInRight).duration(TRANSITION_SPEED).playOn(shareButton);
            shareButton.setVisibility(View.VISIBLE);

            tractFragment.scrollToLastPosition();

        } else {

            shareButton.setScaleX(1);
            shareButton.setScaleY(1);
            YoYo.with(Techniques.SlideOutRight).duration(TRANSITION_SPEED).playOn(shareButton);

            tractFragment.scrollToTop();
        }
    }



    public ImageView getMenuTractImage() {
        return menuTractImage;
    }

    public TextView getMenuTractTitle() {
        return menuTractTitle;
    }

    public View getMenuTractHeaderBox() {
        return menuTractHeaderBox;
    }

    public boolean isRunning() {
        return isRunning;
    }


}
