package com.techlung.android.glow.ui;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.techlung.android.glow.GlowActivity;
import com.techlung.android.glow.R;
import com.techlung.android.glow.model.GlowData;
import com.techlung.android.glow.model.Tract;
import com.techlung.android.glow.settings.Preferences;
import com.techlung.android.glow.utils.Gauss;
import com.techlung.android.glow.utils.ToolBox;

import java.util.ArrayList;
import java.util.Stack;

public class SelectionViewController {

    public static final String TAG = SelectionViewController.class.getName();
    public static final int TOUCH_MOVEMENT_THRESHOLD_DP = 10;
    public static final int TRACT_TOUCH_ANIMATION_LENGTH = 100;

    private static final double GAUSS_SCALE = 0.6;
    private static final double GAUSS_SCROLL_SCALE = 0.4f;
    public static final int TRACT_ELEVATION_DP = 15;
    public static final int SCROLL_ELEVATION_DP = 8;
    public static final int SCROLL_WIDTH_DP = 30;
    public static final int MENU_HEIGHT_DP = 56;
    public static final int STATUS_BAR_HEIGHT_DP = 28;
    public static final int START_POINT_Y_CHANGE = 20;
    private Gauss gauss;
    private Gauss gaussScroll;

    ArrayList<SelectionItem> items = new ArrayList<SelectionItem>();
    ArrayList<SelectionItem> scrollItems = new ArrayList<SelectionItem>();
    ArrayList<SelectionItem> clickedItems = new ArrayList<SelectionItem>();
    Stack<Float> lastMovementSpeeds = new Stack<Float>();

    private float scrollPosition = 0;

    private int tractWidthPx;
    private int tractHeightPx;

    private int scrollTractWidthPx;
    private int scrollTractHeightPx;

    private int screenWidthPx;
    private int screenHeightPx;

    int tractCount = 0;

    private float tractStartingPoint;
    private float currentMovementDifferenceId = 0;
    private float currentMovementSpeed = 0;

    private Activity activity;
    private View view;

    private RelativeLayout rootView;
    private RelativeLayout scrollView;
    private TextView debug;

    public SelectionViewController(ViewGroup container) {
        activity = GlowActivity.getInstance();
        initMeasures();

        gauss = new Gauss(screenHeightPx, GAUSS_SCALE);
        gaussScroll = new Gauss(screenHeightPx, GAUSS_SCROLL_SCALE);

        int counter = 0;
        for (Tract tract : GlowData.getInstance().getPamphlets()) {
            SelectionItem item = new SelectionItem();
            item.tract = tract;
            item.x = getXForItemPosition(counter);
            item.y = getYForItemPosition(counter);
            ++counter;

            items.add(item);
        }

        counter = 0;
        for (Tract tract : GlowData.getInstance().getPamphlets()) {
            SelectionItem item = new SelectionItem();
            item.tract = tract;
            item.x = getXForScrollItemPosition(counter);
            item.y = getYForScrollItemPosition(counter);
            ++counter;

            scrollItems.add(item);
        }

        view = createView(LayoutInflater.from(activity), container);

        repositionItems();
        repositionScrollItems();
    }

    public View getView() {
        return view;
    }

    private void initMeasures() {

        tractCount = GlowData.getInstance().getPamphlets().size();

        screenWidthPx = ToolBox.getScreenWidthPx(activity);
        screenHeightPx = ToolBox.getScreenHeightPx(activity) - ToolBox.convertDpToPixel(MENU_HEIGHT_DP, activity) - ToolBox.convertDpToPixel(STATUS_BAR_HEIGHT_DP, activity); // minus menu height

        tractWidthPx = (int) (screenWidthPx * 0.5f);
        tractHeightPx = (int) (tractWidthPx * 1.5f);

        scrollTractWidthPx = ToolBox.convertDpToPixel(SCROLL_WIDTH_DP, activity);
        scrollTractHeightPx = (int) (scrollTractWidthPx * 1.5f);

        tractStartingPoint = screenHeightPx / 2 - tractHeightPx / 2 - ToolBox.convertDpToPixel(START_POINT_Y_CHANGE, activity);;
    }

    private View createView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.selection_flow_fragment, container, false);

        rootView = (RelativeLayout) view.findViewById(R.id.flow_root);
        rootView.setOnTouchListener(new ItemOnTouchListener());

        debug = (TextView) view.findViewById(R.id.debug);

        for (SelectionItem item : items) {
            item.view = inflater.inflate(R.layout.selection_flow_item, rootView, false);
            if (android.os.Build.VERSION.SDK_INT >= 21){
                item.view.setElevation(0);
            }
            item.view.getLayoutParams().width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            item.view.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            item.image = (ImageView) item.view.findViewById(R.id.image);
            item.overlay = item.view.findViewById(R.id.overlay);
            item.debug = (TextView) item.view.findViewById(R.id.debug);
            item.image.getLayoutParams().height = tractHeightPx;
            item.image.getLayoutParams().width = tractWidthPx;

            item.image.setImageDrawable(item.tract.getCoverDrawable(activity));

            rootView.addView(item.view);
        }

        scrollView = (RelativeLayout) view.findViewById(R.id.flow_scroll);
        scrollView.setOnTouchListener(new ScrollItemOnTouchListener());

        for (SelectionItem scrollItem : scrollItems) {
            scrollItem.view = inflater.inflate(R.layout.selection_flow_item, scrollView, false);
            scrollItem.image = (ImageView) scrollItem.view.findViewById(R.id.image);
            scrollItem.overlay = scrollItem.view.findViewById(R.id.overlay);
            scrollItem.image.getLayoutParams().height = scrollTractHeightPx;
            scrollItem.image.getLayoutParams().width = scrollTractWidthPx;
            scrollItem.image.setImageDrawable(scrollItem.tract.getCoverDrawable(activity));

            scrollView.addView(scrollItem.view);
        }

        return view;
    }

    private void repositionItems() {
        for (int i = 0; i < tractCount; ++i) {
            SelectionItem item = items.get(i);

            item.x = getXForItemPosition(i);
            item.y = getYForItemPosition(i);
            item.scale = getScaleForPosition(item.x, item.y);

            item.view.setX(item.x);
            item.view.setY(item.y);
            item.view.setScaleX(item.scale);
            item.view.setScaleY(item.scale);
            //item.view.setAlpha(item.scale);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                item.view.setElevation(item.scale * ToolBox.convertDpToPixel(TRACT_ELEVATION_DP, GlowActivity.getInstance()) + 4);
            }
        }
        printDebugInfo();
        rootView.invalidate();
    }

    private void printDebugInfo() {
        if (GlowActivity.DEBUG) {
            for (int i = 0; i < tractCount; ++i) {
                SelectionItem item = items.get(i);

                String debugString = "";
                debugString += item.x + ", " + item.y + "\n";
                debugString += item.scale;
                item.debug.setText(debugString);
            }

            String debugString2 = "";
            debugString2 += "scrollposition: " + scrollPosition + "\n";
            debugString2 += "speed: " + currentMovementSpeed + "\n";
            debugString2 += "10 last speeds: " + getLastMovementSpeedsSum() + "";
            debug.setText(debugString2);
        }
    }

    private void repositionScrollItems() {
        for (int i = 0; i < tractCount; ++i) {
            SelectionItem item = scrollItems.get(i);

            item.x = getXForScrollItemPosition(i);
            item.y = getYForScrollItemPosition(i);
            item.scale = getScrollScaleForPosition(items.get(i).x, items.get(i).y);

            item.view.setX(item.x);
            item.view.setY(item.y);
            item.view.setScaleX(item.scale);
            item.view.setScaleY(item.scale);
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                item.view.setElevation(item.scale * ToolBox.convertDpToPixel(SCROLL_ELEVATION_DP, GlowActivity.getInstance()) + 4);
            }
        }
        scrollView.invalidate();
    }

    public class ItemOnTouchListener implements View.OnTouchListener {
        private float totalMovement = 0;
        private float lastMovement = 0;

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
                    lastMovement = difference;
                    totalMovement += Math.abs(difference);

                    addMovementDifferenceToScrollPositionContiuousDecrease(difference, false);

                    repositionItems();
                    return true;
                case (MotionEvent.ACTION_UP):
                    hideTouchOverlays();

                    if (totalMovement < ToolBox.convertDpToPixel(TOUCH_MOVEMENT_THRESHOLD_DP, activity)) {
                        onTouchUpPosition(event.getX(), event.getY());
                    }

                    addMovementDifferenceToScrollPositionContiuousDecrease(lastMovement, true);

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

    public class ScrollItemOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                case (MotionEvent.ACTION_MOVE):
                    float y = event.getY();

                    float newScrollPosition = ((y) / screenHeightPx) * tractCount - 0.5f;
                    if (newScrollPosition < 0) {
                        newScrollPosition = 0;
                    } else if (newScrollPosition > tractCount -1) {
                        newScrollPosition = tractCount - 1;
                    }
                    moveScrollPosition(newScrollPosition, ++currentMovementDifferenceId);

                    return true;
                default:
                    return true;
            }
        }
    }

    private void moveScrollPosition(final float newScrollPosition, final float id) {
        if (!GlowActivity.getInstance().isRunning()) {
            currentMovementSpeed = 0;
            return;
        }

        if (currentMovementDifferenceId > id) {
            currentMovementSpeed = 0;
            return;
        }

        if (Math.abs(scrollPosition - newScrollPosition) < 0.01) {
            currentMovementSpeed = 0;
            return;
        } else {
            addMovementDifferenceToScrollPosition((scrollPosition - newScrollPosition) * tractHeightPx * 0.05f);

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    moveScrollPosition(newScrollPosition, id);
                }
            });

            repositionItems();
            repositionScrollItems();
        }
    }

    public void hideTouchOverlays() {
        for (final SelectionItem item : clickedItems) {
            YoYo.with(Techniques.FadeOut).duration(TRACT_TOUCH_ANIMATION_LENGTH).playOn(item.overlay);
        }
        clickedItems.clear();

    }

    private void onTouchDownPosition(float x, float y) {
        for (final SelectionItem item : items) {
            if (inViewInBounds(item, (int) x, (int) y)) {
                item.overlay.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.FadeIn).duration(TRACT_TOUCH_ANIMATION_LENGTH).playOn(item.overlay);
                clickedItems.add(item);
            }
        }
    }

    private void onTouchUpPosition(float x, float y) {
        for (SelectionItem item : items) {
            if (inViewInBounds(item, (int) x, (int) y)) {
                GlowActivity.getInstance().showTract(item.tract);
            }
        }
    }


    private boolean inViewInBounds(SelectionItem item, int x, int y) {
        float tractWidth = (tractWidthPx * item.scale);
        float tractHeight = (tractHeightPx * item.scale);
        float itemStartX = item.x + (tractWidthPx - tractWidth) / 2;
        float itemStartY = item.y + (tractHeightPx - tractHeight) / 2;

        if (x > itemStartX && x < itemStartX + tractWidth &&
                y > itemStartY && y < itemStartY + tractHeight) {
            return true;
        } else {
            return false;
        }
    }

    private float getYDifference(MotionEvent event) {
        float y = event.getY();
        float yOld = event.getHistoricalY(0);

        float difference = y - yOld;
        difference *= 1.4;
        return difference;
    }

    private void addMovementDifferenceToScrollPosition(float difference) {
        lastMovementSpeeds.push(currentMovementSpeed);
        if (lastMovementSpeeds.size() > 10) {
            lastMovementSpeeds.pop();
        }
        currentMovementSpeed = difference / tractHeightPx;
        scrollPosition -= currentMovementSpeed;
    }

    private void addMovementDifferenceToScrollPositionContiuousDecrease(final float difference, boolean continuous) {
        ++currentMovementDifferenceId;
        addMovementDifferenceToScrollPositionContiuousDecrease(difference, currentMovementDifferenceId, continuous);
    }

    private void addMovementDifferenceToScrollPositionContiuousDecrease(final float difference, final float id, final boolean continuous) {
        if (!GlowActivity.getInstance().isRunning()) {
            currentMovementSpeed = 0;
            return;
        }

        if (currentMovementDifferenceId > id) {
            currentMovementSpeed = 0;
            return;
        }

        if (Math.abs(difference) < 1 && !isScrollOutideBounds()) {
            currentMovementSpeed = 0;
            return;
        } else {
            addMovementDifferenceToScrollPosition(difference);
            attractToNeighbours();

            if (continuous) {
                if (scrollPosition < 0) {
                    addMovementDifferenceToScrollPosition(scrollPosition * tractHeightPx * 0.1f - 1);
                } else if (scrollPosition > tractCount - 1) {
                    addMovementDifferenceToScrollPosition((scrollPosition - (tractCount - 1)) * tractHeightPx * 0.1f + 1);
                }

                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        addMovementDifferenceToScrollPositionContiuousDecrease(difference * 0.95f, id, continuous);
                    }
                });
            }

            repositionItems();
            repositionScrollItems();
        }
    }

    private void attractToNeighbours() {
        int nHigh = (int) Math.ceil(scrollPosition);
        int nLow = (int) Math.floor(scrollPosition);
        float differenceToPosition = 0;

        if (positionInRange(nLow)) {
            differenceToPosition = nLow - scrollPosition;
            addMovementDifferenceToScrollPosition(differenceToPosition);
        }

        if (positionInRange(nHigh)) {
            differenceToPosition = nHigh - scrollPosition;
            addMovementDifferenceToScrollPosition(differenceToPosition);
        }

    }

    private float getLastMovementSpeedsSum() {
        float value = 0;
        for (Float speed : lastMovementSpeeds) {
            value += speed;
        }
        return value;
    }

    private boolean positionInRange(int position) {
        if (position < 0 || position > tractCount - 1) {
            return false;
        } else {
            return true;
        }
    }



    private boolean isScrollOutideBounds() {
        return scrollPosition < 0 || scrollPosition > tractCount - 1;
    }

    private float getXForItemPosition(int itemPosition) {
        return ((screenWidthPx / 2) - (tractWidthPx / 2)) - (scrollTractWidthPx / 2);
    }

    private float getYForItemPosition(int itemPosition) {
        return ((itemPosition * tractHeightPx) - scrollPosition * tractHeightPx + tractStartingPoint);
    }

    private float getScaleForPosition(float x, float y) {
        double scale = gauss.gaussForScreenY((int) y + tractHeightPx / 2) * 1.5f + 0.4;

        return (float) scale;
    }

    // Scrolling
    private float getXForScrollItemPosition(int itemPosition) {
        return 0;
    }

    private float getYForScrollItemPosition(int itemPosition) {
        return ((float) screenHeightPx / (tractCount + 1f)) * itemPosition;
    }

    private float getScrollScaleForPosition(float x, float y) {
        double scale = gaussScroll.gaussForScreenY((int) y + tractHeightPx / 2) * 1.5f + 0.4;

        return (float) scale;
    }

    public float getScrollPosition() {
        return scrollPosition;
    }
}
