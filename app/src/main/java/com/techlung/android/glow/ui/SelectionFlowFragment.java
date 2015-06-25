package com.techlung.android.glow.ui;

import android.graphics.Rect;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.utils.ToolBox;

import java.io.File;
import java.util.ArrayList;

public class SelectionFlowFragment extends Fragment {

    public static final String TAG = SelectionFlowFragment.class.getName();
    public static final int TOUCH_MOVEMENT_THRESHOLD_DP = 10;
    public static final int TRACT_TOUCH_ANIMATION_LENGTH = 100;

    ArrayList<SelectionFlowItem> items = new ArrayList<SelectionFlowItem>();
    ArrayList<SelectionFlowItem> clickedItems = new ArrayList<SelectionFlowItem>();

    private float scrollPosition = 0;

    private int tractWidthPx;
    private int tractHeightPx;

    private int screenWidthPx;
    private int screenHeightPx;

    RelativeLayout rootView;

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
        tractHeightPx = (int) (tractWidthPx * 1.5f);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (RelativeLayout) inflater.inflate(R.layout.selection_flow_fragment, container, false);

        rootView.setOnTouchListener(new MyOnTouchListener());

        for (SelectionFlowItem item : items) {
            item.view = inflater.inflate(R.layout.selection_flow_item, rootView, false);
            item.image = (ImageView) item.view.findViewById(R.id.image);
            item.overlay = item.view.findViewById(R.id.overlay);
            item.image.getLayoutParams().height = tractHeightPx;
            item.image.getLayoutParams().width = tractWidthPx;
            item.image.setImageURI(Uri.fromFile(new File(item.tract.getCoverPath())));
            //item.image.setImageDrawable(item.tract.getCover());

            rootView.addView(item.view);
        }

        repositionItems();

        return rootView;
    }

    private void repositionItems() {
        for (int i = 0; i < items.size(); ++i) {
            SelectionFlowItem item = items.get(i);

            item.x = getXForItemPosition(i);
            item.y = getYForItemPosition(i);
            item.scale = getScaleForPosition(item.x, item.y);

            item.view.setX(item.x);
            item.view.setY(item.y);
            item.view.setScaleX(item.scale);
            item.view.setScaleY(item.scale);
        }
    }

    public class MyOnTouchListener implements View.OnTouchListener {
        private float totalMovement = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);
            float difference;

            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    onTouchDownPosition(event.getX(), event.getY());
                    totalMovement = 0;
                    Log.d(TAG, "Action was DOWN");
                    return true;
                case (MotionEvent.ACTION_MOVE):
                    if (event.getHistorySize() == 0) {
                        return true;
                    }

                    difference = getYDifference(event);
                    totalMovement += Math.abs(difference);
                    addMovementDifferenceToScrollPosition(difference);

                    repositionItems();
                    Log.d(TAG, "Action was MOVE");
                    return true;
                case (MotionEvent.ACTION_UP):
                    hideTouchOverlays();

                    if (totalMovement < ToolBox.convertDpToPixel(TOUCH_MOVEMENT_THRESHOLD_DP, getActivity())) {
                        onTouchUpPosition(event.getX(), event.getY());
                    }

                    if (event.getHistorySize() == 0) {
                        return true;
                    }

                    difference = getYDifference(event);
                    addMovementDifferenceToScrollPositionContiuousDecrease(difference);

                    Log.d(TAG, "Action was UP");
                    return true;
                case (MotionEvent.ACTION_CANCEL):
                    Log.d(TAG, "Action was CANCEL");
                    return true;
                case (MotionEvent.ACTION_OUTSIDE):
                    Log.d(TAG, "Movement occurred outside bounds " +
                            "of current screen element");
                    return true;
                default:
                    return true;
            }
        }
    }

    private void hideTouchOverlays() {
        for (final SelectionFlowItem item : clickedItems) {
            YoYo.with(Techniques.FadeOut).duration(TRACT_TOUCH_ANIMATION_LENGTH).playOn(item.overlay);
        }
        clickedItems.clear();

    }

    private void onTouchDownPosition(float x, float y) {
        for (final SelectionFlowItem item : items) {
            if (inViewInBounds(item.view, (int) x, (int) y)) {
                item.overlay.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.FadeIn).duration(TRACT_TOUCH_ANIMATION_LENGTH).playOn(item.overlay);
                clickedItems.add(item);
            }
        }
    }

    private void onTouchUpPosition(float x, float y) {
        for (SelectionFlowItem item : items) {
            if (inViewInBounds(item.view, (int) x, (int) y)) {
                GlowActivity.getInstance().showTract(item.tract);
            }
        }
    }

    Rect outRect = new Rect();
    int[] location = new int[2];

    private boolean inViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private float getYDifference(MotionEvent event) {
        float y = event.getY();
        float yOld = event.getHistoricalY(0);

        float difference = y - yOld;
        return difference;
    }

    private void addMovementDifferenceToScrollPosition(float difference) {
        scrollPosition += (difference / tractHeightPx);
    }

    private void addMovementDifferenceToScrollPositionContiuousDecrease(final float difference) {
        if (difference < 0.1) {
            return;
        } else {
            addMovementDifferenceToScrollPosition(difference);
            addMovementDifferenceToScrollPositionContiuousDecrease(difference * 0.9f);
            repositionItems();
            //rootView.invalidate();
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
