package com.techlung.android.glow;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.pixplicity.easyprefs.library.Prefs;
import com.techlung.android.glow.io.ContentStorageLoader;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
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
    //private View pagerSelectorSelect;
    //private View pagerSelectorTract;
    //private View pagerSelectorBar;

    private TextView menuTractTitle;
    private ImageView menuTractImage;
    private View menuTractHeaderBox;

    public enum State {
        SELECTION, TRACT
    }

    public State currentState = State.SELECTION;
    public int currentScrollPosition = 0;
    private boolean isRunning;

    private FloatingActionButton shareButton;
    private ListView shareBottomList;

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

        setContentView(R.layout.glow_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("TAG!!!", "" + findViewById(R.id.menu_main_bar).getElevation());
        }
        //setReadingBackgroundColor();
        //changeState(State.SELECTION);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //float drawerWidth = getResources().getDimension(R.dimen.drawer_width);
                //float scale = (drawerWidth - slideOffset) / drawerWidth;
                drawerToggle.setScaleX(1 - slideOffset);
                drawerToggle.setScaleY(1 - slideOffset);
                drawerToggle.setRotation(slideOffset * 180);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //YoYo.with(Techniques.TakingOff).duration(TRANSITION_SPEED).playOn(drawerToggle);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //YoYo.with(Techniques.Landing).duration(TRANSITION_SPEED).playOn(drawerToggle);
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

    /*
    private void setReadingBackgroundColor() {
        View mainHideMask = findViewById(R.id.main_hide_mask);
        TextView mainHideMaskText = (TextView) findViewById(R.id.main_hide_mask_text);

        if (Preferences.isGeneralBrightBackground()) {
            mainHideMask.setBackgroundResource(R.color.readingBackgroundBright);
            mainHideMaskText.setTextColor(getResources().getColor(R.color.text_inverted));

        } else {
            mainHideMask.setBackgroundResource(R.color.readingBackgroundDark);
            mainHideMaskText.setTextColor(getResources().getColor(R.color.text));
        }
    }*/

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

    private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.main_pager);
        pager.setAdapter(new MyAdapter());

        //pagerSelectorBar = findViewById(R.id.menu_pager_bar);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            float screenWidthPx = ToolBox.getScreenWidthPx(GlowActivity.this);

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 0) {
                    menuTractImage.setTranslationX(tractFragment.getMenuTractImageTranslationX() + screenWidthPx - (positionOffset * screenWidthPx));
                    menuTractTitle.setTranslationX(tractFragment.getMenuTractTitleTranslationX() + screenWidthPx - (positionOffset * screenWidthPx));
                    menuTractHeaderBox.setTranslationX(screenWidthPx - (positionOffset * screenWidthPx));
                } else if (position == 1) {
                    menuTractImage.setTranslationX(tractFragment.getMenuTractImageTranslationX());
                    menuTractTitle.setTranslationX(tractFragment.getMenuTractTitleTranslationX());
                    menuTractHeaderBox.setTranslationX(0);
                }

                //Log.d("TAG", position  + " " + positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                /*
                if (position == 0) {
                    changeState(State.SELECTION);
                    //pagerSelectorBar.setTranslationX(0);
                } else {
                    changeState(State.TRACT);
                    //pagerSelectorBar.setTranslationX(screenWidthPx / 2);
                }*/
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

        /*
        pagerSelectorSelect = findViewById(R.id.menu_pager_select);
        pagerSelectorSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState != State.SELECTION) {
                    pagerToSelection();
                }
            }
        });

        pagerSelectorTract = findViewById(R.id.menu_pager_tract);
        pagerSelectorTract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState != State.TRACT) {
                    pagerToTract();
                }
            }
        });*/

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
        shareButton = (FloatingActionButton) findViewById(R.id.share_dynamic_button);
        shareButton.setVisibility(View.GONE);

        shareButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //YoYo.with(Techniques.Pulse).duration(TRANSITION_SPEED).playOn(shareButton);

                        //DialogHelper.showShareTractAlert(GlowActivity.this, tractFragment.getTract());
                        //shareBottomSheet.expandFab();
                    }
                });

    }


    private void checkFirstStart() {
        if (settings.isFirstStart()) {
            DialogHelper.showCheckAndSetUserType(this);
            settings.setFirstStart(false);
        }
    }


    private void loadContent() {
        AsyncTask<Void, Void, Void> loadTask = new AsyncTask<Void, Void, Void>() {

            //ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                //progressDialog = new ProgressDialog(GlowActivity.this);
                //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //progressDialog.show();

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
                //if (progressDialog != null) {
                //    progressDialog.dismiss();
                //}
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
        pagerToTract();
    }

    private void pagerToSelection() {
        pager.setCurrentItem(0, true);
        //pager.scrollTo(0, 0);
        changeState(State.SELECTION);
    }

    private void pagerToTract() {
        //pager.scrollTo(1,0);
        pager.setCurrentItem(1, true);
        changeState(State.TRACT);
    }

    private void changeState(State state) {
        this.currentState = state;

        if (state == State.TRACT) {
            shareButton.setScaleX(1);
            shareButton.setScaleY(1);
            YoYo.with(Techniques.BounceInRight).duration(TRANSITION_SPEED).playOn(shareButton);
            shareButton.setVisibility(View.VISIBLE);

            tractFragment.scrollToLastPosition();

            DialogHelper.showScrollDialog(this);

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
