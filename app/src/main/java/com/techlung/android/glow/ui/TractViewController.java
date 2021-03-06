package com.techlung.android.glow.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.techlung.android.glow.Common;
import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.enums.ColorTheme;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Preferences;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.utils.ContactUtil;
import com.techlung.android.glow.utils.ThemeUtil;
import com.techlung.android.glow.utils.ToolBox;

import java.io.InputStream;

public class TractViewController {
    public enum ImageGetterLoadSource {TRACT, ROOT}

    private static final int MENU_FADE_DISTANCE_DP = 130;

    GlowActivity activity;

    Tract tract;
    Settings s;

    TextView contentView;
    TextView additionalView;
    ScrollView scrollView;


    View viewRoot;

    View menuTractHeaderBox;
    View menuBar;
    View menuLogo;

    ImageView menuTractImage;
    TextView menuTractTitle;

    // menu transaction
    int menuTractImageTopDistance;
    int menuTractImageRightDistance;
    float menuTractImageScaleDistance;

    int menuTractTitleTopDistance;
    int menuTractTitleRightDistance;

    int tractHeaderBoxMarginDistance;
    float menuElevation;
    float tractHeaderBoxDefaultElevation;
    int menuFadeDistancePx;
    float currentScrollY;

    //int menuHeight;

    Typeface blairFont;

    public TractViewController(ViewGroup container, View header) {
        activity = GlowActivity.getInstance();
        s = Settings.getInstance(activity);

        blairFont = Typeface.createFromAsset(GlowActivity.getInstance().getAssets(), "fonts/Blair.otf");

        menuTractHeaderBox = activity.getMenuTractHeaderBox();
        menuTractImage = activity.getMenuTractImage();
        menuTractTitle = activity.getMenuTractTitle();
        menuTractTitle.setTypeface(blairFont);
        menuLogo = header.findViewById(R.id.header_logo);


        viewRoot = createView(LayoutInflater.from(activity), container);

        menuFadeDistancePx = ToolBox.convertDpToPixel(MENU_FADE_DISTANCE_DP, activity);

        menuTractImageTopDistance = ToolBox.convertDpToPixel(105, activity);
        menuTractImageRightDistance = ToolBox.convertDpToPixel(41, activity);
        //menuHeight = ToolBox.convertDpToPixel(56, activity);
        menuTractImageScaleDistance = 1.0f - (44.0f / 90.0f);

        menuTractTitleRightDistance = ToolBox.convertDpToPixel(24, activity);
        menuTractTitleTopDistance = ToolBox.convertDpToPixel(105, activity);

        tractHeaderBoxMarginDistance = ToolBox.convertDpToPixel(16, activity);
        menuBar = header.findViewById(R.id.menu_main_bar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            menuElevation = menuBar.getElevation();
            tractHeaderBoxDefaultElevation = menuTractHeaderBox.getElevation();
        }

        setTract(GlowData.getInstance().getPamphlets().get(0));
    }

    public View getViewRoot() {
        return viewRoot;
    }

    private View createView(LayoutInflater inflater, final ViewGroup container) {

        viewRoot = inflater.inflate(R.layout.tract_fragment, container, false);

        viewRoot.setBackgroundResource(ThemeUtil.getBackgroundColorId());

        scrollView = (ScrollView) viewRoot.findViewById(R.id.activity_glow_pamphlet_table);
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

        contentView = (TextView) viewRoot.findViewById(R.id.activity_glow_pamphlet_list_content);
        additionalView = (TextView) viewRoot.findViewById(R.id.activity_glow_pamphlet_list_additional);

        contentView.setTextColor(ContextCompat.getColor(activity, ThemeUtil.getTextColorId()));
        additionalView.setTextColor(ContextCompat.getColor(activity, ThemeUtil.getTextColorId()));

        viewRoot.findViewById(R.id.additionalContainer).setBackgroundResource(ThemeUtil.getBackgroundOverlayColorId());

        ((ImageView) viewRoot.findViewById(R.id.mailImage)).setImageResource(ThemeUtil.getImageMail());
        viewRoot.findViewById(R.id.contact_mail_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doMailContact(activity);
            }
        });

        ((ImageView) viewRoot.findViewById(R.id.phoneImage)).setImageResource(ThemeUtil.getImagePhone());
        viewRoot.findViewById(R.id.contact_phone_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doPhoneContact(activity);
            }
        });

        ((ImageView) viewRoot.findViewById(R.id.wwwImage)).setImageResource(ThemeUtil.getImageWww());
        viewRoot.findViewById(R.id.contact_www_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.doWWWContact(activity);
            }
        });

        return viewRoot;
    }

    public float getMenuTractImageTranslationX() {
        if (currentScrollY > menuFadeDistancePx) {
            return menuTractImageRightDistance;
        } else {
            float progress = (currentScrollY / (float) menuFadeDistancePx);
            return menuTractImageRightDistance * progress;
        }
    }

    public float getMenuTractTitleTranslationX() {
        if (currentScrollY > menuFadeDistancePx) {
            return menuTractTitleRightDistance;
        } else {
            float progress = (currentScrollY / (float) menuFadeDistancePx);
            return menuTractTitleRightDistance * progress;
        }
    }

    public void scrollToTop() {
        tract.setScrollPosition(currentScrollY);
        scrollView.smoothScrollTo(0, 0);
    }

    public void scrollPageDown() {
        scrollView.smoothScrollBy(0, scrollView.getMeasuredHeight() - ToolBox.convertDpToPixel(16, activity));
    }

    public void scrollPageUp() {
        scrollView.smoothScrollBy(0, -1 * (scrollView.getMeasuredHeight() - ToolBox.convertDpToPixel(16, activity)));
    }

    public void updateMenuFade() {
        activity.updateMenuElementPositions();

        menuTractHeaderBox.setTranslationY(-1 * currentScrollY);

        if (currentScrollY > menuFadeDistancePx) {
            menuLogo.setAlpha(0);
            //menuLogo.setTranslationY(0);

            menuTractHeaderBox.setAlpha(1);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuTractHeaderBox.getLayoutParams();
            params.setMargins(0, params.topMargin, 0, params.bottomMargin);
            menuTractHeaderBox.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                menuTractHeaderBox.setElevation(menuElevation);
            }

            menuTractImage.setTranslationY(-1 * menuTractImageTopDistance);
            menuTractImage.setScaleX(1 - menuTractImageScaleDistance);
            menuTractImage.setScaleY(1 - menuTractImageScaleDistance);

            menuTractTitle.setTranslationY(-1 * menuTractTitleTopDistance);

            menuTractTitle.setTextColor(0xff000000);
        } else {
            float progress = (currentScrollY / (float) menuFadeDistancePx);
            menuLogo.setAlpha(1.0f - progress);
            //menuLogo.setTranslationY(-1 * menuHeight * 0.5f * progress);

            /*
            float alpha = 0.25f + (0.75f * progress * 1.5f);
            if (alpha > 1) {
                alpha = 1;
            } else if (alpha < 0) {
                alpha = 0;
            }
            menuTractHeaderBox.setAlpha(alpha);*/

            menuTractHeaderBox.setAlpha(1.0f - progress);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuTractHeaderBox.getLayoutParams();
            int margin = (int) (tractHeaderBoxMarginDistance - tractHeaderBoxMarginDistance * progress);
            params.setMargins(margin, params.topMargin, margin, params.bottomMargin);
            menuTractHeaderBox.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float elevationProgress = progress * 5;
                if (elevationProgress > 1) {
                    elevationProgress = 1;
                }
                menuTractHeaderBox.setElevation(tractHeaderBoxDefaultElevation + (menuElevation - tractHeaderBoxDefaultElevation) * elevationProgress);
            }

            menuTractImage.setTranslationY(-1 * menuTractImageTopDistance * progress);
            menuTractImage.setScaleX(1 - menuTractImageScaleDistance * progress);
            menuTractImage.setScaleY(1 - menuTractImageScaleDistance * progress);

            menuTractTitle.setTranslationY(-1 * menuTractTitleTopDistance * progress);

            if (Preferences.getColorTheme() == ColorTheme.DARK) {
                int grey = (int) (255 - 255 * progress);
                if (grey > 255) {
                    grey = 255;
                } else if (grey < 0) {
                    grey = 0;
                }
                menuTractTitle.setTextColor(Color.argb(255, grey, grey, grey));
            } else {
                menuTractTitle.setTextColor(ContextCompat.getColor(activity, ThemeUtil.getTextColorId()));
            }

        }
    }

    public Tract getTract() {
        return this.tract;
    }

    public void setTract(Tract p) {
        this.tract = p;
        dataToUi();
    }

    private void dataToUi() {

        if (tract == null) {
            return;
        }

        System.gc();

        Picasso.get().load(tract.getCoverUri()).into(menuTractImage);
        menuTractTitle.setText(Html.fromHtml(tract.getHtmlTitle()));

        contentView.setText(Html.fromHtml(tract.getHtmlContent(), new ImageGetter(ImageGetterLoadSource.TRACT), null));
        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        additionalView.setText(Html.fromHtml(GlowData.getInstance().getAdditionalHtml(), new ImageGetter(ImageGetterLoadSource.ROOT), null));
        additionalView.setMovementMethod(LinkMovementMethod.getInstance());

        scrollToLastPosition();

        updateMenuFade();
    }

    public void scrollToLastPosition() {
        currentScrollY = tract.getScrollPosition();

        scrollView.smoothScrollTo(0, (int) currentScrollY);

        updateMenuFade();
    }


    private class ImageGetter implements Html.ImageGetter {

        float height = 0.0f;
        float width = 0.0f;

        float width_src = 0.0f;
        float height_src = 0.0f;

        float factor = 0.0f;

        ImageGetterLoadSource source;

        ImageGetter(ImageGetterLoadSource source) {
            this.source = source;
        }

        @Override
        public Drawable getDrawable(String imageName) {

            try {
                Drawable d;

                if (this.source == ImageGetterLoadSource.TRACT) {
                    d = tract.getImageDrawable(activity, imageName);
                } else {
                    String lang = Preferences.getLang();
                    InputStream ims = activity.getAssets().open(lang + "/" + imageName);
                    d = Drawable.createFromStream(ims, null);
                }

                if (d == null) {
                    return null;
                }

                DisplayMetrics metrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

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
