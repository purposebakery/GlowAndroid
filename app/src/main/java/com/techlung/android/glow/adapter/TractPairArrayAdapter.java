package com.techlung.android.glow.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techlung.android.glow.R;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.model.TractPair;
import com.techlung.android.glow.settings.Common;
import com.techlung.android.glow.utils.ToolBox;

import java.util.ArrayList;

public class TractPairArrayAdapter extends ArrayAdapter<TractPair> {

    private ArrayList<TractPair> objects;
    private Activity activity;
    private TractPairArrayAdapter.Callback listener;

    public TractPairArrayAdapter(Activity activity, int resource, ArrayList<TractPair> colors) {

        super(activity, resource, colors);
        this.activity = activity;
        this.objects = colors;
    }

    public TractPairArrayAdapter.Callback getListener() {
        return listener;
    }

    public void setListener(TractPairArrayAdapter.Callback listener) {
        this.listener = listener;
    }

    public View getView(int pos, View inView, ViewGroup parent) {

        int screenwidth = 0;
        if (Common.isXLargeScreen(activity)) {
            screenwidth = ToolBox.convertDpToPixel(300, activity);
        } else {
            screenwidth = ToolBox.getScreenWidthPx(activity);
        }
        int tractWidth = (screenwidth / 5) * 2;
        int tractHeight = (int) (tractWidth * 1.5);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.selection_list_item, parent, false);

        final TractPair object = objects.get(pos);
        final Tract tract1 = object.getTractOne();
        final Tract tract2 = object.getTractTwo();

        // TRACT 1
        View tract1View = v.findViewById(R.id.tract1);

        tract1View.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTractSelected(tract1);
                }
            }
        });

        TextView tract1Title = (TextView) v.findViewById(R.id.tract1_title);
        tract1Title.setText(tract1.getTitle());

        ImageView tract1Image = (ImageView) v.findViewById(R.id.tract1_image);

        tract1Image.setImageDrawable(tract1.getCover());
        tract1Image.getLayoutParams().width = tractWidth;
        tract1Image.getLayoutParams().height = tractHeight;


        // TRACT 2
        if (tract2 == null) {
            return v;
        }

        View tract2View = v.findViewById(R.id.tract2);

        tract2View.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTractSelected(tract2);
                }
            }
        });

        TextView tract2Title = (TextView) v.findViewById(R.id.tract2_title);
        tract2Title.setText(tract2.getTitle());

        ImageView tract2Image = (ImageView) v.findViewById(R.id.tract2_image);
        tract2Image.setImageDrawable(tract2.getCover());
        tract2Image.getLayoutParams().width = tractWidth;
        tract2Image.getLayoutParams().height = tractHeight;

        return (v);
    }

    public interface Callback {
        void onTractSelected(Tract f);
    }


}
