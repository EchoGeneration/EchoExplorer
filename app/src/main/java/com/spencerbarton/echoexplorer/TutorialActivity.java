package com.spencerbarton.echoexplorer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.spencerbarton.echoexplorer.database.TutorialStepTable.TutorialStepTableHelp;
import com.spencerbarton.echoexplorer.database.TutorialStepTable.TutorialStep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO abstract lesson step
// TODO complete evaluations
// TODO test - include real audio in db

public class TutorialActivity extends ActionBarActivity implements SwipeGestureDetector.SwipeGestureHandler {

    private final static String TAG = "TutorialActivity";
    private SwipeGestureDetector mSwipeGestureDetector;
    private LessonManager mLessonManager;
    private String mTutorialName = "";
    private List<TutorialStep> mStepsData;
    private List<TutorialStepManager> mStepManagers;
    private int mCurStep = 0;

    //----------------------------------------------------------------------------------------------
    // Startup
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Extract info on which tutorial was instantiated
        Intent intent = getIntent();
        mTutorialName = intent.getStringExtra(LessonManager.EXTRA_LESSON_NAME);
        int lessonNumber = intent.getIntExtra(LessonManager.EXTRA_LESSON_NUMBER, -1);

        // Add gesture recognition
        mSwipeGestureDetector = new SwipeGestureDetector(this, this);

        // Add lesson movement management
        mLessonManager = new LessonManager(this, lessonNumber);

        // Get tutorial steps
        try {
            TutorialStepTableHelp tutorialStepTable = new TutorialStepTableHelp(this);

            // Get sorted steps
            mStepsData = tutorialStepTable.getAllRows(lessonNumber);

        } catch (IOException e) {

            // Leave tutorial on data loading error
            Log.e(TAG, e.getMessage());
            mLessonManager.goHome();
        }

        onCreateAudio();

    }

    @Override
    protected void onStart() {
        super.onStart();
        onStartAudio();
    }


    @Override
    protected void onStop() {
        super.onStop();
        onStopAudio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);
        setTitle(mTutorialName);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mSwipeGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    //----------------------------------------------------------------------------------------------
    // Handlers
    //----------------------------------------------------------------------------------------------

    public void onEchoBtn(View view) {
        Log.d("Echo Button", "Echo button pressed"); mStepManagers.get(mCurStep).handleEchoBtn();
    }

    @Override
    public void onSwipeRight() {

        mCurStep--;

        // Go to prev tutorial because done with steps
        if (mCurStep < 0) {
            mLessonManager.goPrev();
        } else {
            mStepManagers.get(mCurStep).play();
        }

    }

    @Override
    public void onSwipeLeft() {

        mCurStep++;

        // Go to next tutorial because done with steps
        if (mCurStep >= mStepManagers.size()) {
            mLessonManager.goNext();
        } else {
            mStepManagers.get(mCurStep).play();
        }

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

    //----------------------------------------------------------------------------------------------
    // PlayAudioService
    //----------------------------------------------------------------------------------------------

    // TODO put into separate audio manager
    private PlayAudioService mService;
    private boolean mIsBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayAudioService.PlayAudioBinder binder = (PlayAudioService.PlayAudioBinder) service;
            mService = binder.getService();
            mIsBound = true;
            initSteps();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsBound = false;
        }
    };

    private void onCreateAudio() {
        // Setup audio volume controls, note only called once
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void onStartAudio() {

        // Bind audio service
        Intent intent = new Intent(this, PlayAudioService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void onStopAudio() {
        // Unbind audio service (will kill it as this is the only binding activity)
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Step Object - handles all step specific activities
    //----------------------------------------------------------------------------------------------

    // TODO assumes audio binding follows DB callback
    public void initSteps() {

        // Requires bound audio service
        if (mIsBound) {

            mStepManagers = new ArrayList<>();
            for (TutorialStep step : mStepsData) {
                mStepManagers.add(new TutorialStepManager(step, mService));
            }

            // Begin first step
            mStepManagers.get(0).play();
            mCurStep = 0;
        }
    }

    class TutorialStepManager {

        private int mDirectionsAudioFile;
        private int mEchoAudioFile;
        private String mTextDirections;
        private boolean mDirectionsPlayed = false;
        private PlayAudioService mAudioService;

        public TutorialStepManager(TutorialStep stepData, PlayAudioService service) {
            mTextDirections = stepData.textDirections;
            mAudioService = service;

            // Get resource ids
            mDirectionsAudioFile = TutorialActivity.this.getResources().getIdentifier(
                    stepData.audioDirFile, "raw", TutorialActivity.this.getPackageName());
            mEchoAudioFile = TutorialActivity.this.getResources().getIdentifier(
                    stepData.echoFile, "raw", TutorialActivity.this.getPackageName());
        }

        public void play() {
            postDirections();
            playDirections(); // Goes into echo mode immediately after
        }

        private void postDirections() {
            TextView textView = (TextView) TutorialActivity.this.findViewById(R.id.tutorial_directions);
            textView.setText(mTextDirections);
        }

        private void playDirections() {
            if (!mDirectionsPlayed) {
                mAudioService.playAudio(mDirectionsAudioFile, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mDirectionsPlayed = true;
                    }
                });
            }
        }

        public void handleEchoBtn() {
            if (mDirectionsPlayed) {
                mAudioService.playAudio(mEchoAudioFile);
            }
        }

    }

}
