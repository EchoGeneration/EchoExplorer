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

import com.spencerbarton.echoexplorer.database.TutorialStepDb.TutorialStepTable;
import com.spencerbarton.echoexplorer.database.TutorialStepDb.TutorialStep;

import java.io.IOException;

public class TutorialActivity extends ActionBarActivity implements SwipeGestureDetector.SwipeGestureHandler {

    private final static String TAG = "TutorialActivity";
    private SwipeGestureDetector mSwipeGestureDetector;
    private LessonManager mLessonManager;
    private String mName = "";
    private TutorialStep[] mSteps;
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
        mName = intent.getStringExtra(LessonManager.EXTRA_LESSON_NAME);
        int id = intent.getIntExtra(LessonManager.EXTRA_LESSON_ID, -1);

        // Add gesture recognition
        mSwipeGestureDetector = new SwipeGestureDetector(this, this);

        // Add lesson movement management
        mLessonManager = new LessonManager(this, id);

        // Get tutorial steps
        try {
            TutorialStepTable tutorialStepTable = new TutorialStepTable(this);

            // Get sorted steps
            // TODO utilize updated method
            mSteps = tutorialStepTable.getTutorials();
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

    //----------------------------------------------------------------------------------------------
    // PlayAudioService
    //----------------------------------------------------------------------------------------------

    // TODO put into serperate audio manager

    private PlayAudioService mService;
    private boolean mIsBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayAudioService.PlayAudioBinder binder = (PlayAudioService.PlayAudioBinder) service;
            mService = binder.getService();
            mIsBound = true;
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

    // TODO abstract lesson step
    // TODO add in step manegement
    // TODO complete evaluations
    // TODO test with DB
    // TODO incorporate DB
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
                    stepData.directionsAudioFile, "raw", TutorialActivity.this.getPackageName());
            mEchoAudioFile = TutorialActivity.this.getResources().getIdentifier(
                    stepData.echoAudioFile, "raw", TutorialActivity.this.getPackageName());
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
            mAudioService.playAudio(mDirectionsAudioFile, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mDirectionsPlayed = true;
                }
            });
        }

        public void handleEchoBtn() {
            if (mDirectionsPlayed) {
                mAudioService.playAudio(mEchoAudioFile);
            }
        }

    }

}
