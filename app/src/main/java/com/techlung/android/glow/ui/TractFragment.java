package com.techlung.android.glow.ui;

import com.techlung.android.glow.R;
import com.techlung.android.glow.ui.dialogs.ContactDialog;
import com.techlung.android.glow.ui.dialogs.MoreDialog;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Common;
import com.techlung.android.glow.settings.Settings;
import com.techlung.android.glow.utils.ToolBox;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TractFragment extends Fragment {

    public static final String TAG = TractFragment.class.getName();

    private static final String STATE_TRACT_ID = "STATE_TRACT_ID";

    Tract tract;
    Settings s;

    TextView contentView;
    TextView additionalView;

    ImageView image;
    TextView title;

    ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        s = Settings.getInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            String tractId = savedInstanceState.getString(STATE_TRACT_ID);
            tract = GlowData.getInstance().getTract(tractId);
        }

        View v = inflater.inflate(R.layout.tract_fragment, container, false);

        scrollView = (ScrollView) v.findViewById(R.id.activity_glow_pamphlet_table);

        contentView = (TextView) v.findViewById(R.id.activity_glow_pamphlet_list_content);
        additionalView = (TextView) v.findViewById(R.id.activity_glow_pamphlet_list_additional);
        image = (ImageView) v.findViewById(R.id.activity_glow_pamphlet_list_image);
        title = (TextView) v.findViewById(R.id.activity_glow_pamphlet_list_title);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataToUi();
    }

    public void setTract(Tract p) {
        this.tract = p;

        if (getView() != null) {
            dataToUi();
        }
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

        scrollView.scrollTo(0,0);
    }



    public class ImageGetter implements Html.ImageGetter {

        float height = 0.0f;
        float width = 0.0f;

        float width_src = 0.0f;
        float height_src = 0.0f;

        float factor = 0.0f;

        public Drawable getDrawable(String source) {

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(
                        tract.getImagePath(source), options);
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay()
                        .getMetrics(metrics);

                height = metrics.heightPixels;
                width = metrics.widthPixels;

                if (Common.isXLargeScreen(getActivity())) {
                    width -= ToolBox.convertDpToPixel(300, getActivity());
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

    public void onSaveInstanceState(Bundle inState) {
        if (tract != null) {
            inState.putString(STATE_TRACT_ID, tract.getId());
        }
    }
}
