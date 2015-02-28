package com.spencerbarton.echoexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class TutorialActivity extends ActionBarActivity implements SwipeGestureDetector.SwipeGestureHandler {

    private final static String TAG = "TutorialActivity";
    private SwipeGestureDetector mSwipeGestureDetector;
    private LessonManager mLessonManager;
    private String mName = "";

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
        mName = intent.getStringExtra(LessonManager.EXTRA_LESSON_NAME);
        int id = intent.getIntExtra(LessonManager.EXTRA_LESSON_ID, -1);

        // Add gesture recognition
        mSwipeGestureDetector = new SwipeGestureDetector(this, this);

        // Add lesson movement management
        mLessonManager = new LessonManager(this, id);

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
        mLessonManager.goNext();
    }

    @Override
    public void onSwipeLeft() {
        mLessonManager.goPrev();
    }

    @Override
    public void onSwipeUp() {

        // Do nothing
        Log.i(TAG, "UP");
    }

    @Override
    public void onSwipeDown() {
        mLessonManager.goHome();
    }

}
