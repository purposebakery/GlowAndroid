package com.techlung.android.glow.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Common;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.utils.ToolBox;

public class TractViewController {

    public static final String TAG = TractViewController.class.getName();

    Tract tract;
    Settings s;

    TextView contentView;
    TextView additionalView;

    ImageView image;
    TextView title;

    ScrollView scrollView;

    Activity activity;

    View view;

    public TractViewController(ViewGroup container) {
        activity = GlowActivity.getInstance();
        s = Settings.getInstance(activity);
        view = createView(LayoutInflater.from(activity), container);
        setTract(GlowData.getInstance().getPamphlets().get(0));
    }

    public View getView() {
        return view;
    }

    private View createView(LayoutInflater inflater, ViewGroup container) {

        view = inflater.inflate(R.layout.tract_fragment, container, false);

        scrollView = (ScrollView) view.findViewById(R.id.activity_glow_pamphlet_table);

        contentView = (TextView) view.findViewById(R.id.activity_glow_pamphlet_list_content);
        additionalView = (TextView) view.findViewById(R.id.activity_glow_pamphlet_list_additional);
        image = (ImageView) view.findViewById(R.id.activity_glow_pamphlet_list_image);
        title = (TextView) view.findViewById(R.id.activity_glow_pamphlet_list_title);

        return view;
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

        contentView.setText("");
        additionalView.setText("");
        System.gc();

        image.setImageURI(tract.getCoverPathUri());
        title.setText(tract.getTitle());

        String content = tract.getHtmlContent();
        if (content == null) {
            content = "";
        }
        contentView.setText(Html.fromHtml(content, new ImageGetter(), null));
        contentView.setMovementMethod(LinkMovementMethod.getInstance());

        String additional = tract.getHtmlAdditional();// readRawTextFile(this,
        if (additional == null) {
            additional = ""; // R.raw.glow_f01_v01);
        }
        additionalView.setText(Html.fromHtml(additional, new ImageGetter(), null));
        additionalView.setMovementMethod(LinkMovementMethod.getInstance());

        scrollView.scrollTo(0, 0);
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
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(
                        tract.getImagePath(source), options);
                Drawable d = new BitmapDrawable(activity.getResources(), bitmap);
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
