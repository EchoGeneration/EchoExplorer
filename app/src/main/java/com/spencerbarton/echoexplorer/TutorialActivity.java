package com.spencerbarton.echoexplorer;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class TutorialActivity extends ActionBarActivity implements SwipeGestureDetector.SwipeGestureHandler {

    private final static String TAG = "TutorialActivity";
    private String mName = "";
    private SwipeGestureDetector mSwipeGestureDetector;

    //----------------------------------------------------------------------------------------------
    // Startup
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Extract info on which tutorial was instantiated
        Intent intent = getIntent();
        // TODO extract tutorial id and then look-up info in DB
        // TODO replace name with id
        mName = intent.getStringExtra(TutorialsMenuActivity.EXTRA_TUTORIAL_NAME);
        long id = intent.getLongExtra(TutorialsMenuActivity.EXTRA_TUTORIAL_ID, 0);
        Log.i(TAG, "Id: " + id + ", Name: " + mName);

        // Add gesture recognition
        mSwipeGestureDetector = new SwipeGestureDetector(this, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        setTitle(mName);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSwipeGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //----------------------------------------------------------------------------------------------
    // Handlers
    //----------------------------------------------------------------------------------------------

    public void onEchoBtn(View view) {
        Log.i(TAG, "Echo btn clicked");
    }

    @Override
    public void onSwipeRight() {

        // Previous step or tutorial
        Log.i(TAG, "RIGHT");
    }

    @Override
    public void onSwipeLeft() {

        // Next step or tutorial
        Log.i(TAG, "LEFT");
    }

    @Override
    public void onSwipeUp() {

        // Do nothing
        Log.i(TAG, "UP");
    }

    @Override
    public void onSwipeDown() {

        // Return to menu
        Intent intent = new Intent(this, TutorialsMenuActivity.class);
        startActivity(intent);
    }
}
