package com.spencerbarton.echoexplorer;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;

/**
 * Created by Spencer on 2/27/2015.
 */
public class SwipeGesturerListener implements GestureOverlayView.OnGesturePerformedListener {



    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

    }

    public interface SwipeGestureHandler {

        public void onSwipeRight();
        public void onSwipeLeft();
        public void onSwipeUp();
        public void onSwipeDown();
    }
}
