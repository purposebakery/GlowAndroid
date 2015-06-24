package com.techlung.android.glow.ui;

import com.techlung.android.glow.R;
import com.techlung.android.glow.dialogs.ContactDialog;
import com.techlung.android.glow.dialogs.MoreDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class TractFragment extends Fragment {

    public static final String TAG = TractFragment.class.getName();

    private static final String STATE_TRACT_ID = "STATE_TRACT_ID";

    Tract tract;
    Settings s;

    TextView contentView;
    TextView additionalView;
    LinearLayout contact;

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

        contentView = (TextView) v
                .findViewById(R.id.activity_glow_pamphlet_list_content);
        additionalView = (TextView) v
                .findViewById(R.id.activity_glow_pamphlet_list_additional);
        contact = (LinearLayout) v
                .findViewById(R.id.activity_glow_pamphlet_contact_elements);

        View back = v.findViewById(R.id.header_logo);
        if (back != null) {
            back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        v.findViewById(R.id.more_container).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MoreDialog.show(getActivity());
                    }
                });

        v.findViewById(R.id.share_container).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share();
                    }
                });

        v.findViewById(R.id.contact_container).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContactDialog.show(getActivity());
                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataToUi();
    }

    public void setPamphlet(Tract p) {
        this.tract = p;

        if (getView() != null) {
            dataToUi();
        }

    }

    private void dataToUi() {

        if (tract == null) {
            return;
        }

        contentView.setText("");
        additionalView.setText("");
        System.gc();

        String content = tract.getHtmlContent();
        if (content == null) {
            content = "";
        }
        if (!Common.isXLargeScreen(getActivity())) {
            content = "<br>" + content;
        }
        contentView.setText(Html.fromHtml(content, new ImageGetter(), null));
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
        contentView.setPadding(10, 10, 10, 10);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        String additional = tract.getHtmlAdditional();// readRawTextFile(this,
        if (additional == null) {
            additional = ""; // R.raw.glow_f01_v01);
        }
        additionalView.setText(Html.fromHtml(additional, new ImageGetter(),
                null));
        additionalView.setMovementMethod(LinkMovementMethod.getInstance());
        additionalView.setPadding(10, 10, 10, 10);
        additionalView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getResources()
                .getString(R.string.share_text) + " " + tract.getUrl());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
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
