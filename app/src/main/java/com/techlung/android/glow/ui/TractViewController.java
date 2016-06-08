package com.techlung.android.glow.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Common;
import com.techlung.android.glow.settings.Preferences;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.utils.ContactUtil;
import com.techlung.android.glow.utils.ToolBox;

import java.io.InputStream;

public class TractViewController {

    public static final String TAG = TractViewController.class.getName();

    private static final int MENU_FADE_DISTANCE_DP = 150;

    Tract tract;
    Settings s;

    TextView contentView;
    TextView additionalView;

    ImageView image;
    TextView title;

    ScrollView scrollView;

    Activity activity;

    View view;

    ImageView menuTractCover;
    TextView menuTractTitle;
    View menuTractContainer;
    View menuLogo;

    Typeface blairFont;

    private int menuFadeDistancePx;
    private float currentScrollY;

    public TractViewController(ViewGroup container, View header) {
        activity = GlowActivity.getInstance();
        s = Settings.getInstance(activity);

        blairFont = Typeface.createFromAsset(GlowActivity.getInstance().getAssets(), "fonts/Blair.otf");

        menuTractCover = (ImageView) header.findViewById(R.id.menu_tract_cover);
        menuTractTitle = (TextView) header.findViewById(R.id.menu_tract_title);
        menuTractTitle.setTypeface(blairFont);
        menuTractContainer = header.findViewById(R.id.menu_tract_container);
        menuLogo = header.findViewById(R.id.header_logo);

        view = createView(LayoutInflater.from(activity), container);

        menuFadeDistancePx = ToolBox.convertDpToPixel(MENU_FADE_DISTANCE_DP, activity);

        setTract(GlowData.getInstance().getPamphlets().get(0));
    }

    public View getView() {
        return view;
    }

    private View createView(LayoutInflater inflater, final ViewGroup container) {

        view = inflater.inflate(R.layout.tract_fragment, container, false);

        scrollView = (ScrollView) view.findViewById(R.id.activity_glow_pamphlet_table);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getScrollY() == currentScrollY) {
                    return;
                }
                currentScrollY = scrollView.getScrollY();
                updateMenuFade();
            }
        });

        contentView = (TextView) view.findViewById(R.id.activity_glow_pamphlet_list_content);
        additionalView = (TextView) view.findViewById(R.id.activity_glow_pamphlet_list_additional);

        view.findViewById(R.id.contact_mail_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doMailContact(activity);
            }
        });

        view.findViewById(R.id.contact_phone_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doPhoneContact(activity);
            }
        });

        view.findViewById(R.id.contact_www_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doWWWContact(activity);
            }
        });

        image = (ImageView) view.findViewById(R.id.activity_glow_pamphlet_list_image);
        title = (TextView) view.findViewById(R.id.activity_glow_pamphlet_list_title);

        title.setTypeface(blairFont);

        return view;
    }

    public void updateMenuFade() {
        if (currentScrollY > menuFadeDistancePx) {
            menuTractContainer.setAlpha(1);
            menuLogo.setAlpha(0);
        } else {
            float alpha = (currentScrollY / (float)menuFadeDistancePx);
            menuTractContainer.setAlpha(alpha);
            menuLogo.setAlpha(1.0f - alpha);
        }
    }

    public void updateMenuFadeContinuousTract() {
        float targetTractAlpha;
        float targetLogoAlpha;
        if (currentScrollY > menuFadeDistancePx) {
            targetTractAlpha = 1;
            targetLogoAlpha = 0;
        } else {
            float alpha = (currentScrollY / (float)menuFadeDistancePx);
            targetTractAlpha = alpha;
            targetLogoAlpha = 1.0f - alpha;
        }

        menuTractContainer.animate().alpha(targetTractAlpha).setDuration(GlowActivity.TRANSITION_SPEED);
        menuLogo.animate().alpha(targetLogoAlpha).setDuration(GlowActivity.TRANSITION_SPEED);

        /*
        AlphaAnimation tractAnimation = new AlphaAnimation(menuTractContainer.getAlpha(), targetTractAlpha);
        tractAnimation.setDuration(GlowActivity.TRANSITION_SPEED);
        tractAnimation.setFillAfter(true);
        tractAnimation.setFillEnabled(true);
        menuTractContainer.startAnimation(tractAnimation);

        AlphaAnimation logoAnimation = new AlphaAnimation(menuLogo.getAlpha(), targetLogoAloha);
        logoAnimation.setDuration(GlowActivity.TRANSITION_SPEED);
        logoAnimation.setFillAfter(true);
        logoAnimation.setFillEnabled(true);
        menuLogo.startAnimation(logoAnimation);*/
    }

    public void updateMenuFadeContinuousSelection() {
/*        Log.d(TAG, "Update Menu updateMenuFadeContinuousSelection");
        menuTractContainer.setVisibility(View.VISIBLE);
        AlphaAnimation tractAnimation = new AlphaAnimation(menuTractContainer.getAlpha(), 0.0f);
        tractAnimation.setDuration(GlowActivity.TRANSITION_SPEED);
        tractAnimation.setFillAfter(true);
        tractAnimation.setFillEnabled(true);
        menuTractContainer.startAnimation(tractAnimation);
*/
        menuTractContainer.animate().alpha(0.0f).setDuration(GlowActivity.TRANSITION_SPEED);

        menuLogo.animate().alpha(1.0f).setDuration(GlowActivity.TRANSITION_SPEED);
        /*
        menuLogo.setVisibility(View.VISIBLE);
        AlphaAnimation logoAnimation = new AlphaAnimation(menuLogo.getAlpha() + 0.01f, 1.0f);
        logoAnimation.setDuration(GlowActivity.TRANSITION_SPEED);
        logoAnimation.setFillAfter(true);
        logoAnimation.setFillEnabled(true);
        menuLogo.startAnimation(logoAnimation);*/
    }


    public void setTract(Tract p) {
        this.tract = p;
        dataToUi();
    }

    public Tract getTract() {
        return this.tract;
    }

    private void dataToUi() {

        if (tract == null) {
            return;
        }

        //contentView.setText("");
        //additionalView.setText("");
        System.gc();

        image.setImageDrawable(tract.getCoverDrawable(activity));
        title.setText(Html.fromHtml(tract.getHtmlTitle()));

        menuTractCover.setImageDrawable(tract.getCoverDrawable(activity));
        menuTractTitle.setText(Html.fromHtml(tract.getHtmlTitle()));

        contentView.setText(Html.fromHtml(tract.getHtmlContent(), new ImageGetter(), null));
        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        additionalView.setText(Html.fromHtml(tract.getHtmlAdditional(), new ImageGetter(), null));
        additionalView.setMovementMethod(LinkMovementMethod.getInstance());

        scrollView.scrollTo(0, 0);
        currentScrollY = 0;
    }

    public class ImageGetter implements Html.ImageGetter {

        float height = 0.0f;
        float width = 0.0f;

        float width_src = 0.0f;
        float height_src = 0.0f;

        float factor = 0.0f;

        @Override
        public Drawable getDrawable(String source) {

            try {

                Drawable d = tract.getImageDrawable(activity, source);
                if (d == null) {
                    return null;
                }

                DisplayMetrics metrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay()
                        .getMetrics(metrics);

                height = metrics.heightPixels;
                width = metrics.widthPixels;

                if (Common.isXLargeScreen(activity)) {
                    width -= ToolBox.convertDpToPixel(300, activity);
                }

                height *= 0.7;
                width *= 0.7;

                width_src = d.getIntrinsicWidth();
                height_src = d.getIntrinsicHeight();

                if (width_src <= height_src) {
                    factor = height / height_src;
                } else {
                    factor = width / width_src;
                }

                d.setBounds(0, 0, (int) (width_src * factor),
                        (int) (height_src * factor));

                return d;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
    }



}
