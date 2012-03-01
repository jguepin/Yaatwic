package com.chmod0.yaatwic;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {

    private int REL_SWIPE_MIN_DISTANCE;
    private int REL_SWIPE_MAX_OFF_PATH;
    private int REL_SWIPE_THRESHOLD_VELOCITY;

    public SwipeDetector(int densityDpi) {
        REL_SWIPE_MIN_DISTANCE = (int) (120.0f * densityDpi / 160.0f + 0.5);
        REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * densityDpi / 160.0f + 0.5);
        REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * densityDpi / 160.0f + 0.5);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
            return false;
        try {
            if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                onRTLFling();
                return true;
            } else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE &&
                    Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
                onLTRFling();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void onRTLFling() {
        Log.d("Yaa", "Right to Left Fling");
    }

    private void onLTRFling() {
        Log.d("Yaa", "Left to Right Fling");
    }
}
