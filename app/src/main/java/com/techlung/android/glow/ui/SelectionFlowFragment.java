package com.techlung.android.glow.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;
import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.utils.ToolBox;

import java.io.File;
import java.util.ArrayList;

import hugo.weaving.DebugLog;

public class SelectionFlowFragment extends Fragment {

    public static final String TAG = SelectionFlowFragment.class.getName();

    ArrayList<SelectionFlowItem> items = new ArrayList<SelectionFlowItem>();

    private float scrollPosition = 0;

    private int tractWidthPx;
    private int tractHeightPx;

    private int screenWidthPx;
    private int screenHeightPx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initMeasures();

        int counter = 0;
        for (Tract tract : GlowData.getInstance().getPamphlets()) {
            SelectionFlowItem item = new SelectionFlowItem();
            item.tract = tract;
            item.x = getXForItemPosition(counter);
            item.y = getYForItemPosition(counter);
            ++counter;

            items.add(item);
        }
    }

    private void initMeasures() {
        screenWidthPx = ToolBox.getScreenWidthPx(getActivity());
        screenHeightPx = ToolBox.getScreenHeightPx(getActivity());

        tractWidthPx = (int) (screenWidthPx * 0.5f);
        tractHeightPx = (int) (screenWidthPx * 1.5f);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.selection_flow_fragment, container, false);

        rootView.setOnTouchListener(new MyOnTouchListener());

        for (SelectionFlowItem item : items) {
            View itemView = inflater.inflate(R.layout.selection_flow_item, rootView, false);
            ImageView image = (ImageView) itemView.findViewById(R.id.image);
            image.getLayoutParams().height = tractHeightPx;
            image.getLayoutParams().width = tractWidthPx;
            image.setImageURI(Uri.fromFile(new File(item.tract.getCoverPath())));
            //image.setImageDrawable(item.tract.getCover());

            item.view = itemView;
            rootView.addView(itemView);
        }

        repositionItems();

        return rootView;
    }

    private void repositionItems() {
        Logger.d("repositionItems $s", items.size());

        for (int i = 0; i < items.size(); ++i) {
            SelectionFlowItem item = items.get(i);

            item.x = getXForItemPosition(i);
            item.y = getYForItemPosition(i);
            item.scale = getScaleForPosition(item.x, item.y);

            item.view.setX(item.x);
            item.view.setY(item.y);
            item.view.setScaleX(item.scale);
        }
    }

    public class MyOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);

            switch(action) {
                case (MotionEvent.ACTION_DOWN) :
                    Log.d(TAG,"Action was DOWN");
                    return true;
                case (MotionEvent.ACTION_MOVE) :
                    if (event.getHistorySize() == 0) {
                        return true;
                    }

                    float y = event.getY();
                    float yOld = event.getHistoricalY(0);

                    float difference = y - yOld;
                    scrollPosition += (difference / tractHeightPx);

                    repositionItems();
                    Log.d(TAG,"Action was MOVE");
                    return true;
                case (MotionEvent.ACTION_UP) :
                    Log.d(TAG, "Action was UP");
                    return true;
                case (MotionEvent.ACTION_CANCEL) :
                    Log.d(TAG,"Action was CANCEL");
                    return true;
                case (MotionEvent.ACTION_OUTSIDE) :
                    Log.d(TAG,"Movement occurred outside bounds " +
                            "of current screen element");
                    return true;
                default :
                    return true;
            }
        }
    }

    private float getXForItemPosition(int itemPosition) {
        return ((screenWidthPx / 2) - (tractWidthPx / 2));
    }

    private float getYForItemPosition(int itemPosition) {
        return ((itemPosition * tractHeightPx) + scrollPosition * tractHeightPx);
    }

    private float getScaleForPosition(float x, float y) {
        float xCenter = x + tractWidthPx / 2;
        float yCenter = y + tractHeightPx / 2;

        float xCenterAbsolute = screenWidthPx / 2;
        float yCenterAbsolute = screenHeightPx / 2;

        // 0 center, 1 border, > 1 outside
        float relativeDistanceToCenter = Math.abs(yCenter - yCenterAbsolute) / (screenHeightPx / 2);
        float scale = 1 - relativeDistanceToCenter;
        if (scale < 0) {
            scale = 0;
        }
        return scale;
    }
}
