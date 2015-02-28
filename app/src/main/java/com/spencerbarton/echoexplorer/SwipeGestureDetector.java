package com.spencerbarton.echoexplorer;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Swipe gesture detection
 *
 * TODO detect gestures over whole screen including over top of buttons
 *
 * Created by Spencer on 2/27/2015.
 */
public class SwipeGestureDetector {

    private GestureDetectorCompat mGestureDetectorCompat;

    public SwipeGestureDetector(Context context, SwipeGestureHandler handler) {
        mGestureDetectorCompat = new GestureDetectorCompat(context, new SwipeGestureListener(handler));
    }

    public void onTouchEvent(MotionEvent event) {
        mGestureDetectorCompat.onTouchEvent(event);
    }

    // Gesture detection listener
    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener{

        private SwipeGestureHandler mHandler;
        private final float SWIPE_SLOPE_MAX_ANGLE = 15;
        private final float SWIPE_SLOPE_THRESHOLD = (float)Math.tan(
                Math.toRadians(SWIPE_SLOPE_MAX_ANGLE));
        private final float SWIPE_VELOCITY_THRESHOLD = 100;

        public SwipeGestureListener(SwipeGestureHandler handler) {
            super();
            mHandler = handler;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            // All gestures start with onDown so want to handle
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velX, float velY) {

            boolean isSwipe = false;

            // Detect swipes
            float dX = event2.getX() - event1.getX();
            float dY = event2.getY() - event1.getY();
            float slope;

            if (abs(dX) > abs(dY)) {

                // Right-left potential swipe
                slope = dY / dX;
                if ((abs(slope) < SWIPE_SLOPE_THRESHOLD) &&
                        (abs(velX) > SWIPE_VELOCITY_THRESHOLD)) {

                    isSwipe = true;

                    if (dX > 0) {
                        mHandler.onSwipeRight();
                    } else {
                        mHandler.onSwipeLeft();
                    }
                }
            } else {

                // Up-down potential swipe
                slope = dX / dY;
                if ((abs(slope) < SWIPE_SLOPE_THRESHOLD) &&
                        (abs(velY) > SWIPE_VELOCITY_THRESHOLD)) {

                    isSwipe = true;

                    // Note flipped from expected
                    if (dY < 0) {
                        mHandler.onSwipeUp();
                    } else {
                        mHandler.onSwipeDown();
                    }
                }
            }
            return isSwipe;
        }

        private float abs(float x) {
            return Math.abs(x);
        }

    }

    // Interface for activity to implement
    public interface SwipeGestureHandler {
        public void onSwipeRight();
        public void onSwipeLeft();
        public void onSwipeUp();
        public void onSwipeDown();
    }
}
